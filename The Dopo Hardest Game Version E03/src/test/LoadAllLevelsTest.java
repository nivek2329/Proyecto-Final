package test;

import main.domain.GameConfiguration;
import org.junit.Test;

/**
 * Verifica que todos los niveles del proyecto carguen sin error.
 */
public class LoadAllLevelsTest {

    private static final String[] LEVELS = {
            "configs/level1.txt",
            "configs/level2.txt",
            "configs/level3.txt",
            "configs/demo_final.txt",
            "configs/nivelsupremo.txt",
            "configs/level_gemas.txt",
            "configs/level_relampago.txt",
            "configs/level_duelo.txt"
    };

    @Test
    public void shouldLoadAllPackagedLevels() throws Exception {
        for (final String path : LEVELS) {
            final GameConfiguration cfg = new GameConfiguration();
            cfg.load(path);
        }
    }
}
