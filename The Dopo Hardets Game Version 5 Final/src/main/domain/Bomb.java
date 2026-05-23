package main.domain;

import java.awt.*;

/**
 * Bomba que causa la muerte instantánea al jugador que la toca.
 * Es un objeto estático que permanece en el tablero hasta ser consumido.
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
     * Retorna el color negro de la bomba para su renderizado.
     *
     * @return color negro de la bomba (Color.BLACK)
     */
    @Override
    public Color getColor() {
        return Color.BLACK;
    }

    /**
     * Retorna el tipo de este power-up en formato de texto.
     *
     * @return tipo del power-up ("BOMB")
     */
    @Override
    public String getType() {
        return "BOMB";
    }
}
