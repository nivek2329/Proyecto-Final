package test;

import static org.junit.Assert.*;
import org.junit.Test;
import main.domain.HardestGameException;
import main.domain.GameLogger;

/**
 * Clase de pruebas unitarias para la excepción unificada del dominio.
 */
public class ExceptionsTest {

    @Test
    public void testHardestGameExceptionConstructors() {
        HardestGameException ex1 = new HardestGameException("msg");
        assertEquals("msg", ex1.getMessage());

        Throwable cause = new RuntimeException("root cause");
        HardestGameException ex2 = new HardestGameException("msg", cause);
        assertEquals("msg", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    public void testHardestGameExceptionConstants() {
        assertNotNull(HardestGameException.COIN_ALREADY_COLLECTED);
        assertNotNull(HardestGameException.GAME_ALREADY_RUNNING);
        assertNotNull(HardestGameException.INSUFFICIENT_TIME);
        assertNotNull(HardestGameException.INVALID_GAME_STATE);
        assertNotNull(HardestGameException.INVALID_LEVEL_FORMAT);
        assertNotNull(HardestGameException.INVALID_MOVEMENT);
        assertNotNull(HardestGameException.INVALID_MULTIPLAYER_CONFIG);
        assertNotNull(HardestGameException.INVALID_SKIN);
        assertNotNull(HardestGameException.LEVEL_ACCESS_DENIED);
        assertNotNull(HardestGameException.LEVEL_NOT_FOUND);
        assertNotNull(HardestGameException.MISSING_ENTITY);
        assertNotNull(HardestGameException.OUT_OF_BOUNDS);
        
        // Comprobar mensajes descriptivos en español
        assertTrue(HardestGameException.COIN_ALREADY_COLLECTED.contains("recolectar"));
        assertTrue(HardestGameException.LEVEL_NOT_FOUND.contains("no encontrado"));
    }

    @Test
    public void testGameLoggerUnexpectedErrorLogging() throws java.io.IOException {
        // Log an unexpected mock exception
        Throwable testEx = new NullPointerException("Mock unexpected exception for test");
        GameLogger.logError("Mock error message", testEx);
        
        // Read the file and verify it contains our mock exception message
        java.nio.file.Path path = java.nio.file.Paths.get("logs/game_errors.log");
        assertTrue(java.nio.file.Files.exists(path));
        String content = new String(java.nio.file.Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(content.contains("Mock unexpected exception for test"));
        assertTrue(content.contains("NullPointerException"));
    }

    @Test
    public void testGameLogConstructorAndAlreadyInitialized() throws Exception {
        // Private constructor coverage
        java.lang.reflect.Constructor<main.domain.GameLog> c = main.domain.GameLog.class.getDeclaredConstructor();
        c.setAccessible(true);
        assertNotNull(c.newInstance());

        // Get logger when already initialized
        assertNotNull(main.domain.GameLog.getLogger(ExceptionsTest.class));
    }

    @Test
    public void testGameLoggerComprehensive() throws Exception {
        // Constructor coverage
        java.lang.reflect.Constructor<GameLogger> c = GameLogger.class.getDeclaredConstructor();
        c.setAccessible(true);
        assertNotNull(c.newInstance());

        // logError single argument
        GameLogger.logError("Single argument error");

        // logInfo
        GameLogger.logInfo("Information log message");

        // logUserReport with null inputs
        GameLogger.logUserReport(null, null);
        
        // logUserReport with values
        GameLogger.logUserReport("My User feedback", "Level 3");

        // logError with exception cause and extra context
        Throwable rootCause = new IllegalArgumentException("Root bad argument");
        Throwable exception = new RuntimeException("Runtime wrapper", rootCause);
        
        // We can use reflection to call the private method: logError(String, Throwable, String)
        java.lang.reflect.Method m = GameLogger.class.getDeclaredMethod("logError", String.class, Throwable.class, String.class);
        m.setAccessible(true);
        m.invoke(null, "Test error message", exception, "Custom Extra Context");

        // Verify report file
        java.nio.file.Path path = java.nio.file.Paths.get("logs/user_reports.log");
        assertTrue(java.nio.file.Files.exists(path));
        String content = new String(java.nio.file.Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(content.contains("REPORTE DE USUARIO"));
        assertTrue(content.contains("desconocido"));
        assertTrue(content.contains("My User feedback"));
        assertTrue(content.contains("Level 3"));
    }
}
