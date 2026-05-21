package main.domain;

/**
 * Excepción personalizada del juego para errores de configuración y lógica.
 * Incluye categorización por tipo de error para facilitar el diagnóstico
 * y el registro estructurado en los logs del programador.
 *
 * Esta clase centraliza todas las excepciones del dominio como clases estáticas anidadas.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class HardestGameException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Categorías de error del juego, usadas para clasificar el origen
     * del problema en los logs y en el reporte del usuario.
     */
    public enum ErrorType {
        /** Error al leer o parsear el archivo de configuración del nivel. */
        CONFIG_IO,
        /** Los datos del archivo de configuración son inválidos o incompletos. */
        CONFIG_INVALID,
        /** Error en la lógica interna del juego durante la ejecución. */
        GAME_LOGIC,
        /** Error no clasificado o inesperado. */
        UNKNOWN
    }

    private final ErrorType errorType;

    /**
     * Crea una excepción con un mensaje descriptivo.
     * El tipo de error se establece como {@link ErrorType#UNKNOWN}.
     *
     * @param message descripción del error
     */
    public HardestGameException(final String message) {
        super(message);
        this.errorType = ErrorType.UNKNOWN;
    }

    /**
     * Crea una excepción con un mensaje y la causa original.
     * El tipo de error se establece como {@link ErrorType#UNKNOWN}.
     *
     * @param message descripción del error
     * @param cause   causa original de la excepción
     */
    public HardestGameException(final String message, final Throwable cause) {
        super(message, cause);
        this.errorType = ErrorType.UNKNOWN;
    }

    /**
     * Crea una excepción con un mensaje y un tipo de error específico.
     *
     * @param message   descripción del error
     * @param errorType categoría del error
     */
    public HardestGameException(final String message, final ErrorType errorType) {
        super(message);
        this.errorType = errorType != null ? errorType : ErrorType.UNKNOWN;
    }

    /**
     * Crea una excepción con mensaje, tipo de error y causa original.
     *
     * @param message   descripción del error
     * @param errorType categoría del error
     * @param cause     causa original de la excepción
     */
    public HardestGameException(final String message, final ErrorType errorType,
                                final Throwable cause) {
        super(message, cause);
        this.errorType = errorType != null ? errorType : ErrorType.UNKNOWN;
    }

    /**
     * Retorna la categoría del error para clasificación en logs y reportes.
     *
     * @return tipo de error de esta excepción
     */
    public ErrorType getErrorType() {
        return errorType;
    }

    /**
     * Retorna una representación textual completa de la excepción,
     * incluyendo el tipo de error, el mensaje y la causa si existe.
     *
     * @return cadena con el detalle completo del error
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[").append(errorType).append("] ");
        sb.append(getMessage());
        if (getCause() != null) {
            sb.append(" | Causa: ").append(getCause().getMessage());
        }
        return sb.toString();
    }

    // =========================================================================
    // EXCEPCIONES ANIDADAS UNIFICADAS
    // =========================================================================

    /**
     * Intento de recolectar una moneda ya recolectada.
     */
    public static class CoinAlreadyCollectedException extends HardestGameException {
        private static final long serialVersionUID = 1L;
        /**
         * Crea la excepción con el mensaje indicado.
         *
         * @param message mensaje descriptivo del error
         */
        public CoinAlreadyCollectedException(final String message) {
            super(message, ErrorType.GAME_LOGIC);
        }
    }

    /**
     * Intento de cargar un nivel nuevo mientras hay una partida activa.
     */
    public static class GameAlreadyRunningException extends HardestGameException {
        private static final long serialVersionUID = 1L;
        /**
         * Crea la excepción con el mensaje indicado.
         *
         * @param message mensaje descriptivo del error
         */
        public GameAlreadyRunningException(final String message) {
            super(message, ErrorType.GAME_LOGIC);
        }
    }

    /**
     * Tiempo límite del nivel menor al mínimo permitido.
     */
    public static class InsufficientTimeException extends HardestGameException {
        private static final long serialVersionUID = 1L;
        /**
         * Crea la excepción con el mensaje indicado.
         *
         * @param message mensaje descriptivo del error
         */
        public InsufficientTimeException(final String message) {
            super(message, ErrorType.CONFIG_INVALID);
        }
    }

    /**
     * Solicitud de movimiento u operación en estado de juego inválido.
     */
    public static class InvalidGameStateException extends HardestGameException {
        private static final long serialVersionUID = 1L;
        /**
         * Crea la excepción con el mensaje indicado.
         *
         * @param message mensaje descriptivo del error
         */
        public InvalidGameStateException(final String message) {
            super(message, ErrorType.GAME_LOGIC);
        }
    }

    /**
     * Formato o sintaxis de archivo de nivel incorrecta.
     */
    public static class InvalidLevelFormatException extends HardestGameException {
        private static final long serialVersionUID = 1L;
        /**
         * Crea la excepción con el mensaje indicado.
         *
         * @param message mensaje descriptivo del error
         */
        public InvalidLevelFormatException(final String message) {
            super(message, ErrorType.CONFIG_INVALID);
        }
        /**
         * Crea la excepción con mensaje y causa original.
         *
         * @param message mensaje descriptivo
         * @param cause   causa del error
         */
        public InvalidLevelFormatException(final String message, final Throwable cause) {
            super(message, ErrorType.CONFIG_INVALID, cause);
        }
    }

    /**
     * Movimiento o patrón de enemigo que viola restricciones del dominio.
     */
    public static class InvalidMovementException extends HardestGameException {
        private static final long serialVersionUID = 1L;
        /**
         * Crea la excepción con el mensaje indicado.
         *
         * @param message mensaje descriptivo del error
         */
        public InvalidMovementException(final String message) {
            super(message, ErrorType.CONFIG_INVALID);
        }
    }

    /**
     * Configuración del modo multijugador inválida.
     */
    public static class InvalidMultiplayerConfigException extends HardestGameException {
        private static final long serialVersionUID = 1L;
        /**
         * Crea la excepción con el mensaje indicado.
         *
         * @param message mensaje descriptivo del error
         */
        public InvalidMultiplayerConfigException(final String message) {
            super(message, ErrorType.CONFIG_INVALID);
        }
    }

    /**
     * Skin no registrado o de formato incorrecto.
     */
    public static class InvalidSkinException extends HardestGameException {
        private static final long serialVersionUID = 1L;
        /**
         * Crea la excepción con el mensaje indicado.
         *
         * @param message mensaje descriptivo del error
         */
        public InvalidSkinException(final String message) {
            super(message, ErrorType.CONFIG_INVALID);
        }
    }

    /**
     * Sin permisos de lectura sobre el archivo o directorio del nivel.
     */
    public static class LevelAccessDeniedException extends HardestGameException {
        private static final long serialVersionUID = 1L;
        /**
         * Crea la excepción con mensaje y causa original.
         *
         * @param message mensaje descriptivo
         * @param cause   causa del error
         */
        public LevelAccessDeniedException(final String message, final Throwable cause) {
            super(message, ErrorType.CONFIG_IO, cause);
        }
    }

    /**
     * Archivo de nivel no encontrado.
     */
    public static class LevelNotFoundException extends HardestGameException {
        private static final long serialVersionUID = 1L;
        /**
         * Crea la excepción con mensaje y causa original.
         *
         * @param message mensaje descriptivo
         * @param cause   causa del error
         */
        public LevelNotFoundException(final String message, final Throwable cause) {
            super(message, ErrorType.CONFIG_IO, cause);
        }
    }

    /**
     * Falta alguna entidad requerida en la configuración del nivel (ej. SAFE_START).
     */
    public static class MissingEntityException extends HardestGameException {
        private static final long serialVersionUID = 1L;
        /**
         * Crea la excepción con el mensaje indicado.
         *
         * @param message mensaje descriptivo del error
         */
        public MissingEntityException(final String message) {
            super(message, ErrorType.CONFIG_INVALID);
        }
    }

    /**
     * Coordenadas fuera de las dimensiones del tablero.
     */
    public static class OutOfBoundsException extends HardestGameException {
        private static final long serialVersionUID = 1L;
        /**
         * Crea la excepción con el mensaje indicado.
         *
         * @param message mensaje descriptivo del error
         */
        public OutOfBoundsException(final String message) {
            super(message, ErrorType.GAME_LOGIC);
        }
    }
}