variable "project_id" {
  description = "Google Cloud Project ID"
  type        = string
}

variable "region" {
  description = "GCP Region"
  type        = string
  default     = "europe-west6"
}

variable "zone" {
  description = "GCP Zone"
  type        = string
  default     = "europe-west6-a"
}

variable "environment" {
  description = "Deployment environment"
  type        = string
  default     = "prod"
}

variable "machine_type" {
  description = "GCE machine type."
  type        = string
  default     = "e2-medium"
}

variable "boot_disk_size_gb" {
  description = "Grösse der Boot-Disk in GB"
  type        = number
  default     = 30
}

variable "ssh_user" {
  description = "SSH-Username, der für GitHub Actions Deployment verwendet wird"
  type        = string
  default     = "deploy"
}

variable "ssh_public_key" {
  description = "Öffentlicher SSH-Key, der auf der VM hinterlegt wird"
  type        = string
}
