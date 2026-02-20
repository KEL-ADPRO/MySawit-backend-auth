# MySawit Backend - Authentication 🌴

This is the Spring Boot backend service for the **MySawit** application, specifically handling the Authentication module. It uses a PostgreSQL database and integrates with a Next.js frontend.

## 🚀 Prerequisites
To run this project locally, ensure you have the following installed:
1. Java Development Kit (JDK) 21
2. PostgreSQL (or a cloud provider like Neon DB)
3. Git

## ⚙️ Local Setup & Installation

**1. Clone the repository**
```bash
git clone https://github.com/KEL-ADPRO/MySawit-backend-auth.git
cd MySawit-backend-auth
```

**2. Setup Environment Variables**
Insert the example environment variables below to your local `.env` file:
```bash
# Database Configuration (Neon DB / PostgreSQL)
# Replace the values below with your local or cloud PostgreSQL credentials
NEON_DB_URL=your_databse_url
NEON_DB_USERNAME=your_database_username
NEON_DB_PASSWORD=your_database_password

# Server Configuration
# Note: The frontend expects this service to run on port 8082
SERVER_PORT=8082
```
*Update the newly created `.env` file with your active PostgreSQL URL, Username, and Password.*
- Go to or create `src/main/resources/application.properties` and paste
```bash
server.port=8082

spring.datasource.url=${NEON_DB_URL}

spring.datasource.username=${NEON_DB_USERNAME}
spring.datasource.password=${NEON_DB_PASSWORD}

spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```

**3. Run the Application (via IntelliJ IDEA)**
- Click the three dots beside the Run button at the top right and select **Edit Configurations**.
- Click on the blue **Modify options** text, navigate to the **Operating System** section, then enable **Environment variables**.
- An environment variable input field should now be available.
- On the right of the input field, click on the folder/document icon and select your `.env` file. (Alternatively, if you use the EnvFile plugin, check "Enable EnvFile" and add it there).
- Start the Spring Boot server. The backend will start running on `http://localhost:8082`.

**4. Run the FrontEnd**\
*Note: This requires the separate frontend repository.*
Navigate to your frontend project directory in a separate terminal and start the Next.js server:
```bash
npm install
npm run dev
```

*(Note: CORS is configured to allow requests from the frontend running on `http://localhost:3000`)*
