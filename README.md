# BM Internship App

Backend API for a simple recruitment management platform.

The project helps HR teams manage users, uploaded candidate documents, and candidate profiles. It includes JWT authentication, role-based access control, MySQL persistence, Flyway migrations, Swagger documentation, and SeaweedFS file storage.

## Features

- User management
- Login with JWT token
- Role-based access control
- Upload candidate CVs/documents
- Upload documents in bulk
- Download and delete documents
- Create and manage candidate profiles
- Track candidate status
- Add tags to candidates
- Attach uploaded documents to candidates

## Roles

The project has three user roles:

```text
ADMIN
HR
INTERVIEWER
```

Access rules:

| Role | Access |
| --- | --- |
| `ADMIN` | Can manage users, candidates, and documents |
| `HR` | Can manage candidates and documents |
| `INTERVIEWER` | Can view candidates/documents and update candidate status |

## Technologies

- Java 25
- Spring Boot 4.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT
- MySQL
- Flyway
- SeaweedFS
- Swagger/OpenAPI
- Gradle
- Docker Compose
- JUnit

## Folder Structure

```text
BM_InternApp/
+-- src/
|   +-- main/
|   |   +-- java/com/example/demo/
|   |   |   +-- config/        # Security, JWT filter, Swagger config
|   |   |   +-- controller/    # API controllers
|   |   |   +-- dto/           # Request/response objects
|   |   |   +-- entity/        # Database entities and enums
|   |   |   +-- mapper/        # Entity/DTO mapping
|   |   |   +-- repository/    # JPA repositories
|   |   |   +-- service/       # Business logic
|   |   +-- resources/
|   |       +-- db/migration/  # Flyway database migrations
|   |       +-- application.yml
|   +-- test/
+-- internapp-infra/
|   +-- docker-compose.yml     # MySQL and SeaweedFS
+-- build.gradle
+-- settings.gradle
```

## Database Migrations

Flyway migrations are in:

```text
src/main/resources/db/migration
```

Current migrations:

```text
V1__create_users_table.sql
V2__create_documents_table.sql
V3__create_candidates_table.sql
V4__add_user_roles_and_candidate_tags.sql
```

Important: after a migration has already run in MySQL, do not edit it. Create a new migration instead.

## Setup

Make sure Docker is running, then start MySQL and SeaweedFS:

```bash
docker compose -f internapp-infra/docker-compose.yml up -d
```

Check containers:

```bash
docker compose -f internapp-infra/docker-compose.yml ps
```

The project uses these default database settings:

```text
Database: internship_db
Username: intern_user
Password: intern_pass
Port: 3306
```

SeaweedFS runs on:

```text
Master: http://localhost:9333
Volume: http://localhost:8090
```

## Build

Compile the project:

```bash
./gradlew compileJava
```

Run the full build:

```bash
./gradlew build
```

If Flyway fails because local migrations changed during development, reset the local database:

```bash
docker compose -f internapp-infra/docker-compose.yml down -v
docker compose -f internapp-infra/docker-compose.yml up -d
./gradlew build
```

Warning: `down -v` deletes local MySQL data.

## Run

Start the Spring Boot app:

```bash
./gradlew bootRun
```

The app runs on:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Create Test Users

Open MySQL:

```bash
docker exec -it internapp-infra-mysql-1 mysql -uintern_user -pintern_pass internship_db
```

Insert test users:

```sql
INSERT INTO users (username, email, password, role)
VALUES
('admin', 'admin@test.com', '123', 'ADMIN'),
('hr', 'hr@test.com', '123', 'HR'),
('interviewer', 'interviewer@test.com', '123', 'INTERVIEWER');
```

Passwords are plain text for now because this is a simple internship project version.

## Authentication

Login:

```text
POST /auth/login?username=admin&password=123
```

The response is a JWT token.

For protected APIs, send:

```text
Authorization: Bearer <token>
```

## API Endpoints

### Auth

```text
POST /auth/login
GET  /auth/test
```

### Users

Requires `ADMIN`.

```text
GET  /users
POST /users
```

Example create user body:

```json
{
  "username": "newhr",
  "email": "newhr@test.com",
  "password": "123",
  "role": "HR"
}
```

### Documents

```text
POST   /api/documents
POST   /api/documents/bulk
GET    /api/documents
GET    /api/documents/{id}
GET    /api/documents/{id}/download
DELETE /api/documents/{id}
```

Single upload uses multipart form key:

```text
file
```

Bulk upload uses multipart form key:

```text
files
```

### Candidates

```text
POST   /api/candidates
GET    /api/candidates
GET    /api/candidates?search=java
GET    /api/candidates?status=APPLIED
GET    /api/candidates/{id}
PUT    /api/candidates/{id}
PATCH  /api/candidates/{id}/status?status=INTERVIEW
PATCH  /api/candidates/{id}/document/{documentId}
PATCH  /api/candidates/{id}/tags?tags=java,spring
DELETE /api/candidates/{id}
```

Example candidate body:

```json
{
  "fullName": "Ahmed Ali",
  "email": "ahmed@test.com",
  "phone": "01000000000",
  "position": "Backend Developer",
  "tags": "java,spring,mysql",
  "status": "APPLIED",
  "documentId": 1
}
```

Candidate statuses:

```text
APPLIED
INTERVIEW
OFFER
REJECTED
```

## Testing With Bruno

Create a Bruno environment:

```text
baseUrl = http://localhost:8080
token = paste_jwt_token_here
```

Login first:

```text
POST {{baseUrl}}/auth/login?username=admin&password=123
```

Copy the token, then add this header to protected requests:

```text
Authorization: Bearer {{token}}
```

For document upload:

```text
Body: Multipart Form
Key: file
Type: File
```

For bulk document upload:

```text
Body: Multipart Form
Key: files
Type: File
```

Add multiple `files` rows for multiple uploads.

## Useful Commands

Start infrastructure:

```bash
docker compose -f internapp-infra/docker-compose.yml up -d
```

Stop infrastructure:

```bash
docker compose -f internapp-infra/docker-compose.yml down
```

View logs:

```bash
docker compose -f internapp-infra/docker-compose.yml logs -f
```

Run app:

```bash
./gradlew bootRun
```

Build app:

```bash
./gradlew build
```

Compile only:

```bash
./gradlew compileJava
```
