# Parte Teórica — Respuestas y Justificación Técnica

## 1. Optimización en sistemas financieros
Para procesar transacciones en tiempo real sin que el sistema se degrade ante picos de tráfico, trabajaría en cuatro frentes:

* **Concurrencia:** Utilizar programación reactiva con Spring WebFlux o Virtual Threads en Java 21 para evitar que los hilos queden bloqueados esperando red o base de datos.
* **Desacoplamiento por eventos:** Enviar operaciones que no requieren respuesta inmediata en pantalla —como notificaciones, auditoría o reportes— a colas de mensajes en Apache Kafka para absorber ráfagas sin saturar los servicios centrales.
* **Caché:** Usar Redis para lecturas frecuentes que cambian poco, como tasas o parámetros de producto, bajando la carga sobre la base transaccional.
* **Base de datos y patrones:** Aplicar CQRS para separar lecturas de escrituras, ajustar el pool de conexiones con HikariCP, indexar columnas críticas y dirigir consultas a réplicas de lectura.

---

## 2. Seguridad en APIs financieras
La seguridad debe cubrir desde que entra la petición hasta que se persiste el dato:

* **Inyección SQL:** Usar siempre consultas parametrizadas, JPQL o Criteria API a través de Spring Data JPA para que ningún parámetro se interprete como instrucción SQL.
* **XSS:** Validar estrictamente los DTOs con Bean Validation rechazando caracteres o formatos inválidos, y configurar cabeceras de seguridad como `Content-Security-Policy` y `X-Content-Type-Options`.
* **CSRF:** Al trabajar con una API stateless donde el token JWT viaja en la cabecera `Authorization` y no en cookies automáticas, el riesgo de CSRF desaparece. Si se usaran cookies, deberían llevar las banderas `HttpOnly`, `Secure` y `SameSite=Strict`.
* **Medidas adicionales:** Tokens JWT de corta duración con refresh tokens, HTTPS obligatorio, CORS limitado únicamente al dominio del front-end, rate limiting en el gateway y manejo global de errores con `@RestControllerAdvice` para no exponer trazas internas en las respuestas.

---

## 3. Transacciones en sistemas distribuidos
En microservicios no es viable usar un `@Transactional` tradicional entre distintas bases de datos porque genera bloqueos y cuellos de botella. Implementaría tres mecanismos:

* **Patrón Saga:** Dividir la transferencia en transacciones locales independientes. Si el servicio origen debita pero el destino falla al acreditar, se ejecuta una transacción compensatoria que revierte el débito para mantener consistencia eventual.
* **Idempotencia:** Exigir un identificador único por transacción en cada solicitud para evitar cobros o transferencias duplicadas cuando haya reintentos por fallos de red.
* **Tolerancia a fallos:** Aplicar Circuit Breaker y reintentos con backoff exponencial mediante Resilience4j, y enviar a un Dead Letter Topic en Kafka los eventos que sigan fallando para auditarlos sin detener el flujo general.

---

## 4. Pruebas unitarias y de integración
Diseñaría una suite en dos niveles complementarios:

* **Pruebas unitarias:** Con JUnit 5 y Mockito sobre los servicios para probar de forma aislada tanto el camino normal como casos límite: montos inválidos, saldos insuficientes, cuentas bloqueadas o transiciones de estado no permitidas.
* **Pruebas de integración:** Con `@SpringBootTest`, `MockMvc` y Testcontainers levantando una base de datos real en Docker para verificar de punta a punta que los controladores, validaciones, permisos de Spring Security y persistencia JPA funcionen juntos y respondan los códigos HTTP esperados.
* **Automatización:** Medir cobertura sobre las reglas críticas con JaCoCo y ejecutar toda la suite en el pipeline de CI/CD antes de aprobar cualquier merge.

---

## 5. Front-end
En Angular priorizaría que la sesión sea segura y que la vista nunca muestre saldos desactualizados:

* **Autenticación:** Aunque en un MVP se puede usar `localStorage` por simplicidad, en un entorno bancario en producción manejaría el token mediante cookies `HttpOnly` y `Secure` o en memoria con expiración corta, apoyado en un HTTP Interceptor para adjuntar las credenciales y cerrar sesión si el backend responde `401`.
* **Rutas:** Proteger las vistas con Guards (`CanActivate`) para bloquear el acceso según sesión y rol.
* **Estado y coherencia:** Centralizar el estado en servicios reactivos con `BehaviorSubject` o Signals. Tras cada operación monetaria, el front-end debe consultar nuevamente el saldo al backend en lugar de calcularlo localmente, garantizando que lo mostrado en pantalla coincida con la base de datos.

---

## 6. Spring Boot

1. **Qué pasa internamente con `@SpringBootApplication` en el arranque:**
   Agrupa tres anotaciones clave: `@SpringBootConfiguration` marca la clase principal como fuente de configuración; `@ComponentScan` escanea el paquete base y subpaquetes para registrar beans en el contenedor IoC; y `@EnableAutoConfiguration` inspecciona las dependencias del classpath para configurar componentes por defecto —como el servidor Tomcat embebido o el `DataSource`— dejando el `ApplicationContext` listo antes de abrir el puerto.

2. **Cómo funciona el ciclo de vida de un bean y cómo intervenir en él:**
   Spring lee la definición del bean, lo instancia en memoria, inyecta sus dependencias, pasa por las interfaces `Aware` si corresponde, ejecuta la fase de inicialización y, al apagar la aplicación, ejecuta la fase de destrucción. Se puede intervenir ejecutando lógica justo después de la inyección con `@PostConstruct`, liberando recursos antes del cierre con `@PreDestroy`, o interceptando beans de forma transversal mediante `BeanPostProcessor`.

3. **Cómo personalizar la autoconfiguración sin romper la filosofía de convención sobre configuración:**
   Primero ajustando propiedades estándar en `application.properties` o `application.yml`. Si se requiere cambiar el comportamiento de un componente específico, se declara un `@Bean` propio en una clase `@Configuration`, ante lo cual Spring desactiva automáticamente su bean por defecto de forma condicional. Como último recurso, se excluyen autoconfiguraciones puntuales usando el atributo `exclude` en `@SpringBootApplication`.
