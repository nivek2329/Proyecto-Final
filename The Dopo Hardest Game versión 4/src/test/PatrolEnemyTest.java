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
}
