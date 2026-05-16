package main.domain;

import main.domain.GameSnapshot.EnemySnapshot;

import java.awt.*;

/**
 * Deslizador vertical (Tipo V): solo se mueve en línea recta vertical
 * y rebota al chocar con muros o bordes. Dificultad baja (más lento).
 */
public class VerticalEnemy extends Enemy {

    private static final int MOVE_EVERY = 6;

    private int deltaRow;
    private int tickCount;

    public VerticalEnemy(final int row, final int col) {
        super(row, col);
        deltaRow = 1;
        tickCount = 0;
    }

    @Override
    public void update(final Board board) {
        tickCount++;
        if (tickCount < MOVE_EVERY) {
            return;
        }
        tickCount = 0;

        int nextRow = row + deltaRow;
        if (!board.isValidPositionForEnemy(nextRow, col)) {
            deltaRow = -deltaRow;
            nextRow = row + deltaRow;
        }
        if (board.isValidPositionForEnemy(nextRow, col)) {
            row = nextRow;
        }
    }

    public void restoreSavedState(final int newRow, final int newCol,
                                  final int newDeltaRow, final int newTickCount) {
        row = newRow;
        col = newCol;
        deltaRow = newDeltaRow == 0 ? 1 : newDeltaRow;
        tickCount = newTickCount;
    }

    EnemySnapshot toSnapshot() {
        return new EnemySnapshot(row, col, deltaRow, 0, tickCount);
    }

    @Override
    public Color getColor() {
        return new Color(80, 200, 255);
    }
}
