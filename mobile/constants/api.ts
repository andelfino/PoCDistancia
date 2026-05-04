/**
 * URL base del backend Sabore.
 *
 * Se lee desde la variable de entorno EXPO_PUBLIC_API_BASE_URL (ver .env.example).
 * Si no está definida, cae a localhost (útil en Expo Web durante desarrollo).
 *
 * En emulador Android usar http://10.0.2.2:8080
 * En dispositivo físico usar la IP LAN de la PC, ej. http://192.168.0.X:8080
 */
export const API_BASE_URL: string =
  process.env.EXPO_PUBLIC_API_BASE_URL ?? 'http://localhost:8080';
