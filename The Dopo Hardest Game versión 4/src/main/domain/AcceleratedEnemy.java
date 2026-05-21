package main.domain;

import main.domain.GameSnapshot.EnemySnapshot;

import java.awt.*;

/**
 * Enemigo acelerado (Tipo A): línea recta horizontal o vertical
 * al doble de velocidad que el enemigo básico. Rebota en obstáculos.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class AcceleratedEnemy extends Enemy {

    private static final int MOVE_EVERY = 2;

    private int deltaRow;
    private int deltaCol;
    private int tickCount;

    /**
     * Crea un enemigo acelerado en la posición indicada.
     *
     * @param row            fila inicial del enemigo en el tablero
     * @param col            columna inicial del enemigo en el tablero
     * @param horizontal     indica si el movimiento es horizontal (true) o vertical (false)
     * @param horizontalStep sentido del movimiento horizontal cuando aplica
     */
    public AcceleratedEnemy(final int row, final int col,
                            final boolean horizontal, final int horizontalStep) {
        super(row, col);
        tickCount = 0;
        if (horizontal) {
            deltaRow = 0;
            deltaCol = horizontalStep >= 0 ? 1 : -1;
        } else {
            deltaRow = 1;
            deltaCol = 0;
        }
    }

    /**
     * Actualiza la posición y comportamiento del enemigo acelerado.
     *
     * @param board tablero sobre el cual se actualiza el enemigo
     */
    @Override
    public void update(final Board board) {
        tickCount++;
        if (tickCount < MOVE_EVERY) {
            return;
        }
        tickCount = 0;

        int nextRow = row + deltaRow;
        int nextCol = col + deltaCol;

        if (!board.isValidPositionForEnemy(nextRow, nextCol)) {
            deltaRow = -deltaRow;
            deltaCol = -deltaCol;
            nextRow = row + deltaRow;
            nextCol = col + deltaCol;
        }

        if (board.isValidPositionForEnemy(nextRow, nextCol)) {
            row = nextRow;
            col = nextCol;
        }
    }

    /**
     * Restaura posición, vector de movimiento y ticks desde una partida guardada.
     *
     * @param newRow      nueva fila del enemigo
     * @param newCol      nueva columna del enemigo
     * @param newDeltaRow nuevo desplazamiento en filas
     * @param newDeltaCol nuevo desplazamiento en columnas
     * @param newTickCount acumulado de ticks para el siguiente movimiento
     */
    public void restoreSavedState(final int newRow, final int newCol,
                                  final int newDeltaRow, final int newDeltaCol,
                                  final int newTickCount) {
        row = newRow;
        col = newCol;
        deltaRow = newDeltaRow;
        deltaCol = newDeltaCol;
        tickCount = newTickCount;
    }

    /**
     * Crea una instantánea del estado de movimiento del enemigo acelerado.
     *
     * @return instantánea del estado del enemigo acelerado
     */
    EnemySnapshot toSnapshot() {
        return new EnemySnapshot(row, col, deltaRow, deltaCol, tickCount);
    }

    /**
     * Retorna el color asociado a la representación visual del enemigo acelerado.
     *
     * @return color naranja del enemigo
     */
    @Override
    public Color getColor() {
        return new Color(255, 90, 40);
    }
}
