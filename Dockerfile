FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Step 2 — Push to GitHub**, then go to [render.com](https://render.com):

1. New → **Web Service**
2. Connect your GitHub repo
3. Set these fields:
   - **Environment**: Docker
   - **Port**: 8082

**Step 3 — Add environment variables** in Render's dashboard under "Environment":
```
NEON_DB_URL        = your_neon_jdbc_url
NEON_DB_USERNAME   = your_neon_username
NEON_DB_PASSWORD   = your_neon_password
JWT_SECRET         = your_generated_secret
JWT_EXPIRATION_MS  = 86400000