import React, { useState } from 'react';
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { estimarRuta, EstimarRutaResponse } from '../services/rutasService';

// ─── Pantalla principal ───────────────────────────────────────────────────────

export default function PocEstimarRutaScreen() {
  const [direccion, setDireccion] = useState('');
  const [cargando, setCargando] = useState(false);
  const [resultado, setResultado] = useState<EstimarRutaResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleEstimar = async () => {
    if (!direccion.trim()) {
      setError('Ingresá una dirección para estimar.');
      setResultado(null);
      return;
    }

    setCargando(true);
    setError(null);
    setResultado(null);

    try {
      const data = await estimarRuta(direccion.trim());
      setResultado(data);
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Error inesperado.');
    } finally {
      setCargando(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={styles.flex}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      <ScrollView
        contentContainerStyle={styles.container}
        keyboardShouldPersistTaps="handled"
      >
        {/* Encabezado */}
        <Text style={styles.titulo}>Estimación de ruta</Text>
        <Text style={styles.subtitulo}>PoC · TomTom API</Text>

        {/* Origen fijo (informativo) */}
        <View style={styles.tarjetaOrigen}>
          <Text style={styles.origenEtiqueta}>Origen</Text>
          <Text style={styles.origenValor}>Shopping Tres Cruces, Montevideo, Uruguay</Text>
        </View>

        {/* Formulario */}
        <View style={styles.seccion}>
          <Text style={styles.label}>Dirección del cliente</Text>
          <TextInput
            style={styles.input}
            placeholder="Ej: Av. 18 de Julio 1234, Montevideo"
            placeholderTextColor="#9ca3af"
            value={direccion}
            onChangeText={setDireccion}
            onSubmitEditing={handleEstimar}
            returnKeyType="send"
            autoCorrect={false}
            autoCapitalize="sentences"
            editable={!cargando}
          />

          <Pressable
            style={({ pressed }) => [
              styles.boton,
              cargando && styles.botonDeshabilitado,
              pressed && styles.botonPresionado,
            ]}
            onPress={handleEstimar}
            disabled={cargando}
          >
            {cargando ? (
              <ActivityIndicator color="#fff" />
            ) : (
              <Text style={styles.botonTexto}>Calcular ruta</Text>
            )}
          </Pressable>
        </View>

        {/* Error */}
        {error && (
          <View style={styles.tarjetaError}>
            <Text style={styles.errorTexto}>⚠ {error}</Text>
          </View>
        )}

        {/* Resultado */}
        {resultado && (
          <View style={styles.tarjetaResultado}>
            <Text style={styles.resultadoTitulo}>Resultado</Text>

            <FilaResultado etiqueta="Origen" valor={resultado.origen} />
            <FilaResultado etiqueta="Destino" valor={resultado.destino} />

            <View style={styles.separador} />

            <FilaResultado
              etiqueta="Distancia"
              valor={`${resultado.distanciaKm.toFixed(2)} km`}
              destacado
            />
            <FilaResultado
              etiqueta="Duración estimada"
              valor={`${resultado.duracionMinutos} min`}
              destacado
            />

            <View style={styles.separador} />

            <FilaResultado
              etiqueta="Coordenadas destino"
              valor={`${resultado.destinoLat.toFixed(6)}, ${resultado.destinoLon.toFixed(6)}`}
            />
          </View>
        )}
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

// ─── Componente auxiliar ──────────────────────────────────────────────────────

function FilaResultado({
  etiqueta,
  valor,
  destacado = false,
}: {
  etiqueta: string;
  valor: string;
  destacado?: boolean;
}) {
  return (
    <View style={styles.fila}>
      <Text style={styles.filaEtiqueta}>{etiqueta}</Text>
      <Text style={[styles.filaValor, destacado && styles.filaValorDestacado]}>
        {valor}
      </Text>
    </View>
  );
}

// ─── Estilos ──────────────────────────────────────────────────────────────────

const styles = StyleSheet.create({
  flex: {
    flex: 1,
    backgroundColor: '#f3f4f6',
  },
  container: {
    padding: 24,
    paddingTop: 24,
    flexGrow: 1,
  },

  // Encabezado
  titulo: {
    fontSize: 26,
    fontWeight: '700',
    color: '#111827',
    marginBottom: 4,
  },
  subtitulo: {
    fontSize: 13,
    color: '#6b7280',
    marginBottom: 24,
  },

  // Tarjeta de origen fijo
  tarjetaOrigen: {
    backgroundColor: '#eff6ff',
    borderWidth: 1,
    borderColor: '#bfdbfe',
    borderRadius: 10,
    padding: 14,
    marginBottom: 20,
  },
  origenEtiqueta: {
    fontSize: 11,
    fontWeight: '600',
    color: '#3b82f6',
    textTransform: 'uppercase',
    letterSpacing: 0.8,
    marginBottom: 4,
  },
  origenValor: {
    fontSize: 14,
    color: '#1e40af',
    fontWeight: '500',
  },

  // Formulario
  seccion: {
    marginBottom: 16,
  },
  label: {
    fontSize: 14,
    fontWeight: '600',
    color: '#374151',
    marginBottom: 8,
  },
  input: {
    borderWidth: 1,
    borderColor: '#d1d5db',
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 13,
    fontSize: 15,
    color: '#111827',
    backgroundColor: '#fff',
    marginBottom: 12,
  },
  boton: {
    backgroundColor: '#2563eb',
    borderRadius: 10,
    paddingVertical: 14,
    alignItems: 'center',
  },
  botonDeshabilitado: {
    opacity: 0.6,
  },
  botonPresionado: {
    opacity: 0.85,
  },
  botonTexto: {
    color: '#fff',
    fontWeight: '700',
    fontSize: 16,
  },

  // Error
  tarjetaError: {
    backgroundColor: '#fef2f2',
    borderWidth: 1,
    borderColor: '#fca5a5',
    borderRadius: 10,
    padding: 14,
    marginBottom: 16,
  },
  errorTexto: {
    color: '#b91c1c',
    fontSize: 14,
    lineHeight: 20,
  },

  // Resultado
  tarjetaResultado: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 18,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.08,
    shadowRadius: 6,
    elevation: 2,
  },
  resultadoTitulo: {
    fontSize: 15,
    fontWeight: '700',
    color: '#111827',
    marginBottom: 14,
  },
  separador: {
    height: 1,
    backgroundColor: '#f3f4f6',
    marginVertical: 8,
  },
  fila: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    paddingVertical: 7,
  },
  filaEtiqueta: {
    fontSize: 13,
    color: '#6b7280',
    flex: 1,
    marginRight: 12,
  },
  filaValor: {
    fontSize: 13,
    color: '#374151',
    fontWeight: '500',
    flex: 2,
    textAlign: 'right',
  },
  filaValorDestacado: {
    fontSize: 15,
    fontWeight: '700',
    color: '#111827',
  },
});
