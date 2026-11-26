FROM eclipse-temurin:21-jdk-noble AS construccion
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline
COPY src src
RUN ./mvnw -B -q package -DskipTests

FROM eclipse-temurin:21-jre-noble
WORKDIR /app
RUN useradd --system --uid 10001 casas
COPY --from=construccion /app/target/*.jar app.jar
USER casas
ENV TZ=America/Santiago
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:+UseSerialGC", "-Xss512k", "-XX:TieredStopAtLevel=1", "-jar", "app.jar"]
