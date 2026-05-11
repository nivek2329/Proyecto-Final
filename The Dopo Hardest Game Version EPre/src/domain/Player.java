package domain;

import java.awt.Color;

/**
 * Clase abstracta que representa al jugador dentro del dominio del juego.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public abstract class Player {

    protected int row;
    protected int col;
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
     * Desplaza al jugador en la dirección indicada si la celda destino es válida.
     *
     * @param deltaRow desplazamiento en filas
     * @param deltaCol desplazamiento en columnas
     * @param board    tablero sobre el cual se evalúa el movimiento
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
     * Reubica al jugador en el centro de la zona indicada e incrementa el contador de muertes.
     *
     * @param zone zona donde reaparecerá el jugador
     */
    public void respawn(final Zone zone) {
        row = zone.getRow() + zone.getRows() / 2;
        col = zone.getCol() + zone.getCols() / 2;
        deaths++;
    }

    /**
     * Retorna la fila actual del jugador en el tablero.
     *
     * @return fila del jugador
     */
    public int getRow() { 
        return row;    
    }

    /**
     * Retorna la columna actual del jugador en el tablero.
     *
     * @return columna del jugador
     */
    public int getCol() { 
        return col;    
    }

    /**
     * Retorna la cantidad de muertes acumuladas del jugador.
     *
     * @return número de muertes
     */
    public int getDeaths() { 
        return deaths; 
    }

    /**
     * Retorna el color asociado a la representación visual del jugador.
     *
     * @return color del jugador
     */
    public abstract Color  getColor();

    /**
     * Retorna el nombre identificador del jugador.
     *
     * @return nombre del jugador
     */
    public abstract String getName();

    /**
     * Retorna el factor de escala del tamaño visual del jugador.
     *
     * @return tamaño relativo del jugador
     */
    public abstract double getSize();

    /**
     * Retorna cada cuántos ticks de juego se mueve el jugador.
     * Valor menor indica mayor velocidad.
     *
     * @return ticks entre movimientos
     */
    public abstract int getMoveTicks();
}
