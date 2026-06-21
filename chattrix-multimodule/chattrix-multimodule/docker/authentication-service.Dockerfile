# Dockerfile für authentication-service – Multi-Module Maven Build
#
# WICHTIG: Dieses Dockerfile muss mit dem REPO-ROOT als Build-Context gebaut werden,
# nicht mit dem Service-Unterordner! Sonst findet Maven die Parent-pom.xml nicht.
#
# Lokal testen (vom Repo-Root aus):
#   docker build -f docker/authentication-service.Dockerfile -t test-auth .

# ── Stage 1: Build ─────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

# 1. Zuerst NUR alle pom.xml-Dateien kopieren (Parent + shared + alle Module)
#    -> Docker Layer Cache greift, solange sich an den pom.xml nichts ändert
COPY pom.xml .
COPY shared/pom.xml shared/pom.xml
COPY authentication-service/pom.xml authentication-service/pom.xml
COPY user-service/pom.xml user-service/pom.xml
COPY chat-service/pom.xml chat-service/pom.xml
COPY websocket-service/pom.xml websocket-service/pom.xml
COPY gateway-service/pom.xml gateway-service/pom.xml

# 2. Dependencies für das Zielmodul + alle Module, von denen es abhängt
#    (inkl. "shared"), vorladen.
#    -pl = "project list" (nur dieses Modul)  -am = "also make" (+ Module, von denen es abhängt)
RUN mvn dependency:go-offline -B -pl authentication-service -am || true

# 3. Jetzt den kompletten Quellcode kopieren (shared + alle Services, da -am
#    zur Build-Zeit prüft, was tatsächlich gebraucht wird)
COPY shared/src shared/src
COPY authentication-service/src authentication-service/src
COPY user-service/src user-service/src
COPY chat-service/src chat-service/src
COPY websocket-service/src websocket-service/src
COPY gateway-service/src gateway-service/src

# 4. Nur das Zielmodul (+ Abhängigkeiten wie "shared") bauen, Tests überspringen
RUN mvn clean package -DskipTests -B -pl authentication-service -am

# 5. WICHTIG: Spring Boot erzeugt neben dem ausführbaren Fat-JAR zusätzlich ein
#    "*-plain.jar" (nur kompilierter Code, ohne Dependencies, ohne Main-Class).
#    Dieses hier (noch als root in der Build-Stage) entfernen, damit im nächsten
#    Schritt garantiert nur das ausführbare JAR übrig bleibt.
RUN rm -f authentication-service/target/*-plain.jar

# ── Stage 2: Runtime ───────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Jetzt liegt nur noch genau ein JAR im target/-Ordner -> *.jar trifft sicher
COPY --from=build /build/authentication-service/target/*.jar app.jar

EXPOSE 3001

HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:3001/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
