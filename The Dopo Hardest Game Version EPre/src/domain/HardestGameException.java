package domain;

/**
 * Excepción personalizada del juego para errores de configuración y lógica.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class HardestGameException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Crea una excepción con un mensaje descriptivo.
     *
     * @param message descripción del error
     */
    public HardestGameException(final String message) {
        super(message);
    }

    /**
     * Crea una excepción con un mensaje y la causa original.
     *
     * @param message descripción del error
     * @param cause   causa original de la excepción
     */
    public HardestGameException(final String message, final Throwable cause) {
        super(message, cause);
    }
}