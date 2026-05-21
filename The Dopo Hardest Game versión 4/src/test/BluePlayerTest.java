package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import main.domain.Board;
import main.domain.HardestGameException.*;
import main.domain.BluePlayer;
import main.domain.Zone;

/**
 * Clase de pruebas unitarias para la clase BluePlayer.
 * Verifica el comportamiento del jugador azul en cuanto
 * a movimiento, reaparición, y sus propiedades específicas.
 */
public class BluePlayerTest {

    private BluePlayer player;
    private Board board;

    @Before
    public void setUp() {
        board = new Board(5, 5);
        player = new BluePlayer(2, 1);
    }

    @After
    public void tearDown() {
        player = null;
        board = null;
    }

    @Test
    public void shouldNotMoveRespetaPared() throws OutOfBoundsException {
        board.setWall(2, 2);

        player.move(0, 1, board);
        assertEquals(
                "El jugador azul no debe moverse hacia la pared en col 2",
                1, player.getCol()
        );
    }

    @Test
    public void shouldGetColorBlue() {
        assertNotNull("El color del jugador no debe ser null", player.getColor());
        assertEquals("El color del jugador azul debe ser Color.BLUE", java.awt.Color.BLUE, player.getColor());
    }

    @Test
    public void shouldGetNameInky() {
        assertEquals("El nombre del jugador azul debe ser Inky", "Inky", player.getName());
    }

    @Test
    public void shouldGetSizeOnePointFive() {
        assertEquals("El tamaño del jugador azul debe ser 1.5", 1.5, player.getSize(), 0.001);
    }

    @Test
    public void shouldGetMoveTicksOne() {
        assertEquals("El jugador azul debe moverse cada 1 tick", 1, player.getMoveTicks());
    }
}
