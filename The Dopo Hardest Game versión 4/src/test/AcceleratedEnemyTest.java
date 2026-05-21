package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import main.domain.Board;
import main.domain.AcceleratedEnemy;
import main.domain.HardestGameException.*;
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
    public void shouldBounceOnWalls() throws OutOfBoundsException {
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
    }
}
