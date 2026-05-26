FROM eclipse-temurin:21-jdk

WORKDIR /chattrix-backend

COPY target/chattrix-backend.jar chattrix-backend.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "chattrix-backend.jar"]