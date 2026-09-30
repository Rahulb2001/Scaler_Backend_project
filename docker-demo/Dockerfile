# Build the jar first with: mvn clean package
# Then build the image:      docker build -t scaler-backend/docker-demo:1.0.0 .
# Run it:                    docker run -d --name docker-demo -p 8080:8080 scaler-backend/docker-demo:1.0.0

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/docker-demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
