package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import main.domain.BasicEnemy;
import main.domain.Board;
import main.domain.HardestGameException;
import main.domain.Zone;

/**
 * Clase de pruebas unitarias para la clase BasicEnemy.
 * Verifica el comportamiento del enemigo básico en sus
 * movimientos horizontales y verticales, así como su
 * interacción con muros y zonas prohibidas para enemigos.
 *
 * Estas pruebas aseguran que el patrón de movimiento,
 * el rebote ante obstáculos y la representación visual
 * del enemigo funcionen correctamente.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class BasicEnemyTest {

    private Board board;

    /**
     * Inicializa el tablero antes de cada prueba.
     */
    @Before
    public void setUp() {
        board = new Board(10, 10);
    }

    /**
     * Libera el tablero después de cada prueba.
     */
    @After
    public void tearDown() {
        board = null;
    }

    /**
     * Verifica el avance horizontal del enemigo tras cuatro ticks.
     */
    @Test
    public void shouldHorizontalIzquierdaAvanzaDespuesDeCuatroTicks() throws Exception {
        Zone z = new Zone(0, 0, 3, 2, Zone.Type.INITIAL);
        board.addEnemyForbiddenZone(z);

        BasicEnemy e = new BasicEnemy(1, 4, BasicEnemy.Direction.HORIZONTAL, 1);
        for (int i = 0; i < 4; i++) e.update(board);

        assertEquals(
                "El enemigo deberia haber avanzado una columna a la derecha",
                5, e.getCol()
        );
    }

    /**
     * Comprueba el rebote horizontal del enemigo al colisionar con un muro.
     */
    @Test
    public void shouldHorizontalRebotaEnPared() throws Exception {
        board.setWall(1, 9);

        BasicEnemy e = new BasicEnemy(1, 8, BasicEnemy.Direction.HORIZONTAL, 1);
        for (int i = 0; i < 4; i++) e.update(board);

        assertEquals(
                "El enemigo deberia haber rebotado y estar en col 7",
                7, e.getCol()
        );
    }

    /**
     * Verifica que el enemigo no entre en zonas seguras.
     */
    @Test
    public void shouldNotHorizontalEntraEnZonaVerdeEnemigo() throws Exception {
        Zone green = new Zone(0, 0, 3, 3, Zone.Type.INITIAL);
        board.addEnemyForbiddenZone(green);

        BasicEnemy e = new BasicEnemy(1, 3, BasicEnemy.Direction.HORIZONTAL, -1);
        for (int i = 0; i < 4; i++) e.update(board);

        assertEquals(
                "El enemigo no debe entrar en la zona verde, debe rebotar a col 4",
                4, e.getCol()
        );
    }

    /**
     * Comprueba el movimiento vertical del enemigo tras los ticks requeridos.
     */
    @Test
    public void shouldVerticalDesdeArribaSeMueveConTick() {
        BasicEnemy e = new BasicEnemy(1, 2, BasicEnemy.Direction.VERTICAL, 1);
        for (int i = 0; i < 4; i++) e.update(board);

        assertEquals(
                "El enemigo vertical deberia estar en fila 2",
                2, e.getRow()
        );
    }

    /**
     * Verifica que el enemigo tenga un color válido.
     */
    @Test
    public void shouldGetColorBlue() {
        BasicEnemy e = new BasicEnemy(1, 1, BasicEnemy.Direction.HORIZONTAL, 1);
        assertNotNull(
                "El color del enemigo no debe ser null",
                e.getColor()
        );
    }

    /**
     * Prueba métodos adicionales de cobertura para alcanzar el 100%.
     */
    @Test
    public void testAdditionalCoverage() throws Exception {
        // Constructor horizontal con paso negativo
        BasicEnemy e1 = new BasicEnemy(1, 1, BasicEnemy.Direction.HORIZONTAL, -1);
        assertEquals(1, e1.getCol());

        // Snapshot y restauración
        e1.restoreSavedState(5, 5, 0, 1, 3);
        assertEquals(5, e1.getRow());
        assertEquals(5, e1.getCol());
        assertNotNull(e1.toSnapshot());

        // Escenario de enemigo bloqueado en ambas direcciones
        board.setWall(1, 0);
        board.setWall(1, 2);
        BasicEnemy stuck = new BasicEnemy(1, 1, BasicEnemy.Direction.HORIZONTAL, 1);
        for (int i = 0; i < 4; i++) {
            stuck.update(board);
        }
        // Debería intentar ir a 2, chocar, rebotar e intentar ir a 0, chocar y quedarse en 1.
        assertEquals(1, stuck.getCol());
    }
}
