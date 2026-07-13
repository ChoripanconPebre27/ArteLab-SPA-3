# Auditoria Examen Transversal - ArteLab SPA

Fecha de cierre: 2026-07-06

Documentos usados como criterio:
- LISTAS DE CHEQUEO PARA EXAMEN TRANSVERSAL.pdf
- Rubrica ET (1).pdf

Microservicios revisados:
- artelab
- usuarios

## Problemas encontrados y soluciones aplicadas

| Requisito | Archivos afectados | Problema encontrado | Solucion aplicada |
| --- | --- | --- | --- |
| Arquitectura CSR | `artelab/src/main/java/cl/artelab_spa/artelab/controller/*Controller.java`, `artelab/src/main/java/cl/artelab_spa/artelab/service/*Service.java` | Los controllers CRUD decidian existencia de recursos con `existsById` antes de llamar al service. Esto duplicaba flujo, mezclaba reglas de negocio con HTTP y generaba dobles consultas. | Se movio la responsabilidad de existencia al service. Los controllers ahora llaman `getById`, `update` o `delete` y el `GlobalExceptionHandler` traduce `EntityNotFoundException` a 404. |
| Arquitectura y calidad Spring | `artelab/.../controller/CategoriaController.java`, `ProductoController.java`, `PromocionController.java`, `usuarios/.../controller/UsuarioController.java`, `artelab/.../config/DataLoader.java` | Habia inyeccion por campo con `@Autowired`, menos testeable y menos explicita. | Se migro a constructor injection con dependencias `final`, manteniendo la API publica intacta. |
| Validaciones Bean Validation | `artelab/.../model/Categoria.java`, `Producto.java`, `Promocion.java`, `artelab/.../dto/LoginRequest.java`, `usuarios/.../dto/LoginRequest.java`, `UsuarioRequestDto.java` | Faltaban restricciones de longitud, formato y obligatoriedad en entradas clave. En promociones faltaban fechas obligatorias y categoria requerida. | Se agregaron `@NotBlank`, `@NotNull`, `@Size`, `@Email`, `@Pattern`, `@Positive`, `@PositiveOrZero`, `@Min` y `@Max` segun cada campo. |
| Reglas de negocio en Service | `artelab/.../service/ProductoService.java`, `PromocionService.java`, `CategoriaService.java`, `usuarios/.../service/UsuarioService.java` | Algunas reglas dependian de persistencia o quedaban incompletas: categoria no validada contra BD, fechas de promocion inconsistentes, delete sin 404 controlado, duplicados tratados como 400. | Se centralizo validacion en service: resolucion de categoria por repository, validacion de precio/stock/descuento/fechas, delete con busqueda previa y duplicados con `ResourceConflictException` 409. |
| Manejo global de errores | `artelab/.../exception/GlobalExceptionHandler.java`, `usuarios/.../exception/GlobalExceptionHandler.java`, `ResourceConflictException.java` en ambos modulos | Los 500 podian devolver mensajes internos y los conflictos no tenian respuesta 409 uniforme. | Se agrego `ResourceConflictException`, handler 409, 401 para autenticacion fallida, 503 para fallos remotos en artelab y mensajes genericos para 500. |
| Comunicacion entre microservicios | `artelab/.../client/UsuarioClient.java`, `artelab/.../config/WebClientConfig.java`, `artelab/src/main/resources/META-INF/additional-spring-configuration-metadata.json` | El WebClient no tenia timeout configurable y no habia pruebas directas del cliente remoto. | Se agrego timeout de conexion/respuesta con `usuarios.service.timeout-seconds` y tests para 200, 404, 5xx y fallo de conexion. |
| Swagger / OpenAPI | `artelab/.../config/SecurityConfig.java`, `artelab/src/main/resources/application.properties`, `usuarios/src/main/resources/application.properties` | `artelab` no liberaba todas las rutas usuales de Swagger UI. | Se permitieron `/swagger-ui/**`, `/swagger-ui.html`, `/doc/**`, `/doc.html`, `/v3/api-docs/**`, `/swagger-resources/**` y `/webjars/**`. |
| Logging | Services, WebClient y exception handlers | Se debia evitar salida directa y registrar operaciones CRUD, errores y llamadas remotas. | Se mantuvo SLF4J y se verifico ausencia de `System.out` y `printStackTrace` en `src/main`. |
| Pruebas y cobertura | `artelab/pom.xml`, `usuarios/pom.xml`, tests de controller/service/client | No existia gate formal de cobertura y faltaban pruebas de reglas de service y cliente remoto. | Se agrego JaCoCo 0.8.15 con check minimo de 80% sobre controller/service/client. Se ampliaron tests de services, controllers y `UsuarioClient`. |
| Persistencia y modelo | `artelab/.../model/*`, `usuarios/.../model/Usuario.java`, repositories | Las entidades no declaraban todas las restricciones que ya eran esperables por el dominio o la BD. | Se alinearon `nullable`, `unique`, longitudes y validaciones JPA/Bean Validation. Las relaciones `ManyToOne` ahora se validan antes de persistir. |

## Verificaciones ejecutadas

Comandos ejecutados:

```powershell
cmd /c mvnw.cmd verify "-Dspring.datasource.url=jdbc:sqlite:target/codex-artelab-final-verify-2.db"
cmd /c mvnw.cmd verify "-Dspring.datasource.url=jdbc:sqlite:target/codex-usuarios-final-verify.db"
rg -n "System\.out|printStackTrace|existsById\(|@Autowired" artelab\src\main\java usuarios\src\main\java
```

Resultados:
- `artelab`: BUILD SUCCESS, 63 tests, 0 failures, JaCoCo check aprobado.
- `usuarios`: BUILD SUCCESS, 28 tests, 0 failures, JaCoCo check aprobado.
- Busqueda de malas practicas: sin resultados en `src/main`.

## Estado por requisito

| Requisito | Estado |
| --- | --- |
| Controller -> Service -> Repository | Cumplido |
| REST y codigos HTTP | Cumplido |
| Validaciones de entrada | Cumplido |
| `@ControllerAdvice` / errores uniformes | Cumplido |
| Logging sin `System.out` | Cumplido |
| Persistencia y CRUD | Cumplido |
| Comunicacion entre microservicios | Cumplido |
| Swagger accesible | Cumplido |
| Pruebas unitarias y cobertura >= 80% | Cumplido sobre controller/service/client |
| Calidad de codigo Spring | Cumplido |

## Observaciones

- El gate de JaCoCo excluye clases de infraestructura, DTOs, modelos, assemblers, excepciones, seguridad y arranque. La rubrica pide priorizar Service y Controller; en `artelab` tambien se incluyo el cliente remoto.
- El archivo trackeado `artelab.db` aparece modificado en el working tree. No fue revertido para no descartar datos locales sin autorizacion.
- No quedan requisitos funcionales o tecnicos pendientes de implementacion segun la auditoria realizada.
