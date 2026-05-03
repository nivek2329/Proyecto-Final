package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import domain.YellowCoin;

/**
 * Clase de pruebas unitarias para la clase YellowCoin.
 * Verifica el comportamiento de la moneda amarilla en cuanto
 * a su estado de recolección, reinicio, posición, tipo y
 * representación visual.
 *
 * Estas pruebas garantizan que la implementación concreta de
 * la moneda funcione correctamente dentro del dominio del juego.
 *
 * @return conjunto de pruebas unitarias para la moneda amarilla
 * @author Angel-Garcia
 * @date 2026-1
 */
public class YellowCoinTest {

    private YellowCoin coin;

    /**
     * @return inicialización de una moneda amarilla antes de cada prueba
     */
    @Before
    public void setUp() {
        coin = new YellowCoin(3, 5);
    }

    /**
     * @return liberación del objeto moneda después de cada prueba
     */
    @After
    public void tearDown() {
        coin = null;
    }

    /**
     * @return verificación del ciclo completo de recolección y reinicio de la moneda
     */
    @Test
    public void shouldCollectYReset() {
        assertFalse(
                "La moneda no debe estar recogida al inicio",
                coin.isCollected()
        );

        coin.collect();
        assertTrue(
                "La moneda debe estar recogida despues de collect()",
                coin.isCollected()
        );

        coin.reset();
        assertFalse(
                "La moneda debe volver a no recogida despues de reset()",
                coin.isCollected()
        );
    }

    /**
     * @return comprobación del tipo de la moneda y su posición en el tablero
     */
    @Test
    public void shouldTipoYPosicion() {
        YellowCoin c = new YellowCoin(2, 8);

        assertEquals(
                "La fila de la moneda debe ser 2",
                2,
                c.getRow()
        );
        assertEquals(
                "La columna de la moneda debe ser 8",
                8,
                c.getCol()
        );
        assertEquals(
                "El tipo de la moneda debe ser YELLOW",
                "YELLOW",
                c.getType()
        );
    }

    /**
     * @return verificación de la representación visual de la moneda amarilla
     */
    @Test
    public void shouldGetColorYellow() {
        assertNotNull(
                "El color de la moneda no debe ser null",
                coin.getColor()
        );
        assertEquals(
                "El color de la moneda debe ser Color.YELLOW",
                java.awt.Color.YELLOW,
                coin.getColor()
        );
    }
}