FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
COPY shared/pom.xml shared/pom.xml
COPY authentication-service/pom.xml authentication-service/pom.xml
COPY user-service/pom.xml user-service/pom.xml
COPY chat-service/pom.xml chat-service/pom.xml
COPY websocket-service/pom.xml websocket-service/pom.xml
COPY gateway-service/pom.xml gateway-service/pom.xml

RUN mvn dependency:go-offline -B -pl chat-service -am || true

COPY shared/src shared/src
COPY authentication-service/src authentication-service/src
COPY user-service/src user-service/src
COPY chat-service/src chat-service/src
COPY websocket-service/src websocket-service/src
COPY gateway-service/src gateway-service/src

RUN mvn clean package -DskipTests -B -pl chat-service -am

RUN rm -f chat-service/target/*-plain.jar

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /build/chat-service/target/*.jar app.jar

EXPOSE 3003

ENTRYPOINT ["java", "-jar", "app.jar"]
