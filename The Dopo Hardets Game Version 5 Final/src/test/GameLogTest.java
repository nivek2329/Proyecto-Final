package test;

import static org.junit.Assert.*;
import org.junit.Test;
import main.domain.GameLog;
import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * Pruebas de cobertura para {@link GameLog}.
 * Cubre la rama de inicialización normal (primera llamada),
 * la rama ya-inicializado (segunda llamada) y la rama catch-IOException
 * forzando un fallo en la creación del FileHandler.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class GameLogTest {

    /**
     * Utilidad: resetea la bandera {@code initialized} de GameLog vía reflexión.
     */
    private static void resetInitialized(final boolean value) throws Exception {
        final Field f = GameLog.class.getDeclaredField("initialized");
        f.setAccessible(true);
        f.setBoolean(null, value);
    }

    /**
     * Verifica que {@code getLogger} retorna un Logger no-nulo (rama normal).
     */
    @Test
    public void shouldReturnNonNullLogger() {
        final Logger log = GameLog.getLogger(GameLogTest.class);
        assertNotNull("getLogger debe retornar un Logger no nulo", log);
    }

    /**
     * Verifica que una segunda llamada a {@code getLogger} utiliza la
     * rama ya-inicializado (retorno temprano sin re-crear handlers).
     */
    @Test
    public void shouldReturnLoggerOnSecondCall() {
        final Logger first  = GameLog.getLogger(GameLogTest.class);
        final Logger second = GameLog.getLogger(GameLogTest.class);
        assertNotNull("Primera llamada no debe ser null", first);
        assertNotNull("Segunda llamada no debe ser null", second);
        assertEquals("El nombre del logger debe ser el mismo en ambas llamadas",
                first.getName(), second.getName());
    }

    /**
     * Cubre la rama catch-IOException colocando un directorio donde el
     * FileHandler espera encontrar un fichero, de forma que la creación
     * del handler falla sin lanzar una excepción hacia arriba.
     *
     * El test verifica que GameLog no propaga la excepción y que
     * {@code getLogger} sigue retornando un Logger válido.
     */
    /**
     * Cubre la rama catch-IOException reseteando {@code initialized} mientras
     * el fichero de log ya está abierto por el FileHandler activo.
     * En ese caso Windows devuelve un error de acceso, GameLog lo absorbe
     * sin propagar la excepción, y {@code getLogger} sigue siendo funcional.
     */
    @Test
    public void shouldHandleIOExceptionGracefully() throws Exception {
        final Field f = GameLog.class.getDeclaredField("initialized");
        f.setAccessible(true);
        final boolean originalValue = f.getBoolean(null);
        try {
            // Primero nos aseguramos de que el log fue inicializado al menos una vez
            GameLog.getLogger(GameLogTest.class);

            // Reseteamos el flag → ensureInitialized() intentará crear de nuevo el FileHandler
            // sobre un fichero ya abierto (error de Windows) → IOException → catch absorbido
            f.setBoolean(null, false);
            final Logger logger = GameLog.getLogger(GameLogTest.class);

            // getLogger no debe lanzar excepción ni retornar null
            assertNotNull("getLogger debe retornar Logger incluso tras fallo de re-init", logger);
        } finally {
            f.setBoolean(null, originalValue);
        }
    }

    /**
     * Verifica que el nombre del logger coincide con la clase solicitada.
     */
    @Test
    public void shouldReturnLoggerWithCorrectName() {
        final Logger logger = GameLog.getLogger(String.class);
        assertEquals("El nombre del logger debe ser el nombre completo de la clase",
                String.class.getName(), logger.getName());
    }
}
