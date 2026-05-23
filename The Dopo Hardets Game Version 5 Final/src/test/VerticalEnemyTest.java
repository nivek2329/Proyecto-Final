package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import main.domain.Board;
import main.domain.VerticalEnemy;
import main.domain.HardestGameException;
import java.awt.Color;

/**
 * Clase de pruebas unitarias para VerticalEnemy.
 */
public class VerticalEnemyTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board(10, 10);
    }

    @Test
    public void shouldInitializeAndMoveEverySixTicks() {
        VerticalEnemy enemy = new VerticalEnemy(2, 3);
        assertEquals(2, enemy.getRow());
        assertEquals(3, enemy.getCol());
        assertNotNull(enemy.getColor());

        // Update 6 times to move once
        for (int i = 0; i < 5; i++) {
            enemy.update(board);
        }
        assertEquals(2, enemy.getRow()); // not moved yet

        enemy.update(board);
        assertEquals(3, enemy.getRow()); // moved down to row 3
    }

    @Test
    public void shouldBounceOnWalls() throws Exception {
        board.setWall(3, 3);
        VerticalEnemy enemy = new VerticalEnemy(2, 3);

        // Update 6 times
        for (int i = 0; i < 6; i++) {
            enemy.update(board);
        }
        // Encountered wall, should change direction and move back to row 1
        assertEquals(1, enemy.getRow());
    }

    @Test
    public void shouldRestoreStateAndProduceSnapshot() {
        VerticalEnemy enemy = new VerticalEnemy(2, 3);
        enemy.restoreSavedState(4, 5, -1, 3);
        assertEquals(4, enemy.getRow());
        assertEquals(5, enemy.getCol());
    }
}
