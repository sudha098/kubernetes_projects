FROM openjdk:17-jdk-slim
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw package -DskipTests
EXPOSE 9090
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
