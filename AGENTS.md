# AGENTS.md

## Build Commands

```bash
./gradlew build
./gradlew bootRun      # starts on port 8080
./gradlew test
./gradlew test --tests "com.ecommerce.project.SbEcomApplicationTests"
```

## Architecture

Spring Boot 4.0 REST API (Java 21) with **layer-based packaging**:

```
src/main/java/com/ecommerce/project/
├── model/       # JPA entities (Product, Category)
├── dto/          # Request/Response DTOs
├── repository/   # Spring Data JPA repositories
├── service/      # Interface + Impl pattern
├── controller/   # @RestController classes
├── config/       # Configuration classes
└── exception/   # Exception handling
```

## Key Facts

- Uses **Spring Data JPA** with **H2 in-memory database** (not just ArrayList anymore)
- H2 console available at `/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`)
- No Spring Security configured (public/admin paths have no auth)
- Public endpoints: `/api/public/*`, Admin endpoints: `/api/admin/*`

## Git Branch Naming

`<type>/<numeric_id>_<branch-name>` (e.g., `feature/42_add-product-entity`)

## Testing

- JUnit 5 with Spring Boot Test
- Integration tests use `@SpringBootTest`
- MockMvc for controller tests

## No Lint/Typecheck

No Checkstyle, Spotless, or other lint tools configured. Build success = code is valid.

## Custom Agents

OpenCode uses `.opencode/agents/` for custom agents. To invoke:

```
@spring-test-planner analyze CategoryServiceImpl
```

This will create a test plan and hand off to `@spring-test-writer` to implement.