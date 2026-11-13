# Frontend

Esta carpeta contiene las pantallas que ves en el navegador. Está hecho con React
y Vite; obtiene y guarda datos mediante la API de Spring Boot.

## Iniciar

Desde una terminal en `frontend`:

```powershell
npm ci
npm run dev
```

Abre <http://localhost:5173>. El backend debe estar activo en
<http://localhost:8080>.

## Comandos útiles

| Comando | Para qué sirve |
|---|---|
| `npm run dev` | Inicia la app para trabajar en local |
| `npm run lint` | Busca errores de estilo y problemas comunes |
| `npm run build` | Prepara los archivos para publicar |
| `npm run preview` | Muestra la versión preparada para publicar |

## Cómo está organizado

- `src/pages`: una pantalla por sección, como biblioteca o búsqueda.
- `src/components`: elementos que se reutilizan en varias pantallas.
- `src/api/client.js`: dirección del backend y envío automático del token de sesión.
- `src/App.jsx`: direcciones de las pantallas y páginas que requieren iniciar sesión.
- `src/index.css` y `src/App.css`: estilos.
- `public`: imágenes y archivos servidos tal cual.

Para entender una petición, busca la pantalla que la inicia y sigue la llamada a
`api.get`, `api.post` o `api.put`. El cliente añade el token automáticamente.
La dirección de la API está al principio de `src/api/client.js`; por defecto es
`http://localhost:8080/api`.

## Pantallas

`/login`, `/register`, `/`, `/buscar`, `/biblioteca`, `/juegos/:id`, `/importar`,
`/asistente`, `/perfil` y `/admin`.

La búsqueda usa Steam y, si está configurado, IGDB. Las fichas pueden consultar
Wikipedia para completar la información. No se descargan catálogos completos.

## Pendiente y mejoras futuras

### Limitaciones actuales

- La búsqueda consulta juegos cuando alguien los busca; no descarga todo el catálogo
  de Steam ni de otros servicios. GOG todavía no está integrado.
- IGDB es opcional y necesita las credenciales de Twitch indicadas en la guía del
  backend. Las portadas remotas dependen de que Steam o IGDB estén accesibles desde
  la red del usuario; si una imagen falla, se muestra una imagen de reemplazo.
- El asistente actual no usa un modelo de IA: recomienda hasta cinco juegos
  pendientes según el tiempo y la duración conocida. El ánimo y la plataforma que
  se escriben solo aparecen en el motivo; todavía no cambian la selección.
- No hay pruebas automatizadas propias del frontend. Por ahora se puede comprobar
  el código con `npm run lint` y crear una compilación con `npm run build`.

### Optimizaciones que podemos abordar después

1. Añadir pruebas de interfaz para iniciar sesión, importar CSV, guardar juegos y
   mostrar errores de carga o de imágenes.
2. Incorporar paginación en el servidor para bibliotecas y búsquedas grandes, y
   cargar más resultados solo cuando la persona los solicite.
3. Medir las consultas lentas y añadir índices en la base de datos solo donde las
   mediciones demuestren que hacen falta.
4. Mejorar la accesibilidad con navegación por teclado, etiquetas claras y
   comprobaciones de contraste.
5. Añadir límites y mensajes claros cuando un proveedor externo tarde demasiado
   o no esté disponible.

### Plan para integrar Gemini

Cuando tengamos una clave de Gemini:

1. Guardarla en el backend como variable de entorno (por ejemplo,
   `GEMINI_API_KEY`); nunca incluirla en React, en este README ni en el repositorio.
2. Hacer que el backend llame a Gemini. El navegador debe seguir llamando a nuestra
   API, para proteger la clave y poder controlar errores y tiempos de espera.
3. Enviar solo la información necesaria para recomendar —por ejemplo, los juegos
   pendientes y las preferencias indicadas— y no enviar datos personales.
4. Pedir una respuesta estructurada, comprobar en el backend que los juegos
   recomendados pertenecen a la biblioteca del usuario y mostrar un mensaje útil si
   Gemini no responde.
5. Mantener una alternativa local para que el asistente siga funcionando si la API
   no está configurada o no está disponible, y probar ambos caminos con tests.
