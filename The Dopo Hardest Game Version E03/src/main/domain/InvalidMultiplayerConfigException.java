package main.domain;

/**
 * Configuración de multijugador inválida (PvP/PvM).
 */
public class InvalidMultiplayerConfigException extends HardestGameException {

    private static final long serialVersionUID = 1L;

    public InvalidMultiplayerConfigException(final String message) {
        super(message, ErrorType.CONFIG_INVALID);
    }
}
