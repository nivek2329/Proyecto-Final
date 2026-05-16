package main.domain;

import java.util.logging.Logger;

/**
 * Tablero lógico del juego: muros y zonas prohibidas para enemigos.
 */
public class Board {

    private static final Logger LOGGER = GameLog.getLogger(Board.class);

    private final int rows;
    private final int cols;
    private final boolean[][] walls;
    private final boolean[][] enemyForbidden;

    public Board(final int rows, final int cols) {
        super();
        this.rows           = rows;
        this.cols           = cols;
        this.walls          = new boolean[rows][cols];
        this.enemyForbidden = new boolean[rows][cols];
    }

    public void addEnemyForbiddenZone(final Zone zone) throws OutOfBoundsException {
        for (int rowIdx = zone.getRow(); rowIdx < zone.getRow() + zone.getRows(); rowIdx++) {
            for (int colIdx = zone.getCol(); colIdx < zone.getCol() + zone.getCols(); colIdx++) {
                if (!inBounds(rowIdx, colIdx)) {
                    final String msg = "Zona segura fuera del tablero en (" + rowIdx + "," + colIdx + ")";
                    LOGGER.severe(msg);
                    throw new OutOfBoundsException(msg);
                }
                enemyForbidden[rowIdx][colIdx] = true;
            }
        }
    }

    public boolean isEnemyForbidden(final int row, final int col) {
        return inBounds(row, col) && enemyForbidden[row][col];
    }

    public boolean isValidPositionForEnemy(final int row, final int col) {
        return isValidPosition(row, col) && !enemyForbidden[row][col];
    }

    public void setWall(final int row, final int col) throws OutOfBoundsException {
        if (!inBounds(row, col)) {
            final String msg = "Muro fuera del tablero en (" + row + "," + col + ")";
            LOGGER.severe(msg);
            throw new OutOfBoundsException(msg);
        }
        walls[row][col] = true;
    }

    public boolean isWall(final int row, final int col) {
        return inBounds(row, col) && walls[row][col];
    }

    public boolean inBounds(final int row, final int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean isValidPosition(final int row, final int col) {
        return inBounds(row, col) && !walls[row][col];
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
}
