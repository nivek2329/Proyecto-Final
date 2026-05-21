package main.domain;

import java.util.logging.Logger;
import main.domain.HardestGameException.*;

/**
 * Tablero lógico del juego: muros y zonas prohibidas para enemigos.
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
     * Crea un tablero con las dimensiones indicadas.
     *
     * @param rows cantidad de filas del tablero
     * @param cols cantidad de columnas del tablero
     */
    public Board(final int rows, final int cols) {
        super();
        this.rows           = rows;
        this.cols           = cols;
        this.walls          = new boolean[rows][cols];
        this.enemyForbidden = new boolean[rows][cols];
    }

    /**
     * Añade una zona segura en la que los enemigos no pueden entrar.
     *
     * @param zone la zona segura a añadir
     * @throws OutOfBoundsException si la zona está fuera de los límites del tablero
     */
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

    /**
     * Consulta si una celda está en una zona prohibida para enemigos.
     *
     * @param row fila a consultar
     * @param col columna a consultar
     * @return true si la posición está dentro del tablero y es zona prohibida
     */
    public boolean isEnemyForbidden(final int row, final int col) {
        return inBounds(row, col) && enemyForbidden[row][col];
    }

    /**
     * Consulta si la posición es válida para un enemigo (no es muro ni zona prohibida).
     *
     * @param row fila a evaluar
     * @param col columna a evaluar
     * @return true si el enemigo puede ubicarse en la celda
     */
    public boolean isValidPositionForEnemy(final int row, final int col) {
        return isValidPosition(row, col) && !enemyForbidden[row][col];
    }

    /**
     * Define una celda específica como muro.
     *
     * @param row fila del tablero
     * @param col columna del tablero
     * @throws OutOfBoundsException si la celda está fuera de los límites del tablero
     */
    public void setWall(final int row, final int col) throws OutOfBoundsException {
        if (!inBounds(row, col)) {
            final String msg = "Muro fuera del tablero en (" + row + "," + col + ")";
            LOGGER.severe(msg);
            throw new OutOfBoundsException(msg);
        }
        walls[row][col] = true;
    }

    /**
     * Consulta si una celda específica es un muro.
     *
     * @param row fila a evaluar
     * @param col columna a evaluar
     * @return true si la celda es un muro o está fuera de límites
     */
    public boolean isWall(final int row, final int col) {
        return inBounds(row, col) && walls[row][col];
    }

    /**
     * Consulta si una celda está dentro de los límites físicos del tablero.
     *
     * @param row fila a evaluar
     * @param col columna a evaluar
     * @return true si la posición es válida dentro del tablero
     */
    public boolean inBounds(final int row, final int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * Consulta si la posición es transitable por jugadores (dentro de límites y sin muros).
     *
     * @param row fila a evaluar
     * @param col columna a evaluar
     * @return true si la posición es transitable
     */
    public boolean isValidPosition(final int row, final int col) {
        return inBounds(row, col) && !walls[row][col];
    }

    /**
     * Retorna la cantidad de filas del tablero.
     *
     * @return cantidad de filas
     */
    public int getRows() { return rows; }

    /**
     * Retorna la cantidad de columnas del tablero.
     *
     * @return cantidad de columnas
     */
    public int getCols() { return cols; }
}
