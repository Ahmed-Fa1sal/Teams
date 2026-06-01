# Teams Collaboration Platform — Constitution

A Microsoft Teams-like enterprise collaboration platform. **The Organization is the top-level boundary of all data and access.**

---

## Architecture
- Four layers: Controller → Service → Repository → Database. No layer may skip another.
- Controllers validate input and delegate. No business logic in controllers or repositories.
- Services own all business logic and DTO mapping. Each service has an interface and one `Impl`.
- Entities are never exposed via HTTP. DTOs are used for all API input and output.

## Security
- All endpoints require a valid JWT except auth and public documentation routes.
- Every non-public controller method must have `@PreAuthorize`. Missing authorization is a blocking defect.
- Passwords are hashed with BCrypt. JWT secrets and database credentials must never be committed.

## Organization Boundaries
- Hierarchy: `Organization → Team → Channel → Message`.
- A Team must belong to an Organization. A Channel must belong to a Team.
- A user may only act on a resource if they are an `OrganizationMember` of the owning organization.
- Cross-organization data access is forbidden.
- Every new entity scoped to an organization must carry an `organization_id` foreign key.

## Database & Flyway
- Hibernate must never generate or alter the schema (`ddl-auto=none`). Flyway owns all DDL.
- Never modify an applied migration. All schema changes require a new versioned script.
- Migration naming: `V{next_integer}__{Description}.sql`, sequential with no gaps.
- Migrations must be idempotent.
- Physical deletes are forbidden for user-facing entities. Use soft delete (`deleted`, `deleted_at`).
- All queries on soft-deletable entities must filter `WHERE deleted = false`.

## API Standards
- All endpoints are served under `/api/v1`. Breaking changes require a new version prefix.
- All responses use the `ApiResponse<T>` envelope. List endpoints return `PagedResponse<T>`.
- HTTP 500 responses must never expose stack traces.
- Every controller and endpoint must have OpenAPI annotations (`@Tag`, `@Operation`).

## Auditing
- All entities must extend `BaseEntity` to get `createdAt`, `updatedAt`, `createdBy`, `updatedBy` automatically.
- Administrative actions must write an explicit `AuditLog` record from the service layer.
- Audit logs are read-only via the API and restricted to admin roles.

## Backward Compatibility
- Never remove or rename a stable API field without a versioned migration plan.
- Database column renames require a Flyway migration before the Java rename is committed.
- Enum values stored as strings are part of the schema contract. Renaming requires a data migration.
- Do not introduce shared base services or generic CRUD abstractions across domain boundaries.

---

**Version**: 1.2.0 | **Ratified**: 2026-06-01 | **Last Amended**: 2026-06-01
