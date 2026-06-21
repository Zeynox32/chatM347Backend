# Chattrix – Multi-Module Maven Deployment (GCP)

Dieses Setup ist für ein **Multi-Module Maven-Projekt** angepasst: ein Repo mit einer Parent-`pom.xml` und allen 5 Backend-Services als Module darin.

---

## Was sich gegenüber dem Single-Module-Setup ändert

Vorher hat jeder Service sein eigenes Dockerfile in seinem eigenen Ordner, mit dem Service-Ordner selbst als Docker Build-Context. Bei einem Multi-Module-Projekt **funktioniert das nicht**, weil Maven die Parent-`pom.xml` braucht, um die Module-Abhängigkeiten aufzulösen.

Stattdessen gilt jetzt:
- **Ein zentraler `docker/`-Ordner** im Repo-Root enthält alle 5 Dockerfiles (eines pro Service)
- **Build-Context ist immer das Repo-Root** (`.`), nicht der Service-Unterordner
- Jedes Dockerfile nutzt `mvn ... -pl <service> -am`, um **nur das benötigte Modul + seine Abhängigkeiten** zu bauen (nicht das ganze Projekt – das wäre unnötig langsam)

---

## Repo-Struktur (so muss es aussehen)

```
dein-backend-repo/                  ← Repo-Root, hier liegt die Parent-pom.xml
├── pom.xml                         ← Parent-POM (packaging: pom)
├── shared/
│   ├── pom.xml
│   └── src/
├── authentication-service/
│   ├── pom.xml
│   └── src/
├── user-service/
│   ├── pom.xml
│   └── src/
├── chat-service/
│   ├── pom.xml
│   └── src/
├── websocket-service/
│   ├── pom.xml
│   └── src/
├── gateway-service/
│   ├── pom.xml
│   └── src/
├── docker/                          ← NEU: aus diesem Archiv
│   ├── authentication-service.Dockerfile
│   ├── user-service.Dockerfile
│   ├── chat-service.Dockerfile
│   ├── websocket-service.Dockerfile
│   └── gateway-service.Dockerfile
├── docker-compose-files/            ← aus diesem Archiv (für die VM)
│   ├── docker-compose.yml
│   ├── Caddyfile
│   ├── .env.example
│   └── scripts/init-mariadb.sql
├── terraform/                       ← aus diesem Archiv
│   ├── main.tf
│   ├── variables.tf
│   ├── outputs.tf
│   └── startup-script.sh
└── .github/workflows/
    └── deploy.yml                   ← aus diesem Archiv (aktualisiert!)
```

---

## Falls eure Module anders heissen

Die Dockerfiles gehen davon aus, dass eure Maven-Module **exakt so heissen** wie die Service-Ordner: `authentication-service`, `user-service`, `chat-service`, `websocket-service`, `gateway-service`. Das war eure Bestätigung – falls sich das ändert, müssen in jedem Dockerfile die `-pl <name>` Stellen sowie alle `COPY <name>/...` Zeilen angepasst werden.

---

## Gemeinsames Modul "shared" ist bereits eingebaut

Euer `shared`-Modul wird in allen 5 Dockerfiles automatisch mitgebaut (sowohl die `pom.xml` als auch der `src`-Ordner werden kopiert, und `-am` sorgt dafür, dass Maven es als Abhängigkeit mitkompiliert). Voraussetzung: der Ordner heisst exakt `shared` und liegt im Repo-Root auf derselben Ebene wie die anderen Service-Ordner.

Falls `shared` aus Untermodulen besteht oder Submodule selbst weitere Module referenziert, müsste das Schema entsprechend erweitert werden – sag in dem Fall Bescheid.

---

## Wichtig: Parent-POM muss `packaging: pom` haben

Eure Root-`pom.xml` sollte so aussehen:

```xml
<project>
    <groupId>com.chattrix</groupId>
    <artifactId>chattrix-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
        <module>shared</module>
        <module>authentication-service</module>
        <module>user-service</module>
        <module>chat-service</module>
        <module>websocket-service</module>
        <module>gateway-service</module>
    </modules>
</project>
```

Falls `<modules>` fehlt oder Modul-Namen abweichen, schlägt `mvn -pl <modul>` fehl, weil Maven das Modul nicht im Reactor findet.

---

## Lokal testen, bevor ihr pusht

**Wichtig:** immer vom Repo-Root aus bauen (`-f` zeigt nur, welches Dockerfile genutzt wird, der Build-Context `.` bleibt das Root):

```bash
# Vom Repo-Root aus:
docker build -f docker/authentication-service.Dockerfile -t test-auth .
docker run -p 3001:3001 -e DB_HOST=host.docker.internal -e DB_PASSWORD=test test-auth

curl http://localhost:3001/actuator/health
```

Falls der Build mit `Could not find artifact com.chattrix:...` fehlschlägt, prüfen:
1. Liegt die `pom.xml` wirklich im Repo-Root?
2. Stimmen die Modul-Namen in `<modules>` mit den Ordnernamen überein?
3. Ist die `groupId`/`version` im Parent identisch mit der, die die Module referenzieren?

---

## Workflow-Änderung im Detail

In `.github/workflows/deploy.yml` wurde geändert:

```yaml
# Vorher (Single-Module):
context: ./${{ matrix.service }}

# Jetzt (Multi-Module):
context: .
file: ./docker/${{ matrix.service }}.Dockerfile
```

Dadurch sieht Docker beim Build immer das komplette Repo (inkl. Parent-POM und aller Module), während trotzdem nur das jeweils benötigte Modul tatsächlich kompiliert wird.

---

## Restliches Setup unverändert

Terraform, GitHub Secrets, Docker Compose auf der VM, Frontend-Deployment – das alles bleibt exakt wie in der vorherigen Anleitung. Nur der **Build-Prozess der Backend-Images** wurde für Multi-Module Maven angepasst.
