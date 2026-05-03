package domain;

import java.awt.Color;

/**
 * Clase abstracta que representa una moneda dentro del dominio del juego.
 * Define la información básica común a todos los tipos de monedas, incluyendo
 * su posición en el tablero y su estado de recolección.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public abstract class Coin {

    /** Fila del tablero donde se ubica la moneda. */
    protected int row;

    /** Columna del tablero donde se ubica la moneda. */
    protected int col;

    /** Estado de recolección de la moneda. */
    protected boolean collected;

    /**
     * Crea una moneda en la posición indicada en estado no recolectado.
     *
     * @param row fila del tablero donde se ubica la moneda
     * @param col columna del tablero donde se ubica la moneda
     */
    public Coin(final int row, final int col) {
        super();
        this.row       = row;
        this.col       = col;
        this.collected = false;
    }

    /**
     * Marca la moneda como recolectada.
     */
    public void collect() { collected = true; }

    /**
     * Restaura la moneda a estado no recolectado.
     */
    public void reset() { collected = false; }

    /**
     * Retorna el estado de recolección de la moneda.
     *
     * @return true si la moneda ya fue recolectada
     */
    public boolean isCollected() { return collected; }

    /**
     * Retorna la fila del tablero donde se encuentra la moneda.
     *
     * @return fila de la moneda
     */
    public int getRow() { return row; }

    /**
     * Retorna la columna del tablero donde se encuentra la moneda.
     *
     * @return columna de la moneda
     */
    public int getCol() { return col; }

    /**
     * Retorna el color asociado a la representación visual de la moneda.
     *
     * @return color de la moneda
     */
    public abstract Color getColor();

    /**
     * Retorna el tipo de moneda como cadena de texto.
     *
     * @return tipo de moneda
     */
    public abstract String getType();
}