package domain;

import java.awt.Color;

/**
 * Clase abstracta que representa a un enemigo dentro del dominio del juego.
 * Define la información básica común a todos los enemigos, como su posición
 * en el tablero, así como las operaciones para actualizar su comportamiento
 * y detectar colisiones con el jugador.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public abstract class Enemy {

    /** Fila actual del enemigo en el tablero. */
    protected int row;

    /** Columna actual del enemigo en el tablero. */
    protected int col;

    /**
     * Crea un enemigo en la posición indicada.
     *
     * @param row fila inicial del enemigo en el tablero
     * @param col columna inicial del enemigo en el tablero
     */
    public Enemy(final int row, final int col) {
        super();
        this.row = row;
        this.col = col;
    }

    /**
     * Actualiza la posición y comportamiento del enemigo.
     *
     * @param board tablero sobre el cual se actualiza el enemigo
     */
    public abstract void update(Board board);

    /**
     * Retorna la fila actual del enemigo en el tablero.
     *
     * @return fila del enemigo
     */
    public int getRow() { return row; }

    /**
     * Retorna la columna actual del enemigo en el tablero.
     *
     * @return columna del enemigo
     */
    public int getCol() { return col; }

    /**
     * Determina si el enemigo colisiona con el jugador.
     *
     * @param player jugador con el cual se evalúa la colisión
     * @return true si el enemigo está en la misma celda que el jugador
     */
    public boolean collidesWith(final Player player) {
        return row == player.getRow() && col == player.getCol();
    }

    /**
     * Retorna el color asociado a la representación visual del enemigo.
     *
     * @return color del enemigo
     */
    public abstract Color getColor();
}