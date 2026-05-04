import { API_BASE_URL } from '../constants/api';

// ─── Tipos ────────────────────────────────────────────────────────────────────

export interface EstimarRutaResponse {
  origen: string;
  destino: string;
  destinoLat: number;
  destinoLon: number;
  distanciaMetros: number;
  distanciaKm: number;
  duracionSegundos: number;
  duracionMinutos: number;
}

// ─── Función principal ────────────────────────────────────────────────────────

/**
 * Llama al backend para obtener la distancia y duración estimada
 * desde Shopping Tres Cruces hasta la dirección ingresada por el cliente.
 *
 * El backend consume TomTom Search y Routing APIs internamente.
 * Nunca se expone la API Key en el frontend.
 */
export async function estimarRuta(direccionCliente: string): Promise<EstimarRutaResponse> {
  const url = `${API_BASE_URL}/api/rutas/estimar`;

  let response: Response;
  try {
    response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ direccionCliente }),
    });
  } catch {
    throw new Error('No se pudo conectar con el servidor. Verificá que el backend esté corriendo.');
  }

  if (!response.ok) {
    const errorBody = await response.json().catch(() => null);
    const mensaje = errorBody?.error ?? mensajePorEstado(response.status);
    throw new Error(mensaje);
  }

  return response.json() as Promise<EstimarRutaResponse>;
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

function mensajePorEstado(status: number): string {
  if (status === 400) return 'La dirección ingresada no es válida.';
  if (status === 404) return 'No se encontró la dirección. Intentá con más detalle.';
  if (status === 422) return 'No se pudo calcular la ruta para esa dirección.';
  if (status === 502) return 'Error al consultar el servicio de rutas. Intentá más tarde.';
  return 'Error inesperado. Intentá nuevamente.';
}
