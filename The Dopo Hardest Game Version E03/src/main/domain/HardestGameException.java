package main.domain;

/**
 * Excepción personalizada del juego para errores de configuración y lógica.
 * Incluye categorización por tipo de error para facilitar el diagnóstico
 * y el registro estructurado en los logs del programador.
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
}