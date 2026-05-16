package main.domain;

/**
 * Entidad o celda fuera de los límites del tablero.
 */
public class OutOfBoundsException extends HardestGameException {

    private static final long serialVersionUID = 1L;

    public OutOfBoundsException(final String message) {
        super(message, ErrorType.GAME_LOGIC);
    }
}
