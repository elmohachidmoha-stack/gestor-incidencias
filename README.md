# gestor-incidencias
API REST para gestionar incidencias con Java, Spring Boot y PostgreSQL

## Cómo se ha desarrollado
Proyecto desarrollado por mí con la ayuda de Claude Code (modelo Claude Opus 5.5). Yo defino el
alcance de cada etapa, reviso el código que se genera, lo ejecuto y lo documento. El historial
de commits refleja el avance etapa por etapa.

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

## Endpoints

| Método | Ruta                     | Descripción                                   | Respuestas      |
|--------|--------------------------|-----------------------------------------------|-----------------|
| GET    | `/api/incidencias`       | Lista todas las incidencias                   | 200             |
| GET    | `/api/incidencias/{id}`  | Obtiene una incidencia por su id              | 200, 404        |
| POST   | `/api/incidencias`       | Crea una incidencia (nace en estado `ABIERTA`) | 201 + `Location`, 404 si el reportador no existe |

Prioridades válidas: `BAJA`, `MEDIA` (por defecto), `ALTA`, `CRITICA`.

Listar:
```bash
curl http://localhost:8081/api/incidencias
```

Obtener por id:
```bash
curl http://localhost:8081/api/incidencias/1
```

Crear (`-i` muestra la cabecera `Location` con la URL de la nueva incidencia):
```bash
curl -i -X POST http://localhost:8081/api/incidencias \
  -H "Content-Type: application/json" \
  -d '{"titulo":"La impresora no imprime","descripcion":"Los trabajos se quedan en cola","prioridad":"ALTA","reportadorId":4}'
```
En Windows usa `curl.exe` (en PowerShell `curl` es un alias de otro comando).

Ejemplo de respuesta:
```json
{
  "id": 21,
  "titulo": "La impresora no imprime",
  "descripcion": "Los trabajos se quedan en cola",
  "estado": "ABIERTA",
  "prioridad": "ALTA",
  "reportador": { "id": 4, "nombre": "Javier López" },
  "responsable": null,
  "fechaCreacion": "2026-10-14T13:14:04.850652Z",
  "fechaActualizacion": "2026-10-14T13:14:04.850652Z"
}
```

## Roadmap

- [x] Día 1: base del proyecto, BD en Docker Compose, Flyway (esquema + datos de prueba)
- [x] Día 2: entidades, repositorios y primeros endpoints + flujo con ramas/PR e issues
- [ ] Día 3: CRUD completo, DTOs, validación, manejo de errores + CI con GitHub Actions
- [ ] Día 4: estados (transiciones válidas) y asignación + Actuator
- [ ] Día 5: historial de cambios + Dockerfile multi-stage
- [ ] Día 6: filtros, paginación, estadísticas + stack completo en Compose
- [ ] Día 7: scripts SQL de validación/corrección + backup y restore
- [ ] Día 8: tests (Mockito, MockMvc, @DataJpaTest) + JaCoCo
- [ ] Día 9: Swagger, README completo, diagrama de BD, imagen en ghcr.io, release v1.0.0
- [ ] Día 10: extras (Prometheus/Grafana, despliegue)
