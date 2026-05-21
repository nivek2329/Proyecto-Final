package test;

import static org.junit.Assert.*;
import org.junit.Test;
import main.domain.*;
import main.domain.HardestGameException.*;

/**
 * Clase de pruebas unitarias para todas las excepciones del dominio.
 * Incrementa la cobertura de código para llegar a 100 pruebas.
 */
public class ExceptionsTest {

    @Test
    public void testCoinAlreadyCollectedException() {
        CoinAlreadyCollectedException ex = new CoinAlreadyCollectedException("test message");
        assertEquals("test message", ex.getMessage());
        assertEquals(HardestGameException.ErrorType.GAME_LOGIC, ex.getErrorType());
    }

    @Test
    public void testGameAlreadyRunningException() {
        GameAlreadyRunningException ex = new GameAlreadyRunningException("test message");
        assertEquals("test message", ex.getMessage());
        assertEquals(HardestGameException.ErrorType.GAME_LOGIC, ex.getErrorType());
    }

    @Test
    public void testHardestGameExceptionConstructors() {
        HardestGameException ex1 = new HardestGameException("msg");
        assertEquals("msg", ex1.getMessage());
        assertEquals(HardestGameException.ErrorType.UNKNOWN, ex1.getErrorType());
        assertTrue(ex1.toString().contains("UNKNOWN"));

        Throwable cause = new RuntimeException("root cause");
        HardestGameException ex2 = new HardestGameException("msg", cause);
        assertEquals(cause, ex2.getCause());
        assertTrue(ex2.toString().contains("root cause"));

        HardestGameException ex3 = new HardestGameException("msg", HardestGameException.ErrorType.CONFIG_IO);
        assertEquals(HardestGameException.ErrorType.CONFIG_IO, ex3.getErrorType());

        HardestGameException ex4 = new HardestGameException("msg", HardestGameException.ErrorType.CONFIG_INVALID, cause);
        assertEquals(HardestGameException.ErrorType.CONFIG_INVALID, ex4.getErrorType());
        assertEquals(cause, ex4.getCause());
    }

    @Test
    public void testInsufficientTimeException() {
        InsufficientTimeException ex = new InsufficientTimeException("test message");
        assertEquals("test message", ex.getMessage());
        assertEquals(HardestGameException.ErrorType.CONFIG_INVALID, ex.getErrorType());
    }

    @Test
    public void testInvalidGameStateException() {
        InvalidGameStateException ex = new InvalidGameStateException("test message");
        assertEquals("test message", ex.getMessage());
        assertEquals(HardestGameException.ErrorType.GAME_LOGIC, ex.getErrorType());
    }

    @Test
    public void testInvalidLevelFormatException() {
        InvalidLevelFormatException ex1 = new InvalidLevelFormatException("test message");
        assertEquals("test message", ex1.getMessage());
        assertEquals(HardestGameException.ErrorType.CONFIG_INVALID, ex1.getErrorType());

        Throwable cause = new RuntimeException("error");
        InvalidLevelFormatException ex2 = new InvalidLevelFormatException("test message", cause);
        assertEquals("test message", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
        assertEquals(HardestGameException.ErrorType.CONFIG_INVALID, ex2.getErrorType());
    }

    @Test
    public void testInvalidMovementException() {
        InvalidMovementException ex = new InvalidMovementException("test message");
        assertEquals("test message", ex.getMessage());
        assertEquals(HardestGameException.ErrorType.CONFIG_INVALID, ex.getErrorType());
    }

    @Test
    public void testInvalidMultiplayerConfigException() {
        InvalidMultiplayerConfigException ex = new InvalidMultiplayerConfigException("test message");
        assertEquals("test message", ex.getMessage());
        assertEquals(HardestGameException.ErrorType.CONFIG_INVALID, ex.getErrorType());
    }

    @Test
    public void testInvalidSkinException() {
        InvalidSkinException ex = new InvalidSkinException("test message");
        assertEquals("test message", ex.getMessage());
        assertEquals(HardestGameException.ErrorType.CONFIG_INVALID, ex.getErrorType());
    }

    @Test
    public void testLevelAccessDeniedException() {
        Throwable cause = new RuntimeException("error");
        LevelAccessDeniedException ex = new LevelAccessDeniedException("test message", cause);
        assertEquals("test message", ex.getMessage());
        assertEquals(cause, ex.getCause());
        assertEquals(HardestGameException.ErrorType.CONFIG_IO, ex.getErrorType());
    }

    @Test
    public void testLevelNotFoundException() {
        Throwable cause = new RuntimeException("error");
        LevelNotFoundException ex = new LevelNotFoundException("test message", cause);
        assertEquals("test message", ex.getMessage());
        assertEquals(cause, ex.getCause());
        assertEquals(HardestGameException.ErrorType.CONFIG_IO, ex.getErrorType());
    }

    @Test
    public void testMissingEntityException() {
        MissingEntityException ex = new MissingEntityException("test message");
        assertEquals("test message", ex.getMessage());
        assertEquals(HardestGameException.ErrorType.CONFIG_INVALID, ex.getErrorType());
    }

    @Test
    public void testOutOfBoundsException() {
        OutOfBoundsException ex = new OutOfBoundsException("test message");
        assertEquals("test message", ex.getMessage());
        assertEquals(HardestGameException.ErrorType.GAME_LOGIC, ex.getErrorType());
    }

    @Test
    public void testHardestGameExceptionNullErrorType() {
        HardestGameException ex = new HardestGameException("test message", (HardestGameException.ErrorType) null);
        assertEquals("test message", ex.getMessage());
        assertEquals(HardestGameException.ErrorType.UNKNOWN, ex.getErrorType());
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
}
