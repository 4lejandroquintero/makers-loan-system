# Especificaciones y Decisiones Técnicas

**Proyecto:** Sistema de Gestión de Préstamos Bancarios (Java 21 + Angular 20)  
**Autor:** Alejandro Quintero Alzate

---

## 1. Alcance y Stack

* **Objetivo:** Desarrollar una API REST y una aplicación web con control de roles para solicitar, consultar, aprobar y rechazar préstamos bancarios.
* **Stack:** Java 21, Spring Boot, Spring Data JPA, Hibernate Validator, Spring Security con JWT, Spring Cache, H2, JUnit 5, Mockito y Angular 20.
* **Alcance del MVP:**
  * Autenticación con JWT y autorización por roles (`ROLE_USER`, `ROLE_ADMIN`).
  * Flujo de usuario: solicitar préstamo indicando monto y plazo, y consultar sus propios préstamos.
  * Flujo de administrador: listar solicitudes y aprobar o rechazar préstamos pendientes.
  * Pruebas unitarias sobre las reglas críticas de negocio y respuestas de la parte teórica.
* **Fuera de alcance:** Despliegue con Docker o CI/CD, paginación avanzada y tabla de amortización.

---

## 2. Especificaciones Funcionales

* **RF-01 Autenticación y roles:** Login con JWT y usuarios semilla precargados (`usuario@test.com` y `admin@test.com` con clave `123`).
* **RF-02 Solicitud de préstamo:** Un usuario autenticado puede solicitar un préstamo que nace en estado `PENDING`, validando monto mínimo y plazo permitido.
* **RF-03 Consulta de estado:** El usuario consulta únicamente sus préstamos; el administrador visualiza todas las solicitudes.
* **RF-04 Gestión administrativa:** Solo `ROLE_ADMIN`, validado en Spring Security y Angular Guards, puede aprobar (`APPROVED`) o rechazar (`REJECTED`) préstamos que sigan en estado `PENDING`.

---

## 3. Plan de Implementación

1. Definición de arquitectura, especificaciones y decisiones técnicas.
2. **Backend:** Capa `domain` con modelos y puertos -> `application` con casos de uso y `@Transactional` -> `infrastructure` con adaptadores JPA, controladores REST, caché, `GlobalExceptionHandler` y Spring Security con JWT -> pruebas unitarias con JUnit 5 y Mockito.
3. **Frontend:** Modelos e interfaces -> `AuthService`, `LoanService`, interceptor JWT y Guards -> vistas de login, usuario y administrador con `ReactiveFormsModule` -> pruebas end-to-end contra la API.
4. Cierre de documentación (`README.md` y `PREGUNTAS_TEORICAS.md`) y verificación en GitHub.

---

## 4. Decisiones Técnicas y de Arquitectura

* **Arquitectura Hexagonal:** Se separa `domain` de `application` e `infrastructure` para que las reglas de negocio —como permitir aprobar o rechazar únicamente préstamos en estado `PENDING`— no dependan de anotaciones de Spring ni de JPA.
* **Spring Web MVC + JPA en lugar de WebFlux:** Como JPA sobre una base relacional es bloqueante y transaccional, montar WebFlux encima no aporta reactividad real y complica innecesariamente el manejo de transacciones. Web MVC es la opción más sólida para este caso.
* **Base de datos H2 en memoria con `DataInitializer`:** Permite clonar y probar el proyecto de inmediato sin levantar motores externos, dejando la puerta abierta a cambiar a PostgreSQL o MySQL solo ajustando propiedades.
* **Caché con `@Cacheable` y `@CacheEvict`:** Se cachean las consultas de préstamos para evitar lecturas repetidas y se invalida el caché automáticamente cada vez que se crea o cambia de estado un préstamo.
* **Transaccionalidad:** Las operaciones de aprobación y rechazo llevan `@Transactional` para asegurar consistencia si ocurre algún fallo al persistir.
* **Manejo global de errores:** `GlobalExceptionHandler` centraliza las excepciones y devuelve códigos HTTP claros (`400`, `401`, `403`, `404`) que el front-end interpreta directamente.
* **Gestión de estado y formularios en Angular:** Se centralizó el estado de sesión y préstamos en `AuthService` y `LoanService` usando `BehaviorSubject` en vez de agregar NgRx, evitando sobreingeniería para el tamaño del proyecto, junto con `ReactiveFormsModule` para validar entradas antes de enviarlas al backend.
