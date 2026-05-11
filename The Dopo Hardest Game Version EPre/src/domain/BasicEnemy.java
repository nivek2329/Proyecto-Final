package domain;

import java.awt.Color;

/**
 * Clase concreta que representa un enemigo básico dentro del juego.
 * Se mueve de forma automática siguiendo un patrón lineal horizontal
 * o vertical, cambiando de dirección al encontrar un obstáculo o una
 * zona no permitida.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class BasicEnemy extends Enemy {

    public enum Direction { HORIZONTAL, VERTICAL }

    private static final int MOVE_EVERY = 4;

    private int deltaRow;
    private int deltaCol;
    private int tickCount;

    /**
     * Crea un enemigo básico en la posición indicada con un patrón de movimiento.
     *
     * @param row            fila inicial del enemigo en el tablero
     * @param col            columna inicial del enemigo en el tablero
     * @param dir            dirección principal del movimiento del enemigo
     * @param horizontalStep sentido del movimiento horizontal cuando aplica
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
     * Actualiza la posición del enemigo según su patrón de movimiento.
     *
     * @param board tablero sobre el cual se mueve el enemigo
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