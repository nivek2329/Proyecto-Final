package domain;

import java.awt.Color;

/**
 * Clase abstracta que representa al jugador dentro del dominio del juego.
 * Define la posición actual en el tablero, la cantidad de muertes acumuladas
 * y la lógica general de movimiento y reaparición.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public abstract class Player {

    /** Fila actual del jugador en el tablero. */
    protected int row;

    /** Columna actual del jugador en el tablero. */
    protected int col;

    /** Cantidad de muertes acumuladas durante la partida. */
    protected int deaths;

    /**
     * Crea un jugador en la posición indicada con cero muertes.
     *
     * @param row fila inicial del jugador en el tablero
     * @param col columna inicial del jugador en el tablero
     */
    public Player(final int row, final int col) {
        super();
        this.row    = row;
        this.col    = col;
        this.deaths = 0;
    }

    /**
     * Mueve al jugador si la posición destino es válida en el tablero.
     *
     * @param deltaRow desplazamiento en filas
     * @param deltaCol desplazamiento en columnas
     * @param board    tablero sobre el cual se realiza el movimiento
     */
    public void move(final int deltaRow, final int deltaCol, final Board board) {
        final int nextRow = row + deltaRow;
        final int nextCol = col + deltaCol;
        if (board.isValidPosition(nextRow, nextCol)) {
            row = nextRow;
            col = nextCol;
        }
    }

    /**
     * Reposiciona al jugador en el centro de la zona indicada e incrementa muertes.
     *
     * @param zone zona a partir de la cual el jugador reaparece
     */
    public void respawn(final Zone zone) {
        row = zone.getRow() + zone.getRows() / 2;
        col = zone.getCol() + zone.getCols() / 2;
        deaths++;
    }

    /**
     * Retorna la fila actual del jugador.
     *
     * @return fila del jugador
     */
    public int getRow() { return row; }

    /**
     * Retorna la columna actual del jugador.
     *
     * @return columna del jugador
     */
    public int getCol() { return col; }

    /**
     * Retorna la cantidad de muertes acumuladas.
     *
     * @return número de muertes
     */
    public int getDeaths() { return deaths; }

    /**
     * Retorna el color asociado a la representación visual del jugador.
     *
     * @return color del jugador
     */
    public abstract Color getColor();

    /**
     * Retorna el nombre del jugador.
     *
     * @return nombre del jugador
     */
    public abstract String getName();

    /**
     * Retorna el tamaño relativo del jugador utilizado para el renderizado.
     *
     * @return tamaño del jugador
     */
    public abstract double getSize();
}