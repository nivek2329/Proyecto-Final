package domain;

import java.awt.Color;

/**
 * Clase concreta que representa al jugador principal de color rojo.
 * Define las características visuales e identidad específicas del jugador.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class RedPlayer extends Player {

    /**
     * Crea el jugador rojo en la posición indicada.
     *
     * @param row fila inicial del jugador en el tablero
     * @param col columna inicial del jugador en el tablero
     */
    public RedPlayer(final int row, final int col) {
        super(row, col);
    }

    /**
     * Retorna el color rojo del jugador.
     *
     * @return Color.RED
     */
    @Override
    public Color getColor() { return Color.RED; }

    /**
     * Retorna el nombre identificador del jugador.
     *
     * @return nombre del jugador
     */
    @Override
    public String getName() { return "Blinky"; }

    /**
     * Retorna el tamaño relativo estándar del jugador para el renderizado.
     *
     * @return tamaño del jugador
     */
    @Override
    public double getSize() { return 1.0; }
}