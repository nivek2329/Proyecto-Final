package main.domain;

import java.awt.Color;

/**
 * Representa cualquier entidad que ocupa una posición (fila, columna)
 * y tiene un color en el tablero de juego.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public interface GridEntity {

    /**
     * Retorna la fila de la entidad en el tablero.
     *
     * @return fila (coordenada Y)
     */
    int getRow();

    /**
     * Retorna la columna de la entidad en el tablero.
     *
     * @return columna (coordenada X)
     */
    int getCol();

    /**
     * Retorna el color para la representación gráfica de la entidad.
     *
     * @return color de la entidad
     */
    Color getColor();
}
