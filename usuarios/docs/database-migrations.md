# Database migrations

This project uses Flyway to manage the SQLite schema.

## Location

Migrations live in:

```text
src/main/resources/db/migration
```

## Current migrations

- `V1__initial_schema.sql`: creates the `usuario` table and unique indexes for `nombre_usuario` and `correo`.

## Runtime behavior

- Flyway runs automatically during Spring Boot startup.
- Hibernate is configured with `spring.jpa.hibernate.ddl-auto=validate`.
- Hibernate must only validate the schema. It must not create, update, or drop tables automatically.

## Entity compatibility

The initial migration matches the current `Usuario` entity:

- `id`
- `nombre_usuario`
- `clave`
- `correo`

The service layer enforces uniqueness for `nombreUsuario` and `correo`; the database also enforces it with unique indexes.

## Adding future changes

Create a new versioned SQL file for each schema change:

```text
V2__describe_change.sql
V3__describe_next_change.sql
```

Do not edit migrations that have already been applied to a database. Add a new migration instead.
