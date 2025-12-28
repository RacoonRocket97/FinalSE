# LMS - Learning Management System

Learning Management System with role-based access control for admins, teachers, and students.

## Tech Stack

- Spring Boot 4.0.1, Java 21
- MySQL 8.0, Spring Data JPA, Hibernate
- Liquibase (migrations)
- MapStruct (DTO mapping)
- Spring Security (HTTP Basic Auth)
- JUnit 5 + Mockito

## Architecture

```
Controllers → Services → Repositories → Database
     ↕           ↕
   DTOs      Mappers
```

**Layers:**
- **Controllers**: Handle HTTP requests, apply security
- **Services**: Business logic, transactions
- **Repositories**: Database operations
- **DTOs**: Request/Response objects
- **Mappers**: Entity ↔ DTO conversion (MapStruct)

## Database Entities (7)

1. User
2. Role
3. Course
4. Lesson
5. Enrollment
6. Assignment
7. User_Roles (junction table)

## Setup

### 1. Database

```sql
CREATE DATABASE lms_db;
```

Update `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lms_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 2. Run Application

```bash
./gradlew bootRun
```

Runs on: `http://localhost:8080`

### 3. Migrations

Liquibase runs automatically on startup. 10 changesets:
- 001-007: Create tables (roles, users, user_roles, courses, lessons, enrollments, assignments)
- 008: Insert roles (ADMIN, TEACHER, STUDENT)
- 009: Insert sample users
- 010: Insert sample courses/lessons/assignments

Location: `src/main/resources/db/changelog/changes/`

## API Endpoints

**Authentication:** HTTP Basic Auth (email:password)

### Default Users

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@lms.com | admin123 |
| Teacher | nazerke.sultan@lms.com | admin123 |
| Student | bekzat.kumarbek@lms.com | admin123 |

### Endpoints

**Auth**
- `POST /api/auth/register` - Register (public)
- `POST /api/auth/change-password` - Change password

**Users** (Admin)
- `GET /api/users` - All users
- `POST /api/users` - Create user
- `PUT /api/users/{id}/block` - Block user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/profile` - My profile
- `PUT /api/users/profile` - Update profile

**Courses**
- `GET /api/courses` - All courses (public)
- `POST /api/courses` - Create (teacher)
- `PUT /api/courses/{id}` - Update (teacher)
- `DELETE /api/courses/{id}` - Delete (teacher)

**Lessons**
- `GET /api/lessons/course/{courseId}` - By course
- `POST /api/lessons` - Create (teacher)
- `PUT /api/lessons/{id}` - Update (teacher)
- `DELETE /api/lessons/{id}` - Delete (teacher)

**Enrollments**
- `POST /api/enrollments` - Enroll (student)
- `GET /api/enrollments/my` - My enrollments (student)
- `PUT /api/enrollments/{id}/progress?progress=75` - Update progress (teacher)
- `PUT /api/enrollments/{id}/drop` - Drop (student)

**Assignments**
- `POST /api/assignments` - Create (teacher)
- `GET /api/assignments/my` - My assignments (student)
- `POST /api/assignments/{id}/submit` - Submit (student)
- `PUT /api/assignments/{id}/grade` - Grade (teacher)
- `DELETE /api/assignments/{id}` - Delete (teacher)

## Postman Collection

Import `lms_postman_collection.json` into Postman.

Set variable: `base_url = http://localhost:8080`

Contains 33 requests with pre-configured auth.

## Testing

```bash
./gradlew test
```

Unit tests for all services using JUnit + Mockito.

## Project Structure

```
src/main/java/com/lms/
├── config/          # Security config
├── controllers/     # REST endpoints
├── dto/             # Request/Response DTOs
├── mappers/         # MapStruct interfaces
├── models/          # JPA entities
├── repositories/    # Data access
└── services/        # Business logic
```

---
