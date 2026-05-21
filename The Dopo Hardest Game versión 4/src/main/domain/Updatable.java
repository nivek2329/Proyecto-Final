package main.domain;

/**
 * Representa cualquier componente o entidad lógica del juego cuyo estado
 * debe actualizarse o recibir notificaciones en cada tick de juego.
 */
@FunctionalInterface
public interface Updatable {

    /**
     * Actualiza el estado lógico de la entidad interactuando con el tablero.
     *
     * @param board tablero del juego
     */
    void update(Board board);
}
