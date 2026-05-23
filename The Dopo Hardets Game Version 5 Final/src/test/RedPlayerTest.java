package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import main.domain.Board;
import main.domain.HardestGameException;
import main.domain.RedPlayer;
import main.domain.Zone;

/**
 * Clase de pruebas unitarias para la clase RedPlayer.
 * Verifica el comportamiento del jugador rojo en cuanto
 * a movimiento respetando muros, reaparición en zonas,
 * conteo de muertes y correcta exposición de sus
 * atributos visuales y descriptivos.
 *
 * Estas pruebas aseguran que la implementación concreta
 * del jugador cumpla correctamente las reglas del dominio.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class RedPlayerTest {

    private RedPlayer player;
    private Board board;

    /**
     * Inicializa el jugador y el tablero antes de cada prueba.
     */
    @Before
    public void setUp() {
        board = new Board(5, 5);
        player = new RedPlayer(2, 1);
    }

    /**
     * Libera los recursos después de cada prueba.
     */
    @After
    public void tearDown() {
        player = null;
        board = null;
    }

    /**
     * Verifica que el jugador no atraviesa muros al desplazarse.
     */
    @Test
    public void shouldNotMoveRespetaPared() throws Exception {
        board.setWall(2, 2);

        player.move(0, 1, board);
        assertEquals(
                "El jugador no debe moverse hacia la pared en col 2",
                1, player.getCol()
        );

        player.move(0, 1, board);
        assertEquals(
                "El jugador debe seguir bloqueado en col 1",
                1, player.getCol()
        );
    }

    /**
     * Comprueba reaparición del jugador en el centro de una zona.
     */
    @Test
    public void shouldRespawnCentroZonaCuatroPorCuatro() {
        Zone z = new Zone(0, 0, 4, 4, Zone.Type.INITIAL);
        RedPlayer p = new RedPlayer(9, 9);

        assertEquals(
                "El jugador debe iniciar con 0 muertes",
                0, p.getDeaths()
        );

        p.respawn(z);

        assertEquals(
                "El jugador debe reaparecer en el centro de la zona: fila 2",
                2, p.getRow()
        );
        assertEquals(
                "El jugador debe reaparecer en el centro de la zona: col 2",
                2, p.getCol()
        );
        assertEquals(
                "El contador de muertes debe aumentar a 1 tras el respawn",
                1, p.getDeaths()
        );
    }

    /**
     * Verifica el color del jugador rojo.
     */
    @Test
    public void shouldGetColorRed() {
        assertNotNull(
                "El color del jugador no debe ser null",
                player.getColor()
        );
        assertEquals(
                "El color del jugador rojo debe ser Color.RED",
                java.awt.Color.RED,
                player.getColor()
        );
    }

    /**
     * Comprueba el nombre del jugador rojo.
     */
    @Test
    public void shouldGetNameBlinky() {
        assertEquals(
                "El nombre del jugador rojo debe ser Blinky",
                "Blinky",
                player.getName()
        );
    }

    /**
     * Verifica el tamaño relativo del jugador rojo.
     */
    @Test
    public void shouldGetSizeOne() {
        assertEquals(
                "El tamanio del jugador rojo debe ser 1.0",
                1.0,
                player.getSize(),
                0.001
        );
    }
}
