package main.domain;

import main.domain.GameSnapshot.EnemySnapshot;

import java.awt.*;

/**
 * Enemigo acelerado (Tipo A): línea recta horizontal o vertical
 * al doble de velocidad que el enemigo básico. Rebota en obstáculos.
 */
public class AcceleratedEnemy extends Enemy {

    private static final int MOVE_EVERY = 2;

    private int deltaRow;
    private int deltaCol;
    private int tickCount;

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

    public void restoreSavedState(final int newRow, final int newCol,
                                  final int newDeltaRow, final int newDeltaCol,
                                  final int newTickCount) {
        row = newRow;
        col = newCol;
        deltaRow = newDeltaRow;
        deltaCol = newDeltaCol;
        tickCount = newTickCount;
    }

    EnemySnapshot toSnapshot() {
        return new EnemySnapshot(row, col, deltaRow, deltaCol, tickCount);
    }

    @Override
    public Color getColor() {
        return new Color(255, 90, 40);
    }
}
