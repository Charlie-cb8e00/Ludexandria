# Ludexandria Backend (Spring Boot)

API REST para el frontend React. MySQL + JDBC (`JdbcTemplate`) + JWT + Swagger.

Cumple el MVP del documento de planificación (Grupo G5):

- Registro / login con JWT
- CRUD de biblioteca personal (estado, nota, horas)
- Búsqueda bajo demanda en Steam e IGDB, con caché local de resultados
- Importación de biblioteca desde CSV
- Asistente de recomendaciones (basado en la biblioteca real; preparado para IA)
- Panel de administración básico (roles, stats, consumo IA)
- Esquema reducido a usuarios, juegos, biblioteca y uso del asistente

## Requisitos

- Java 17+
- Maven 3.9+
- MySQL 8

## Configuración

Edita `src/main/resources/application.properties` o usa variables de entorno:

```properties
spring.datasource.username=root
spring.datasource.password=root
app.jwt.secret=cambia-esta-clave-en-produccion
app.cors.allowed-origins=http://localhost:5173

# Opcional (IGDB / Twitch)
IGDB_CLIENT_ID=...
IGDB_CLIENT_SECRET=...

# Opcional (IA generativa)
IA_API_KEY=...
IA_BASE_URL=https://api.openai.com/v1
```

Al arrancar, `schema.sql` crea las tablas principales si no existen. Las instalaciones
anteriores conservan sus tablas de catálogos no utilizadas; no se eliminan ni se borran
datos automáticamente.

## Arranque

```bash
cd backend
mvn spring-boot:run
```

- API: http://localhost:8080  
- Swagger: http://localhost:8080/swagger-ui.html  

## Endpoints principales

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | `/api/auth/registro` | No | Alta `{ email, password, nombre? }` |
| POST | `/api/auth/login` | No | Login → `{ token, email, nombre, rol }` |
| GET | `/api/juegos?q=&page=&size=` | Sí | Buscar juegos |
| GET | `/api/juegos/buscar?q=&page=&size=` | Sí | Buscar juegos e informar avisos de fuentes; respuesta `{ juegos, advertencias }` |
| GET | `/api/juegos/fuentes` | Sí | Estado de disponibilidad de Steam e IGDB |
| GET | `/api/juegos/{id}` | Sí | Ficha |
| POST | `/api/juegos/demo` | Sí | Crear juego de prueba |
| GET | `/api/biblioteca?estado=` | Sí | Mi biblioteca |
| GET | `/api/biblioteca/juego/{idJuego}` | Sí | Estado y nota personal del juego (204 si no está en la biblioteca) |
| POST | `/api/biblioteca` | Sí | Añadir `{ idJuego, estado?, notaPersonal?, horasJugadas? }` |
| POST | `/api/biblioteca/populares` | Sí | Añadir 50 títulos populares con estado pendiente y 0 horas |
| PUT | `/api/biblioteca/{id}` | Sí | Actualizar estado/nota/horas |
| DELETE | `/api/biblioteca/{id}` | Sí | Quitar de la biblioteca |
| POST | `/api/importaciones` | Sí | Multipart `file` (CSV) |
| POST | `/api/recomendaciones` | Sí | `{ tiempo, plataforma?, animo? }` |
| GET | `/api/admin/usuarios` | ADMIN | Listar usuarios |
| PUT | `/api/admin/usuarios/{id}/rol` | ADMIN | Cambiar rol |
| GET | `/api/admin/consumo-ia` | ADMIN | Tokens por usuario |
| GET | `/api/admin/stats` | ADMIN | Contadores |

### Formato CSV de importación

```csv
nombre,plataforma,estado,nota,horas
Hades,PC,pendiente,9,12.5
Celeste,Switch,completado,10,8
```

Estados válidos: `pendiente`, `jugando`, `completado`, `abandonado`.

La pantalla de biblioteca permite importar el CSV o añadir una selección de 50 juegos
populares. La carga es idempotente, no duplica entradas existentes y rellena portadas
de Steam para los títulos disponibles en su catálogo.

La búsqueda consulta Steam bajo demanda y consulta IGDB cuando están configuradas
`IGDB_CLIENT_ID` e `IGDB_CLIENT_SECRET`. `/api/juegos/buscar` también devuelve avisos
si una fuente externa no está disponible. Solo los resultados de la búsqueda se guardan
en la caché local; no se descarga el catálogo completo de ningún proveedor.

## Estructura de capas

```
controller/   → Auth, Juego, Biblioteca, Recomendacion, Importacion, Admin
service/      → lógica de negocio + IgdbService
dao/          → JDBC + JdbcTemplate (Usuario, Juego, Biblioteca)
model/        → entidades
dto/          → request/response
security/     → JWT filter + JwtService
exception/    → ApiException + @ControllerAdvice
config/       → SecurityConfig (CORS, roles)
```

## Notas del enunciado

- Claves IGDB e IA solo en backend (variables de entorno).
- El frontend se ejecuta aparte; CORS ya está configurado.
- DAO con PreparedStatement vía JdbcTemplate.
- Pendiente de confirmar con el profesor: JDBC clásico puro vs JdbcTemplate (actualmente se usa JdbcTemplate).
