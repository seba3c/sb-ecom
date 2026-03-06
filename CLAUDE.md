# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build the project
./gradlew build

# Run the application (starts on port 8080)
./gradlew bootRun

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.ecommerce.project.SbEcomApplicationTests"
```

## Git Branch Naming

Branches must follow this pattern: `<type>/<numeric_id>_<branch-name>`

- **`<type>`** — one of: `feature`, `fix`
- **`<numeric_id>`** — issue or ticket number (e.g. `42`)
- **`<branch-name>`** — short kebab-case description

Examples:
```
feature/42_add-product-entity
fix/7_category-update-stub
```

## Architecture

Spring Boot 4.0 REST API (Java 21) with a layered architecture:

- **`model/`** — Plain Java POJOs (no JPA yet; `Category` has `id` and `name`)
- **`service/`** — Interface + `Impl` pattern. `CategoryServiceImpl` currently uses an in-memory `ArrayList` (no database)
- **`controller/`** — `@RestController` classes under `/api`. Public endpoints at `/api/public/`, admin endpoints at `/api/admin/`

### Current state
- No database or JPA — data is held in memory and lost on restart
- `CategoryServiceImpl.update()` is not yet implemented (throws 404)
- No Spring Security configured despite the public/admin URL split

### API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/public/categories` | List all categories |
| POST | `/api/public/categories` | Create a category |
| DELETE | `/api/admin/categories/{id}` | Delete a category |
| PUT | `/api/admin/categories/{id}` | Update a category (stub) |
