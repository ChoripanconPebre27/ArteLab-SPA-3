# Informe Técnico - ArteLab

## 2. Lista de Chequeo: Informe Técnico

Este informe tiene como objetivo respaldar, de forma teórica y conceptual, cómo se construyó el ecosistema de microservicios de ArteLab.

---

## 2.1 Arquitectura y Flujo de Datos

ArteLab fue desarrollado como un ecosistema de microservicios compuesto por dos módulos principales:
- ArteLab: módulo principal del negocio, encargado de gestionar productos, categorías, promociones y comunicación con el servicio de usuarios.
- Usuarios: módulo encargado de administrar usuarios, autenticación y datos de integración.

### Patrón CSR aplicado
El proyecto sigue el patrón Controller → Service → Repository:
- Controller: recibe las peticiones HTTP y delega el procesamiento.
- Service: concentra la lógica de negocio, validaciones y reglas del dominio.
- Repository: gestiona el acceso a la persistencia.

Este patrón permite separar responsabilidades, mejorar la mantenibilidad del código y facilitar la prueba de cada capa.

### Flujo de datos general
1. El cliente realiza una petición HTTP a un endpoint del sistema.
2. El controller recibe la solicitud y la delega al service correspondiente.
3. El service valida la información y aplica reglas de negocio.
4. El repository accede a la base de datos si corresponde.
5. El resultado se transforma en una respuesta HTTP clara y estructurada.

### Diagrama de paquetes del proyecto
El proyecto está organizado de forma modular por capas y responsabilidades:
- controller: rutas y operaciones expuestas por la API
- service: lógica de negocio
- repository: acceso a datos
- model: entidades JPA
- dto: objetos de transferencia de datos
- exception: manejo de errores
- config: seguridad, WebClient y configuración general

Este diseño permite una mejor organización del ecosistema y una mayor claridad para el desarrollo y la evaluación técnica.

---

## 2.2 Modelo de Base de Datos y Entidades

El proyecto utiliza entidades JPA para representar el modelo relacional del negocio.

### Entidades principales

#### Categoria
Representa una categoría para agrupar productos y promociones.
- Tiene una relación de uno a muchos con productos.
- Tiene una relación de uno a muchos con promociones.

#### Producto
Representa un producto del catálogo.
- Tiene una relación ManyToOne con Categoria.
- Se modela con atributos como descripción, precio, stock y categoría asociada.

#### Promocion
Representa una promoción aplicada sobre una categoría.
- Tiene una relación ManyToOne con Categoria.
- Incluye fechas de vigencia y porcentaje de descuento.

#### Usuario
Representa a un usuario del sistema.
- Tiene atributos como nombreUsuario, clave y correo.
- Se usa tanto para administración local como para integración con otros microservicios.

### Relación entre entidades
- Categoria → Producto: relación uno a muchos.
- Categoria → Promocion: relación uno a muchos.
- Producto → Categoria: relación muchos a uno.
- Promocion → Categoria: relación muchos a uno.
- Usuario: entidad independiente usada en el microservicio de usuarios.

### Justificación de las relaciones
Las relaciones se modelaron para reflejar el dominio del problema:
- una categoría puede agrupar varios productos y promociones
- un producto y una promoción pertenecen a una sola categoría
- los usuarios se gestionan de forma independiente para permitir la integración entre microservicios

---

## 2.3 Matriz de Endpoints y Contratos de la API

### Microservicio ArteLab

#### Categorías
| Endpoint | Método | Recibe | Responde | Códigos posibles |
|---|---|---|---|---|
| /api/v1/categorias | GET | Ninguno | Lista de categorías | 200, 204, 500 |
| /api/v1/categorias | POST | JSON de categoría | Categoría creada | 201, 400, 500 |
| /api/v1/categorias/{id} | GET | Ninguno | Categoría específica | 200, 404, 500 |
| /api/v1/categorias/{id} | PUT | JSON de categoría | Categoría actualizada | 200, 400, 404, 500 |
| /api/v1/categorias/{id} | DELETE | Ninguno | Sin contenido | 204, 404, 500 |

#### Productos
| Endpoint | Método | Recibe | Responde | Códigos posibles |
|---|---|---|---|---|
| /api/v1/productos | GET | Ninguno | Lista de productos | 200, 204, 500 |
| /api/v1/productos | POST | JSON de producto | Producto creado | 201, 400, 500 |
| /api/v1/productos/{id} | GET | Ninguno | Producto específico | 200, 404, 500 |
| /api/v1/productos/{id} | PUT | JSON de producto | Producto actualizado | 200, 400, 404, 500 |
| /api/v1/productos/{id} | DELETE | Ninguno | Sin contenido | 204, 404, 500 |

#### Promociones
| Endpoint | Método | Recibe | Responde | Códigos posibles |
|---|---|---|---|---|
| /api/v1/promociones | GET | Ninguno | Lista de promociones | 200, 204, 500 |
| /api/v1/promociones | POST | JSON de promoción | Promoción creada | 201, 400, 500 |
| /api/v1/promociones/usuario/{usuarioId} | POST | JSON de promoción + usuarioId | Promoción creada | 201, 400, 404, 503, 500 |
| /api/v1/promociones/{id} | GET | Ninguno | Promoción específica | 200, 404, 500 |
| /api/v1/promociones/{id} | PUT | JSON de promoción | Promoción actualizada | 200, 400, 404, 500 |
| /api/v1/promociones/{id} | DELETE | Ninguno | Sin contenido | 204, 404, 500 |

### Microservicio Usuarios

| Endpoint | Método | Recibe | Responde | Códigos posibles |
|---|---|---|---|---|
| /api/v1/usuarios | GET | Ninguno | Lista de usuarios | 200, 204, 500 |
| /api/v1/usuarios/{id} | GET | Ninguno | Usuario específico | 200, 404, 500 |
| /api/v1/usuarios/{id}/lookup | GET | Ninguno | Datos públicos del usuario | 200, 404, 500 |
| /api/v1/usuarios | POST | JSON de usuario | Usuario creado | 201, 400, 500 |
| /api/v1/usuarios/{id} | PUT | JSON de usuario | Usuario actualizado | 200, 400, 404, 500 |
| /api/v1/usuarios/{id} | DELETE | Ninguno | Sin contenido | 204, 404, 500 |

### Estructura general del JSON
Los cuerpos de entrada se componen de campos como:
- categoría: descripción
- producto: descripción, precio, stock, categoría
- promoción: descripción, fechas, descuento, categoría
- usuario: nombreUsuario, correo, clave

---

## 2.4 Estrategia de Gestión de Errores y Validaciones

El sistema cuenta con una estrategia clara de manejo de errores y validaciones.

### Validaciones implementadas
Se incorporaron restricciones con Bean Validation para evitar que se ingresen datos inválidos.

Ejemplos:
- @NotBlank para campos obligatorios
- @NotNull para valores indispensables
- @Size para limitar tamaño de texto
- @Positive y @PositiveOrZero para valores numéricos
- @Min y @Max para rangos válidos
- @Email y @Pattern para formatos específicos

### Reglas de negocio implementadas
Las reglas de negocio se implementaron en la capa de servicios, no en los controladores.

Ejemplos:
- descuentos válidos entre 1 y 100
- fechas de promoción correctas
- categoría obligatoria para productos y promociones
- usuarios únicos por correo y nombre de usuario
- validación de existencia antes de actualizar o eliminar

### Manejo global de excepciones
Se implementó un @ControllerAdvice para centralizar el manejo de errores.

Los tipos de error capturados incluyen:
- EntityNotFoundException → 404
- MethodArgumentNotValidException → 400
- BusinessRuleViolationException → 400
- ResourceConflictException → 409
- AuthenticationException → 401
- RemoteServiceException → 503
- errores inesperados → 500

Esto asegura respuestas HTTP claras, consistentes y más seguras para los consumidores de la API.

---

## 2.5 Gestión del Proyecto (Evidencias de Trabajo Colaborativo)

El proyecto fue gestionado de forma organizada y con evidencia de avance técnico y documental.

### Evidencias disponibles
- documentación de auditoría en el repositorio
- reporte de cambios del proyecto
- historial de commits en GitHub
- organización de cambios por módulos y funcionalidades
- evidencia de tareas realizadas en artefactos de desarrollo y revisión técnica

### Qué demuestra esta evidencia
- el trabajo fue realizado de forma progresiva
- se detallaron cambios importantes por módulo
- se registraron mejoras de arquitectura, validaciones, errores, seguridad y pruebas
- se dejó trazabilidad para evaluación académica o técnica

---

## 2.6 Reporte Formal de Cobertura de Pruebas

El proyecto incorpora JaCoCo para medir la cobertura de pruebas.

### Evidencia de cobertura
Se configuró JaCoCo en ambos módulos con:
- plugin jacoco-maven-plugin
- versión 0.8.15
- ejecución en la fase verify
- reporte generado en target/site/jacoco
- umbral mínimo de cobertura del 80%

### Resultado esperado y verificado
- arteLab: pruebas ejecutadas con éxito
- usuarios: pruebas ejecutadas con éxito
- cobertura aprobada según el chequeo configurado

Esta evidencia es clave para respaldar que el proyecto cumple con la exigencia de calidad y validación automática.

---

## 2.7 Guía de Despliegue y Configuración

El proyecto puede ser levantado localmente siguiendo una configuración básica de Spring Boot.

### Requisitos conceptuales
- Java 25
- Maven
- base de datos local SQLite
- dependencias de Spring Boot, JPA, Flyway y Spring Security

### Propuesta de despliegue local
1. Clonar o abrir el proyecto en la carpeta correspondiente.
2. Ejecutar el módulo arteLab y el módulo usuarios.
3. Configurar las propiedades de conexión y puertos si es necesario.
4. Validar que los endpoints estén disponibles.
5. Usar Swagger para probar la API.

### Propuesta de despliegue en la nube
El proyecto es compatible con enfoques modernos de despliegue como:
- Render
- Railway
- Docker
- servicios de contenedores o plataformas de Spring Boot

### Consideración importante
La arquitectura modular facilita el despliegue por separado de cada microservicio, lo que mejora la escalabilidad y la modularidad del sistema.

---

## Conclusión

ArteLab fue desarrollado como un ecosistema de microservicios con una estructura clara, validaciones, reglas de negocio, manejo de errores, integración entre servicios, pruebas y cobertura. El informe presentado permite respaldar técnicamente el proyecto y demostrar que cumple con los puntos esenciales de la lista de chequeo del examen transversal.
