# Contact Management System - Backend

This directory contains the Spring Boot backend for the Contact Management System internship project.

Tech stack:
- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Security
- PostgreSQL
- Lombok
- Validation

Run locally:
1. Ensure PostgreSQL is running and a database `contact_management_db` exists (or change the URL in `src/main/resources/application.properties`).
2. Update `spring.datasource.password` in `application.properties`.
3. Build and run:

```
./mvnw spring-boot:run
```

or

```
mvn spring-boot:run
```


