package main.domain;

/**
 * Intento de cargar un nivel mientras una partida ya está en curso.
 */
public class GameAlreadyRunningException extends HardestGameException {

    private static final long serialVersionUID = 1L;

    public GameAlreadyRunningException(final String message) {
        super(message, ErrorType.GAME_LOGIC);
    }
}
