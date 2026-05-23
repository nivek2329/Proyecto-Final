package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import main.domain.HardestGame;
import main.domain.HardestGameException;
import main.domain.LifeSource;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clase de pruebas de integración para power-ups dentro de HardestGame.
 * Verifica el comportamiento completo de bombas y fuentes de vida
 * en el contexto del juego: muerte instantánea por bomba, acumulación
 * de vidas extra, y uso de vidas extra al colisionar con enemigos.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class PowerUpIntegrationTest {

    private HardestGame game;

    /**
     * Retorna la definición textual de un nivel con bomba.
     */
    private String levelWithBomb() {
        return String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "COIN YELLOW 2 4",
                "POWERUP BOMB 2 3",
                "ENEMY BASIC 4 6 VERTICAL",
                "");
    }

    /**
     * Retorna la definición textual de un nivel con fuente de vida.
     */
    private String levelWithLifeSource() {
        return String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "COIN YELLOW 2 4",
                "POWERUP LIFE 2 3",
                "ENEMY BASIC 4 6 VERTICAL",
                "");
    }

    /**
     * Retorna la definición textual de un nivel con ambos power-ups.
     */
    private String levelWithBothPowerUps() {
        return String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "COIN YELLOW 2 4",
                "POWERUP BOMB 2 3",
                "POWERUP LIFE 2 5",
                "ENEMY BASIC 4 6 VERTICAL",
                "");
    }

    /**
     * Inicializa el entorno de prueba antes de cada test.
     */
    @Before
    public void setUp() throws Exception {
        game = new HardestGame();
    }

    /**
     * Libera el entorno de prueba después de cada test.
     */
    @After
    public void tearDown() {
        game = null;
    }

    /**
     * Verifica que la bomba mata instantáneamente al jugador.
     */
    @Test
    public void shouldBombKillsInstantly() throws Exception {
        Path f = Files.createTempFile("dopo-bomb-", ".txt");
        Files.writeString(f, levelWithBomb());
        game.loadConfiguration(f.toString());

        int deathsBefore = game.getDeaths();
        // Mover hacia la bomba en (2,3)
        game.movePlayer(0, 1); // col 1
        game.movePlayer(0, 1); // col 2
        game.movePlayer(0, 1); // col 3 -> toca bomba

        assertEquals(
                "El jugador debe haber muerto por la bomba",
                deathsBefore + 1,
                game.getDeaths()
        );
        assertEquals(
                "La bomba debe estar consumida",
                1,
                game.getPowerUps().stream().filter(p -> p.isConsumed()).count()
        );
    }

    /**
     * Verifica que la fuente de vida otorga una vida extra.
     */
    @Test
    public void shouldLifeSourceGivesExtraLife() throws Exception {
        Path f = Files.createTempFile("dopo-life-", ".txt");
        Files.writeString(f, levelWithLifeSource());
        game.loadConfiguration(f.toString());

        assertEquals(
                "No debe haber vidas extra al inicio",
                0,
                game.getExtraLives()
        );

        // Mover hacia la fuente de vida en (2,3)
        game.movePlayer(0, 1); // col 1
        game.movePlayer(0, 1); // col 2
        game.movePlayer(0, 1); // col 3 -> toca fuente de vida

        assertEquals(
                "Debe haber 1 vida extra tras recoger la fuente de vida",
                1,
                game.getExtraLives()
        );
        assertTrue(
                "La fuente de vida debe estar consumida",
                game.getPowerUps().get(0).isConsumed()
        );
    }

    /**
     * Verifica que la vida extra protege al jugador de una muerte por enemigo.
     */
    @Test
    public void shouldExtraLifeProtectsFromEnemy() throws Exception {
        String level = String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "COIN YELLOW 2 4",
                "POWERUP LIFE 2 3",
                "ENEMY BASIC 2 6 VERTICAL",
                "");

        Path f = Files.createTempFile("dopo-life-protect-", ".txt");
        Files.writeString(f, level);
        game.loadConfiguration(f.toString());

        // Recoger fuente de vida
        game.movePlayer(0, 1);
        game.movePlayer(0, 1);
        game.movePlayer(0, 1); // toca fuente de vida
        assertEquals("Debe tener 1 vida extra", 1, game.getExtraLives());

        // Recoger moneda
        game.movePlayer(0, 1);
        assertEquals("Debe tener 1 moneda", 1, game.getCoinsCollected());

        // Mover hacia el enemigo en (2,6)
        game.movePlayer(0, 1); // col 5
        int deathsBefore = game.getDeaths();
        game.movePlayer(0, 1); // col 6 -> colisión con enemigo, usa vida extra

        assertEquals(
                "No debe aumentar las muertes al absorber con vida extra",
                deathsBefore,
                game.getDeaths()
        );
        assertTrue(
                "Debe quedar inmune brevemente tras absorber",
                game.getPlayer().isHitImmune()
        );
        assertEquals(
                "Las vidas extra deben ser 0 tras usar una",
                0,
                game.getExtraLives()
        );
        assertEquals(
                "Las monedas NO deben resetearse al usar vida extra",
                1,
                game.getCoinsCollected()
        );
    }

    /**
     * Verifica que la fuente de vida no reaparece tras morir.
     */
    @Test
    public void shouldLifeSourceNotReappearAfterDeath() throws Exception {
        Path f = Files.createTempFile("dopo-life-death-", ".txt");
        Files.writeString(f, levelWithLifeSource());
        game.loadConfiguration(f.toString());

        // Recoger fuente de vida
        game.movePlayer(0, 1);
        game.movePlayer(0, 1);
        game.movePlayer(0, 1);
        assertEquals("Debe tener 1 vida extra", 1, game.getExtraLives());

        // Morir por enemigo (simulado: mover hasta enemigo)
        // Primero recoger moneda
        game.movePlayer(0, 1);
        // Ahora ir hacia enemigo en (4,6)
        game.movePlayer(1, 0);
        game.movePlayer(0, 1);
        game.movePlayer(0, 1); // colisión

        final LifeSource life = (LifeSource) game.getPowerUps().get(0);
        assertTrue(
                "La fuente de vida no debe volver al tablero tras usarse",
                !life.isVisibleOnBoard()
        );
        assertTrue(
                "La fuente de vida debe quedar permanentemente usada",
                life.isPermanentlyUsed()
        );
    }

    /**
     * Verifica que la bomba consume una vida extra antes de reiniciar monedas.
     */
    @Test
    public void shouldBombConsumesExtraLifeFirst() throws Exception {
        final String level = String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "POWERUP LIFE 2 3",
                "POWERUP BOMB 2 5",
                "ENEMY BASIC 4 6 VERTICAL",
                "");
        final Path f = Files.createTempFile("dopo-bomb-life-", ".txt");
        Files.writeString(f, level);
        game.loadConfiguration(f.toString());

        game.movePlayer(0, 1);
        game.movePlayer(0, 1);
        game.movePlayer(0, 1);
        assertEquals("Debe tener 1 vida extra", 1, game.getExtraLives());

        game.movePlayer(0, 1);
        game.movePlayer(0, 1);
        game.movePlayer(0, 1);
        assertEquals("La bomba debe consumir la vida extra", 0, game.getExtraLives());
        assertEquals("Las monedas no deben resetearse al usar vida extra", 0, game.getCoinsCollected());
    }

    /**
     * Verifica carga correcta de power-ups desde configuración.
     */
    @Test
    public void shouldLoadPowerUpsFromConfig() throws Exception {
        Path f = Files.createTempFile("dopo-powerups-", ".txt");
        Files.writeString(f, levelWithBothPowerUps());
        game.loadConfiguration(f.toString());

        assertEquals(
                "Debe haber 2 power-ups cargados",
                2,
                game.getPowerUps().size()
        );
        assertEquals(
                "El primer power-up debe ser BOMB",
                "BOMB",
                game.getPowerUps().get(0).getType()
        );
        assertEquals(
                "El segundo power-up debe ser LIFE_SOURCE",
                "LIFE_SOURCE",
                game.getPowerUps().get(1).getType()
        );
    }

    /**
     * Verifica que el estado del juego no cambia al tocar power-ups.
     */
    @Test
    public void shouldStateRemainsPlayingAfterPowerUp() throws Exception {
        Path f = Files.createTempFile("dopo-state-", ".txt");
        Files.writeString(f, levelWithLifeSource());
        game.loadConfiguration(f.toString());

        game.movePlayer(0, 1);
        game.movePlayer(0, 1);
        game.movePlayer(0, 1);

        assertEquals(
                "El estado debe seguir siendo PLAYING tras recoger fuente de vida",
                HardestGame.State.PLAYING,
                game.getState()
        );
    }
}
