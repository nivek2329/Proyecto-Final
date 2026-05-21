package main.domain;

import java.awt.*;

/**
 * Bomba que mata instantáneamente al jugador que la toca.
 * Objeto estático que permanece en el tablero hasta ser recogido,
 * aplicando efecto de muerte inmediata.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class Bomb extends PowerUp {

    /**
     * Crea una bomba en la posición indicada.
     *
     * @param row fila del tablero donde se ubica la bomba
     * @param col columna del tablero donde se ubica la bomba
     */
    public Bomb(final int row, final int col) {
        super(row, col);
    }

    /**
     * Retorna el color negro de la bomba.
     *
     * @return Color.BLACK
     */
    @Override
    public Color getColor() {
        return Color.BLACK;
    }

    /**
     * Retorna el tipo de power-up como cadena de texto.
     *
     * @return "BOMB"
     */
    @Override
    public String getType() {
        return "BOMB";
    }
}
