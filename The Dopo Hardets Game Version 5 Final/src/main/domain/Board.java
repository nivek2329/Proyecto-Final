package main.domain;

import java.util.logging.Logger;
import main.domain.HardestGameException;

/**
 * Tablero lógico del juego que define los muros y zonas prohibidas para los enemigos.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class Board {

    private static final Logger LOGGER = GameLog.getLogger(Board.class);

    private final int rows;
    private final int cols;
    private final boolean[][] walls;
    private final boolean[][] enemyForbidden;

    /**
     * Crea un tablero con el número especificado de filas y columnas.
     *
     * @param rows número de filas del tablero
     * @param cols número de columnas del tablero
     */
    public Board(final int rows, final int cols) {
        super();
        this.rows           = rows;
        this.cols           = cols;
        this.walls          = new boolean[rows][cols];
        this.enemyForbidden = new boolean[rows][cols];
    }

    /**
     * Añade una zona segura donde los enemigos no pueden entrar.
     *
     * @param zone zona a configurar como prohibida para enemigos
     * @throws HardestGameException si la zona se sale de los límites del tablero
     */
    public void addEnemyForbiddenZone(final Zone zone) throws HardestGameException {
        for (int rowIdx = zone.getRow(); rowIdx < zone.getRow() + zone.getRows(); rowIdx++) {
            for (int colIdx = zone.getCol(); colIdx < zone.getCol() + zone.getCols(); colIdx++) {
                if (!inBounds(rowIdx, colIdx)) {
                    final String msg = "Zona segura fuera del tablero en (" + rowIdx + "," + colIdx + ")";
                    LOGGER.severe(msg);
                    throw new HardestGameException(HardestGameException.OUT_OF_BOUNDS);
                }
                enemyForbidden[rowIdx][colIdx] = true;
            }
        }
    }

    /**
     * Determina si una posición en el tablero está prohibida para enemigos.
     *
     * @param row fila a verificar
     * @param col columna a verificar
     * @return true si la posición está dentro de los límites y prohibida para enemigos; false en caso contrario
     */
    public boolean isEnemyForbidden(final int row, final int col) {
        return inBounds(row, col) && enemyForbidden[row][col];
    }

    /**
     * Determina si la posición indicada es válida para que se mueva un enemigo.
     * Una posición es válida si está dentro del tablero, no es un muro y no es una zona prohibida.
     *
     * @param row fila a verificar
     * @param col columna a verificar
     * @return true si el enemigo se puede ubicar en la posición; false en caso contrario
     */
    public boolean isValidPositionForEnemy(final int row, final int col) {
        return isValidPosition(row, col) && !enemyForbidden[row][col];
    }

    /**
     * Coloca un muro en la celda especificada del tablero.
     *
     * @param row fila del muro
     * @param col columna del muro
     * @throws HardestGameException si la celda está fuera de los límites del tablero
     */
    public void setWall(final int row, final int col) throws HardestGameException {
        if (!inBounds(row, col)) {
            final String msg = "Muro fuera del tablero en (" + row + "," + col + ")";
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.OUT_OF_BOUNDS);
        }
        walls[row][col] = true;
    }

    /**
     * Verifica si hay un muro en la celda especificada.
     *
     * @param row fila a verificar
     * @param col columna a verificar
     * @return true si la celda contiene un muro; false en caso contrario
     */
    public boolean isWall(final int row, final int col) {
        return inBounds(row, col) && walls[row][col];
    }

    /**
     * Determina si la posición está dentro de los límites del tablero.
     *
     * @param row fila a verificar
     * @param col columna a verificar
     * @return true si la posición está en los límites; false en caso contrario
     */
    public boolean inBounds(final int row, final int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * Determina si la posición es válida y transitable (no es un muro).
     *
     * @param row fila a verificar
     * @param col columna a verificar
     * @return true si la posición está en límites y no es un muro; false en caso contrario
     */
    public boolean isValidPosition(final int row, final int col) {
        return inBounds(row, col) && !walls[row][col];
    }

    /**
     * Obtiene el número total de filas del tablero.
     *
     * @return número de filas
     */
    public int getRows() { 
        return rows; 
    }

    /**
     * Obtiene el número total de columnas del tablero.
     *
     * @return número de columnas
     */
    public int getCols() { 
        return cols; 
    }
}
