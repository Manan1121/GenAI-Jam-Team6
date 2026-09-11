# Compact Disc Catalog Management System

A REST API for managing a music CD catalog with a web-based UI, built with Spring Boot and MySQL.

## Overview

This Spring Boot application provides complete CRUD functionality for a CD database with:
- RESTful API endpoints for programmatic access
- Interactive web-based user interface
- Swagger API documentation
- MySQL persistence with JPA/Hibernate
- Log4j2 logging and monitoring
- Docker deployment ready

---

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 11 |
| Framework | Spring Boot 2.5.3 |
| Database | MySQL 8.0 |
| ORM | JPA / Hibernate |
| API Docs | Swagger 2 / SpringFox 2.9.2 |
| Logging | Log4j2 |
| Build Tool | Maven 3.6+ |
| Frontend | HTML5, CSS3, jQuery, DataTables |
| Testing | JUnit 4, Mockito |

---

## Prerequisites

- Java Development Kit (JDK) 11+
- Maven 3.6+
- MySQL Server 8.0+

Verify with:
```bash
java -version
mvn --version
mysql --version
```

---

## Quick Start

```bash
# Navigate to project
cd challenges/no-readme

# Create database
mysql -u root -p < sql/createTables.sql

# Build
mvn clean install

# Run
mvn spring-boot:run

# Access
# Web UI: http://localhost:8080/index.html
# API: http://localhost:8080/api/compactdiscs
# Swagger UI: http://localhost:8080/swagger-ui.html
```

---

## Installation & Configuration

### Database Setup

```bash
mysql -u root -p < sql/createTables.sql
```

Includes 8 pre-loaded CDs (The Strokes, Stereophonics, Coldplay, David Gray, Penelope, Feeder, Massive Attack, Spice Girls).

### Configure Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/conygre
spring.datasource.username=root
spring.datasource.password=c0nygre1
logging.file=myapplication.log
server.port=8080
```

### Docker Configuration

Use `src/main/resources/application-docker.properties` for containerized deployment:

```properties
spring.datasource.url=jdbc:mysql://cddb:3306/conygre
spring.datasource.username=root
spring.datasource.password=secret123
```

---

## Running the Application

**Maven (Development):**
```bash
mvn spring-boot:run
```

**JAR (Production):**
```bash
mvn clean package
java -jar target/CompactDiscRestDataBoot-0.0.1-SNAPSHOT.jar
```

**IDE (IntelliJ/Eclipse):**
- Open as Maven project
- Run `AppConfig.main()` from IDE

---

## API Reference

Access full API docs at `http://localhost:8080/swagger-ui.html`

### Core Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/compactdiscs` | List all CDs |
| GET | `/api/compactdiscs/{id}` | Get CD by ID |
| POST | `/api/compactdiscs` | Create CD |
| PUT | `/api/compactdiscs/{id}` | Update CD |
| DELETE | `/api/compactdiscs/{id}` | Delete CD |
| GET | `/api/compactdiscs/findByArtist/{artist}` | Find by artist |

### Example Requests

**List CDs:**
```http
GET /api/compactdiscs
```

**Get CD by ID:**
```http
GET /api/compactdiscs/1
```

**Create CD:**
```http
POST /api/compactdiscs
Content-Type: application/json

{
  "title": "Abbey Road",
  "artist": "The Beatles",
  "price": 12.99,
  "tracks": 17
}
```

**Delete CD:**
```http
DELETE /api/compactdiscs/1
```

---

## Database Schema

**compact_discs table:**
- `id` (INT, PRIMARY KEY)
- `title` (VARCHAR(50))
- `artist` (VARCHAR(30))
- `tracks` (INT)
- `price` (DOUBLE)

**tracks table:**
- `id` (INT, PRIMARY KEY)
- `cd_id` (INT, FOREIGN KEY)
- `title` (VARCHAR(50))

---

## Docker Deployment

### Dockerfile

```dockerfile
FROM maven:3.6-jdk-11 as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/target/CompactDiscRestDataBoot-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'
services:
  cddb:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: secret123
      MYSQL_DATABASE: conygre
    ports:
      - "3306:3306"
    volumes:
      - ./sql/createTables.sql:/docker-entrypoint-initdb.d/init.sql
      - mysql_data:/var/lib/mysql

  cd-app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
    depends_on:
      - cddb

volumes:
  mysql_data:
```

### Deploy

```bash
docker-compose build
docker-compose up -d
docker-compose logs -f cd-app
```

Access at `http://localhost:8080`

---

## Logging & Monitoring

**Log file:** `myapplication.log` (created in application root)

**Log levels:**
- DEBUG: Detailed info (package: `com.conygre`)
- INFO: Operational info
- WARN: Warnings
- ERROR: Errors

**Configure logging** in `src/main/resources/log4j2.properties`:

```properties
log4j.rootLogger=INFO
log4j.logger.com.conygre=DEBUG
appender.file.filename=myapplication.log
```

---

## Troubleshooting

**"Connection refused"**
- Ensure MySQL is running and credentials in `application.properties` are correct

**"Table not found"**
- Run: `mysql -u root -p < sql/createTables.sql`

**Maven build fails**
- Run: `mvn clean install -U`

**Port 8080 already in use**
- Change `server.port=8081` in `application.properties`

---

## License

MIT License