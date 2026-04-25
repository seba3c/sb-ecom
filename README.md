# sb-ecom

A Spring Boot REST API built as a learning project — both for Spring Boot itself and for AI-assisted development with tools like Claude Code, Gemini CLI, Codex CLI, and OpenCode.

## Goals

- Follow the [EmbarkX Spring Boot course](https://github.com/EmbarkXOfficial/spring-boot-course) as a loose guide, adapting the code with different conventions and tooling choices (Gradle Kotlin DSL instead of Maven, etc.)
- Use AI coding assistants to generate as much of the codebase as possible
- Explore AI concepts: skills, agents, and MCP servers in a real project context

## Stack

- Java 21 / Spring Boot 4.0
- Spring Data JPA (MySQL by default; H2 and PostgreSQL profiles available)
- Spring Security with stateless JWT cookie-based authentication
- Gradle with Kotlin DSL

## Quick Start

```bash
# Start MySQL via Docker Compose, then:
./gradlew bootRun          # runs on http://localhost:8080

# Run with H2 in-memory database instead
./gradlew bootRun -Dspring.profiles.active=h2

# Run tests
./gradlew test
```

## Seed Users

| Username | Password     | Roles                              |
|----------|--------------|------------------------------------|
| user     | userpass     | ROLE_USER                          |
| seller   | sellerpass   | ROLE_SELLER                        |
| admin    | adminpass    | ROLE_USER, ROLE_SELLER, ROLE_ADMIN |

## Documentation

See [AGENTS.md](AGENTS.md) for architecture details, API endpoints, and contribution guidelines.
