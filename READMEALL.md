# Chattrix – Google Cloud Deployment

Deployment der Chattrix Microservice-Architektur auf einer **einzelnen Google Cloud VM** mit Docker Compose. Infrastruktur wird mit Terraform erstellt, Images über **GitHub Container Registry (ghcr.io)** verteilt, Deployment läuft automatisch via **GitHub Actions + SSH** bei jedem Push auf `main`.

---

## Architektur

```
Internet
   │
   ▼
Caddy (Reverse Proxy + automatisches SSL)
   │
   ├──► Frontend-Service
   └──► Gateway-Service ──RabbitMQ──► Authentication-Service ──► MariaDB (authentication_db)
                                   ├── User-Service           ──► MariaDB (user_db)
                                   ├── Chat-Service            ──► MongoDB (chat_db)
                                   └── WebSocket-Service       ──► Redis
```

Alle Container laufen auf **einer** Google Compute Engine VM (Ubuntu 22.04), orchestriert mit `docker compose`.

---

## Einmaliges Setup

### 1. Google Cloud Projekt vorbereiten

```bash
# Im Google Cloud Shell (shell.cloud.google.com) oder lokal mit gcloud CLI
gcloud projects create chattrix-prod --name="Chattrix"
gcloud config set project chattrix-prod

# Billing-Konto verknüpfen (im Cloud Console UI, Billing → Link a billing account)
# Compute Engine API aktivieren
gcloud services enable compute.googleapis.com
```

### 2. Service Account für Terraform erstellen

```bash
gcloud iam service-accounts create chattrix-terraform \
  --display-name="Chattrix Terraform"

gcloud projects add-iam-policy-binding chattrix-prod \
  --member="serviceAccount:chattrix-terraform@chattrix-prod.iam.gserviceaccount.com" \
  --role="roles/compute.admin"

gcloud projects add-iam-policy-binding chattrix-prod \
  --member="serviceAccount:chattrix-terraform@chattrix-prod.iam.gserviceaccount.com" \
  --role="roles/iam.serviceAccountUser"

# JSON-Key erstellen – diesen Inhalt brauchst du für GitHub Secrets
gcloud iam service-accounts keys create chattrix-terraform-key.json \
  --iam-account=chattrix-terraform@chattrix-prod.iam.gserviceaccount.com

cat chattrix-terraform-key.json
# → kompletten Inhalt kopieren
```

### 3. Storage Bucket für Terraform State erstellen

```bash
gsutil mb -l europe-west6 gs://chattrix-tfstate-prod
gsutil versioning set on gs://chattrix-tfstate-prod

gcloud projects add-iam-policy-binding chattrix-prod \
  --member="serviceAccount:chattrix-terraform@chattrix-prod.iam.gserviceaccount.com" \
  --role="roles/storage.admin"
```

### 4. SSH-Key-Paar für das Deployment erstellen

```bash
ssh-keygen -t ed25519 -f ./chattrix-deploy-key -C "deploy@chattrix" -N ""

cat chattrix-deploy-key.pub
# → öffentlicher Key, wird Terraform-Variable ssh_public_key

cat chattrix-deploy-key
# → privater Key, wird GitHub Secret VM_SSH_PRIVATE_KEY
```

---

## GitHub Secrets hinterlegen

### Backend-Repo (alle Services + Terraform)

`Settings → Secrets and variables → Actions → New repository secret`

| Secret | Wert |
|---|---|
| `GCP_SA_KEY` | Kompletter Inhalt von `chattrix-terraform-key.json` |
| `GCP_PROJECT_ID` | z.B. `chattrix-prod` |
| `GCS_TFSTATE_BUCKET` | z.B. `chattrix-tfstate-prod` |
| `VM_SSH_PUBLIC_KEY` | Inhalt von `chattrix-deploy-key.pub` |
| `VM_SSH_PRIVATE_KEY` | Inhalt von `chattrix-deploy-key` (privater Key!) |
| `MARIADB_ROOT_PASSWORD` | Selbst wählen |
| `MONGO_ROOT_USERNAME` | z.B. `chattrixadmin` |
| `MONGO_ROOT_PASSWORD` | Selbst wählen |
| `REDIS_PASSWORD` | Selbst wählen |
| `RABBITMQ_USER` | z.B. `chattrix` |
| `RABBITMQ_PASSWORD` | Selbst wählen |
| `JWT_SECRET` | `openssl rand -hex 32` |
| `DOMAIN` | Deine Domain, z.B. `chattrix.example.com` (oder VM-IP falls keine Domain) |
| `PUBLIC_URL` | z.B. `https://chattrix.example.com` |
| `PUBLIC_WS_URL` | z.B. `wss://chattrix.example.com/ws` |
| `CURRENT_FRONTEND_TAG` | Initial `latest`, danach vom Frontend-Workflow verwaltet |

### Frontend-Repo (separat)

| Secret | Wert |
|---|---|
| `VM_SSH_PRIVATE_KEY` | Gleicher Key wie oben |
| `VM_IP` | Statische IP der VM (Output nach erstem Terraform-Run) |
| `GHCR_OWNER` | Dein GitHub-Username/Org |

---

## Repo-Struktur (Backend)

```
dein-backend-repo/
├── authentication-service/
│   └── Dockerfile
├── user-service/
│   └── Dockerfile
├── chat-service/
│   └── Dockerfile
├── websocket-service/
│   └── Dockerfile
├── gateway-service/
│   └── Dockerfile
├── terraform/                  ← aus diesem Archiv
│   ├── main.tf
│   ├── variables.tf
│   ├── outputs.tf
│   └── startup-script.sh
├── docker/                     ← aus diesem Archiv
│   ├── docker-compose.yml
│   ├── Caddyfile
│   ├── .env.example
│   └── scripts/init-mariadb.sql
└── .github/workflows/
    └── deploy.yml               ← aus diesem Archiv
```

---

## Erster Deploy

```bash
git add .
git commit -m "add gcp terraform & ci/cd"
git push origin main
```

Der Workflow läuft automatisch in dieser Reihenfolge:
1. **Build & Push** – alle 5 Backend-Images werden gebaut und nach GHCR gepusht
2. **Terraform Apply** – erstellt VM, Netzwerk, Firewall, statische IP (dauert beim ersten Mal ~3-5 Min)
3. **Deploy via SSH** – kopiert `docker-compose.yml` auf die VM, schreibt `.env`, zieht Images, startet alles

Den Fortschritt siehst du unter dem Tab **Actions** im Repo.

---

## Danach: VM_IP Secret im Frontend-Repo nachtragen

Nach dem ersten erfolgreichen Terraform-Run:

```bash
cd terraform
terraform output vm_external_ip
```

Diese IP als `VM_IP` Secret im **Frontend-Repo** hinterlegen, dann das Frontend-Repo pushen.

---

## Domain einrichten (optional, für SSL)

Falls du eine Domain hast: DNS A-Record auf die statische IP zeigen lassen, dann läuft Caddy automatisch Let's Encrypt SSL.

Ohne Domain: im `Caddyfile` den oberen Block auskommentieren und den `:80`-Block (ohne SSL) aktivieren – dann ist die App nur über `http://VM_IP` erreichbar.

---

## Manuelle Befehle auf der VM (Debugging)

```bash
ssh -i chattrix-deploy-key deploy@<VM_IP>

cd /opt/chattrix
docker compose ps               # Status aller Container
docker compose logs -f gateway-service   # Logs eines Services
docker compose restart chat-service      # Einzelnen Service neu starten
```

---

## Kosten

Eine `e2-medium` VM (2 vCPU, 4 GB RAM) kostet ca. **25 USD/Monat** in `europe-west6`. Mit dem **300$ Google Cloud Startguthaben** für Neukunden läuft das Projekt ca. 12 Monate komplett kostenlos.

Für ein noch günstigeres Setup (echtes Free Tier) `machine_type = "e2-micro"` in `us-west1`, `us-central1` oder `us-east1` verwenden – dort ist eine e2-micro VM dauerhaft gratis, allerdings mit nur 1 GB RAM (eng für 9 Container, ggf. Swap einrichten).
