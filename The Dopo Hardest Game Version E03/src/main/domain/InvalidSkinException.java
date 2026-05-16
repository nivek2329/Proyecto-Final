package main.domain;

/**
 * Skin no registrado en el sistema.
 */
public class InvalidSkinException extends HardestGameException {

    private static final long serialVersionUID = 1L;

    public InvalidSkinException(final String message) {
        super(message, ErrorType.CONFIG_INVALID);
    }
}
