# Teams Platform - Quick Start Guide

## Phase 1 Implementation Complete! ✅

### What's Built
- JWT Authentication & Authorization
- User Management (register, login, profile)
- Database schema with all core entities
- REST API with Swagger documentation
- Security configuration with role-based access control

---

## Quick Setup (5 Minutes)

### 1. PostgreSQL Database Setup

```bash
# Create database
createdb teams_db

# Create user
psql -U postgres -c "CREATE USER teams_user WITH PASSWORD 'teams_pass';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE teams_db TO teams_user;"
```

Or using Docker:
```bash
docker run --name postgres-teams \
  -e POSTGRES_DB=teams_db \
  -e POSTGRES_USER=teams_user \
  -e POSTGRES_PASSWORD=teams_pass \
  -p 5432:5432 \
  -d postgres:15
```

### 2. Update Configuration (Optional)
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/teams_db
spring.datasource.username=teams_user
spring.datasource.password=teams_pass
app.jwt.secret=your-super-secret-key-change-in-production
```

### 3. Run the Application

```bash
# Clone/Navigate to project
cd C:\Users\Ahmed\Desktop\Work\Teams\Teams

# Build
./mvnw clean install -DskipTests

# Run
./mvnw spring-boot:run
```

Or using the JAR directly:
```bash
java -jar target/Teams-0.0.1-SNAPSHOT.jar
```

### 4. Access the Application

- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **API Base**: http://localhost:8080/api/v1
- **Health**: http://localhost:8080/api/v1/health

---

## API Examples

### Register User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "username": "john_doe",
    "password": "SecurePassword123!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePassword123!"
  }'

# Response:
# {
#   "code": 200,
#   "message": "Login successful",
#   "data": {
#     "accessToken": "eyJhbGc...",
#     "refreshToken": "eyJhbGc...",
#     "tokenType": "Bearer",
#     "expiresIn": 86400,
#     "user": { ... }
#   }
# }
```

### Get Current User Profile (with token)
```bash
curl -X GET http://localhost:8080/api/v1/users/{userId} \
  -H "Authorization: Bearer <access_token>"
```

### Search Users
```bash
curl -X GET "http://localhost:8080/api/v1/users/search?query=john" \
  -H "Authorization: Bearer <access_token>"
```

---

## Project Structure

```
Teams/
├── .specify/
│   ├── plan-microsoftTeamsLikePlatform.prompt.md  [Implementation Plan]
│   ├── PHASE_1_IMPLEMENTATION_SUMMARY.md           [This Document]
│   └── specs/
│       └── microsoft_teams_like_platform_sow_brs.md [Original Specification]
├── src/
│   ├── main/
│   │   ├── java/com/teams/teams/
│   │   │   ├── controller/        [REST Endpoints]
│   │   │   ├── service/           [Business Logic]
│   │   │   ├── repository/        [Data Access]
│   │   │   ├── domain/            [JPA Entities]
│   │   │   ├── dto/               [DTOs]
│   │   │   ├── security/          [JWT & Auth]
│   │   │   ├── exception/         [Error Handling]
│   │   │   ├── config/            [Configurations]
│   │   │   └── TeamsApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/      [Flyway Migrations]
│   └── test/
└── pom.xml                         [Maven Config]
```

---

## Key Technologies

| Component | Technology |
|-----------|-----------|
| Framework | Spring Boot 4.0.6 |
| Language | Java 17 |
| Database | PostgreSQL 13+ |
| Authentication | JWT (JJWT 0.12.3) |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security 7.0 |
| API Doc | SpringDoc OpenAPI (Swagger 3.0) |
| Migrations | Flyway |
| Build | Maven |
| Containerization | Docker (ready) |

---

## Database Schema

### Core Tables
- `users` - User accounts
- `roles` - Role definitions
- `permissions` - Permission definitions
- `user_roles` - User-role mapping
- `teams` - Workspaces
- `teams_members` - Team membership
- `channels` - Chat channels
- `channel_members` - Channel membership
- `messages` - Chat messages
- `conversations` - DM conversations
- `attachments` - File uploads
- `notifications` - In-app notifications
- `audit_logs` - Activity logs

### Indexes
- User email/username for fast lookup
- Team/channel queries
- Message queries per channel/conversation
- Notification user queries

---

## Default Roles (Pre-configured)

1. **ROLE_SYSTEM_ADMIN** - Full system access
2. **ROLE_ORG_ADMIN** - Organization administration
3. **ROLE_TEAM_OWNER** - Team management
4. **ROLE_STANDARD_USER** - Regular user (default for new registrations)
5. **ROLE_GUEST** - Limited guest access

---

## Security Features

✅ JWT Access Tokens (24 hours)
✅ JWT Refresh Tokens (7 days)
✅ BCrypt password hashing
✅ Account locking after 5 failed attempts
✅ CORS configuration
✅ CSRF protection (disabled for stateless API)
✅ Session management (stateless)
✅ Role-based access control (RBAC)
✅ Method-level authorization

---

## Troubleshooting

### Database Connection Error
```
Error: org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```
**Solution**: Ensure PostgreSQL is running:
```bash
# Windows
net start PostgreSQL

# macOS
brew services start postgresql

# Linux
sudo systemctl start postgresql
```

### Port Already in Use
```
Error: Tomcat port 8080 already in use
```
**Solution**: Change port in application.properties:
```properties
server.port=8081
```

### JWT Token Expired
```
Error: Token expired
```
**Solution**: Use the refresh token to get a new access token:
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh?refreshToken=<token>
```

---

## Next Phase (Phase 2)

Planned features for Phase 2:
1. Teams CRUD operations
2. Channels management
3. Messaging system
4. WebSocket real-time communication
5. Presence tracking
6. Read receipts
7. Message reactions

---

## Development Notes

- All timestamps are in UTC
- Responses use consistent ApiResponse wrapper
- Pagination uses Spring Data's Page interface
- Default page size: 20 items
- JWT secret should be at least 32 characters for production
- All endpoints require authentication except /auth/*, /health, and /info

---

## Support & Documentation

- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v1/v3/api-docs
- **Plan Document**: `.specify/plan-microsoftTeamsLikePlatform.prompt.md`
- **Specification**: `.specify/specs/microsoft_teams_like_platform_sow_brs.md`

---

## Build & Deployment

### Local Build
```bash
./mvnw clean install -DskipTests
```

### Run Tests
```bash
./mvnw test
```

### Docker Build (Ready)
```bash
docker build -t teams-platform .
docker run -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/teams_db teams-platform
```

---

**Project Status**: ✅ Phase 1 Complete
**Last Updated**: May 11, 2026
**Build**: Teams-0.0.1-SNAPSHOT.jar

