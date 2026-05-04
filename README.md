# PoC distancia — estimación de ruta (Sabore)

Prueba de concepto: el **cliente** indica una dirección de entrega y el sistema estima **distancia y tiempo** del recorrido desde un origen fijo (**Shopping Tres Cruces**, Montevideo) usando la API de **TomTom**. Incluye un backend **Spring Boot** y una app **Expo (React Native)** que consume ese backend.

Repositorio: [github.com/andelfino/PoCDistancia](https://github.com/andelfino/PoCDistancia).

---

## Qué incluye y qué no

| Incluye | No es objetivo de esta PoC |
|--------|----------------------------|
| `POST /api/rutas/estimar` con geocodificación y ruta vía TomTom | Pedidos completos, pagos, usuarios finales |
| Pantalla mínima en mobile para probar el flujo | Persistencia de rutas en base de datos propia |

---

## Estructura del repositorio

```text
PoCdistancia/
├── backend/          # Spring Boot 3.2, Java 17, puerto 8080
│   ├── .env.example  # plantilla → copiar a .env (no subir .env)
│   └── src/...
└── mobile/           # Expo ~52, React Native
    ├── .env.example  # plantilla → copiar a .env si hace falta
    └── ...
```

Los archivos **`.env`** reales **no** están en Git (figuran en `.gitignore`). Cada persona que clone debe crear los suyos a partir de las plantillas **`.env.example`**.

---

## Requisitos previos

- **Backend:** [JDK 17](https://adoptium.net/) y [Maven](https://maven.apache.org/) (o usar el wrapper si lo agregás al proyecto).
- **Mobile:** [Node.js](https://nodejs.org/) (LTS recomendado) y [npm](https://www.npmjs.com/).
- Cuenta en [TomTom for Developers](https://developer.tomtom.com/) para obtener una **API key** (uso acotado a esta PoC).

---

## Configuración sensible (obligatoria en backend)

### Backend — `TOMTOM_API_KEY`

1. En la carpeta `backend/`, copiá `.env.example` como **`.env`**.
2. Reemplazá el placeholder por tu clave real de TomTom.

El arranque carga `backend/.env` si existe. Si **`TOMTOM_API_KEY`** ya está definida en el sistema operativo o en el IDE, ese valor **tiene prioridad** sobre el archivo `.env`.

**No subas** el archivo `.env` ni pegues la clave en `application.yml` ni en issues de GitHub.

### Mobile — `EXPO_PUBLIC_API_BASE_URL` (opcional según entorno)

1. Copiá `mobile/.env.example` a **`mobile/.env`** si necesitás cambiar la URL del backend.
2. Si no definís nada, la app usa por defecto `http://localhost:8080` (código en `mobile/constants/api.ts`).

Guía rápida de URLs:

| Dónde corre la app | URL típica del backend |
|--------------------|-------------------------|
| Expo Web en la misma PC | `http://localhost:8080` |
| Emulador Android | `http://10.0.2.2:8080` |
| Teléfono físico (misma Wi‑Fi) | `http://<IP-LAN-de-tu-PC>:8080` |

Después de cambiar `.env` en mobile, reiniciá el bundler de Expo (`npm start` de nuevo).

---

## Cómo ejecutar el backend

```bash
cd backend
mvn spring-boot:run
```

Servicio HTTP en **http://localhost:8080** (configurable en `application.yml`).

---

## Cómo ejecutar el mobile

```bash
cd mobile
npm install
npm start
```

Desde el menú de Expo podés abrir **Web**, **Android** o **iOS**. Asegurate de que la URL base apunte al backend que tengas accesible desde ese entorno (ver tabla anterior).

---

## API REST (PoC)

### `POST /api/rutas/estimar`

Estima la ruta desde el origen fijo de la PoC hasta la dirección del cliente.

**Headers:** `Content-Type: application/json`

**Cuerpo (JSON):**

```json
{
  "direccionCliente": "Av. 18 de Julio 1234, Montevideo"
}
```

**Respuesta (200):** JSON con campos como `origen`, `destino`, `destinoLat`, `destinoLon`, `distanciaMetros`, `distanciaKm`, `duracionSegundos`, `duracionMinutos` (ver `EstimarRutaResponse` en el código).

**Errores:** el backend devuelve cuerpos de error según los manejadores definidos en el proyecto (validación, dirección no encontrada, fallos de TomTom, etc.).

Ejemplo con `curl` (con el backend levantado):

```bash
curl -s -X POST http://localhost:8080/api/rutas/estimar ^
  -H "Content-Type: application/json" ^
  -d "{\"direccionCliente\": \"Av. 18 de Julio 1234, Montevideo\"}"
```

*(En bash/Linux/macOS usá `\` en lugar de `^` para partir líneas.)*

---

## CORS

El backend incluye configuración CORS para permitir llamadas desde el cliente web de Expo durante el desarrollo. Si cambiás orígenes o desplegás en otro dominio, revisá `CorsConfig` en el código.

---

## Licencia y créditos

Proyecto académico / PoC. Las respuestas de mapas y rutas dependen de los términos de uso de **TomTom**.

Si extendés este README, conviene mantener una sección explícita sobre **variables de entorno** y **no commitear secretos**, para quien clone por primera vez.
