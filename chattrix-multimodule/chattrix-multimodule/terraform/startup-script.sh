#!/bin/bash
# Läuft automatisch beim ersten Boot der VM (Google Cloud Startup Script)
# Installiert Docker, damit die VM sofort bereit für Deployments ist

set -e

# System aktualisieren
apt-get update -y
apt-get upgrade -y

# Docker installieren (offizielles Skript)
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
rm get-docker.sh

systemctl enable docker
systemctl start docker

# Verzeichnis für das Chattrix-Deployment anlegen
mkdir -p /opt/chattrix
chmod 777 /opt/chattrix

# Docker-Gruppe existiert jetzt – jeden zukünftigen SSH-Login-User automatisch hinzufügen.
# Da GCE den User erst beim allerersten SSH-Connect anlegt, hängen wir uns
# in einen Cron-Check, der einmalig nachzieht.
cat > /etc/cron.d/chattrix-docker-group << 'CRON'
* * * * * root for u in $(ls /home 2>/dev/null); do usermod -aG docker "$u" 2>/dev/null; done
CRON

echo "Chattrix VM Setup abgeschlossen am $(date)" > /var/log/chattrix-setup-done.log
