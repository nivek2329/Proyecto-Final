package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import main.domain.Bomb;
import main.domain.LifeSource;
import main.domain.PowerUp;

/**
 * Clase de pruebas unitarias para la clase abstracta PowerUp.
 * Verifica el comportamiento base compartido por todas las implementaciones
 * de power-ups: posición, estado de consumo y reinicio.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class PowerUpTest {

    private PowerUp bomb;
    private PowerUp lifeSource;

    /**
     * Inicializa power-ups de prueba antes de cada test.
     */
    @Before
    public void setUp() {
        bomb = new Bomb(1, 2);
        lifeSource = new LifeSource(3, 4);
    }

    /**
     * Libera los recursos después de cada test.
     */
    @After
    public void tearDown() {
        bomb = null;
        lifeSource = null;
    }

    /**
     * Verifica que las posiciones iniciales sean correctas.
     */
    @Test
    public void shouldPosicionesInicialesCorrectas() {
        assertEquals("La fila de la bomba debe ser 1", 1, bomb.getRow());
        assertEquals("La columna de la bomba debe ser 2", 2, bomb.getCol());
        assertEquals("La fila de la fuente de vida debe ser 3", 3, lifeSource.getRow());
        assertEquals("La columna de la fuente de vida debe ser 4", 4, lifeSource.getCol());
    }

    /**
     * Verifica que el estado inicial no sea consumido.
     */
    @Test
    public void shouldNoConsumidosAlInicio() {
        assertFalse("La bomba no debe estar consumida al inicio", bomb.isConsumed());
        assertFalse("La fuente de vida no debe estar consumida al inicio", lifeSource.isConsumed());
    }

    /**
     * Verifica el ciclo de consumo y reinicio.
     */
    @Test
    public void shouldConsumoYReinicio() {
        bomb.consume();
        assertTrue("La bomba debe estar consumida", bomb.isConsumed());

        bomb.reset();
        assertFalse("La bomba debe volver a no consumida", bomb.isConsumed());
    }

    /**
     * Verifica que los colores no sean null.
     */
    @Test
    public void shouldColoresNoNull() {
        assertNotNull("El color de la bomba no debe ser null", bomb.getColor());
        assertNotNull("El color de la fuente de vida no debe ser null", lifeSource.getColor());
    }

    /**
     * Verifica que los tipos sean distintos.
     */
    @Test
    public void shouldTiposDistintos() {
        assertEquals("BOMB", bomb.getType());
        assertEquals("LIFE_SOURCE", lifeSource.getType());
        assertNotEquals("Los tipos deben ser distintos", bomb.getType(), lifeSource.getType());
    }
}
