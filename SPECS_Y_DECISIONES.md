# Acta de Constitución, Specs y Decisiones Técnicas
**Proyecto:** Sistema de Gestión de Préstamos Bancarios para la prueba técnica makers (Java 21 + Angular 20)
**Autor:** Alejandro Quintero Alzate

## 1. Acta de Constitución
- **Objetivo:** Desarrollar una API REST y aplicación web con control de roles para solicitar, consultar y aprobar o rechazar préstamos bancarios.
- **Stack:** Java 21, Spring Boot, Spring Data JPA, Hibernate Validator, Spring Security (JWT), Caché, H2, JUnit 5 + Angular 20 (Reactive Forms).
- **Alcance (MVP):**
  - Autenticación con JWT y autorización por roles (`ROLE_USER`, `ROLE_ADMIN`).
  - Flujo de usuario: solicitar préstamo (monto y plazo) y consultar sus préstamos.
  - Flujo de administrador: listar solicitudes y aprobar o rechazar préstamos pendientes.
  - Pruebas unitarias sobre reglas críticas y documentación de la parte teórica (30%).
- **Fuera de alcance (por límite de tiempo de 2h prácticas):**
  - Despliegue con Docker/CI-CD, paginación avanzada en tablas y tabla de amortización.

## 2. Especificaciones Funcionales (Specs)
- **RF-01 (Auth y Roles):** Login con JWT; usuarios semilla precargados (`usuario@test.com` y `admin@test.com`, clave `123`).
- **RF-02 (Solicitar préstamo):** Usuario autenticado solicita préstamo en estado `PENDING` validando monto y plazo positivos.
- **RF-03 (Consultar estado):** Usuario consulta únicamente sus préstamos; Admin consulta todas las solicitudes.
- **RF-04 (Gestión Admin):** Solo `ROLE_ADMIN` (validado en Spring Security y Angular Guards) puede aprobar (`APPROVED`) o rechazar (`REJECTED`) préstamos en estado `PENDING`.

## 3. Path de Trabajo
1. Definición de arquitectura, specs y decisiones (este documento).
2. Backend: Dominio y puertos -> Casos de uso y `@Transactional` -> Adaptadores REST, JPA, Caché, `GlobalExceptionHandler` y Spring Security (JWT) -> Tests unitarios (JUnit 5) -> Pruebas de API.
3. Frontend: Modelos e interfaces -> `AuthService` + `Interceptor` + `Guards` -> Componentes con `ReactiveFormsModule` -> Integración End-to-End.
4. Documentación de Parte Teórica (30%), `README.md` y verificación final en GitHub.

## 4. Registro de Decisiones Técnicas
- **Arquitectura Hexagonal:** Se separa `domain` (entidades puras y puertos) de `application` (casos de uso) e `infrastructure` (JPA, REST, Security) para desacoplar la lógica bancaria del framework.
- **Spring MVC + JPA (criterio sobre WebFlux):** Como JPA/Hibernate bloquea el hilo al consultar la base relacional, usar WebFlux encima no sería reactivo real; se opta por Spring MVC tradicional para mantener consistencia transaccional.
- **Base de datos H2 en memoria + DataInitializer:** Permite clonar y ejecutar el proyecto de inmediato sin instalar motores externos, manteniendo compatibilidad con PostgreSQL/MySQL por configuración.
- **Caché (`@Cacheable` / `@CacheEvict`):** Aplicado en la consulta de estado de préstamos por usuario para evitar lecturas repetitivas, invalidando al crear o cambiar el estado de un préstamo.
- **Transaccionalidad (`@Transactional`):** Aplicada en la aprobación y rechazo de préstamos para garantizar consistencia ante fallos.
- **Manejo global de errores:** `GlobalExceptionHandler` centralizado devolviendo códigos HTTP semánticos (`400`, `401`, `403`, `404`) consumidos en Angular.
- **Estado y formularios en Angular:** Se usa `AuthService` (manejo reactivo con RxJS/Signals) en lugar de NgRx para no sobre-ingenierizar el MVP, junto con `ReactiveFormsModule` para validar entradas.