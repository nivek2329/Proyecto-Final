package main.domain;

/**
 * Acción no permitida en el estado actual del juego.
 */
public class InvalidGameStateException extends HardestGameException {

    private static final long serialVersionUID = 1L;

    public InvalidGameStateException(final String message) {
        super(message, ErrorType.GAME_LOGIC);
    }
}
