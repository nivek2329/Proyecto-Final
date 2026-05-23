package main.domain;

import java.awt.*;

/**
 * Jugador azul (Inky). Más rápido y de mayor tamaño que el rojo.
 * Velocidad 1.5x (1 tick entre movimientos), tamaño 1.5x.
 * Hitbox expandida: colisiona en cruz de 5 celdas por su tamaño visual.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class BluePlayer extends Player {

    private static final double PLAYER_SIZE = 1.5;

    /**
     * Crea un jugador azul en la posición indicada.
     *
     * @param row fila inicial del jugador en el tablero
     * @param col columna inicial del jugador en el tablero
     */
    public BluePlayer(final int row, final int col) {
        super(row, col);
    }

    /**
     * Retorna el color azul del jugador.
     *
     * @return color azul del jugador
     */
    @Override 
    public Color getColor() { 
        return Color.BLUE; 
    }

    /**
     * Retorna el nombre identificador del jugador azul.
     *
     * @return nombre del jugador azul ("Inky")
     */
    @Override 
    public String getName() { 
        return "Inky";     
    }

    /**
     * Retorna el tamaño visual expandido del jugador azul.
     *
     * @return tamaño de escala del jugador (1.5)
     */
    @Override 
    public double getSize() { 
        return PLAYER_SIZE; 
    }

    /**
     * Retorna el intervalo de ticks entre movimientos del jugador azul.
     * Velocidad 1.5x: se mueve cada 1 tick.
     *
     * @return número de ticks entre movimientos (1)
     */
    @Override 
    public int getMoveTicks() { 
        return 1;
    }
}
