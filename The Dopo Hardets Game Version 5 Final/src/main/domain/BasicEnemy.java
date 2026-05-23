package main.domain;

import main.domain.GameSnapshot.EnemySnapshot;

import java.awt.*;

/**
 * Representa un enemigo básico dentro del juego. Se mueve de forma automática
 * siguiendo un patrón lineal horizontal o vertical y cambia de dirección al encontrar
 * un obstáculo o zona no permitida.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class BasicEnemy extends Enemy {

    /**
     * Define las direcciones de movimiento posibles para el enemigo básico.
     */
    public enum Direction { 
        /** Movimiento en el eje horizontal. */
        HORIZONTAL, 
        /** Movimiento en el eje vertical. */
        VERTICAL 
    }

    private static final int MOVE_EVERY = 4;

    private int deltaRow;
    private int deltaCol;
    private int tickCount;

    /**
     * Crea un enemigo básico en la posición y dirección especificadas.
     *
     * @param row fila inicial del enemigo en el tablero
     * @param col columna inicial del enemigo en el tablero
     * @param dir dirección principal del movimiento
     * @param horizontalStep sentido del paso horizontal (positivo derecha, negativo izquierda)
     */
    public BasicEnemy(final int row, final int col,
                      final Direction dir, final int horizontalStep) {
        super(row, col);
        tickCount = 0;
        if (dir == Direction.HORIZONTAL) {
            deltaRow = 0;
            deltaCol = horizontalStep >= 0 ? 1 : -1;
        } else {
            deltaRow = 1;
            deltaCol = 0;
        }
    }

    /**
     * Restaura la posición y el vector de movimiento de este enemigo.
     *
     * @param newRow nueva fila del enemigo
     * @param newCol nueva columna del enemigo
     * @param newDeltaRow nuevo desplazamiento en filas
     * @param newDeltaCol nuevo desplazamiento en columnas
     * @param newTickCount nuevo contador de ticks acumulados
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
     * Captura el estado actual de este enemigo básico en una instantánea.
     *
     * @return instantánea con el estado de posición y movimiento del enemigo
     */
    public EnemySnapshot toSnapshot() {
        return new EnemySnapshot(row, col, deltaRow, deltaCol, tickCount);
    }

    /**
     * Actualiza la posición y comportamiento del enemigo en el tablero.
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
     * Retorna el color asociado a la representación visual del enemigo básico.
     *
     * @return color azul del enemigo
     */
    @Override
    public Color getColor() {
        return new Color(0, 100, 255);
    }
}