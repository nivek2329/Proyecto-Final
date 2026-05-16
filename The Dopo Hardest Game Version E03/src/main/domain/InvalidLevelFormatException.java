package main.domain;

/**
 * Formato de archivo de nivel inválido.
 */
public class InvalidLevelFormatException extends HardestGameException {

    private static final long serialVersionUID = 1L;

    public InvalidLevelFormatException(final String message) {
        super(message, ErrorType.CONFIG_INVALID);
    }

    public InvalidLevelFormatException(final String message, final Throwable cause) {
        super(message, ErrorType.CONFIG_INVALID, cause);
    }
}
