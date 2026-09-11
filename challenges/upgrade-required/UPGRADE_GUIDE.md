# Spring Boot Application Upgrade Guide

## Current State
- **Java Version**: 11
- **Spring Boot**: 2.5.3
- **MySQL Connector**: 8.0.22
- **Swagger/Springfox**: 2.9.2
- **Mockito**: 2.22.0
- **H2 Database**: 1.4.200
- **Testing**: JUnit Vintage (JUnit 4)

## Target State
- **Java Version**: 17 or 21 (LTS recommended)
- **Spring Boot**: 3.3.x or 3.4.x (or 4.x if you want the latest)
- **MySQL Connector**: 8.4.0
- **Swagger/API Docs**: Springdoc-openapi 2.x (replaces Springfox)
- **Mockito**: 5.x
- **H2 Database**: 2.x
- **Testing**: JUnit 5 (Jupiter)

## Why This Upgrade Matters

### Java Version (11 → 17/21)
- **11 is no longer supported** (ended September 2023)
- Java 17 and 21 are LTS (Long-Term Support) versions
- Performance improvements and security patches included

### Spring Boot (2.5.3 → 3.3+)
- **2.5.3 is very outdated** and no longer receiving updates
- Spring Boot 3.x requires Java 17+
- **Major change**: Spring 6.0+ uses `jakarta.*` packages instead of `javax.*` packages
- This is a breaking change that requires code updates

### Swagger (Springfox 2.9.2 → Springdoc-openapi 2.x)
- **Springfox 2.x is deprecated** and incompatible with Spring Boot 3.x
- Springdoc-openapi is the modern replacement
- Uses Jakarta namespace (javax → jakarta)
- Better integration with Spring Boot 3.x

### Other Dependencies
- **MySQL Connector**: 8.0.22 is from 2020; upgrade for security patches
- **Mockito**: 2.22.0 is ancient; update for compatibility with modern testing
- **H2**: 1.4.200 needs updates; 2.x is the current version

---

## Step-by-Step Upgrade Process

### Step 1: Update pom.xml - Core Dependencies

Replace the current `pom.xml` configuration with:

```xml
<properties>
    <java.version>17</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <start-class>com.conygre.spring.boot.AppConfig</start-class>
</properties>

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.3</version>
</parent>

<dependencies>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.4.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
        <version>2.2.220</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Springdoc OpenAPI for API documentation (replaces Springfox) -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.3.0</version>
    </dependency>

    <!-- Mockito for testing -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.7.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Key changes:**
- Java version: `11` → `17`
- Spring Boot parent: `2.5.3` → `3.3.3`
- MySQL dependency: `mysql:mysql-connector-java` → `com.mysql:mysql-connector-j`
- **Removed** Springfox Swagger dependencies (2.9.2)
- **Added** Springdoc-openapi (2.3.0) - the modern replacement
- **Removed** Mockito version override (uses Spring Boot parent version)
- H2: `1.4.200` → `2.2.220`

### Step 2: Update SwaggerConfig.java

**Old approach (Springfox)** - REMOVE/REPLACE the entire SwaggerConfig.java

The SwaggerConfig class is **no longer needed** with Springdoc. Spring Boot 3.x with Springdoc handles Swagger/OpenAPI automatically.

**Option A: Delete SwaggerConfig.java completely**
- Springdoc auto-configures everything
- Access Swagger UI at: `http://localhost:8080/swagger-ui.html`

**Option B: Keep minimal configuration** (if you need customization)

Replace SwaggerConfig.java with:

```java
package com.conygre.spring.boot;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("Album REST API")
                    .description("This API allows you to interact with albums. It is a CRUD API")
                    .contact(new Contact()
                        .name("Nick Todd")
                        .url("http://www.conygre.com")
                        .email("nick.todd@conygre.com")));
    }
}
```

**Key changes:**
- Replaced `@EnableSwagger2` with `@Configuration`
- Changed from `Docket` (Springfox) to `OpenAPI` (Springdoc/OpenAPI 3.0)
- Updated imports to `io.swagger.v3.oas.*` packages

### Step 3: Update Swagger Annotations in CompactDiscController.java

**Old annotations (Springfox):**
```java
import io.swagger.annotations.ApiOperation;

@ApiOperation(value = "findAll", nickname = "findAll")
```

**New annotations (Springdoc/OpenAPI 3.0):**
```java
import io.swagger.v3.oas.annotations.Operation;

@Operation(summary = "findAll", description = "Get all compact discs")
```

Update all `@ApiOperation` annotations to `@Operation` throughout your controllers.

### Step 4: Remove @Import from AppConfig.java

**Old:**
```java
@SpringBootApplication
@Import(SwaggerConfig.class)
@ComponentScan
public class AppConfig {
```

**New (if you kept SwaggerConfig):**
```java
@SpringBootApplication
@ComponentScan
public class AppConfig {
```

Or simply:
```java
@SpringBootApplication
public class AppConfig {
```

Spring Boot 3.x will auto-discover and auto-configure everything.

---

## Breaking Changes to Handle

### 1. Package Naming: javax → jakarta
With Spring Boot 3.x / Spring 6.0, all javax.* packages become jakarta.*

**This should be handled automatically** for standard Spring imports, but if you added any custom code using javax packages, update them:

```java
// OLD
import javax.persistence.*;
import javax.servlet.*;

// NEW
import jakarta.persistence.*;
import jakarta.servlet.*;
```

### 2. Test Dependencies
The `junit-vintage-engine` dependency can be removed. Spring Boot 3.x uses JUnit 5 by default.

Remove from pom.xml:
```xml
<!-- REMOVE THIS -->
<dependency>
    <groupId>org.junit.vintage</groupId>
    <artifactId>junit-vintage-engine</artifactId>
    <scope>test</scope>
    <exclusions>...</exclusions>
</dependency>
```

### 3. RequestMethod Usage (Optional but Recommended)

**Old style:**
```java
@RequestMapping(method = RequestMethod.GET)
public Iterable<CompactDisc> findAll() { ... }
```

**Modern style (no change required, but more concise):**
```java
@GetMapping
public Iterable<CompactDisc> findAll() { ... }
```

Similarly use `@PostMapping`, `@PutMapping`, `@DeleteMapping`, etc.

---

## Verification Steps

1. **Build the project:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Test Swagger UI:**
   - Navigate to: `http://localhost:8080/swagger-ui.html`
   - Should display the API documentation

4. **Test API endpoints:**
   - Use the Swagger UI or your REST client
   - Verify all endpoints work correctly

5. **Test the database connection:**
   - Ensure MySQL connection works
   - Verify CRUD operations function properly

---

## Common Issues & Solutions

### Issue 1: "Cannot find symbol: class EnableSwagger2"
**Solution:** Delete the `@EnableSwagger2` annotation and the import. It doesn't exist in Springdoc.

### Issue 2: "Cannot find symbol: class ApiOperation"
**Solution:** Replace with `@Operation` from `io.swagger.v3.oas.annotations.Operation`

### Issue 3: Maven build fails with dependency conflicts
**Solution:** Run `mvn dependency:tree` to check for conflicts. The Spring Boot 3.3.3 parent should manage most versions.

### Issue 3: "Package javax.servlet does not exist"
**Solution:** Update to use `jakarta.servlet` instead.

### Issue 4: Tests fail after upgrade
**Solution:** Ensure you've removed `junit-vintage-engine` and removed any JUnit 4 specific code.

---

## Summary of Changes

| Component | Old | New | Action |
|-----------|-----|-----|--------|
| Java | 11 | 17 | Update pom.xml property |
| Spring Boot | 2.5.3 | 3.3.3 | Update parent version |
| MySQL Connector | 8.0.22 | 8.4.0 | Update dependency & artifact ID |
| Swagger | Springfox 2.9.2 | Springdoc 2.3.0 | Replace dependency & code |
| Swagger Config | @EnableSwagger2 + Docket | @Configuration + OpenAPI | Rewrite config class |
| Annotations | @ApiOperation | @Operation | Update controller |
| Tests | JUnit 4 + junit-vintage | JUnit 5 | Remove vintage dependency |
| H2 | 1.4.200 | 2.2.220 | Update version |
| Package Names | javax.* | jakarta.* | Auto-updated by Spring |

---

## Additional Resources

- [Spring Boot 3.x Migration Guide](https://spring.io/blog/2022/05/13/spring-boot-3-0-goes-ga)
- [Springdoc-openapi Documentation](https://springdoc.org/)
- [Spring Framework 6 & Jakarta EE Guide](https://spring.io/blog/2022/02/21/spring-boot-3-0-0-rc1-released)
