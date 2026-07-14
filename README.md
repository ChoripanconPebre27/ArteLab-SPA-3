# ArteLab SPA - Documentación integral

## Descripción general

ArteLab SPA es un proyecto de desarrollo backend orientado a la gestión de una tienda digital de materiales artísticos. El sistema está diseñado como un ecosistema modular compuesto por dos microservicios principales:

- ArteLab: módulo principal del negocio, encargado de la administración de categorías, productos, promociones y la integración con el servicio de usuarios.
- Usuarios: módulo encargado de la gestión de usuarios, autenticación y operaciones de consulta para integración con otros servicios.

Este repositorio reúne el código fuente, la documentación técnica, los reportes de auditoría y la presentación de defensa correspondiente al examen transversal.

---

## Estructura del proyecto

### 1. Microservicio ArteLab

El módulo ArteLab contiene la lógica del negocio central del sistema. Incluye:

- Gestión de categorías
- Gestión de productos
- Gestión de promociones
- Integración con el servicio de usuarios mediante WebClient
- Manejo centralizado de errores y validaciones
- Documentación de API con Swagger/OpenAPI
- Pruebas unitarias y cobertura con JaCoCo

### 2. Microservicio Usuarios

El módulo Usuarios gestiona la identidad y los datos relacionados con los usuarios del sistema. Incluye:

- Registro y administración de usuarios
- Autenticación básica y control de acceso
- Endpoints de consulta para integración con otros módulos
- Validaciones y reglas de negocio para evitar duplicidades
- Pruebas automatizadas y cobertura de calidad

---

## Arquitectura implementada

El proyecto sigue una arquitectura por capas basada en el patrón Controller → Service → Repository:

1. Controller: recibe y procesa las solicitudes HTTP.
2. Service: concentra la lógica de negocio y las reglas del dominio.
3. Repository: gestiona el acceso a los datos.
4. Exception handling: centraliza los errores y devuelve respuestas consistentes.

Además, se implementa una arquitectura de microservicios donde ArteLab consume servicios de Usuarios para validar información y mantener un diseño distribuido y escalable.

---

## Modelo de datos y dominio

El sistema modela entidades clave para representar el negocio:

- Categoría: agrupa productos y promociones.
- Producto: representa el catálogo de la tienda.
- Promoción: define descuentos y vigencia temporal asociada a una categoría.
- Usuario: representa al usuario del sistema y permite integración entre módulos.

Estas entidades se relacionan de manera coherente para reflejar las reglas del negocio y la lógica del dominio.

---

## Validaciones, reglas de negocio y errores

El sistema incorpora:

- Validaciones con Bean Validation
- Reglas de negocio en la capa de servicios
- Manejo global de excepciones con respuestas HTTP claras
- Control de conflictos, recursos no encontrados y errores remotos
- Seguridad básica para proteger endpoints y operaciones sensibles

---

## Pruebas y calidad

Se incorporaron pruebas automatizadas para los distintos módulos del sistema, incluyendo:

- Controladores
- Servicios
- Cliente de integración
- Casos de validación y manejo de errores

Asimismo, se configuró JaCoCo para medir la cobertura de pruebas con un umbral mínimo de calidad.

---

## Documentación incluida

Este repositorio incorpora los siguientes documentos de apoyo:

- INFORME_EXAMEN_TRANSVERSAL_ARTE_LAB.md: informe técnico con checklist del examen transversal.
- PPT_ET_FullStack.pdf: presentación en formato PDF preparada para la defensa oral.

Estos archivos quedan como parte central del entregable, junto con la documentación del proyecto y la estructura de los microservicios.

### Relación con la presentación

El material preparado para la defensa incluye:
- informe técnico con enfoque de examen transversal
- presentación en formato PPT
- apoyo visual y conceptual para explicar el proyecto

---

## Ejecución y despliegue

El proyecto puede ejecutarse de forma local mediante Spring Boot y Maven, considerando:

- Java 25
- Maven
- Base de datos local SQLite
- Dependencias de Spring Boot, JPA, Flyway y Spring Security

La arquitectura modular facilita además un despliegue independiente de cada microservicio.

---

## Resumen ejecutivo

ArteLab SPA demuestra la construcción de una solución backend completa, con enfoque en arquitectura, modularidad, validaciones, seguridad, pruebas y documentación técnica. El proyecto no solo cumple con la funcionalidad esperada, sino que también refleja buenas prácticas de desarrollo profesional y preparación para una defensa académica o técnica.
