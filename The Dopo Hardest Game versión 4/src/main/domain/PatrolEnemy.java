package main.domain;

import main.domain.GameSnapshot.EnemySnapshot;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Punto azul patrullero: recorre el perímetro de una zona rectangular
 * (patrulla geométrica en bucle).
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class PatrolEnemy extends Enemy {

    private static final int MOVE_EVERY = 4;

    private final List<int[]> waypoints;
    private boolean waypointsReady;
    private int waypointIndex;
    private int tickCount;

    /**
     * Crea un enemigo patrullero con la zona de patrulla indicada.
     *
     * @param row      fila inicial del enemigo
     * @param col      columna inicial
     * @param zoneRow  fila superior izquierda de la zona de patrulla
     * @param zoneCol  columna superior izquierda
     * @param zoneRows alto de la zona
     * @param zoneCols ancho de la zona
     */
    public PatrolEnemy(final int row, final int col,
                       final int zoneRow, final int zoneCol,
                       final int zoneRows, final int zoneCols) {
        super(row, col);
        waypoints = new ArrayList<>(buildPerimeter(zoneRow, zoneCol, zoneRows, zoneCols));
        waypointsReady = false;
        waypointIndex = 0;
        tickCount = 0;
    }

    @Override
    public void update(final Board board) {
        prepareWaypoints(board);
        if (waypoints.isEmpty()) {
            return;
        }
        tickCount++;
        if (tickCount < MOVE_EVERY) {
            return;
        }
        tickCount = 0;

        final int[] target = waypoints.get(waypointIndex);
        if (row == target[0] && col == target[1]) {
            waypointIndex = (waypointIndex + 1) % waypoints.size();
        }
        if (!stepToward(waypoints.get(waypointIndex), board)) {
            waypointIndex = (waypointIndex + 1) % waypoints.size();
        }
    }

    private void prepareWaypoints(final Board board) {
        if (waypointsReady) {
            return;
        }
        waypoints.removeIf(w -> !board.isValidPositionForEnemy(w[0], w[1]));
        if (!board.isValidPositionForEnemy(row, col) && !waypoints.isEmpty()) {
            final int[] first = waypoints.get(0);
            row = first[0];
            col = first[1];
        }
        waypointIndex = nearestWaypointIndex(row, col);
        waypointsReady = true;
    }

    private boolean stepToward(final int[] target, final Board board) {
        if (row == target[0] && col == target[1]) {
            return true;
        }
        if (tryStep(target, board, true)) {
            return true;
        }
        return tryStep(target, board, false);
    }

    private boolean tryStep(final int[] target, final Board board, final boolean rowFirst) {
        if (rowFirst) {
            if (row != target[0]) {
                final int nextRow = row + Integer.signum(target[0] - row);
                if (board.isValidPositionForEnemy(nextRow, col)) {
                    row = nextRow;
                    return true;
                }
            }
            if (col != target[1]) {
                final int nextCol = col + Integer.signum(target[1] - col);
                if (board.isValidPositionForEnemy(row, nextCol)) {
                    col = nextCol;
                    return true;
                }
            }
        } else {
            if (col != target[1]) {
                final int nextCol = col + Integer.signum(target[1] - col);
                if (board.isValidPositionForEnemy(row, nextCol)) {
                    col = nextCol;
                    return true;
                }
            }
            if (row != target[0]) {
                final int nextRow = row + Integer.signum(target[0] - row);
                if (board.isValidPositionForEnemy(nextRow, col)) {
                    row = nextRow;
                    return true;
                }
            }
        }
        return false;
    }

    private int nearestWaypointIndex(final int r, final int c) {
        int best = 0;
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < waypoints.size(); i++) {
            final int[] w = waypoints.get(i);
            final int dist = Math.abs(w[0] - r) + Math.abs(w[1] - c);
            if (dist < bestDist) {
                bestDist = dist;
                best = i;
            }
        }
        return best;
    }

    private static List<int[]> buildPerimeter(final int zoneRow, final int zoneCol,
                                              final int zoneRows, final int zoneCols) {
        if (zoneRows <= 0 || zoneCols <= 0) {
            return Collections.emptyList();
        }
        final List<int[]> pts = new ArrayList<>();
        final int r0 = zoneRow;
        final int c0 = zoneCol;
        final int r1 = zoneRow + zoneRows - 1;
        final int c1 = zoneCol + zoneCols - 1;

        for (int c = c0; c <= c1; c++) {
            pts.add(new int[]{r0, c});
        }
        for (int r = r0 + 1; r <= r1; r++) {
            pts.add(new int[]{r, c1});
        }
        if (r1 > r0) {
            for (int c = c1 - 1; c >= c0; c--) {
                pts.add(new int[]{r1, c});
            }
        }
        if (c1 > c0) {
            for (int r = r1 - 1; r > r0; r--) {
                pts.add(new int[]{r, c0});
            }
        }
        return pts;
    }

    /**
     * Restaura la posición, el índice del waypoint y los ticks del patrullero.
     *
     * @param newRow           nueva fila del enemigo
     * @param newCol           nueva columna del enemigo
     * @param newWaypointIndex índice del waypoint destino actual
     * @param newTickCount     ticks acumulados
     */
    public void restoreSavedState(final int newRow, final int newCol,
                                  final int newWaypointIndex, final int newTickCount) {
        row = newRow;
        col = newCol;
        waypointIndex = waypoints.isEmpty() ? 0
                : Math.floorMod(newWaypointIndex, waypoints.size());
        tickCount = newTickCount;
    }

    /**
     * Crea una instantánea del estado de movimiento del enemigo patrullero.
     *
     * @return instantánea del estado del enemigo patrullero
     */
    EnemySnapshot toSnapshot() {
        return new EnemySnapshot(row, col, waypointIndex, 0, tickCount);
    }

    @Override
    public Color getColor() {
        return new Color(30, 80, 220);
    }
}
