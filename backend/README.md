# Ludexandria Backend (Spring Boot)

API REST para el frontend React. MySQL + JDBC (JdbcTemplate) + JWT.

## Requisitos
- Java 17+
- Maven 3.9+ (o el wrapper de IntelliJ)
- MySQL 8 en marcha

## Configuración
Edita `src/main/resources/application.properties`:
- `spring.datasource.username` / `password`
- Opcional: variables `IGDB_CLIENT_ID`, `IGDB_CLIENT_SECRET`, `IA_API_KEY`

Al arrancar, `schema.sql` crea las tablas si no existen (con `createDatabaseIfNotExist=true` en la URL).

Si las tablas no se crean solas, ejecuta a mano el contenido de `schema.sql` en MySQL.

## Arranque en IntelliJ
1. File → Open → carpeta `ludexandria-backend`
2. Espera a que indexe Maven
3. Ejecuta `LudexandriaApplication`
4. Swagger: http://localhost:8080/swagger-ui.html

## Endpoints principales
- POST `/api/auth/registro` `{ "email", "password", "nombre" }`
- POST `/api/auth/login` `{ "email", "password" }` → `{ token, email, nombre, rol }`
- GET `/api/juegos?q=&page=0&size=20` (Bearer)
- GET `/api/juegos/{id}`
- POST `/api/juegos/demo` `{ "nombre": "Hades" }` (datos de prueba)
- GET `/api/biblioteca?estado=pendiente`
- POST `/api/biblioteca` `{ "idJuego", "estado" }`
- PUT `/api/biblioteca/{id}`
- DELETE `/api/biblioteca/{id}`
- POST `/api/recomendaciones` `{ "tiempo", "plataforma", "animo" }`

## Conectar con el frontend
En `frontend/src/api/client.js` ya apunta a `http://localhost:8080/api`.
Tras login real, guarda `data.token` en `localStorage`.
