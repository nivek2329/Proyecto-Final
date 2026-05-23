package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import main.domain.AcceleratedEnemy;
import main.domain.BasicEnemy;
import main.domain.Bomb;
import main.domain.GameConfiguration;
import main.domain.HardestGameException;
import main.domain.LifeSource;
import main.domain.PatrolEnemy;
import main.domain.SkinCoin;
import main.domain.VerticalEnemy;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clase de pruebas unitarias para la clase GameConfiguration.
 * Verifica la correcta carga y validación de archivos de configuración
 * de niveles, asegurando que los parámetros mínimos obligatorios
 * sean interpretados correctamente y que las configuraciones inválidas
 * generen las excepciones correspondientes.
 *
 * Las pruebas incluyen validación de dimensiones, tiempo límite,
 * creación de enemigos, power-ups y detección de omisión de zonas obligatorias.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class GameConfigurationTest {

    private GameConfiguration cfg;

    private static final String MINIMAL = String.join("\n",
            "ROWS 5",
            "COLS 10",
            "TIME 60",
            "SAFE_START 0 0 5 2",
            "SAFE_FINAL 0 8 5 2",
            "COIN YELLOW 2 4",
            "ENEMY BASIC 2 5 VERTICAL",
            "");

    /**
     * Inicializa el cargador de configuración antes de cada prueba.
     */
    @Before
    public void setUp() {
        cfg = new GameConfiguration();
    }

    /**
     * Libera el cargador de configuración después de cada prueba.
     */
    @After
    public void tearDown() {
        cfg = null;
    }

    /**
     * Verifica carga correcta de un archivo de configuración mínimo.
     */
    @Test
    public void shouldCargaArchivoMinimo() throws Exception {
        Path f = Files.createTempFile("dopo-test-", ".txt");
        Files.writeString(f, MINIMAL);

        cfg.load(f.toString());

        assertEquals("Las filas deben ser 5", 5, cfg.getRows());
        assertEquals("Las columnas deben ser 10", 10, cfg.getCols());
        assertEquals("El tiempo limite debe ser 60", 60, cfg.getTimeLimit());
        assertEquals("Debe haber exactamente 1 moneda", 1, cfg.getCoins().size());
        assertEquals("Debe haber exactamente 1 enemigo", 1, cfg.getEnemies().size());
        assertTrue("El enemigo debe ser una instancia de BasicEnemy",
                cfg.getEnemies().get(0) instanceof BasicEnemy);
    }

    /**
     * Comprueba lectura correcta de direcciones horizontales LEFT y RIGHT.
     */
    @Test
    public void shouldHorizontalLeftRightSteps() throws Exception {
        String body = String.join("\n",
                "ROWS 3", "COLS 8", "TIME 10",
                "SAFE_START 0 0 3 2",
                "SAFE_FINAL 0 6 3 2",
                "ENEMY BASIC 1 4 HORIZONTAL LEFT",
                "ENEMY BASIC 1 5 HORIZONTAL RIGHT",
                "");

        Path f = Files.createTempFile("dopo-test2-", ".txt");
        Files.writeString(f, body);

        cfg.load(f.toString());

        assertEquals(
                "Deben haberse cargado 2 enemigos con direcciones LEFT y RIGHT",
                2, cfg.getEnemies().size()
        );
    }

    /**
     * Verifica carga correcta de power-ups (bomba y fuente de vida).
     */
    @Test
    public void shouldCargaPowerUps() throws Exception {
        String body = String.join("\n",
                "ROWS 5", "COLS 10", "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "POWERUP BOMB 2 3",
                "POWERUP LIFE 3 4",
                "");

        Path f = Files.createTempFile("dopo-powerup-test-", ".txt");
        Files.writeString(f, body);

        cfg.load(f.toString());

        assertEquals("Debe haber 2 power-ups", 2, cfg.getPowerUps().size());
        assertTrue("El primer power-up debe ser Bomb",
                cfg.getPowerUps().get(0) instanceof Bomb);
        assertTrue("El segundo power-up debe ser LifeSource",
                cfg.getPowerUps().get(1) instanceof LifeSource);
        assertEquals("La bomba debe estar en (2,3)", 2, cfg.getPowerUps().get(0).getRow());
        assertEquals("La bomba debe estar en col 3", 3, cfg.getPowerUps().get(0).getCol());
        assertEquals("La fuente de vida debe estar en (3,4)", 3, cfg.getPowerUps().get(1).getRow());
        assertEquals("La fuente de vida debe estar en col 4", 4, cfg.getPowerUps().get(1).getCol());
    }

    /**
     * Rechaza monedas colocadas sobre un muro.
     */
    @Test(expected = HardestGameException.class)
    public void shouldNotCoinOnWall() throws Exception {
        final String body = String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "WALL 2 4",
                "COIN YELLOW 2 4",
                "");
        final Path f = Files.createTempFile("dopo-coin-wall-", ".txt");
        Files.writeString(f, body);
        cfg.load(f.toString());
    }

    /**
     * Carga enemigos VERTICAL, ACCEL y PATROL junto con monedas skin.
     */
    @Test
    public void shouldCargaEnemigosEspecialesYSkinCoins() throws Exception {
        final String body = String.join("\n",
                "ROWS 7", "COLS 16", "TIME 60",
                "SAFE_START 0 0 7 3",
                "SAFE_FINAL 0 13 7 3",
                "COIN YELLOW 3 8",
                "COIN SKIN BLUE 4 5",
                "COIN SKIN GREEN 5 10",
                "POWERUP LIFE 2 8",
                "POWERUP BOMB 4 8",
                "ENEMY BASIC 2 7 VERTICAL",
                "ENEMY VERTICAL 1 11",
                "ENEMY ACCEL 5 12 HORIZONTAL LEFT",
                "ENEMY PATROL 2 2 3 4",
                "");

        final Path f = Files.createTempFile("dopo-enemies-", ".txt");
        Files.writeString(f, body);
        cfg.load(f.toString());

        assertEquals(3, cfg.getCoins().size());
        assertTrue(cfg.getCoins().get(1) instanceof SkinCoin);
        assertEquals(4, cfg.getEnemies().size());
        assertTrue(cfg.getEnemies().get(0) instanceof BasicEnemy);
        assertTrue(cfg.getEnemies().get(1) instanceof VerticalEnemy);
        assertTrue(cfg.getEnemies().get(2) instanceof AcceleratedEnemy);
        assertTrue(cfg.getEnemies().get(3) instanceof PatrolEnemy);
    }

    /**
     * Verifica lanzamiento de excepción cuando falta la zona segura inicial.
     */
    @Test(expected = HardestGameException.class)
    public void shouldNotFallaSinSafeStart() throws Exception {
        Path f = Files.createTempFile("dopo-test3-", ".txt");
        Files.writeString(f,
                "ROWS 2\n" +
                        "COLS 2\n" +
                        "TIME 60\n" +
                        "SAFE_FINAL 0 0 2 2\n");

        cfg.load(f.toString());
    }

    private void runConfigError(String content, String expectedErrorMsgPrefix) throws Exception {
        Path f = Files.createTempFile("dopo-bad-cfg-", ".txt");
        Files.writeString(f, content);
        GameConfiguration localCfg = new GameConfiguration();
        try {
            localCfg.load(f.toString());
            fail("Debió lanzar excepción para la configuración inválida: " + content);
        } catch (HardestGameException ex) {
            // expected
        }
    }

    @Test
    public void testErrorParsingBranches() throws Exception {
        // Dimensiones faltantes o inválidas
        runConfigError("ROWS\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\n", "ROWS o COLS requiere un valor numérico");
        runConfigError("ROWS -5\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\n", "Dimensiones inválidas");
        runConfigError("ROWS 5\nCOLS 10\nTIME 5\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\n", "Tiempo menor al mínimo");

        // Zonas faltantes o mal formadas
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_FINAL 0 8 5 2\n", "Falta SAFE_START");
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\n", "Falta SAFE_FINAL");
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_START 0 0\nSAFE_FINAL 0 8 5 2\n", "ZONA requiere fila, col, filas y cols");

        // Enemigos inválidos
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\nENEMY\n", "ENEMY requiere tipo");
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\nENEMY UNKNOWN 1 1\n", "Tipo de enemigo desconocido");
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\nENEMY SPINNER 1 1 1\n", "SPINNER requiere");
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\nENEMY BASIC 1 1\n", "BASIC requiere");
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\nENEMY BASIC 1 1 VERTICAL LEFT\n", "no aplica con dirección VERTICAL");
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\nENEMY ACCEL 1 1\n", "ACCEL requiere");
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\nENEMY ACCEL 1 1 DIAGONAL\n", "Dirección ACCEL inválida");
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\nENEMY ACCEL 1 1 VERTICAL LEFT\n", "no aplica con dirección VERTICAL");
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\nENEMY PATROL 1 1 1 1 1\n", "PATROL requiere");
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\nENEMY BASIC 1 1 DIAGONAL\n", "Dirección de enemigo inválida");
        runConfigError("ROWS 5\nCOLS 10\nTIME 60\nSAFE_START 0 0 5 2\nSAFE_FINAL 0 8 5 2\nENEMY BASIC 1 1 HORIZONTAL UP\n", "Sentido horizontal inválido");
        
        // Archivo no encontrado
        try {
            cfg.load("archivo_no_existente_987654321.txt");
            fail("Debió lanzar excepcion de archivo no encontrado");
        } catch (HardestGameException ex) {
            // expected
        }

        // Comandos desconocidos y líneas de comentario/vacías no deben romper la carga
        String validWithComments = String.join("\n",
            "# Un comentario",
            "",
            "ROWS 5",
            "COLS 10",
            "TIME 60",
            "UNKNOWN_CMD 1 2 3",
            "SAFE_START 0 0 5 2",
            "SAFE_FINAL 0 8 5 2",
            "");
        Path f = Files.createTempFile("dopo-comments-", ".txt");
        Files.writeString(f, validWithComments);
        cfg.load(f.toString());
        assertEquals(5, cfg.getRows());
    }
}
