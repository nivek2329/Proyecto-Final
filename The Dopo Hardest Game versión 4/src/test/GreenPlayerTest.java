package test;

import static org.junit.Assert.*;
import org.junit.Test;
import main.domain.GreenPlayer;
import main.domain.HardestGame;
import main.domain.HardestGameException;
import main.domain.Player;
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
}
