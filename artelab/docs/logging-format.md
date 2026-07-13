# Logging Format

This service emits application logs through Logback using Spring Boot structured logging.

Configuration:

- File: `src/main/resources/logback-spring.xml`
- Appender: Logback console appender
- Encoder: `org.springframework.boot.logging.logback.StructuredLogEncoder`
- Structured format: `logstash`
- Charset: Spring Boot `CONSOLE_LOG_CHARSET`, defaulting to UTF-8

Each application log event is written as one newline-delimited JSON object. Observed fields include:

- `@timestamp`: event timestamp with offset
- `@version`: Logstash event version
- `message`: rendered log message
- `logger_name`: logger name
- `thread_name`: emitting thread
- `level`: textual level such as `INFO`, `WARN`, or `ERROR`
- `level_value`: numeric Logback level
- `tags`: optional tags supplied by the logging bridge or framework

SLF4J log statements do not need any code changes. Existing `LoggerFactory` and SLF4J calls are preserved and are encoded by Logback at runtime.

Hibernate SQL logging is routed through Logback by setting `spring.jpa.show-sql=false` and `logging.level.org.hibernate.SQL=DEBUG`. This avoids direct `Hibernate:` stdout lines and keeps SQL statements in the same JSON format.

The Spring Boot banner is disabled with `spring.main.banner-mode=off` so service startup output is not mixed with non-log banner text.

Build tool output, JVM warnings, and early test bootstrap messages produced before the application logging system starts are outside the service Logback configuration.
