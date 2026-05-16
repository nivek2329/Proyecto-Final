package main.domain;

/**
 * Archivo de nivel no encontrado.
 */
public class LevelNotFoundException extends HardestGameException {

    private static final long serialVersionUID = 1L;

    public LevelNotFoundException(final String message, final Throwable cause) {
        super(message, ErrorType.CONFIG_IO, cause);
    }
}
