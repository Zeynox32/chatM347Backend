output "vm_external_ip" {
  description = "Öffentliche IP-Adresse der Chattrix VM"
  value       = google_compute_address.chattrix_static_ip.address
}

output "vm_name" {
  description = "Name der VM-Instanz"
  value       = google_compute_instance.chattrix.name
}

output "ssh_command" {
  description = "Befehl zum manuellen SSH-Login"
  value       = "ssh ${var.ssh_user}@${google_compute_address.chattrix_static_ip.address}"
}
