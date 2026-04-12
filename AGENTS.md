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

`<ai_tool_name>/<type>/<branch-name>` (e.g., `gemini/feature/add-product-entity`)

Where <ai_tool_name> is the name of the AI tool executing, i.e., claude, gemini, codex, opencode, etc.

## Branching Rules

- **Never commit directly to `main`.** All new features, bug fixes, and changes must be implemented on a dedicated
  branch.
- Create a branch before making any code changes.
- Open a pull request to merge changes back into `main`.

## Commit Message Format

Commit messages must include the AI tool name and the model used in the footer:

```
<type>: <short description>

<optional body>

AI-Tool: <tool-name> (<model-id>)
```

If the changes were authored by one model but executed (e.g., applied, run, or reviewed) by a different model, list
both:

```
AI-Tool: <tool-name> (<authoring-model-id>) / <tool-name> (<executing-model-id>)
```

Examples:

```
feat: add product search endpoint

AI-Tool: claude (claude-sonnet-4-6)
```

```
fix: correct category validation logic

AI-Tool: gemini (gemini-2.5-pro) / claude (claude-sonnet-4-6)
```

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