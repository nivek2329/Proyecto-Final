package main.domain;

/**
 * Sin permisos de lectura sobre el archivo o directorio del nivel.
 */
public class LevelAccessDeniedException extends HardestGameException {

    private static final long serialVersionUID = 1L;

    public LevelAccessDeniedException(final String message, final Throwable cause) {
        super(message, ErrorType.CONFIG_IO, cause);
    }
}
