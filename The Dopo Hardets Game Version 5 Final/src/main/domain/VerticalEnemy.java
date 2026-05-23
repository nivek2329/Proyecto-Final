package main.domain;

import main.domain.GameSnapshot.EnemySnapshot;

import java.awt.*;

/**
 * Deslizador vertical (Tipo V): solo se mueve en línea recta vertical
 * y rebota al chocar con muros o bordes. Dificultad baja (más lento).
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class VerticalEnemy extends Enemy {

    private static final int MOVE_EVERY = 6;

    private int deltaRow;
    private int tickCount;

    /**
     * Crea un enemigo vertical en la posición indicada.
     *
     * @param row fila inicial del enemigo
     * @param col columna inicial del enemigo
     */
    public VerticalEnemy(final int row, final int col) {
        super(row, col);
        deltaRow = 1;
        tickCount = 0;
    }

    /**
     * Actualiza el estado y posición del enemigo vertical en el tablero.
     *
     * @param board tablero sobre el cual se evalúa el movimiento
     */
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

    /**
     * Restaura la posición y estado guardado del enemigo vertical.
     *
     * @param newRow nueva fila del enemigo
     * @param newCol nueva columna del enemigo
     * @param newDeltaRow nuevo desplazamiento vertical (dirección)
     * @param newTickCount nuevo contador de ticks del enemigo
     */
    public void restoreSavedState(final int newRow, final int newCol,
                                  final int newDeltaRow, final int newTickCount) {
        row = newRow;
        col = newCol;
        deltaRow = newDeltaRow == 0 ? 1 : newDeltaRow;
        tickCount = newTickCount;
    }

    /**
     * Captura el estado actual de este enemigo en una instantánea.
     *
     * @return instantánea con el estado del enemigo
     */
    public EnemySnapshot toSnapshot() {
        return new EnemySnapshot(row, col, deltaRow, 0, tickCount);
    }

    /**
     * Retorna el color de la representación visual del enemigo vertical.
     *
     * @return color celeste del enemigo
     */
    @Override
    public Color getColor() {
        return new Color(80, 200, 255);
    }
}
