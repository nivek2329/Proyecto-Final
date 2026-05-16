package main.domain;

/**
 * Falta una entidad obligatoria en la configuración del nivel.
 */
public class MissingEntityException extends HardestGameException {

    private static final long serialVersionUID = 1L;

    public MissingEntityException(final String message) {
        super(message, ErrorType.CONFIG_INVALID);
    }
}
