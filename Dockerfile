FROM openjdk:17-jdk-slim
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline
COPY src ./src

RUN ./mvnw package -DskipTests

EXPOSE 8080

ARG JAR_FILE=target/OrderManagementService-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]
