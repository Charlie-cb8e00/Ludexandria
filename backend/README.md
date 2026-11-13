# Backend: API y datos

El backend es el servidor de Ludexandria. Recibe peticiones del frontend, comprueba
la sesión del usuario, aplica las reglas y lee o guarda información en MySQL.

## Iniciar en Windows

Necesitas Java 17+, Maven y MySQL 8. Inicia MySQL y, desde una terminal en `backend`,
ejecuta:

```powershell
mvn spring-boot:run
```

La API estará en <http://localhost:8080> y su documentación interactiva en
<http://localhost:8080/swagger-ui.html>.

Para arrancar el frontend, sigue [su guía](../frontend/README.md).

## Configuración

Los valores de desarrollo están en
[`src/main/resources/application.properties`](./src/main/resources/application.properties).
Por defecto, MySQL usa `localhost`, usuario `root` y contraseña `root`. La app crea la
base `ludexandria` y las tablas que falten; no borra los datos existentes.
Estas credenciales predeterminadas son solo para desarrollo local.

En un servidor real, configura estos valores como variables de entorno:

| Variable | Qué cambia |
|---|---|
| `DATABASE_URL` | Dirección de MySQL |
| `DATABASE_USERNAME` y `DATABASE_PASSWORD` | Usuario y contraseña de MySQL |
| `JWT_SECRET` | Clave privada para las sesiones; usa una propia de 32 caracteres o más |
| `CORS_ALLOWED_ORIGINS` | Dirección del frontend permitida |
| `SERVER_PORT` | Puerto de la API; por defecto, `8080` |
| `JWT_EXPIRATION_MS` | Duración de sesión en milisegundos; por defecto, 24 horas |

El valor JWT predeterminado solo sirve para desarrollo. No uses ese valor al publicar
la app.

## Fuentes de juegos

- Steam se consulta al buscar y no necesita una clave.
- IGDB es opcional; requiere `IGDB_CLIENT_ID` y `IGDB_CLIENT_SECRET` de una aplicación
  de Twitch.
- Si una fuente falla, la búsqueda intenta mostrar resultados locales y avisos.
- No se descargan catálogos completos.

Las recomendaciones actuales seleccionan juegos pendientes de la biblioteca; no usan
una API de IA.

## Cómo encontrar el código

Piensa en una petición como una cadena:

```text
Controller → Service → DAO → MySQL
```

- `controller`: direcciones de la API y datos de entrada/salida.
- `service`: decisiones y reglas de la aplicación.
- `dao`: consultas SQL para guardar y leer datos.
- `model`: objetos principales, como `Juego` y `BibliotecaItem`.
- `dto`: formatos de petición y respuesta.
- `security`: inicio de sesión con JWT y permisos.
- `exception`: errores devueltos por la API.
- `src/main/resources/schema.sql`: tablas de la base de datos.

Ejemplo: la pantalla de búsqueda llama a `/api/juegos/buscar`; `JuegoController` recibe
la petición, `JuegoService` combina las fuentes y `JuegoDao` guarda los resultados.

## Direcciones más usadas

Todas requieren iniciar sesión, salvo registro, acceso y documentación Swagger.

| Método | Dirección | Uso |
|---|---|---|
| `POST` | `/api/auth/registro` | Crear cuenta |
| `POST` | `/api/auth/login` | Iniciar sesión |
| `GET` | `/api/juegos/buscar?q=Hades` | Buscar juegos |
| `GET` | `/api/juegos/{id}` | Ver ficha de un juego |
| `GET` | `/api/biblioteca` | Ver mi biblioteca |
| `GET` | `/api/biblioteca/juego/{idJuego}` | Ver mi estado y nota de un juego |
| `POST` | `/api/biblioteca` | Añadir un juego |
| `POST` | `/api/biblioteca/populares` | Añadir la selección de 50 juegos |
| `PUT` | `/api/biblioteca/{id}` | Cambiar estado, nota u horas |
| `DELETE` | `/api/biblioteca/{id}` | Quitar un juego |
| `POST` | `/api/importaciones` | Importar un archivo CSV |
| `POST` | `/api/recomendaciones` | Elegir juegos pendientes según el tiempo |

La consulta de biblioteca por juego responde `204` si el usuario aún no lo ha añadido.
Las direcciones `/api/admin/...` requieren el rol de administrador.
Las rutas de administración permiten listar usuarios, cambiar roles y consultar
estadísticas de uso.

## Formato del CSV

La cabecera es opcional. El orden de las columnas es:

```csv
nombre,plataforma,estado,nota,horas
Hades,PC,pendiente,9,12.5
Celeste,Switch,completado,10,8
```

Estados permitidos: `pendiente`, `jugando`, `completado` y `abandonado`.

## Pruebas

```powershell
mvn test
```

Si usas Java 27 y aparece un error de Byte Buddy, usa:

```powershell
mvn test "-Dnet.bytebuddy.experimental=true"
```
