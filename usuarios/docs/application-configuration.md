# Application configuration

## Service

- Application name: `usuarios`
- HTTP port: `8081`
- The port is intentionally different from `artelab-main`, which runs on `8080`.

## Datasource

- Driver: `org.sqlite.JDBC`
- URL: `jdbc:sqlite:usuarios.db`
- Database file: `usuarios.db`

## JPA and Hibernate

- Dialect: `org.hibernate.community.dialect.SQLiteDialect`
- Schema mode: `spring.jpa.hibernate.ddl-auto=validate`
- Hibernate validates the schema created by Flyway and does not generate tables automatically.

## Flyway

- Enabled: `true`
- Migration location: `classpath:db/migration`
- Initial migration: `V1__initial_schema.sql`

## Startup validation

Expected startup order:

1. Spring Boot configures the dedicated SQLite datasource.
2. Flyway applies pending migrations from `src/main/resources/db/migration`.
3. Hibernate validates the resulting schema.
4. The service starts on port `8081`.
