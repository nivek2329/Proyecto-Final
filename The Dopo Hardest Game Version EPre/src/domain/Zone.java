package domain;

/**
 * Clase que representa una zona rectangular dentro del tablero del juego.
 * Una zona define un área específica mediante una posición inicial, un tamaño
 * y un tipo que indica su función dentro del nivel.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class Zone {

    public enum Type { INITIAL, INTERMEDIATE, FINAL }

    private int row;
    private int col;
    private int rows;
    private int cols;
    private Type type;

    /**
     * Crea una zona rectangular con su posición, dimensiones y tipo de función.
     *
     * @param row  fila inicial de la zona en el tablero
     * @param col  columna inicial de la zona en el tablero
     * @param rows número de filas que abarca la zona
     * @param cols número de columnas que abarca la zona
     * @param type tipo de zona según su función en el juego
     */
    public Zone(final int row, final int col, final int rows,
                final int cols, final Type type) {
        super();
        this.row  = row;
        this.col  = col;
        this.rows = rows;
        this.cols = cols;
        this.type = type;
    }

    /**
     * Determina si la posición indicada pertenece al área de esta zona.
     *
     * @param rowToCheck fila a evaluar
     * @param colToCheck columna a evaluar
     * @return true si la posición está dentro de los límites de la zona
     */
    public boolean contains(final int rowToCheck, final int colToCheck) {
        return rowToCheck >= row && rowToCheck < row + rows
                && colToCheck >= col && colToCheck < col + cols;
    }

    /**
     * Retorna la fila inicial de la zona dentro del tablero.
     *
     * @return fila inicial de la zona
     */
    public int getRow() { 
        return row; 
    }

    /**
     * Retorna la columna inicial de la zona dentro del tablero.
     *
     * @return columna inicial de la zona
     */
    public int getCol() { 
        return col; 
    }

    /**
     * Retorna el número de filas que abarca la zona.
     *
     * @return cantidad de filas de la zona
     */
    public int getRows() { 
        return rows; 
    }

    /**
     * Retorna el número de columnas que abarca la zona.
     *
     * @return cantidad de columnas de la zona
     */
    public int getCols() { 
        return cols; 
    }

    /**
     * Retorna el tipo funcional asignado a esta zona dentro del nivel.
     *
     * @return tipo de la zona
     */
    public Type getType() { 
        return type; 
    }
}