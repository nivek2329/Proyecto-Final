package main.domain;

/**
 * Movimiento o patrón de enemigo que viola restricciones del dominio.
 */
public class InvalidMovementException extends HardestGameException {

    private static final long serialVersionUID = 1L;

    public InvalidMovementException(final String message) {
        super(message, ErrorType.CONFIG_INVALID);
    }
}
