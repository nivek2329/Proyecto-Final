package main.domain;

/**
 * Excepción personalizada del juego para todos los errores de configuración, lógica y ejecución.
 * Centraliza los mensajes de error del juego en español mediante constantes de texto.
 *
 * @author Estudiante de DOPO
 * @version 2026-1
 */
public class HardestGameException extends Exception {

    /** Constante para error de moneda ya recolectada. */
    public static final String COIN_ALREADY_COLLECTED = "Intento de recolectar una moneda ya recolectada.";
    /** Constante para error de partida activa durante carga. */
    public static final String GAME_ALREADY_RUNNING = "Intento de cargar un nivel nuevo mientras hay una partida activa.";
    /** Constante para error de tiempo insuficiente en configuración. */
    public static final String INSUFFICIENT_TIME = "Tiempo límite del nivel menor al mínimo permitido.";
    /** Constante para error de operación en estado de juego inválido. */
    public static final String INVALID_GAME_STATE = "Solicitud de movimiento u operación en estado de juego inválido.";
    /** Constante para error de formato de nivel incorrecto. */
    public static final String INVALID_LEVEL_FORMAT = "Formato o sintaxis de archivo de nivel incorrecta.";
    /** Constante para error de movimiento o patrón de enemigo no permitido. */
    public static final String INVALID_MOVEMENT = "Movimiento o patrón de enemigo que viola restricciones del dominio.";
    /** Constante para error de configuración de multijugador. */
    public static final String INVALID_MULTIPLAYER_CONFIG = "Configuración del modo multijugador inválida.";
    /** Constante para error de skin inválida. */
    public static final String INVALID_SKIN = "Skin no registrado o de formato incorrecto.";
    /** Constante para error de acceso denegado a archivo de nivel. */
    public static final String LEVEL_ACCESS_DENIED = "Sin permisos de lectura sobre el archivo o directorio del nivel.";
    /** Constante para error de archivo de nivel no encontrado. */
    public static final String LEVEL_NOT_FOUND = "Archivo de nivel no encontrado.";
    /** Constante para error de falta de entidad necesaria en nivel. */
    public static final String MISSING_ENTITY = "Falta alguna entidad requerida en la configuración del nivel (ej. SAFE_START).";
    /** Constante para error de coordenadas fuera de límites. */
    public static final String OUT_OF_BOUNDS = "Coordenadas fuera de las dimensiones del tablero.";

    /**
     * Crea una excepción con un mensaje descriptivo.
     *
     * @param message descripción del error
     */
    public HardestGameException(final String message) {
        super(message);
    }

    /**
     * Crea una excepción con un mensaje y la causa original.
     *
     * @param message descripción del error
     * @param cause   causa original de la excepción
     */
    public HardestGameException(final String message, final Throwable cause) {
        super(message, cause);
    }
}