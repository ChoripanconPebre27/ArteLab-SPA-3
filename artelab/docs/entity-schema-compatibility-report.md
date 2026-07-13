# Entity and schema compatibility report

Date: 2026-06-03

## Scope

Reviewed entity identifiers and Flyway schema definitions for:

- `artelab-main`
- `usuarios-main`

## Identifier compatibility

| Project | Entity | Java ID field | Entity column metadata | Flyway column | Status |
| --- | --- | --- | --- | --- | --- |
| `artelab-main` | `Categoria` | `Long id` | `@Column(columnDefinition = "INTEGER")` | `id INTEGER PRIMARY KEY AUTOINCREMENT` | Compatible |
| `artelab-main` | `Producto` | `Long id` | `@Column(columnDefinition = "INTEGER")` | `id INTEGER PRIMARY KEY AUTOINCREMENT` | Compatible |
| `artelab-main` | `Promocion` | `Long id` | `@Column(columnDefinition = "INTEGER")` | `id INTEGER PRIMARY KEY AUTOINCREMENT` | Compatible |
| `usuarios-main` | `Usuario` | `Long id` | `@Column(columnDefinition = "INTEGER")` | `id INTEGER PRIMARY KEY AUTOINCREMENT` | Compatible |

SQLite identity columns must be declared as `INTEGER PRIMARY KEY` for autoincrement behavior. The entity metadata uses `columnDefinition = "INTEGER"` so Hibernate validation accepts the SQLite column type for `Long` identifiers.

## Mismatches found

- `usuarios-main`: `Usuario.id` was a `Long` without an explicit SQLite column definition. Hibernate expected `BIGINT`, while Flyway created `INTEGER`. Fixed by adding `@Column(columnDefinition = "INTEGER")`.
- `artelab-main`: no entity/schema ID type mismatch was found. A hidden UTF-8 BOM in `ProductoRepository.java` blocked compilation before validation; the file was rewritten without behavior changes.

## Foreign key compatibility

| Project | Entity field | Flyway column | Reference | Status |
| --- | --- | --- | --- | --- |
| `artelab-main` | `Producto.categoria` | `id_categoria INTEGER NOT NULL` | `categoria(id)` | Compatible |
| `artelab-main` | `Promocion.categoria` | `id_categoria INTEGER NOT NULL` | `categoria(id)` | Compatible |

Hibernate's physical naming maps `idCategoria` to `id_categoria`, and validation passed with the current Flyway schema.

## Validation results

Both projects were validated with fresh SQLite databases under `target/`:

```text
artelab-main:
mvn test -Dspring.datasource.url=jdbc:sqlite:target/compatibility-artelab-main-2.db
Result: BUILD SUCCESS

usuarios-main:
mvn test -Dspring.datasource.url=jdbc:sqlite:target/compatibility-usuarios-main.db
Result: BUILD SUCCESS
```

Flyway applied `V1__initial_schema.sql` successfully in both projects, and Hibernate validation completed without schema errors.

## Compatibility rule

For future SQLite identity columns in these projects:

1. Entity ID field: `Long`.
2. Entity ID annotations: `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`, `@Column(columnDefinition = "INTEGER")`.
3. Flyway ID column: `INTEGER PRIMARY KEY AUTOINCREMENT`.
4. Keep `spring.jpa.hibernate.ddl-auto=validate` so Hibernate validates Flyway-managed schema instead of generating it.
