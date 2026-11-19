# HireTrack API

HireTrack is a REST API for tracking job applications from first apply through interviews and offers. When you apply to many roles, details end up scattered across spreadsheets and inboxes, follow-ups slip, and it is hard to see which applications are still alive. HireTrack keeps company, role, source, salary range, notes, pipeline status, follow-up reminders, and hiring metrics in one place.

## Features

- Full CRUD for job applications with validation (including salary min/max)
- Status pipeline with enforced transitions and persisted history
- Timeline endpoint showing every status change
- Filtered, paginated application search (status, company, source, date range, free-text)
- Follow-up reminders with due listing and completion
- Daily scheduler for overdue follow-up flags and ghosting stale applications
- Aggregate stats (response rate, interview rate, average days to first response, top sources)
- Filtered CSV export streamed with proper escaping
- Swagger UI backed by springdoc-openapi

## Tech stack

- Java 21
- Spring Boot 3.4 (Web, Data JPA, Validation, Scheduling)
- PostgreSQL with Flyway migrations (H2 in PostgreSQL mode for local profile)
- springdoc-openapi
- JUnit 5, Mockito, MockMvc, Testcontainers

## How to run

### Maven (local profile)

The default `local` profile uses in-memory H2 in PostgreSQL compatibility mode, so no Docker is required.

```bash
mvn spring-boot:run
```

Run the test suite:

```bash
mvn test
```

Package the application:

```bash
mvn -DskipTests package
```

API base URL: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

### Docker Compose

Build the jar first, then start PostgreSQL and the API:

```bash
mvn -DskipTests package
docker compose up --build
```

### Environment variables (docker profile)

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `docker` | Activates PostgreSQL datasource settings |
| `POSTGRES_HOST` | `postgres` | Database host |
| `POSTGRES_PORT` | `5432` | Database port |
| `POSTGRES_DB` | `hiretrack` | Database name |
| `POSTGRES_USER` | `hiretrack` | Database user |
| `POSTGRES_PASSWORD` | `hiretrack` | Database password |

## API endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/applications` | List applications with filters and pagination |
| POST | `/api/applications` | Create an application |
| GET | `/api/applications/{id}` | Get one application |
| PUT | `/api/applications/{id}` | Replace application fields |
| DELETE | `/api/applications/{id}` | Delete an application |
| PATCH | `/api/applications/{id}/status` | Move application through the pipeline |
| GET | `/api/applications/{id}/timeline` | Ordered status history |
| GET | `/api/applications/export` | CSV export with the same filters as list |
| POST | `/api/applications/{id}/follow-ups` | Add a follow-up reminder |
| PATCH | `/api/applications/{id}/follow-ups/{followUpId}/done` | Mark follow-up complete |
| GET | `/api/follow-ups/due` | Follow-ups due today or overdue |
| GET | `/api/stats` | Aggregate hiring metrics |

## Status pipeline

Statuses: `APPLIED`, `SCREENING`, `INTERVIEW`, `OFFER`, `REJECTED`, `GHOSTED`.

| From | Allowed next statuses |
|------|------------------------|
| APPLIED | SCREENING, INTERVIEW, REJECTED, GHOSTED |
| SCREENING | INTERVIEW, REJECTED, GHOSTED |
| INTERVIEW | OFFER, REJECTED, GHOSTED |
| OFFER | REJECTED |
| REJECTED | (terminal) |
| GHOSTED | (terminal) |

Invalid transitions return **409 Conflict** with a clear message.

## Example requests

Create an application:

```bash
curl -s -X POST http://localhost:8080/api/applications \
  -H 'Content-Type: application/json' \
  -d '{
    "company": "Northwind Systems",
    "role": "Java Developer",
    "jobUrl": "https://northwind.example/jobs/java-developer",
    "source": "LINKEDIN",
    "salaryMin": 85000,
    "salaryMax": 110000,
    "location": "Colombo",
    "remote": true,
    "notes": "Referred by a former teammate.",
    "appliedDate": "2026-02-12"
  }'
```

Move to interview (after creating and moving to SCREENING if needed):

```bash
curl -s -X PATCH http://localhost:8080/api/applications/1/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"INTERVIEW","note":"Technical panel scheduled"}'
```

Invalid transition (409):

```bash
curl -s -o /dev/null -w "%{http_code}\n" -X PATCH http://localhost:8080/api/applications/1/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"OFFER"}'
```

Filter with paging:

```bash
curl -s 'http://localhost:8080/api/applications?status=APPLIED&company=north&page=0&size=10&sort=appliedDate,desc'
```

Add a follow-up:

```bash
curl -s -X POST http://localhost:8080/api/applications/1/follow-ups \
  -H 'Content-Type: application/json' \
  -d '{"dueDate":"2026-02-20","note":"Send a polite check-in email"}'
```

Due follow-ups:

```bash
curl -s http://localhost:8080/api/follow-ups/due
```

Stats:

```bash
curl -s http://localhost:8080/api/stats
```

Export CSV:

```bash
curl -s -OJ 'http://localhost:8080/api/applications/export?source=LINKEDIN'
```

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `hiretrack.ghosting-threshold-days` | `21` | Days without activity before APPLIED/SCREENING apps are marked GHOSTED |
| `hiretrack.scheduler.daily-cron` | `0 0 6 * * *` | Cron for the daily maintenance job |
| `hiretrack.pagination.max-page-size` | `100` | Upper bound for page size |
| `spring.profiles.active` | `local` | `local` uses H2; `docker` uses PostgreSQL |

## Project structure

```
src/main/java/com/hiretrack/
  config/          Clock, JPA auditing, OpenAPI, pagination, properties
  controller/      REST controllers
  domain/          JPA entities and enums
  dto/             Java record DTOs
  exception/       API errors and global handler
  export/          CSV writer
  mapper/          Entity/DTO mappers
  policy/          Status transition rules
  repository/      Spring Data repositories and specifications
  scheduler/       Daily ghosting and follow-up maintenance
  service/         Business logic
src/main/resources/db/migration/   Flyway SQL migrations
src/test/          Unit, MockMvc, and Testcontainers tests
```

## What I learned

- **JPA Specifications** keep filter logic composable and database-friendly instead of hand-built queries in every endpoint.
- **Pagination and sorting** through Spring Data `Pageable`, with a capped page size, keeps list endpoints predictable at scale.
- **State-machine validation** in a single policy class makes pipeline rules easy to test and explain in API errors.
- **Flyway versioning** gives repeatable schema changes across H2 local runs and PostgreSQL deployments.
- **JPA auditing** automatically tracks `createdAt` and `updatedAt` without boilerplate in services.
- **Scheduled jobs** with injectable `Clock` and cron from properties make ghosting and follow-up sweeps testable.
- **CSV streaming** avoids loading large result sets into memory while still escaping special characters correctly.
- **Aggregate SQL** in the stats repository computes hiring metrics in the database instead of in application code.

## License

[MIT License](LICENSE). Developed by Dumidu Lakshan.