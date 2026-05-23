package main.domain;

import main.domain.GameSnapshot.EnemySnapshot;

import java.awt.*;

/**
 * Representa un enemigo acelerado que se mueve en línea recta horizontal o vertical
 * al doble de velocidad que un enemigo básico y rebota ante obstáculos.
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
     * Crea un enemigo acelerado en la posición inicial y con la dirección especificadas.
     *
     * @param row fila inicial en el tablero
     * @param col columna inicial en el tablero
     * @param horizontal determina si el movimiento inicial es horizontal (true) o vertical (false)
     * @param horizontalStep dirección del paso horizontal (positivo derecha, negativo izquierda)
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
     * Actualiza el estado y posición del enemigo en el tablero.
     *
     * @param board tablero del juego para validar movimientos y rebotes
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
     * Restaura el estado guardado del enemigo acelerado.
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
     * Captura el estado actual de este enemigo en una instantánea.
     *
     * @return instantánea con el estado de posición y movimiento del enemigo
     */
    public EnemySnapshot toSnapshot() {
        return new EnemySnapshot(row, col, deltaRow, deltaCol, tickCount);
    }

    /**
     * Obtiene el color de la representación visual del enemigo acelerado.
     *
     * @return color naranja/rojo del enemigo
     */
    @Override
    public Color getColor() {
        return new Color(255, 90, 40);
    }
}
