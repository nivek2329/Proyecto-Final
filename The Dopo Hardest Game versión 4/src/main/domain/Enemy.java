package main.domain;

import java.awt.*;

/**
 * Clase abstracta que representa a un enemigo dentro del dominio del juego.
 * Define la información básica común a todos los enemigos, como su posición
 * en el tablero, así como las operaciones para actualizar su comportamiento
 * y detectar colisiones con el jugador.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public abstract class Enemy implements GridEntity, Updatable {
    protected int row;
    protected int col;

    /**
     * Crea un enemigo en la posición indicada.
     *
     * @param row fila inicial del enemigo en el tablero
     * @param col columna inicial del enemigo en el tablero
     */
    public Enemy(final int row, final int col) {
        super();
        this.row = row;
        this.col = col;
    }

    /**
     * Actualiza la posición y comportamiento del enemigo.
     *
     * @param board tablero sobre el cual se actualiza el enemigo
     */
    @Override
    public abstract void update(Board board);

    /**
     * Retorna la fila actual del enemigo en el tablero.
     *
     * @return fila del enemigo
     */
    @Override
    public int getRow() { 
        return row; 
    }

    /**
     * Retorna la columna actual del enemigo en el tablero.
     *
     * @return columna del enemigo
     */
    @Override
    public int getCol() { 
        return col; 
    }

    /**
     * Determina si el enemigo colisiona con el jugador.
     * La hitbox se adapta al tamaño del jugador:
     * - Jugadores de tamaño 1.0 (Rojo, Verde): colisión exacta en la celda.
     * - Jugadores de tamaño > 1.0 (Azul 1.5x): colisión en cruz de 5 celdas
     *   (celda actual + adyacentes arriba, abajo, izquierda, derecha).
     *
     * @param player jugador con el cual se evalúa la colisión
     * @return true si el enemigo está dentro del área de colisión del jugador
     */
    public boolean collidesWith(final Player player) {
        final int pr = player.getRow();
        final int pc = player.getCol();

        if (row == pr && col == pc) {
            return true;
        }

        if (player.getSize() > 1.0) {
            final int manhattanDist = Math.abs(row - pr) + Math.abs(col - pc);
            return manhattanDist == 1;
        }

        return false;
    }

    /**
     * Retorna el color asociado a la representación visual del enemigo.
     *
     * @return color del enemigo
     */
    @Override
    public abstract Color getColor();
}