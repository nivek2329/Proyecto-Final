package domain;

/**
 * Clase que representa el tablero lógico del juego.
 * Gestiona las celdas que corresponden a muros, así como las zonas
 * inaccesibles para los enemigos pero transitables por el jugador.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class Board {

    private final int rows;
    private final int cols;
    private final boolean[][] walls;
    private final boolean[][] enemyForbidden;

    /**
     * Crea un tablero con las dimensiones indicadas.
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
     * Marca todas las celdas de una zona como inaccesibles para los enemigos.
     *
     * @param zone zona que será marcada como inaccesible para los enemigos
     */
    public void addEnemyForbiddenZone(final Zone zone) {
        for (int rowIdx = zone.getRow(); rowIdx < zone.getRow() + zone.getRows(); rowIdx++) {
            for (int colIdx = zone.getCol(); colIdx < zone.getCol() + zone.getCols(); colIdx++) {
                if (inBounds(rowIdx, colIdx)) {
                    enemyForbidden[rowIdx][colIdx] = true;
                }
            }
        }
    }

    /**
     * Indica si una celda es inaccesible para los enemigos.
     *
     * @param row fila a evaluar
     * @param col columna a evaluar
     * @return true si la celda está prohibida para enemigos
     */
    public boolean isEnemyForbidden(final int row, final int col) {
        return inBounds(row, col) && enemyForbidden[row][col];
    }

    /**
     * Indica si un enemigo puede ocupar la celda indicada.
     *
     * @param row fila a evaluar
     * @param col columna a evaluar
     * @return true si la celda es válida para un enemigo
     */
    public boolean isValidPositionForEnemy(final int row, final int col) {
        return isValidPosition(row, col) && !enemyForbidden[row][col];
    }

    /**
     * Coloca un muro en la celda indicada si está dentro del tablero.
     *
     * @param row fila donde se desea colocar el muro
     * @param col columna donde se desea colocar el muro
     */
    public void setWall(final int row, final int col) {
        if (inBounds(row, col)) {
            walls[row][col] = true;
        }
    }

    /**
     * Indica si la celda es un muro.
     *
     * @param row fila a evaluar
     * @param col columna a evaluar
     * @return true si la celda es un muro
     */
    public boolean isWall(final int row, final int col) {
        return inBounds(row, col) && walls[row][col];
    }

    /**
     * Indica si la posición está dentro de los límites del tablero.
     *
     * @param row fila a evaluar
     * @param col columna a evaluar
     * @return true si la posición está dentro del tablero
     */
    public boolean inBounds(final int row, final int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * Indica si la posición es transitable para el jugador.
     *
     * @param row fila a evaluar
     * @param col columna a evaluar
     * @return true si la posición es válida para el jugador
     */
    public boolean isValidPosition(final int row, final int col) {
        return inBounds(row, col) && !walls[row][col];
    }

    /**
     * Retorna el número de filas del tablero.
     *
     * @return número de filas
     */
    public int getRows() { return rows; }

    /**
     * Retorna el número de columnas del tablero.
     *
     * @return número de columnas
     */
    public int getCols() { return cols; }
}