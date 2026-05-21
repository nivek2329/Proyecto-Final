package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import main.domain.Bomb;

/**
 * Clase de pruebas unitarias para la clase Bomb.
 * Verifica el comportamiento de la bomba en cuanto
 * a su estado de consumo, reinicio, posición, tipo y
 * representación visual.
 *
 * Estas pruebas garantizan que la implementación concreta de
 * la bomba funcione correctamente dentro del dominio del juego.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class BombTest {

    private Bomb bomb;

    /**
     * Inicializa una bomba antes de cada prueba.
     */
    @Before
    public void setUp() {
        bomb = new Bomb(3, 5);
    }

    /**
     * Libera el objeto bomba después de cada prueba.
     */
    @After
    public void tearDown() {
        bomb = null;
    }

    /**
     * Verifica el ciclo completo de consumo y reinicio de la bomba.
     */
    @Test
    public void shouldConsumeYReset() {
        assertFalse(
                "La bomba no debe estar consumida al inicio",
                bomb.isConsumed()
        );

        bomb.consume();
        assertTrue(
                "La bomba debe estar consumida despues de consume()",
                bomb.isConsumed()
        );

        bomb.reset();
        assertFalse(
                "La bomba debe volver a no consumida despues de reset()",
                bomb.isConsumed()
        );
    }

    /**
     * Comprueba el tipo de la bomba y su posición en el tablero.
     */
    @Test
    public void shouldTipoYPosicion() {
        Bomb b = new Bomb(2, 8);

        assertEquals(
                "La fila de la bomba debe ser 2",
                2,
                b.getRow()
        );
        assertEquals(
                "La columna de la bomba debe ser 8",
                8,
                b.getCol()
        );
        assertEquals(
                "El tipo de la bomba debe ser BOMB",
                "BOMB",
                b.getType()
        );
    }

    /**
     * Verifica la representación visual de la bomba.
     */
    @Test
    public void shouldGetColorBlack() {
        assertNotNull(
                "El color de la bomba no debe ser null",
                bomb.getColor()
        );
        assertEquals(
                "El color de la bomba debe ser Color.BLACK",
                java.awt.Color.BLACK,
                bomb.getColor()
        );
    }
}
