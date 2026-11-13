# Ludexandria

Ludexandria es una aplicación para guardar y organizar videojuegos. Puedes buscar
juegos, añadirlos a tu biblioteca, marcar su estado y guardar una nota personal.

## Empezar en Windows

Necesitas Java 17 o más reciente, Maven, Node.js con npm y MySQL 8.

1. Inicia el servidor de MySQL. En una instalación local se usan `root` y `root`;
   la base de datos `ludexandria` y sus tablas se crean al arrancar el backend.
2. Abre una terminal en la carpeta `backend` y ejecuta:

   ```powershell
   mvn spring-boot:run
   ```

3. Abre otra terminal en `frontend` y ejecuta:

   ```powershell
   npm ci
   npm run dev
   ```

4. Abre <http://localhost:5173> y crea una cuenta.

El backend escucha en <http://localhost:8080>. Para configurar contraseñas y
fuentes externas, consulta [la guía del backend](./backend/README.md).
Los valores `root` y `root` son solo para desarrollo local, no para publicar la app.

## Cómo se conectan las piezas

```text
Tu navegador
    ↓
Frontend React: muestra las páginas y envía peticiones
    ↓
API Spring Boot: comprueba la sesión y aplica las reglas
    ↓
MySQL: guarda usuarios, juegos y bibliotecas
```

El frontend envía las peticiones desde `frontend/src/api/client.js`. En el backend,
las rutas están en `controller`, las reglas en `service` y el acceso a MySQL en `dao`.

## Dónde encontrar cada cosa

| Carpeta | Qué contiene |
|---|---|
| `frontend/src/pages` | Pantallas: inicio, búsqueda, biblioteca y fichas |
| `frontend/src/components` | Piezas reutilizables, como las tarjetas |
| `frontend/src/api` | Conexión del frontend con el backend |
| `backend/src/main/java/.../controller` | Direcciones de la API |
| `backend/src/main/java/.../service` | Reglas de la aplicación |
| `backend/src/main/java/.../dao` | Consultas y cambios en MySQL |
| `backend/src/main/resources/schema.sql` | Tablas de la base de datos |

Cada parte tiene una guía propia: [frontend](./frontend/README.md) y
[backend](./backend/README.md).

## Catálogos externos

- **Steam:** búsquedas y portadas; no requiere una clave, pero sí conexión a Internet.
- **IGDB:** opcional. Para usarlo, configura las credenciales de Twitch que explica
  la guía del backend.
- **Wikipedia:** aporta información e imágenes adicionales a algunas fichas.
- **IA generativa:** todavía no está conectada; las recomendaciones actuales usan
  los juegos que ya tienes guardados.

## Comprobar cambios

Desde `backend`, ejecuta `mvn test`. Desde `frontend`, ejecuta `npm run lint` y
`npm run build`. Si usas Java 27 y Maven da un error de Byte Buddy, ejecuta
`mvn test "-Dnet.bytebuddy.experimental=true"`.
