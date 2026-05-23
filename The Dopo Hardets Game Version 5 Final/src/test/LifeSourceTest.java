package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import main.domain.LifeSource;

/**
 * Clase de pruebas unitarias para la clase LifeSource.
 * Verifica el comportamiento de la fuente de vida en cuanto
 * a su estado de consumo, uso permanente, reinicio, posición,
 * tipo y representación visual.
 *
 * Estas pruebas garantizan que la implementación concreta de
 * la fuente de vida funcione correctamente dentro del dominio del juego.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class LifeSourceTest {

    private LifeSource lifeSource;

    /**
     * Inicializa una fuente de vida antes de cada prueba.
     */
    @Before
    public void setUp() {
        lifeSource = new LifeSource(3, 5);
    }

    /**
     * Libera el objeto fuente de vida después de cada prueba.
     */
    @After
    public void tearDown() {
        lifeSource = null;
    }

    /**
     * Verifica el ciclo completo de consumo y uso permanente de la fuente de vida.
     */
    @Test
    public void shouldConsumeYMarkPermanentlyUsed() {
        assertFalse(
                "La fuente de vida no debe estar consumida al inicio",
                lifeSource.isConsumed()
        );
        assertFalse(
                "La fuente de vida no debe estar permanentemente usada al inicio",
                lifeSource.isPermanentlyUsed()
        );

        lifeSource.consume();
        lifeSource.markPermanentlyUsed();
        assertTrue(
                "La fuente de vida debe estar consumida despues de consume()",
                lifeSource.isConsumed()
        );
        assertTrue(
                "La fuente de vida debe estar permanentemente usada despues de markPermanentlyUsed()",
                lifeSource.isPermanentlyUsed()
        );
    }

    /**
     * Comprueba el tipo de la fuente de vida y su posición en el tablero.
     */
    @Test
    public void shouldTipoYPosicion() {
        LifeSource ls = new LifeSource(2, 8);

        assertEquals(
                "La fila de la fuente de vida debe ser 2",
                2,
                ls.getRow()
        );
        assertEquals(
                "La columna de la fuente de vida debe ser 8",
                8,
                ls.getCol()
        );
        assertEquals(
                "El tipo de la fuente de vida debe ser LIFE_SOURCE",
                "LIFE_SOURCE",
                ls.getType()
        );
    }

    /**
     * Verifica la representación visual de la fuente de vida.
     */
    @Test
    public void shouldGetColorPink() {
        assertNotNull(
                "El color de la fuente de vida no debe ser null",
                lifeSource.getColor()
        );
        assertEquals(
                "El color de la fuente de vida debe ser el color núcleo definido",
                LifeSource.CORE_COLOR,
                lifeSource.getColor()
        );
    }

    /**
     * Verifica que el reset no afecte el estado permanente de uso.
     */
    @Test
    public void shouldResetNotAffectPermanentUse() {
        lifeSource.consume();
        lifeSource.markPermanentlyUsed();
        lifeSource.reset();

        assertTrue(
                "El reset no debe desmarcar consumo si ya es permanente",
                lifeSource.isConsumed()
        );
        assertTrue(
                "El reset NO debe afectar isPermanentlyUsed",
                lifeSource.isPermanentlyUsed()
        );
        assertFalse(
                "No debe mostrarse en tablero tras uso permanente",
                lifeSource.isVisibleOnBoard()
        );
    }

    /**
     * Prueba el método restoreLifeSourceSavedState y la visibilidad inicial
     * para completar cobertura de ramas al 100%.
     */
    @Test
    public void testAdditionalCoverage() {
        // Visibilidad inicial
        assertTrue(lifeSource.isVisibleOnBoard());

        // Restauración de estado guardado
        lifeSource.restoreLifeSourceSavedState(true, false);
        assertTrue(lifeSource.isConsumed());
        assertFalse(lifeSource.isPermanentlyUsed());

        // Si no es permanente, reset() restablece consumed a false
        lifeSource.reset();
        assertFalse(lifeSource.isConsumed());

        // Restaurar a consumido y permanente
        lifeSource.restoreLifeSourceSavedState(true, true);
        assertTrue(lifeSource.isConsumed());
        assertTrue(lifeSource.isPermanentlyUsed());
    }
}
