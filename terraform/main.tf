terraform {
  required_version = ">= 1.5.0"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.30"
    }
  }

  backend "gcs" {}
}

provider "google" {
  project = var.project_id
  region  = var.region
  zone    = var.zone
}

resource "google_project_service" "compute" {
  service            = "compute.googleapis.com"
  disable_on_destroy = false
}

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

resource "google_compute_firewall" "allow_ssh" {
  name    = "chattrix-allow-ssh"
  network = google_compute_network.chattrix.id

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }

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

resource "google_compute_address" "chattrix_static_ip" {
  name   = "chattrix-static-ip"
  region = var.region
}

resource "google_service_account" "chattrix_vm" {
  account_id   = "chattrix-vm-sa"
  display_name = "Chattrix VM Service Account"
}

data "google_compute_image" "ubuntu" {
  family  = "ubuntu-2204-lts"
  project = "ubuntu-os-cloud"
}

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
