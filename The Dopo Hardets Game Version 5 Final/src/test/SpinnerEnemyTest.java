package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import main.domain.Board;
import main.domain.SpinnerEnemy;
import main.domain.GameSnapshot.EnemySnapshot;
import java.awt.Color;

/**
 * Clase de pruebas unitarias para SpinnerEnemy.
 */
public class SpinnerEnemyTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board(10, 10);
    }

    @Test
    public void shouldInitializeAndSpinClockwise() {
        // Center is (5,5), radius is 1, initial index is 1 (NE corner)
        SpinnerEnemy enemy = new SpinnerEnemy(5, 5, 1, 1, true, 4);
        
        // Initial NE position: (5 - 1, 5 + 1) = (4, 6)
        assertEquals(4, enemy.getRow());
        assertEquals(6, enemy.getCol());
        assertNotNull(enemy.getColor());

        // Update 3 times (less than moveEvery = 4)
        for (int i = 0; i < 3; i++) {
            enemy.update(board);
        }
        assertEquals(4, enemy.getRow());
        assertEquals(6, enemy.getCol());

        // 4th update: moves to index 2 (E) = (5, 6)
        enemy.update(board);
        assertEquals(5, enemy.getRow());
        assertEquals(6, enemy.getCol());
        assertEquals(2, enemy.getIndex());
    }

    @Test
    public void shouldSpinCounterClockwise() {
        // Center is (5,5), radius is 2, initial index is 0 (N)
        SpinnerEnemy enemy = new SpinnerEnemy(5, 5, 2, 0, false, 2);

        // Initial N position: (5 - 2, 5) = (3, 5)
        assertEquals(3, enemy.getRow());
        assertEquals(5, enemy.getCol());

        // Update 2 times -> moves CCW to index 7 (NW) = (5 - 2, 5 - 2) = (3, 3)
        enemy.update(board);
        enemy.update(board);
        assertEquals(3, enemy.getRow());
        assertEquals(3, enemy.getCol());
        assertEquals(7, enemy.getIndex());
    }

    @Test
    public void shouldRestoreStateAndProduceSnapshot() {
        SpinnerEnemy enemy = new SpinnerEnemy(5, 5, 1, 0, true, 4);
        
        enemy.restoreSavedState(0, 0, 3, 0, 2); // newIndex = 3 (SE), clockwise = false, tickCount = 2
        assertEquals(3, enemy.getIndex());
        assertFalse(enemy.isClockwise());
        
        // SE position: (5 + 1, 5 + 1) = (6, 6)
        assertEquals(6, enemy.getRow());
        assertEquals(6, enemy.getCol());

        EnemySnapshot snapshot = enemy.toSnapshot();
        assertEquals(6, snapshot.getRow());
        assertEquals(6, snapshot.getCol());
        assertEquals(3, snapshot.getDeltaRow()); // stores index
        assertEquals(0, snapshot.getDeltaCol()); // stores clockwise ? 1 : 0
        assertEquals(2, snapshot.getTickCount());
    }
}
