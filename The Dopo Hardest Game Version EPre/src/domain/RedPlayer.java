package domain;

import java.awt.Color;

/**
 * Jugador rojo estándar. Velocidad normal, sin habilidades especiales.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class RedPlayer extends Player {

    /**
     * Crea un jugador rojo en la posición indicada.
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
    @Override public Color  getColor()     { 
        return Color.RED; 
    }

    /**
     * Retorna el nombre identificador del jugador rojo.
     *
     * @return "Blinky"
     */
    @Override public String getName()      { 
        return "Blinky";  
    }

    /**
     * Retorna el tamaño base del jugador.
     *
     * @return 1.0
     */
    @Override public double getSize()      { 
        return 1.0;       
    }

    /**
     * Retorna el intervalo de ticks entre movimientos del jugador rojo.
     *
     * @return 2
     */
    @Override public int    getMoveTicks() { 
        return 2;         
    }
}