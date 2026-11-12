# Ludexandria Backend (Spring Boot)

API REST para el frontend React. MySQL + JDBC (`JdbcTemplate`) + JWT + Swagger.

Cumple el MVP del documento de planificación (Grupo G5):

- Registro / login con JWT
- CRUD de biblioteca personal (estado, nota, horas)
- Búsqueda y ficha de juegos (caché local; preparado para IGDB)
- Importación de biblioteca desde CSV
- Asistente de recomendaciones (basado en la biblioteca real; preparado para IA)
- Panel de administración básico (roles, stats, consumo IA)
- Esquema normalizado (género, tema, plataforma, compañía y relaciones N:M)

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

Al arrancar, `schema.sql` crea las tablas si no existen.

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
| GET | `/api/juegos/{id}` | Sí | Ficha |
| POST | `/api/juegos/demo` | Sí | Crear juego de prueba |
| GET | `/api/biblioteca?estado=` | Sí | Mi biblioteca |
| POST | `/api/biblioteca` | Sí | Añadir `{ idJuego, estado?, notaPersonal?, horasJugadas? }` |
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
