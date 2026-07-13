# Logging Compliance Report

Date: 2026-06-03

Scope:

- `artelab-main`
- `usuarios-main`

## Initial Findings

| Project | Logback config present | JSON logging configured | SLF4J statements |
| --- | --- | --- | --- |
| `artelab-main` | No `src/main/resources/logback-spring.xml` | No JSON encoder or `logging.structured.*` setting found | Present and preserved |
| `usuarios-main` | No `src/main/resources/logback-spring.xml` | No JSON encoder or `logging.structured.*` setting found | Present and preserved |

Both projects already use Spring Boot `4.0.6`, which provides Logback through `spring-boot-starter-logging` and includes `org.springframework.boot.logging.logback.StructuredLogEncoder`. No additional logging dependency was required.

## Remediation

| Requirement | Status | Evidence |
| --- | --- | --- |
| Add `logback-spring.xml` when JSON logging is missing | Compliant | Added in both `src/main/resources` directories |
| Configure JSON structured logging | Compliant | Console appender uses `StructuredLogEncoder` with `logstash` format |
| Preserve existing SLF4J log statements | Compliant | No Java business or logging call sites were edited |
| Document logging format | Compliant | Added `docs/logging-format.md` in both projects |
| Avoid business logic changes | Compliant | Changes are limited to logging configuration and documentation |

Additional logging cleanup:

- Set `spring.jpa.show-sql=false` to prevent Hibernate from writing raw `Hibernate:` lines directly to stdout.
- Set `logging.level.org.hibernate.SQL=DEBUG` so SQL visibility is preserved through Logback JSON output.
- Set `spring.main.banner-mode=off` so startup output is not mixed with non-log banner text.

## Logging Format

Application log events are emitted as newline-delimited JSON using Spring Boot's Logback `StructuredLogEncoder` in `logstash` format. Observed fields include `@timestamp`, `@version`, `message`, `logger_name`, `thread_name`, `level`, `level_value`, and optional `tags`.

The configured appender is console-only. Build tool output, JVM warnings, and early test bootstrap messages are outside the application Logback configuration.

## Verification

| Project | Command | Result |
| --- | --- | --- |
| `artelab-main` | `mvn test -Dspring.datasource.url=jdbc:sqlite:target/compatibility-artelab-main.db` | Pass: 1 test, 0 failures, 0 errors |
| `usuarios-main` | `mvn test -Dspring.datasource.url=jdbc:sqlite:target/logging-usuarios-main.db` | Pass: 2 tests, 0 failures, 0 errors |

The `usuarios-main` run was executed with a fresh SQLite test database because a previous generated database at `target/compatibility-usuarios-main.db` failed Flyway validation due to an existing migration checksum mismatch. That failure was unrelated to logging configuration.

Sample JSON log shape observed during verification:

```json
{"@timestamp":"2026-06-03T18:28:25.6605783-04:00","@version":"1","message":"Starting UsuariosApplicationTests using Java 26","logger_name":"dsy.artelab.usuarios.UsuariosApplicationTests","thread_name":"main","level":"INFO","level_value":20000}
```

## Final Status

Both projects are compliant for application logs emitted through Logback. JSON structured logging is configured, existing SLF4J statements are preserved, and no business logic was modified.
