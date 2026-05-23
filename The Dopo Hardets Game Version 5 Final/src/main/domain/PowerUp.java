package main.domain;

import java.awt.*;

/**
 * Clase abstracta que representa un power-up dentro del dominio del juego.
 * Los power-ups son objetos estáticos que el jugador puede recoger al tocarlos,
 * aplicando efectos inmediatos (positivos o negativos).
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public abstract class PowerUp implements GridEntity {
    protected int row;
    protected int col;
    protected boolean consumed;

    /**
     * Crea un power-up en la posición indicada en estado no consumido.
     *
     * @param row fila del tablero donde se ubica el power-up
     * @param col columna del tablero donde se ubica el power-up
     */
    public PowerUp(final int row, final int col) {
        super();
        this.row = row;
        this.col = col;
        this.consumed = false;
    }

    /**
     * Marca el power-up como consumido.
     */
    public void consume() {
        consumed = true;
    }

    /**
     * Restaura el power-up a estado no consumido.
     */
    public void reset() {
        consumed = false;
    }

    /**
     * Restaura el estado de consumo del power-up desde una partida guardada.
     *
     * @param wasConsumed indica si el power-up estaba consumido
     */
    void restoreSavedState(final boolean wasConsumed) {
        consumed = wasConsumed;
    }

    /**
     * Retorna el estado de consumo del power-up.
     *
     * @return true si el power-up ya fue consumido; false en caso contrario
     */
    public boolean isConsumed() {
        return consumed;
    }

    /**
     * Retorna la fila del tablero donde se encuentra el power-up.
     *
     * @return fila del power-up
     */
    @Override
    public int getRow() {
        return row;
    }

    /**
     * Retorna la columna del tablero donde se encuentra el power-up.
     *
     * @return columna del power-up
     */
    @Override
    public int getCol() {
        return col;
    }

    /**
     * Retorna el color asociado a la representación visual del power-up.
     *
     * @return color del power-up
     */
    @Override
    public abstract Color getColor();

    /**
     * Retorna el tipo de power-up como cadena de texto.
     *
     * @return tipo de power-up en mayúsculas
     */
    public abstract String getType();
}
