
# Stage 1: Build & Extract Layers
FROM eclipse-temurin:21-jdk-jammy AS builder
LABEL authors="echocano"
WORKDIR /application

# Optimization: Cache dependencies separately from source code
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

# Copy source and build the fat jar
COPY src ./src
RUN ./mvnw clean package

# Extract layers using the modern Spring Boot 4 launcher mode
RUN java -Djarmode=layertools -jar target/*.jar extract

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /application

# 1. Security: Create a non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring

# 2. Fix: Install wget for the Healthcheck (missing in base JRE image)
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

USER spring:spring

# 3. Copy layers from the builder stage
# The order matters: least frequent changes first for better caching
COPY --from=builder /application/dependencies/ ./
COPY --from=builder /application/spring-boot-loader/ ./
COPY --from=builder /application/snapshot-dependencies/ ./
COPY --from=builder /application/application/ ./

# 4. Observability: Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# 5. Performance: JVM Tuning
# Note: For Spring Boot 3.2+/4, the launcher class moved to:
# org.springframework.boot.loader.launch.JarLauncher
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "org.springframework.boot.loader.launch.JarLauncher"]