package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import domain.BasicEnemy;
import domain.GameConfiguration;
import domain.HardestGameException;
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
 * creación de enemigos y detección de omisión de zonas obligatorias.
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
     * Verifica lanzamiento de excepción cuando falta la zona segura inicial.
     */
    @Test(expected = HardestGameException.class)
    public void shouldNotFallaSinSafeStart() throws Exception {
        Path f = Files.createTempFile("dopo-test3-", ".txt");
        Files.writeString(f,
                "ROWS 2\n" +
                        "COLS 2\n" +
                        "TIME 1\n" +
                        "SAFE_FINAL 0 0 2 2\n");

        cfg.load(f.toString());
    }
}