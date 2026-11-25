# Sistema de Gestión de Préstamos Bancarios

Aplicación Full Stack construida con **Java 21 (Spring Boot)** y **Angular 20** para gestionar solicitudes de préstamos bancarios. Los usuarios pueden autenticarse, solicitar un préstamo indicando monto y plazo, y hacer seguimiento al estado de sus solicitudes. Los administradores pueden revisar todas las solicitudes y aprobar o rechazar las que estén pendientes.

## Documentación del proyecto

* **Especificaciones y decisiones técnicas:** [`SPECS_Y_DECISIONES.md`](./SPECS_Y_DECISIONES.md)
* **Respuestas de la parte teórica:** [`PREGUNTAS_TEORICAS.md`](./PREGUNTAS_TEORICAS.md)

---

## Arquitectura

### Backend (`loans-backend`)
Organizado bajo **Arquitectura Hexagonal** para aislar la lógica de negocio del framework y la base de datos:
* **`domain/`:** Modelos puros (`Loan`, `User`, `LoanStatus`, `Role`), puertos de entrada y salida, y excepciones de negocio. Sin dependencias de Spring ni JPA.
* **`application/`:** Casos de uso (`LoanService`, `UserService`) con control transaccional mediante `@Transactional` y manejo de caché con `@Cacheable` y `@CacheEvict`.
* **`infrastructure/`:** Adaptadores JPA sobre base de datos H2, seguridad stateless con Spring Security y JWT, controladores REST, DTOs validados con Jakarta Validation y manejo global de excepciones con `@RestControllerAdvice`.

### Frontend (`loans-frontend`)
Construido en **Angular 20** con componentes standalone:
* **`core/`:** Interfaces TypeScript, servicios reactivos con `BehaviorSubject` (`AuthService` y `LoanService`), interceptor HTTP para adjuntar el token JWT en cada petición y Guards (`authGuard`, `adminGuard`) para proteger las rutas por rol.
* **`features/`:** Vistas separadas para inicio de sesión, panel de usuario y panel de administrador, utilizando `ReactiveFormsModule` para validar los formularios.

---

## Reglas de negocio y seguridad

1. **Solicitud de préstamos:** Todo usuario autenticado puede solicitar un préstamo indicando monto mínimo de `$100` y plazo entre `1` y `120` meses. Toda solicitud inicia en estado `PENDING`.
2. **Transición de estados:** Solo un préstamo en estado `PENDING` puede pasar a `APPROVED` o `REJECTED`. Intentar modificar un préstamo ya resuelto devuelve `400 Bad Request`.
3. **Control por roles:**
   * `ROLE_USER`: crea solicitudes y consulta únicamente sus propios préstamos.
   * `ROLE_ADMIN`: único rol autorizado en backend (`@PreAuthorize("hasRole('ADMIN')")`) y en frontend (`adminGuard`) para listar todas las solicitudes, aprobarlas o rechazarlas, y gestionar el CRUD de usuarios.
4. **Consistencia y caché:** La aprobación y el rechazo corren dentro de transacciones y limpian el caché de consultas al crear o actualizar un préstamo.

---

## Base de datos H2 y credenciales de prueba

El proyecto usa **H2 en memoria** y precarga usuarios y préstamos de ejemplo al arrancar mediante `DataInitializer`:

* **Consola H2:** `http://localhost:8080/h2-console` *(usar el JDBC URL configurado en `application.properties`)*
* **Swagger UI:** `http://localhost:8080/swagger-ui.html`

| Rol | Email | Contraseña |
| :--- | :--- | :--- |
| Usuario | `usuario@test.com` | `123` |
| Administrador | `admin@test.com` | `123` |

---

## Endpoints principales (`http://localhost:8080`)

| Método | Ruta | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Público | Autentica credenciales y devuelve JWT |
| `POST` | `/api/loans` | Autenticado | Registra una nueva solicitud de préstamo |
| `GET` | `/api/loans/my` | Autenticado | Lista los préstamos del usuario autenticado |
| `GET` | `/api/loans/{id}` | Autenticado | Consulta el detalle de un préstamo |
| `GET` | `/api/loans` | Admin | Lista todas las solicitudes del sistema |
| `PATCH` | `/api/loans/{id}/approve` | Admin | Aprueba un préstamo pendiente |
| `PATCH` | `/api/loans/{id}/reject` | Admin | Rechaza un préstamo pendiente |
| `DELETE` | `/api/loans/{id}` | Admin | Elimina un préstamo |
| `GET/POST/PUT/DELETE` | `/api/users/**` | Admin | CRUD de usuarios |

---

## Cómo ejecutar el proyecto

### 1. Backend (`loans-backend`)
Desde la carpeta `loans-backend`:

* **Levantar la API:**
  ```bash
  ./mvnw spring-boot:run
  ```
  *(En Windows usar `mvnw.cmd spring-boot:run`, o ejecutar `LoansBackendApplication` desde IntelliJ IDEA).*

* **Correr pruebas unitarias (JUnit 5 + Mockito):**
  ```bash
  ./mvnw test
  ```

### 2. Frontend (`loans-frontend`)
Desde la carpeta `loans-frontend`:

1. Instalar dependencias:
   ```bash
   npm install
   ```
2. Levantar el servidor local:
   ```bash
   ng serve
   ```
3. Abrir en el navegador: `http://localhost:4200`
