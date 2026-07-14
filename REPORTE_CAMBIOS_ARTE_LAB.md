# Reporte de cambios - ArteLab-SPA-NEW-BACKUP

Fecha del reporte: 2026-07-12

Este documento resume los cambios importantes realizados y documentados durante el proceso de revision de `ArteLab-SPA-NEW-BACKUP`. El reporte se basa en el estado actual del working tree, el informe `AUDITORIA_EXAMEN_TRANSVERSAL.md` y las verificaciones ejecutadas durante la conversacion.

## Alcance

Modulos revisados:

- `artelab`
- `usuarios`

Documentos usados como criterio de evaluacion:

- `LISTAS DE CHEQUEO PARA EXAMEN TRANSVERSAL.pdf`
- `Rubrica ET (1).pdf`

Objetivo general del trabajo:

- Mejorar cumplimiento de la arquitectura Controller -> Service -> Repository.
- Reforzar validaciones y reglas de negocio.
- Ordenar respuestas HTTP y manejo global de errores.
- Mejorar seguridad de rutas publicas de Swagger/OpenAPI.
- Agregar pruebas unitarias relevantes.
- Agregar control de cobertura con JaCoCo.
- Dejar evidencia clara para revision academica o tecnica.

## Resumen ejecutivo

Se realizaron cambios importantes en ambos microservicios para que la aplicacion quede mas alineada con una arquitectura por capas, con controladores mas simples, reglas de negocio ubicadas en servicios, validaciones declarativas, errores uniformes y pruebas automatizadas.

Tambien se agrego un gate de cobertura con JaCoCo en ambos modulos. En `artelab` el foco de cobertura quedo sobre controllers, services y cliente remoto. En `usuarios` el foco quedo sobre controllers y services.

La verificacion final registrada fue exitosa:

- `artelab`: 63 tests ejecutados, 0 fallos, build exitoso y JaCoCo aprobado.
- `usuarios`: 28 tests ejecutados, 0 fallos, build exitoso y JaCoCo aprobado.
- Busqueda de malas practicas (`System.out`, `printStackTrace`, `existsById(`, `@Autowired`) sin resultados en `src/main`.

## Cambios importantes en `artelab`

### 1. `pom.xml`

Se agrego configuracion de JaCoCo:

- Plugin `jacoco-maven-plugin` version `0.8.15`.
- Ejecucion `prepare-agent`.
- Reporte en fase `verify`.
- Check de cobertura minima de linea de `80%`.
- Exclusiones para clases de infraestructura o bajo valor de prueba directa, como arranque, config, security, DTOs, modelos, assemblers y excepciones.

Impacto:

- El modulo ahora falla el build si no cumple el minimo de cobertura definido para el codigo objetivo.
- La evaluacion queda mas alineada con el requisito de pruebas y cobertura.

### 2. Configuracion Spring

Archivos principales:

- `artelab/src/main/java/cl/artelab_spa/artelab/config/DataLoader.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/config/SecurityConfig.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/config/WebClientConfig.java`
- `artelab/src/main/resources/META-INF/additional-spring-configuration-metadata.json`

Cambios aplicados:

- `DataLoader` fue migrado a constructor injection con dependencias `final`.
- `SecurityConfig` fue ajustado para permitir rutas Swagger/OpenAPI:
  - `/doc/**`
  - `/doc.html`
  - `/swagger-ui/**`
  - `/swagger-ui.html`
  - `/v3/api-docs/**`
  - `/swagger-resources/**`
  - `/webjars/**`
- `WebClientConfig` incorporo timeout configurable para conexion y respuesta.
- Se agrego metadata para la propiedad `usuarios.service.timeout-seconds`.

Impacto:

- Menor acoplamiento y mejor testeabilidad.
- Documentacion Swagger mas accesible.
- Cliente HTTP remoto con comportamiento mas controlado ante servicios lentos o caidos.

### 3. Controladores

Archivos principales:

- `artelab/src/main/java/cl/artelab_spa/artelab/controller/CategoriaController.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/controller/ProductoController.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/controller/PromocionController.java`

Cambios aplicados:

- Se removio logica de existencia con `existsById` desde los controllers.
- Los controllers quedaron como capa HTTP, delegando reglas al service.
- Se migro a constructor injection.
- Los codigos HTTP quedan resueltos mediante respuestas de controller y `GlobalExceptionHandler`.

Impacto:

- Mejor separacion de responsabilidades.
- Menos consultas duplicadas.
- Flujo mas coherente con Controller -> Service -> Repository.

### 4. DTOs y modelos

Archivos principales:

- `artelab/src/main/java/cl/artelab_spa/artelab/dto/LoginRequest.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/model/Categoria.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/model/Producto.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/model/Promocion.java`

Cambios aplicados:

- Se agregaron validaciones Bean Validation:
  - `@NotBlank`
  - `@NotNull`
  - `@Size`
  - `@Positive`
  - `@PositiveOrZero`
  - `@Min`
  - `@Max`
- Se reforzaron restricciones de campos obligatorios, largos, precios, stock, descuento, fechas y categoria.

Impacto:

- Menos entradas invalidas llegan al service o a persistencia.
- Las reglas basicas del dominio quedan expresadas directamente en el modelo.

### 5. Manejo de errores

Archivos principales:

- `artelab/src/main/java/cl/artelab_spa/artelab/exception/GlobalExceptionHandler.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/exception/ResourceConflictException.java`

Cambios aplicados:

- Se agrego `ResourceConflictException`.
- Se agrego respuesta 409 para conflictos de negocio, por ejemplo duplicados.
- Se reforzaron respuestas para errores de validacion, autenticacion, recursos inexistentes, errores remotos y errores internos.
- Los errores 500 quedan con mensaje generico, evitando exponer detalles internos.

Impacto:

- Respuestas HTTP mas uniformes.
- Mejor seguridad y claridad para consumidores de la API.

### 6. Services

Archivos principales:

- `artelab/src/main/java/cl/artelab_spa/artelab/service/CategoriaService.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/service/ProductoService.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/service/PromocionService.java`

Cambios aplicados:

- `CategoriaService`:
  - Duplicados ahora se manejan como conflicto 409.
  - `update` y `delete` verifican existencia dentro del service.
- `ProductoService`:
  - Valida precio y stock.
  - Resuelve `Categoria` desde repository antes de persistir.
  - `delete` verifica existencia dentro del service.
- `PromocionService`:
  - Valida descuento entre 1 y 100.
  - Valida fechas de inicio y termino.
  - Resuelve categoria desde repository.
  - Valida usuario remoto mediante `UsuarioClient`.
  - `delete` verifica existencia dentro del service.

Impacto:

- Las reglas de negocio quedan centralizadas.
- Los controllers ya no toman decisiones que corresponden al dominio.
- Se reducen inconsistencias entre endpoints.

### 7. Cliente remoto

Archivos principales:

- `artelab/src/main/java/cl/artelab_spa/artelab/client/UsuarioClient.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/config/WebClientConfig.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/client/UsuarioClientTest.java`

Cambios aplicados:

- Se agrego timeout configurable para WebClient.
- Se agregaron pruebas para:
  - Respuesta exitosa 200.
  - Usuario no encontrado 404.
  - Error remoto 5xx.
  - Falla de conexion.

Impacto:

- Mejor control ante indisponibilidad del microservicio `usuarios`.
- Mayor confianza en la comunicacion entre microservicios.

### 8. Pruebas en `artelab`

Archivos principales:

- `artelab/src/test/java/cl/artelab_spa/artelab/controller/CategoriaControllerTest.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/controller/ProductoControllerTest.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/controller/PromocionControllerTest.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/service/CategoriaServiceTest.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/service/ProductoServiceTest.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/service/PromocionServiceTest.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/client/UsuarioClientTest.java`

Cambios aplicados:

- Se ajustaron tests de controller para el nuevo flujo service-driven.
- Se ampliaron tests de service para casos exitosos, validaciones, conflictos, recursos inexistentes y eliminacion.
- Se agrego cobertura directa del cliente remoto `UsuarioClient`.

Resultado registrado:

- 63 tests ejecutados.
- 0 failures.
- 0 errors.
- JaCoCo check aprobado.

## Cambios importantes en `usuarios`

### 1. `pom.xml`

Se agrego configuracion de JaCoCo:

- Plugin `jacoco-maven-plugin` version `0.8.15`.
- Ejecucion `prepare-agent`.
- Reporte en fase `verify`.
- Check de cobertura minima de linea de `80%`.
- Exclusiones para clases de infraestructura o bajo valor de prueba directa, como arranque, config, security, DTOs, modelos, assemblers y excepciones.

Impacto:

- El modulo ahora tiene control automatico de cobertura sobre el codigo objetivo.

### 2. Controlador

Archivo principal:

- `usuarios/src/main/java/dsy/artelab/usuarios/controller/UsuarioController.java`

Cambios aplicados:

- Se migro a constructor injection.
- El controller delega reglas de negocio al service.

Impacto:

- Mejor legibilidad, testeabilidad y separacion de responsabilidades.

### 3. DTOs y modelo

Archivos principales:

- `usuarios/src/main/java/dsy/artelab/usuarios/dto/LoginRequest.java`
- `usuarios/src/main/java/dsy/artelab/usuarios/dto/UsuarioRequestDto.java`
- `usuarios/src/main/java/dsy/artelab/usuarios/model/Usuario.java`

Cambios aplicados:

- Se agregaron o reforzaron validaciones:
  - `@NotBlank`
  - `@Email`
  - `@Size`
  - `@Pattern`
- En `Usuario` se reforzaron restricciones `unique` y `nullable` para campos clave como `nombreUsuario` y `correo`.

Impacto:

- Mayor control de entradas invalidas.
- Modelo mas alineado con las reglas reales del dominio.

### 4. Manejo de errores

Archivos principales:

- `usuarios/src/main/java/dsy/artelab/usuarios/exception/GlobalExceptionHandler.java`
- `usuarios/src/main/java/dsy/artelab/usuarios/exception/ResourceConflictException.java`

Cambios aplicados:

- Se agrego `ResourceConflictException`.
- Se agrego respuesta 409 para conflictos de negocio.
- Se reforzo respuesta 401 para errores de autenticacion.
- Se dejo respuesta generica para errores 500.

Impacto:

- API mas consistente.
- Menor exposicion de detalles internos.

### 5. Service

Archivo principal:

- `usuarios/src/main/java/dsy/artelab/usuarios/service/UsuarioService.java`

Cambios aplicados:

- Se migro a constructor injection.
- Se centralizo validacion de duplicados por correo y nombre de usuario.
- Duplicados ahora se responden como conflicto 409.
- `getById`, `update` y `delete` manejan existencia dentro del service.
- Se mantuvo codificacion de password.
- Se preservo logica de busqueda para lookup de usuarios.

Impacto:

- Reglas de usuario mas claras.
- Mejor coherencia REST.
- Menor duplicacion de validaciones.

### 6. Pruebas en `usuarios`

Archivo principal:

- `usuarios/src/test/java/dsy/artelab/usuarios/service/UsuarioServiceTest.java`

Cambios aplicados:

- Se ampliaron pruebas para:
  - Listado de usuarios.
  - Creacion exitosa.
  - Duplicado por correo.
  - Duplicado por nombre de usuario.
  - Busqueda exitosa y no encontrada.
  - Lookup.
  - Actualizacion exitosa.
  - Actualizacion con conflicto.
  - Eliminacion exitosa.
  - Eliminacion no encontrada.

Resultado registrado:

- 28 tests ejecutados.
- 0 failures.
- 0 errors.
- JaCoCo check aprobado.

## Documentacion agregada

### `AUDITORIA_EXAMEN_TRANSVERSAL.md`

Se agrego un documento de auditoria orientado especificamente a la rubrica y lista de chequeo del examen transversal.

Contiene:

- Problemas encontrados.
- Soluciones aplicadas.
- Verificaciones ejecutadas.
- Estado por requisito.
- Observaciones de cobertura, base de datos local y requisitos pendientes.

### `REPORTE_CAMBIOS_ARTE_LAB.md`

Este documento resume todos los cambios importantes del proceso, separados por modulo y por tipo de mejora.

## Verificaciones ejecutadas

Comandos registrados durante el proceso:

```powershell
cmd /c mvnw.cmd verify "-Dspring.datasource.url=jdbc:sqlite:target/codex-artelab-final-verify-2.db"
cmd /c mvnw.cmd verify "-Dspring.datasource.url=jdbc:sqlite:target/codex-usuarios-final-verify.db"
rg -n "System\.out|printStackTrace|existsById\(|@Autowired" artelab\src\main\java usuarios\src\main\java
```

Resultados:

- `artelab`: build exitoso, 63 tests, JaCoCo aprobado.
- `usuarios`: build exitoso, 28 tests, JaCoCo aprobado.
- La busqueda de malas practicas no encontro resultados en `src/main`.

## Estado actual del working tree

Cambios trackeados detectados:

- `artelab.db` modificado.
- Cambios en archivos productivos y de test de `artelab`.
- Cambios en archivos productivos y de test de `usuarios`.
- `notas_importantes` figura como eliminado.

Archivos nuevos no trackeados detectados:

- `AUDITORIA_EXAMEN_TRANSVERSAL.md`
- `REPORTE_CAMBIOS_ARTE_LAB.md`
- `artelab/src/main/java/cl/artelab_spa/artelab/exception/ResourceConflictException.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/client/UsuarioClientTest.java`
- `usuarios/src/main/java/dsy/artelab/usuarios/exception/ResourceConflictException.java`

Observaciones:

- `artelab.db` no fue revertido para no perder estado local de base de datos.
- `notas_importantes` contenia una nota breve sobre OpenAPI Specifications y aparece eliminado en el working tree. No se restauro para no deshacer cambios no solicitados.
- Git muestra advertencias de conversion LF -> CRLF para varios archivos; esto corresponde a normalizacion de finales de linea en Windows.

## Archivos principales modificados

### `artelab`

- `artelab/pom.xml`
- `artelab/src/main/java/cl/artelab_spa/artelab/config/DataLoader.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/config/SecurityConfig.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/config/WebClientConfig.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/controller/CategoriaController.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/controller/ProductoController.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/controller/PromocionController.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/dto/LoginRequest.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/exception/GlobalExceptionHandler.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/exception/ResourceConflictException.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/model/Categoria.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/model/Producto.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/model/Promocion.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/service/CategoriaService.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/service/ProductoService.java`
- `artelab/src/main/java/cl/artelab_spa/artelab/service/PromocionService.java`
- `artelab/src/main/resources/META-INF/additional-spring-configuration-metadata.json`
- `artelab/src/test/java/cl/artelab_spa/artelab/client/UsuarioClientTest.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/controller/CategoriaControllerTest.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/controller/ProductoControllerTest.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/controller/PromocionControllerTest.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/service/CategoriaServiceTest.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/service/ProductoServiceTest.java`
- `artelab/src/test/java/cl/artelab_spa/artelab/service/PromocionServiceTest.java`

### `usuarios`

- `usuarios/pom.xml`
- `usuarios/src/main/java/dsy/artelab/usuarios/controller/UsuarioController.java`
- `usuarios/src/main/java/dsy/artelab/usuarios/dto/LoginRequest.java`
- `usuarios/src/main/java/dsy/artelab/usuarios/dto/UsuarioRequestDto.java`
- `usuarios/src/main/java/dsy/artelab/usuarios/exception/GlobalExceptionHandler.java`
- `usuarios/src/main/java/dsy/artelab/usuarios/exception/ResourceConflictException.java`
- `usuarios/src/main/java/dsy/artelab/usuarios/model/Usuario.java`
- `usuarios/src/main/java/dsy/artelab/usuarios/service/UsuarioService.java`
- `usuarios/src/test/java/dsy/artelab/usuarios/service/UsuarioServiceTest.java`

## Cumplimiento frente a la pauta

| Area | Estado |
| --- | --- |
| Arquitectura Controller -> Service -> Repository | Cumplido |
| Validaciones de entrada | Cumplido |
| Manejo de errores global | Cumplido |
| Respuestas HTTP coherentes | Cumplido |
| Logging sin `System.out` | Cumplido |
| Comunicacion entre microservicios | Cumplido |
| Swagger/OpenAPI accesible | Cumplido |
| Pruebas unitarias | Cumplido |
| Cobertura con JaCoCo >= 80% | Cumplido sobre codigo objetivo |
| Documentacion de cambios | Cumplido |

## Recomendaciones antes de entregar o commitear

1. Revisar si `artelab.db` debe mantenerse modificado o restaurarse.
2. Confirmar si la eliminacion de `notas_importantes` fue intencional.
3. Agregar al commit los archivos nuevos:
   - `AUDITORIA_EXAMEN_TRANSVERSAL.md`
   - `REPORTE_CAMBIOS_ARTE_LAB.md`
   - `ResourceConflictException.java` en ambos modulos.
   - `UsuarioClientTest.java`.
4. Ejecutar nuevamente `mvnw.cmd verify` en ambos modulos antes del commit final si se realizan cambios adicionales.
5. Probar manualmente Swagger en:
   - `/doc/swagger-ui.html`
   - `/swagger-ui/index.html`

## Conclusion

El proyecto quedo reforzado en arquitectura, validaciones, manejo de errores, pruebas y cobertura. Los cambios principales apuntan a que `artelab` y `usuarios` sean mas consistentes como microservicios Spring Boot, mas faciles de probar y mas defendibles frente a la rubrica del examen transversal.
