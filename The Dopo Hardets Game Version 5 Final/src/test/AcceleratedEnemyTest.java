package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import main.domain.Board;
import main.domain.AcceleratedEnemy;
import main.domain.HardestGameException;
import java.awt.Color;

/**
 * Clase de pruebas unitarias para AcceleratedEnemy.
 */
public class AcceleratedEnemyTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board(10, 10);
    }

    @Test
    public void shouldInitializeCorrectly() {
        AcceleratedEnemy enemy = new AcceleratedEnemy(3, 4, true, 1);
        assertEquals(3, enemy.getRow());
        assertEquals(4, enemy.getCol());
        assertNotNull(enemy.getColor());
    }

    @Test
    public void shouldMoveEveryTwoTicks() {
        AcceleratedEnemy enemy = new AcceleratedEnemy(3, 4, true, 1);
        
        // 1st tick: no move
        enemy.update(board);
        assertEquals(4, enemy.getCol());

        // 2nd tick: moves
        enemy.update(board);
        assertEquals(5, enemy.getCol());
    }

    @Test
    public void shouldBounceOnWalls() throws Exception {
        board.setWall(3, 5);
        AcceleratedEnemy enemy = new AcceleratedEnemy(3, 4, true, 1);

        // 1st tick: no move
        enemy.update(board);
        // 2nd tick: hits wall, bounces
        enemy.update(board);
        
        assertEquals(3, enemy.getCol()); // deltaCol changes direction to -1, nextCol becomes 3
    }

    @Test
    public void shouldRestoreStateAndProduceSnapshot() {
        AcceleratedEnemy enemy = new AcceleratedEnemy(1, 1, true, 1);
        enemy.restoreSavedState(5, 6, -1, 0, 1);
        assertEquals(5, enemy.getRow());
        assertEquals(6, enemy.getCol());
        assertNotNull(enemy.toSnapshot());
    }

    @Test
    public void testAdditionalBranches() throws Exception {
        // Vertical constructor
        AcceleratedEnemy vertical = new AcceleratedEnemy(3, 4, false, 0);
        assertEquals(3, vertical.getRow());

        // Constructor with horizontalStep < 0
        AcceleratedEnemy left = new AcceleratedEnemy(3, 4, true, -1);
        assertEquals(4, left.getCol());

        // Stuck scenario (blocked in both directions)
        board.setWall(3, 3);
        board.setWall(3, 5);
        AcceleratedEnemy stuck = new AcceleratedEnemy(3, 4, true, 1);
        stuck.update(board); // 1 tick
        stuck.update(board); // 2 ticks (hits wall at col 5, bounces to col 3, but col 3 is also wall, so remains at col 4)
        assertEquals(4, stuck.getCol());
    }
}
