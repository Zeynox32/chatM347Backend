terraform {
  required_version = ">= 1.5.0"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.30"
    }
  }

  # Remote State – wird von GitHub Actions per -backend-config befüllt
  backend "gcs" {}
}

provider "google" {
  project = var.project_id
  region  = var.region
  zone    = var.zone
}

# ── APIs aktivieren ────────────────────────────────────────────────────────────
resource "google_project_service" "compute" {
  service            = "compute.googleapis.com"
  disable_on_destroy = false
}

# ── VPC Netzwerk ──────────────────────────────────────────────────────────────
resource "google_compute_network" "chattrix" {
  name                    = "chattrix-vpc"
  auto_create_subnetworks = false
  depends_on              = [google_project_service.compute]
}

resource "google_compute_subnetwork" "chattrix" {
  name          = "chattrix-subnet"
  ip_cidr_range = "10.10.0.0/24"
  region        = var.region
  network       = google_compute_network.chattrix.id
}

# ── Firewall Regeln ───────────────────────────────────────────────────────────
resource "google_compute_firewall" "allow_ssh" {
  name    = "chattrix-allow-ssh"
  network = google_compute_network.chattrix.id

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }

  # Nur GitHub Actions Runner IPs wären sicherer, aber diese ändern sich ständig.
  # Daher offen, aber SSH-Key-Auth ist Pflicht (kein Passwort-Login).
  source_ranges = ["0.0.0.0/0"]
  target_tags   = ["chattrix-vm"]
}

resource "google_compute_firewall" "allow_web" {
  name    = "chattrix-allow-web"
  network = google_compute_network.chattrix.id

  allow {
    protocol = "tcp"
    ports    = ["80", "443"]
  }

  source_ranges = ["0.0.0.0/0"]
  target_tags   = ["chattrix-vm"]
}

resource "google_compute_firewall" "allow_internal" {
  name    = "chattrix-allow-internal"
  network = google_compute_network.chattrix.id

  allow {
    protocol = "tcp"
    ports    = ["0-65535"]
  }

  source_ranges = ["10.10.0.0/24"]
}

# ── Statische IP ──────────────────────────────────────────────────────────────
resource "google_compute_address" "chattrix_static_ip" {
  name   = "chattrix-static-ip"
  region = var.region
}

# ── Service Account für die VM (minimal scope) ────────────────────────────────
resource "google_service_account" "chattrix_vm" {
  account_id   = "chattrix-vm-sa"
  display_name = "Chattrix VM Service Account"
}

# ── Boot Disk Image – Ubuntu 22.04 LTS ────────────────────────────────────────
data "google_compute_image" "ubuntu" {
  family  = "ubuntu-2204-lts"
  project = "ubuntu-os-cloud"
}

# ── Compute Engine VM ──────────────────────────────────────────────────────────
# e2-medium: 2 vCPU, 4 GB RAM – im Free Tier nicht enthalten, aber günstig (~25$/Monat)
# Für echtes Free Tier: e2-micro (0.25-1 vCPU, 1 GB RAM) in us-west1/us-central1/us-east1
resource "google_compute_instance" "chattrix" {
  name         = "chattrix-vm-${var.environment}"
  machine_type = var.machine_type
  zone         = var.zone
  tags         = ["chattrix-vm"]

  boot_disk {
    initialize_params {
      image = data.google_compute_image.ubuntu.self_link
      size  = var.boot_disk_size_gb
      type  = "pd-balanced"
    }
  }

  network_interface {
    network    = google_compute_network.chattrix.id
    subnetwork = google_compute_subnetwork.chattrix.id

    access_config {
      nat_ip = google_compute_address.chattrix_static_ip.address
    }
  }

  metadata = {
    ssh-keys = "${var.ssh_user}:${var.ssh_public_key}"
  }

  metadata_startup_script = file("${path.module}/startup-script.sh")

  service_account {
    email  = google_service_account.chattrix_vm.email
    scopes = ["cloud-platform"]
  }

  allow_stopping_for_update = true

  labels = {
    project     = "chattrix"
    environment = var.environment
    managed_by  = "terraform"
  }
}
