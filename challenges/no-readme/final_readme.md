# Compact Disc Catalog Management System

A production-grade **REST API** for managing a music Compact Disc catalog with a web-based user interface, built with Spring Boot and MySQL.

[![Java](https://img.shields.io/badge/Java-11-orange?logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.5.3-green?logo=spring)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red?logo=apache-maven)](https://maven.apache.org/)

---

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Docker Deployment](#docker-deployment)
- [Monitoring and Logging](#monitoring-and-logging)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)
- [Support](#support)

---

## Features

### Core Functionality

- **Complete CRUD Operations** - Create, read, update, and delete CD records
- **RESTful API** - Standards-compliant REST endpoints with JSON support
- **Web User Interface** - Interactive HTML5 web application for browsing and managing CDs
- **Artist Filtering** - Query CDs by artist name
- **Error Handling** - Proper HTTP status codes including 404 for missing resources
- **CORS Support** - Cross-origin request handling for web applications
- **Transaction Management** - ACID compliance for data operations
- **API Documentation** - Swagger/Swagger UI integration for API exploration

### Operational Features

- **Comprehensive Logging** - Log4j2 configuration for debugging and monitoring
- **Database Persistence** - MySQL backend with JPA/Hibernate ORM
- **Docker Ready** - Pre-configured for containerized deployment
- **Production Configuration** - Environment-specific settings for local and Docker deployments

---

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 11 |
| **Framework** | Spring Boot | 2.5.3 |
| **Database** | MySQL | 8.0 |
| **ORM** | JPA / Hibernate | - |
| **API Documentation** | Swagger 2 / SpringFox | 2.9.2 |
| **Logging** | Log4j2 | - |
| **Build Tool** | Maven | 3.6+ |
| **Frontend** | HTML5, CSS3, jQuery, DataTables | - |
| **Testing** | JUnit 4, Mockito | - |

---

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)** version 11 or higher
- **Maven** version 3.6 or higher
- **MySQL Server** version 8.0 or higher
- **Git** (optional, for version control)

### Verify Installation

```bash
java -version
mvn --version
mysql --version
```

---

## Quick Start

### 1. Clone or Download the Repository

```bash
cd challenges/no-readme
```

### 2. Create the Database

```bash
mysql -u root -p < sql/createTables.sql
```

### 3. Build the Application

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

### 5. Access the Application

- **Web UI:** [http://localhost:8080/index.html](http://localhost:8080/index.html)
- **API Endpoint:** [http://localhost:8080/api/compactdiscs](http://localhost:8080/api/compactdiscs)
- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Installation

### Step 1: Database Setup

Create the MySQL database and tables:

```bash
mysql -u root -p
```

Then execute the SQL schema:

```sql
SOURCE sql/createTables.sql;
```

Alternatively, use the command line:

```bash
mysql -u root -p < sql/createTables.sql
```

**Default Sample Data:** The script includes 8 pre-loaded CDs (The Strokes, Stereophonics, Coldplay, David Gray, Penelope, Feeder, Massive Attack, and Spice Girls) for immediate testing.

### Step 2: Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/conygre
spring.datasource.username=root
spring.datasource.password=c0nygre1
```

### Step 3: Build the Project

```bash
mvn clean install
```

This command:
- Cleans previous builds
- Resolves dependencies from `pom.xml`
- Compiles Java source code
- Runs tests
- Packages the application as a JAR file

---

## Configuration

### Application Properties (Local Development)

**File:** `src/main/resources/application.properties`

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/conygre
spring.datasource.username=root
spring.datasource.password=c0nygre1
spring.jpa.hibernate.ddl-auto=validate

# Logging Configuration
logging.file=myapplication.log
logging.level.root=INFO
logging.level.com.conygre=DEBUG

# Server Configuration
server.port=8080
```

### Application Properties (Docker Deployment)

**File:** `src/main/resources/application-docker.properties`

```properties
# Docker Database Configuration
spring.datasource.url=jdbc:mysql://cddb:3306/conygre
spring.datasource.username=root
spring.datasource.password=secret123
spring.profiles.active=docker
```

**Note:** The hostname `cddb` refers to the MySQL service name in Docker Compose, not localhost.

### Log4j2 Configuration

**File:** `src/main/resources/log4j2.properties`

Configures logging output:
- **Console Logging:** INFO level and above
- **Log File:** `myapplication.log`
- **Log Pattern:** `%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n`

---

## Running the Application

### Option 1: Using Maven (Development)

```bash
mvn spring-boot:run
```

### Option 2: Using JAR File (Production)

```bash
# Build first
mvn clean package

# Run the JAR
java -jar target/CompactDiscRestDataBoot-0.0.1-SNAPSHOT.jar
```

### Option 3: Using IDE (IntelliJ IDEA / Eclipse)

1. Open the project as a Maven project
2. Navigate to `src/main/java/com/conygre/spring/boot/AppConfig.java`
3. Right-click and select **Run 'AppConfig.main()'**

### Verification

Once running, you should see output similar to:

```
... Started AppConfig in X.XXX seconds (JVM running for Y.YYY)
```

The application will be available at `http://localhost:8080`

---

## API Documentation

### Interactive API Docs

Access **Swagger UI** at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Core Endpoints

#### List All CDs

```http
GET /api/compactdiscs
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "First Impressions of Earth",
    "artist": "The Strokes",
    "price": 9.99,
    "tracks": 12
  },
  {
    "id": 2,
    "title": "Word Gets Around",
    "artist": "Stereophonics",
    "price": 8.99,
    "tracks": 10
  }
]
```

#### Get CD by ID

```http
GET /api/compactdiscs/{id}
```

**Example:** `GET /api/compactdiscs/1`

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "First Impressions of Earth",
  "artist": "The Strokes",
  "price": 9.99,
  "tracks": 12
}
```

**Response (404 Not Found):**
```json
{
  "error": "CD not found"
}
```

#### Create a New CD

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

**Response (201 Created):**
```json
{
  "id": 9,
  "title": "Abbey Road",
  "artist": "The Beatles",
  "price": 12.99,
  "tracks": 17
}
```

#### Update a CD

```http
PUT /api/compactdiscs/{id}
Content-Type: application/json

{
  "title": "Abbey Road",
  "artist": "The Beatles",
  "price": 11.99,
  "tracks": 17
}
```

#### Delete a CD by ID

```http
DELETE /api/compactdiscs/{id}
```

**Example:** `DELETE /api/compactdiscs/9`

**Response (204 No Content)**

#### Delete a CD by Entity

```http
DELETE /api/compactdiscs
Content-Type: application/json

{
  "id": 9,
  "title": "Abbey Road",
  "artist": "The Beatles",
  "price": 11.99,
  "tracks": 17
}
```

### Query by Artist

```http
GET /api/compactdiscs/findByArtist/{artist}
```

**Example:** `GET /api/compactdiscs/findByArtist/The Strokes`

---

## Database Schema

### Entity-Relationship Diagram

```
┌─────────────────────────┐
│   compact_discs         │
├─────────────────────────┤
│ id (PK)                 │
│ title                   │
│ artist                  │
│ price                   │
│ tracks                  │
└─────────────────────────┘
         │
         │ 1:N
         │
         ▼
┌─────────────────────────┐
│   tracks                │
├─────────────────────────┤
│ id (PK)                 │
│ cd_id (FK)              │
│ title                   │
└─────────────────────────┘
```

### Table Definitions

#### `compact_discs` Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique CD identifier |
| `title` | VARCHAR(50) | NOT NULL | Album title |
| `artist` | VARCHAR(30) | NOT NULL | Artist name |
| `tracks` | INT | NOT NULL | Number of tracks on the CD |
| `price` | DOUBLE | NOT NULL | CD price |

#### `tracks` Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique track identifier |
| `cd_id` | INT | FOREIGN KEY, NOT NULL | Reference to compact_discs |
| `title` | VARCHAR(50) | NOT NULL | Track title |

### Sample Data

The following CDs are included by default:

| ID | Title | Artist | Price | Tracks |
|----|----|------|-------|--------|
| 1 | First Impressions of Earth | The Strokes | 9.99 | 12 |
| 2 | Word Gets Around | Stereophonics | 8.99 | 10 |
| 3 | Parachutes | Coldplay | 10.99 | 11 |
| 4 | Life in Slow Motion | David Gray | 9.99 | 11 |
| 5 | Everything It Takes | Penelope | 9.99 | 10 |
| 6 | Polythene Scars | Feeder | 8.99 | 13 |
| 7 | Protection | Massive Attack | 11.99 | 10 |
| 8 | Spice | Spice Girls | 7.99 | 11 |

---

## Docker Deployment

### Prerequisites

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 1.29 or higher)

### Dockerfile

Create a `Dockerfile` in the project root:

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

Create a `docker-compose.yml` in the project root:

```yaml
version: '3.8'

services:
  # MySQL Database Service
  cddb:
    image: mysql:8.0
    container_name: cd-mysql-db
    environment:
      MYSQL_ROOT_PASSWORD: secret123
      MYSQL_DATABASE: conygre
    ports:
      - "3306:3306"
    volumes:
      - ./sql/createTables.sql:/docker-entrypoint-initdb.d/init.sql
      - mysql_data:/var/lib/mysql
    networks:
      - cd-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Spring Boot Application Service
  cd-app:
    build: .
    container_name: cd-app-server
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:mysql://cddb:3306/conygre
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: secret123
    depends_on:
      cddb:
        condition: service_healthy
    networks:
      - cd-network
    restart: unless-stopped

volumes:
  mysql_data:

networks:
  cd-network:
    driver: bridge
```

### Build and Run with Docker Compose

```bash
# Build the Docker image
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f cd-app

# Access the application
# Web UI: http://localhost:8080/index.html
# API: http://localhost:8080/api/compactdiscs
# Swagger UI: http://localhost:8080/swagger-ui.html

# Stop all services
docker-compose down
```

### Verify Docker Deployment

```bash
# Check service status
docker-compose ps

# Test API endpoint
curl http://localhost:8080/api/compactdiscs

# View application logs
docker-compose logs cd-app

# Access database container
docker-compose exec cddb mysql -uroot -psecret123 -e "USE conygre; SELECT * FROM compact_discs;"
```

---

## Monitoring and Logging

### Log File Location

Application logs are written to:

```
myapplication.log
```

This file is created in the application's root directory when the application starts.

### Log Levels

- **DEBUG** - Detailed information for development (package: `com.conygre`)
- **INFO** - General operational information
- **WARN** - Warning messages for potential issues
- **ERROR** - Error conditions

### Log Format

```
2024-01-15 10:30:45,123 INFO  AppConfig:42 - Application started
2024-01-15 10:30:46,456 DEBUG CompactDiscService:58 - getting the catalog
2024-01-15 10:30:47,789 INFO  CompactDiscController:25 - managed to call a Get request for findAll
```

### Example Log Entries

```
2024-01-15 10:30:47 INFO  CompactDiscController:25 - managed to call a Get request for findAll
2024-01-15 10:30:48 DEBUG CompactDiscService:58 - getting the catalog
2024-01-15 10:30:49 INFO  CompactDiscService:125 - CD record retrieved: id=1, title=First Impressions of Earth
```

### Monitoring Recommendations

1. **Rotate Logs** - Implement log rotation to manage file size
2. **Monitor Errors** - Set up alerts for ERROR level log entries
3. **Performance Tracking** - Monitor response times through log entries
4. **Database Health** - Watch for connection errors in logs

### Configuring Logging

Edit `src/main/resources/log4j2.properties` to adjust:

```properties
# Set log level
log4j.rootLogger=INFO

# Set specific package level
log4j.logger.com.conygre=DEBUG

# Change log file location
appender.file.filename=logs/application.log
```

---

## Project Structure

```
no-readme/
├── pom.xml                                  # Maven build configuration
├── README.md                                # This file
├── sql/
│   └── createTables.sql                     # Database schema and sample data
├── rest/
│   ├── deletecd.rest                        # REST client delete examples
│   └── postcd.rest                          # REST client create examples
└── src/
    └── main/
        ├── java/
        │   └── com/conygre/
        │       ├── AppConfig.java           # Spring Boot entry point & Swagger config
        │       ├── CompactDiscController.java    # REST API endpoints
        │       ├── CompactDiscService.java       # Business logic interface
        │       ├── CompactDiscServiceImpl.java    # Service implementation
        │       ├── CompactDiscRepository.java    # JPA data access layer
        │       ├── CompactDisc.java             # CD entity class
        │       └── Track.java                   # Track entity class
        ├── resources/
        │   ├── application.properties       # Local environment config
        │   ├── application-docker.properties # Docker environment config
        │   ├── log4j2.properties            # Logging configuration
        │   └── static/
        │       ├── index.html               # Main web UI
        │       ├── listcds.html             # CD list with DataTables
        │       ├── promisefetch.html        # Modern Fetch API UI
        │       ├── constructorfunctionajax.html # Legacy AJAX UI
        │       └── css/
        │           └── styles.css           # Application styles
        └── test/
            └── java/                        # Unit tests
```

---

## Contributing

### Code Style

- Follow Java conventions and naming standards
- Use meaningful variable and method names
- Keep methods focused and under 50 lines
- Add comments for complex logic

### Testing

All new features should include unit tests:

```bash
mvn test
```

### Submitting Changes

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -am 'Add new feature'`)
4. Push to branch (`git push origin feature/new-feature`)
5. Submit a Pull Request

### Reporting Issues

Submit bug reports via the issue tracker with:

- Description of the issue
- Steps to reproduce
- Expected vs. actual behavior
- Environment details (OS, Java version, etc.)

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Support

### Getting Help

- **Documentation:** See this README and Swagger UI for API docs
- **Logs:** Check `myapplication.log` for error messages
- **Database Issues:** Verify MySQL connection with: `mysql -u root -p -e "SELECT 1"`
- **Port Conflicts:** If port 8080 is in use, change `server.port` in `application.properties`

### Common Issues

**Issue:** "Connection refused" error
- **Solution:** Ensure MySQL is running and credentials are correct in `application.properties`

**Issue:** "Table not found" error
- **Solution:** Run `mysql -u root -p < sql/createTables.sql` to create schema

**Issue:** Maven build fails
- **Solution:** Run `mvn clean install -U` to update dependencies

**Issue:** Port 8080 already in use
- **Solution:** Change port in `application.properties`: `server.port=8081`

### Version History

| Version | Release Date | Notes |
|---------|-------------|-------|
| 0.0.1-SNAPSHOT | 2024 | Initial release with CRUD operations |

---

## Contact

For questions or suggestions about this project:

- **Email:** support@conygre.com
- **Website:** www.conygre.com
- **Documentation:** [Swagger UI](http://localhost:8080/swagger-ui.html)

---

**Last Updated:** January 2024  
**Maintained By:** Development Team