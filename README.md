# gestor-incidencias
API REST para gestionar incidencias con Java, Spring Boot y PostgreSQL

## Puesta en marcha

### Requisitos
- JDK 21
- Docker (con Docker Compose)

### Pasos
1. Copia los ficheros de ejemplo y pon **la misma contraseña** en los dos:
   ```bash
   cp .env.example .env
   cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
   ```
   - En `.env`: `DB_PASSWORD=<tu contraseña>`
   - En `application-local.properties`: `spring.datasource.password=<tu contraseña>`

   Ninguno de los dos se sube a Git.
2. Levanta la base de datos (PostgreSQL 17, accesible en `localhost:5433`):
   ```bash
   docker compose up -d --wait
   ```
3. Arranca la aplicación (Flyway crea las tablas y carga los datos de prueba la primera vez):
   ```bash
   ./mvnw spring-boot:run
   ```
   En Windows (cmd o PowerShell): `mvnw.cmd spring-boot:run`.
4. Comprueba que está viva: <http://localhost:8081/actuator/health> debe devolver `"status":"UP"`.

La aplicación usa el puerto **8081** por defecto; se puede cambiar con la variable de entorno `SERVER_PORT`.
