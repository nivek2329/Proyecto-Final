package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import main.domain.Board;
import main.domain.OutOfBoundsException;
import main.domain.Zone;

/**
 * Clase de pruebas unitarias para la clase Board.
 * Verifica el comportamiento del tablero en términos de dimensiones,
 * validación de límites, manejo de muros, zonas prohibidas para enemigos
 * y validación de posiciones transitables.
 *
 * Estas pruebas aseguran que el tablero respete correctamente las reglas
 * del dominio tanto para el jugador como para los enemigos.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class BoardTest {

    private Board board;

    /**
     * Inicializa el tablero antes de cada prueba.
     */
    @Before
    public void setUp() {
        board = new Board(8, 22);
    }

    /**
     * Libera el tablero después de cada prueba.
     */
    @After
    public void tearDown() {
        board = null;
    }

    /**
     * Verifica dimensiones y validación correcta de los límites del tablero.
     */
    @Test
    public void shouldDimensionesYBounds() {
        assertEquals("El tablero debe tener 8 filas", 8, board.getRows());
        assertEquals("El tablero debe tener 22 columnas", 22, board.getCols());
        assertTrue("(0,0) debe estar dentro del tablero", board.inBounds(0, 0));
        assertTrue("(7,21) debe estar dentro del tablero", board.inBounds(7, 21));
        assertFalse("(-1,0) debe estar fuera del tablero", board.inBounds(-1, 0));
        assertFalse("(8,0) debe estar fuera del tablero", board.inBounds(8, 0));
        assertFalse("(0,22) debe estar fuera del tablero", board.inBounds(0, 22));
    }

    /**
     * Comprueba que un muro impide posiciones válidas para el jugador.
     */
    @Test
    public void shouldNotMuroImpidePosicionValidaJugador() throws OutOfBoundsException {
        board.setWall(2, 2);
        assertTrue("(2,2) debe ser reconocido como muro", board.isWall(2, 2));
        assertFalse("(2,2) no debe ser posicion valida para el jugador", board.isValidPosition(2, 2));
        assertTrue("(2,1) si debe ser posicion valida para el jugador", board.isValidPosition(2, 1));
    }

    /**
     * Verifica que las zonas prohibidas para enemigos no afecten al jugador.
     */
    @Test
    public void shouldZonaProhibidaEnemigoNoEsMuroParaJugador() throws OutOfBoundsException {
        Zone green = new Zone(0, 0, 5, 2, Zone.Type.INITIAL);
        board.addEnemyForbiddenZone(green);

        assertTrue("(0,0) debe ser posicion valida para el jugador aunque sea zona verde",
                board.isValidPosition(0, 0));
        assertFalse("(0,0) no debe ser posicion valida para el enemigo",
                board.isValidPositionForEnemy(0, 0));
        assertTrue("(2,5) debe ser posicion valida para el enemigo",
                board.isValidPositionForEnemy(2, 5));
    }

    /**
     * Verifica que fuera de los límites no se registren zonas prohibidas.
     */
    @Test
    public void shouldReturnFalseEnemyForbiddenWhenOutOfBounds() {
        assertFalse("Fuera de bounds (-1,0) no debe ser zona prohibida",
                board.isEnemyForbidden(-1, 0));
        assertFalse("Fuera de bounds (0,-1) no debe ser zona prohibida",
                board.isEnemyForbidden(0, -1));
    }

    /**
     * Comprueba que una celda no marcada no es zona prohibida.
     */
    @Test
    public void shouldReturnFalseEnemyForbiddenWhenNotForbidden() {
        assertFalse("(0,0) no debe ser zona prohibida si no se registro ninguna",
                board.isEnemyForbidden(0, 0));
    }

    /**
     * Verifica que colocar muros fuera de límites lanza OutOfBoundsException.
     */
    @Test(expected = OutOfBoundsException.class)
    public void shouldNotSetWallOutOfBounds() throws OutOfBoundsException {
        board.setWall(-1, 0);
    }
}
