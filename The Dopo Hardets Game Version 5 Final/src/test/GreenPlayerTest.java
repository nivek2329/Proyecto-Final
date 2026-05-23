package test;

import static org.junit.Assert.*;
import org.junit.Test;
import main.domain.GreenPlayer;
import main.domain.HardestGame;
import main.domain.HardestGameException;
import main.domain.Player;
import main.domain.Zone;
import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Pruebas del jugador verde (Clyde): escudo e inmunidad.
 */
public class GreenPlayerTest {

    /**
     * Clyde absorbe el primer golpe de enemigo sin incrementar muertes.
     */
    @Test
    public void shouldClydeShieldAbsorbsEnemyHit() throws Exception {
        final String level = String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "ENEMY BASIC 2 4 VERTICAL",
                "");
        final Path f = Files.createTempFile("dopo-clyde-", ".txt");
        Files.writeString(f, level);

        final HardestGame game = new HardestGame();
        game.setPlayerSkin("Verde (Clyde)");
        game.loadConfiguration(f.toString());

        assertTrue(game.getPlayer() instanceof GreenPlayer);
        assertTrue(((GreenPlayer) game.getPlayer()).hasShield());

        game.movePlayer(0, 1);
        game.movePlayer(0, 1);
        game.movePlayer(0, 1);

        assertEquals("El escudo evita la primera muerte", 0, game.getDeaths());
        assertFalse("El escudo se consume tras el golpe",
                ((GreenPlayer) game.getPlayer()).hasShield());
    }

    /**
     * Clyde absorbe una bomba con el escudo activo.
     */
    @Test
    public void shouldClydeShieldAbsorbsBomb() throws Exception {
        final String level = String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "POWERUP BOMB 2 3",
                "");
        final Path f = Files.createTempFile("dopo-clyde-bomb-", ".txt");
        Files.writeString(f, level);

        final HardestGame game = new HardestGame();
        game.setPlayerSkin("Verde (Clyde)");
        game.loadConfiguration(f.toString());

        game.movePlayer(0, 1);
        game.movePlayer(0, 1);

        assertEquals("La bomba no debe matar a Clyde con escudo", 0, game.getDeaths());
        assertFalse(((GreenPlayer) game.getPlayer()).hasShield());
    }

    /**
     * Tras perder el escudo, una vida extra también absorbe sin muerte.
     */
    @Test
    public void shouldClydeUsesExtraLifeAfterShield() throws Exception {
        final String level = String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "POWERUP LIFE 2 3",
                "ENEMY BASIC 2 5 HORIZONTAL LEFT",
                "POWERUP BOMB 2 6",
                "");
        final Path f = Files.createTempFile("dopo-clyde-life-", ".txt");
        Files.writeString(f, level);

        final HardestGame game = new HardestGame();
        game.setPlayerSkin("Verde (Clyde)");
        game.loadConfiguration(f.toString());

        game.movePlayer(0, 1);
        game.movePlayer(0, 1);
        assertEquals(1, game.getExtraLives());

        game.movePlayer(0, 1);
        game.movePlayer(0, 1);
        assertFalse("Primero debe gastarse el escudo contra el enemigo",
                ((GreenPlayer) game.getPlayer()).hasShield());

        game.movePlayer(0, 1);
        assertEquals("Vida extra absorbe la bomba", 0, game.getExtraLives());
        assertEquals("Sin muerte al absorber", 0, game.getDeaths());
    }

    /**
     * Prueba los atributos básicos y métodos directos de la clase GreenPlayer.
     */
    @Test
    public void testGreenPlayerDirectMethods() {
        GreenPlayer p = new GreenPlayer(5, 6);
        assertEquals(5, p.getRow());
        assertEquals(6, p.getCol());
        assertEquals(Color.GREEN, p.getColor());
        assertEquals("Clyde", p.getName());
        assertEquals(1.0, p.getSize(), 0.001);
        assertEquals(2, p.getMoveTicks());
        assertTrue(p.hasShield());
        assertFalse(p.hasOverlapImmunity());
        assertFalse(p.isInvulnerable());

        // Probar absorción
        assertTrue(p.absorbHit());
        assertFalse(p.hasShield());
        assertTrue(p.hasOverlapImmunity());
        assertTrue(p.isInvulnerable());
        assertEquals(3, p.getMoveTicks()); // Ralentizado
        assertEquals(30, p.getInvulnerableTicksForSave());

        // Probar que no absorbe de nuevo
        assertFalse(p.absorbHit());

        // Probar decremento de ticks de invulnerabilidad
        p.tickInvulnerability();
        assertEquals(29, p.getInvulnerableTicksForSave());

        // Probar copiado de estado
        GreenPlayer other = new GreenPlayer(1, 1);
        other.copyShieldStateFrom(p);
        assertFalse(other.hasShield());
        assertEquals(29, other.getInvulnerableTicksForSave());

        // Probar restauración de estado guardado
        GreenPlayer restored = new GreenPlayer(2, 2);
        restored.restoreGreenSavedState(true, 15);
        assertTrue(restored.hasShield());
        assertEquals(15, restored.getInvulnerableTicksForSave());

        // Probar respawn
        Zone spawnZone = new Zone(0, 0, 2, 2, Zone.Type.INITIAL);
        restored.respawn(spawnZone);
        assertTrue(restored.hasShield());
        assertEquals(0, restored.getInvulnerableTicksForSave());
    }
}
