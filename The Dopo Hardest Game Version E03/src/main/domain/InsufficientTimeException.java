package main.domain;

/**
 * Tiempo límite del nivel menor al mínimo permitido.
 */
public class InsufficientTimeException extends HardestGameException {

    private static final long serialVersionUID = 1L;

    public InsufficientTimeException(final String message) {
        super(message, ErrorType.CONFIG_INVALID);
    }
}
