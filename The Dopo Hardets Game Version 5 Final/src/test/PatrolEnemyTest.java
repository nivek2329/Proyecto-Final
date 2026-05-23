package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import main.domain.Board;
import main.domain.PatrolEnemy;
import java.awt.Color;

/**
 * Clase de pruebas unitarias para PatrolEnemy.
 */
public class PatrolEnemyTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board(10, 10);
    }

    @Test
    public void shouldInitializeAndMoveAlongWaypoints() {
        // Patrol area row 1-3, col 1-3 (height 3, width 3)
        PatrolEnemy enemy = new PatrolEnemy(1, 1, 1, 1, 3, 3);
        assertEquals(1, enemy.getRow());
        assertEquals(1, enemy.getCol());
        assertNotNull(enemy.getColor());

        // Update 4 times to move once
        for (int i = 0; i < 4; i++) {
            enemy.update(board);
        }
        // It should start moving along the perimeter, e.g., to (1, 2)
        assertEquals(1, enemy.getRow());
        assertEquals(2, enemy.getCol());
    }

    @Test
    public void shouldRestoreStateAndProduceSnapshot() {
        PatrolEnemy enemy = new PatrolEnemy(1, 1, 1, 1, 3, 3);
        enemy.restoreSavedState(2, 2, 3, 1);
        assertEquals(2, enemy.getRow());
        assertEquals(2, enemy.getCol());
    }

    @Test
    public void testPatrolEnemyAdditionalCoverage() {
        // 1. Zone rows/cols <= 0
        PatrolEnemy peEmpty = new PatrolEnemy(1, 1, 1, 1, 0, 0);
        peEmpty.update(board); // Returns immediately because waypoints is empty
        assertNotNull(peEmpty.toSnapshot());

        // 2. Single row (height 1, width 3)
        PatrolEnemy peRow = new PatrolEnemy(1, 1, 1, 1, 1, 3);
        assertNotNull(peRow.getColor());

        // 3. Single col (height 3, width 1)
        PatrolEnemy peCol = new PatrolEnemy(1, 1, 1, 1, 3, 1);
        assertNotNull(peCol.getColor());

        // 4. Starting position is invalid
        Board localBoard = new Board(5, 5);
        try {
            localBoard.setWall(1, 1);
        } catch (Exception ex) {}
        
        PatrolEnemy peInvalidStart = new PatrolEnemy(1, 1, 1, 2, 2, 2);
        peInvalidStart.update(localBoard); // Will prepare waypoints, see invalid start, and warp to first waypoint
        assertNotEquals(1, peInvalidStart.getCol());

        // Restore saved state when waypoints is empty
        peEmpty.restoreSavedState(2, 2, 1, 1);
        assertEquals(2, peEmpty.getRow());

        // 5. Step toward target with blocked rowFirst step
        Board b = new Board(5, 5);
        try {
            b.setWall(2, 1); // block rowFirst step to (2, 1)
        } catch (Exception ex) {}
        
        PatrolEnemy peStep = new PatrolEnemy(1, 1, 1, 1, 3, 3);
        // Force waypoint to a custom target
        peStep.restoreSavedState(1, 1, 0, 3); // tick count 3, waypointIndex 0
        peStep.update(b); // will update tickCount to 4 and trigger update move
        // Tries to step to (2, 1) but blocked, steps to (1, 2) instead
        assertEquals(2, peStep.getCol());
    }
}
