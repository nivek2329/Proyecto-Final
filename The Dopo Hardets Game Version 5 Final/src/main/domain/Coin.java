package main.domain;

import java.awt.*;
import java.util.logging.Logger;
import main.domain.HardestGameException;

/**
 * Clase abstracta que representa una moneda dentro del dominio del juego.
 * Define la información básica común a todos los tipos de monedas, incluyendo
 * su posición en el tablero y su estado de recolección.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public abstract class Coin implements GridEntity {

    private static final Logger LOGGER = GameLog.getLogger(Coin.class);

    protected int row;
    protected int col;
    protected boolean collected;
    protected boolean permanentlyCollected;

    /**
     * Crea una moneda en la posición indicada en estado no recolectada.
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
     *
     * @throws HardestGameException si la moneda ya estaba recolectada
     */
    public void collect() throws HardestGameException {
        if (collected) {
            final String msg = "Moneda ya recolectada en (" + row + "," + col + ")";
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.COIN_ALREADY_COLLECTED);
        }
        collected = true;
    }

    /**
     * Marca la moneda como recolectada de forma permanente (mediante un checkpoint).
     * No volverá a aparecer aunque el jugador muera.
     */
    public void markPermanent() {
        permanentlyCollected = true;
        collected = true;
    }

    /**
     * Indica si la moneda quedó fijada permanentemente por un checkpoint intermedio.
     *
     * @return true si la moneda se recolectó permanentemente; false en caso contrario
     */
    public boolean isPermanentlyCollected() {
        return permanentlyCollected;
    }

    /**
     * Restaura la moneda a estado no recolectada, a menos que ya sea permanente.
     */
    public void reset() {
        if (!permanentlyCollected) {
            collected = false;
        }
    }

    /**
     * Restaura el estado de recolección de la moneda desde una partida guardada.
     *
     * @param wasCollected indica si estaba recolectada
     * @param wasPermanent indica si estaba recolectada permanentemente
     */
    public void restoreSavedState(final boolean wasCollected, final boolean wasPermanent) {
        permanentlyCollected = wasPermanent;
        collected = wasCollected;
    }

    /**
     * Retorna el estado de recolección de la moneda.
     *
     * @return true si la moneda ya fue recolectada; false en caso contrario
     */
    public boolean isCollected() { 
        return collected; 
    }

    /**
     * Retorna la fila del tablero donde se encuentra la moneda.
     *
     * @return fila de la moneda
     */
    @Override
    public int getRow() { 
        return row; 
    }

    /**
     * Retorna la columna del tablero donde se encuentra la moneda.
     *
     * @return columna de la moneda
     */
    @Override
    public int getCol() { 
        return col; 
    }

    /**
     * Retorna el color asociado a la representación visual de la moneda.
     *
     * @return color de la moneda
     */
    @Override
    public abstract Color getColor();

    /**
     * Retorna el tipo de moneda como cadena de texto.
     *
     * @return tipo de moneda en mayúsculas
     */
    public abstract String getType();
}