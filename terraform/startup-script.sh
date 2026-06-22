#!/bin/bash

set -e

apt-get update -y
apt-get upgrade -y

curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
rm get-docker.sh

systemctl enable docker
systemctl start docker

mkdir -p /opt/chattrix
chmod 777 /opt/chattrix

cat > /etc/cron.d/chattrix-docker-group << 'CRON'
* * * * * root for u in $(ls /home 2>/dev/null); do usermod -aG docker "$u" 2>/dev/null; done
CRON

echo "Chattrix VM Setup abgeschlossen am $(date)" > /var/log/chattrix-setup-done.log
