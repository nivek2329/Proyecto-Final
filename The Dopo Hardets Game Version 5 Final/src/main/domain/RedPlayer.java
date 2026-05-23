package main.domain;

import java.awt.*;

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
     * @return color rojo (Color.RED)
     */
    @Override 
    public Color getColor()     { 
        return Color.RED; 
    }

    /**
     * Retorna el nombre identificador del jugador rojo.
     *
     * @return nombre del jugador ("Blinky")
     */
    @Override 
    public String getName()      { 
        return "Blinky";  
    }

    /**
     * Retorna el tamaño base del jugador.
     *
     * @return tamaño del jugador (1.0)
     */
    @Override 
    public double getSize()      { 
        return 1.0;       
    }

    /**
     * Retorna el intervalo de ticks entre movimientos del jugador rojo.
     *
     * @return ticks entre movimientos (2)
     */
    @Override 
    public int getMoveTicks() { 
        return 2;         
    }
}