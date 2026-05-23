package main.domain;

import main.domain.GameSnapshot.EnemySnapshot;
import java.awt.Color;

/**
 * Enemigo giratorio (Spinner): rota alrededor de un centro fijo
 * a un radio determinado utilizando 8 posiciones discretas.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class SpinnerEnemy extends Enemy {

    private static final int[][] OFFSETS = {
        {-1, 0},
        {-1, 1},
        {0, 1},
        {1, 1},
        {1, 0},
        {1, -1},
        {0, -1},
        {-1, -1}
    };

    private final int centerRow;
    private final int centerCol;
    private final int radius;
    private final int moveEvery;
    private int index;
    private boolean clockwise;
    private int tickCount;

    /**
     * Crea un enemigo giratorio con los parámetros de órbita y velocidad especificados.
     *
     * @param centerRow fila del centro de rotación
     * @param centerCol columna del centro de rotación
     * @param radius radio del círculo de órbita
     * @param initialIndex índice inicial de posición discreta (0 a 7)
     * @param clockwise sentido de giro horario (true) o antihorario (false)
     * @param moveEvery número de ticks entre movimientos
     */
    public SpinnerEnemy(final int centerRow, final int centerCol, final int radius,
                        final int initialIndex, final boolean clockwise, final int moveEvery) {
        super(centerRow + OFFSETS[Math.floorMod(initialIndex, 8)][0] * radius,
              centerCol + OFFSETS[Math.floorMod(initialIndex, 8)][1] * radius);
        this.centerRow = centerRow;
        this.centerCol = centerCol;
        this.radius = radius;
        this.index = Math.floorMod(initialIndex, 8);
        this.clockwise = clockwise;
        this.moveEvery = moveEvery;
        this.tickCount = 0;
    }

    /**
     * Actualiza la posición del enemigo girándolo en su órbita tras expirar el intervalo de ticks.
     *
     * @param board tablero de juego
     */
    @Override
    public void update(final Board board) {
        if (moveEvery <= 0) {
            return;
        }
        tickCount++;
        if (tickCount < moveEvery) {
            return;
        }
        tickCount = 0;

        if (clockwise) {
            index = (index + 1) % 8;
        } else {
            index = (index + 7) % 8;
        }

        row = centerRow + OFFSETS[index][0] * radius;
        col = centerCol + OFFSETS[index][1] * radius;
    }

    /**
     * Restaura el estado guardado del enemigo giratorio.
     *
     * @param newRow nueva fila del enemigo
     * @param newCol nueva columna del enemigo
     * @param newIndex nuevo índice de rotación
     * @param newClockwise nuevo sentido horario (1 para horario, 0 para antihorario)
     * @param newTickCount nuevo contador de ticks del enemigo
     */
    public void restoreSavedState(final int newRow, final int newCol,
                                  final int newIndex, final int newClockwise,
                                  final int newTickCount) {
        this.index = Math.floorMod(newIndex, 8);
        this.clockwise = (newClockwise != 0);
        this.tickCount = newTickCount;
        this.row = centerRow + OFFSETS[index][0] * radius;
        this.col = centerCol + OFFSETS[index][1] * radius;
    }

    /**
     * Captura el estado actual del enemigo giratorio en una instantánea.
     *
     * @return instantánea con el estado del enemigo
     */
    public EnemySnapshot toSnapshot() {
        return new EnemySnapshot(row, col, index, clockwise ? 1 : 0, tickCount);
    }

    /**
     * Obtiene el color de la representación visual del enemigo giratorio.
     *
     * @return color azul del enemigo (Color(0, 0, 255))
     */
    @Override
    public Color getColor() {
        return new Color(0, 0, 255);
    }

    /**
     * Obtiene la fila del centro de órbita.
     *
     * @return fila del centro
     */
    public int getCenterRow() {
        return centerRow;
    }

    /**
     * Obtiene la columna del centro de órbita.
     *
     * @return columna del centro
     */
    public int getCenterCol() {
        return centerCol;
    }

    /**
     * Obtiene el radio de la órbita de rotación.
     *
     * @return radio de órbita
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Obtiene el índice de posición actual (0 a 7).
     *
     * @return índice discreto de rotación
     */
    public int getIndex() {
        return index;
    }

    /**
     * Indica si el giro se realiza en el sentido de las agujas del reloj.
     *
     * @return true si es horario; false en caso contrario
     */
    public boolean isClockwise() {
        return clockwise;
    }

    /**
     * Obtiene la frecuencia de ticks para cada cambio de posición.
     *
     * @return número de ticks entre pasos
     */
    public int getMoveEvery() {
        return moveEvery;
    }
}
