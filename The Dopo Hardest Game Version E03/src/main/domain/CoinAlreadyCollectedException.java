package main.domain;

/**
 * Intento de recolectar una moneda ya recolectada.
 */
public class CoinAlreadyCollectedException extends HardestGameException {

    private static final long serialVersionUID = 1L;

    public CoinAlreadyCollectedException(final String message) {
        super(message, ErrorType.GAME_LOGIC);
    }
}
