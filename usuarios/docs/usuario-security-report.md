# Usuario security report

Date: 2026-06-03

## Scope

Reviewed the `Usuario` entity, REST response types, service layer, and password storage behavior.

## Password exposure

REST endpoints for usuarios return DTOs only:

- `GET /api/v1/usuarios` returns `List<UsuarioResponseDto>`.
- `GET /api/v1/usuarios/{id}` returns `UsuarioResponseDto`.
- `GET /api/v1/usuarios/{id}/lookup` returns `UsuarioLookupDto`.
- `POST /api/v1/usuarios` returns `UsuarioResponseDto`.

The response DTOs contain only:

- `id`
- `nombreUsuario`
- `correo`

They do not contain `clave` or any password field.

## Request handling

User creation now accepts `UsuarioRequestDto` instead of the JPA entity. The request DTO contains the raw `clave` only as inbound API data.

The service layer maps the request DTO to a `Usuario` entity internally.

## Password hashing

Passwords are hashed through the configured `PasswordEncoder` bean:

```java
new BCryptPasswordEncoder()
```

`UsuarioService.saveUsuario` and `UsuarioService.updateUsuario` call `passwordEncoder.encode(...)` before persistence.

The `usuario.clave` database column is `VARCHAR(60)` to fit standard BCrypt hashes.

## Entity serialization guard

`Usuario.clave` is annotated as write-only for JSON:

```java
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
```

This is a defensive guard. Controllers should still return DTOs, not entities.

## Validation

Added `UsuarioSecurityTests` to verify:

- Saved passwords are not stored in plain text.
- Saved passwords are BCrypt hashes.
- Raw passwords match through `PasswordEncoder`.
- `UsuarioResponseDto` and `UsuarioLookupDto` do not expose `clave` or `password` fields.
