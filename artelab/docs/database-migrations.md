# Database migrations

This project uses Flyway to manage the SQLite schema.

## Location

Migrations live in:

```text
src/main/resources/db/migration
```

## Current migrations

- `V1__initial_schema.sql`: creates the `categoria`, `producto`, and `promocion` tables required by the current JPA entities.

## Runtime behavior

- Flyway runs automatically during Spring Boot startup.
- Hibernate is configured with `spring.jpa.hibernate.ddl-auto=validate`.
- Hibernate must only validate the schema. It must not create, update, or drop tables automatically.

## Entity compatibility

The initial migration matches the current entities:

- `Categoria`: `id`, `des`
- `Producto`: `id`, `des`, `precio`, `stock`, `id_categoria`
- `Promocion`: `id`, `des`, `fecha_ini`, `fecha_ter`, `descuento`, `id_categoria`

Foreign keys from `producto` and `promocion` reference `categoria(id)`.

## Adding future changes

Create a new versioned SQL file for each schema change:

```text
V2__describe_change.sql
V3__describe_next_change.sql
```

Do not edit migrations that have already been applied to a database. Add a new migration instead.
