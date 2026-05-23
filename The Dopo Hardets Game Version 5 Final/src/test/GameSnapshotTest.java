package test;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import main.domain.GameSnapshot;
import main.domain.GameSnapshot.PlayerSnapshot;
import main.domain.GameSnapshot.CoinSnapshot;
import main.domain.GameSnapshot.EnemySnapshot;
import main.domain.GameSnapshot.PowerUpSnapshot;
import main.domain.HardestGame;

/**
 * Pruebas unitarias para GameSnapshot y sus clases anidadas.
 * Permite elevar el coverage al 100%.
 */
public class GameSnapshotTest {

    @Test
    public void testPlayerSnapshotGetters() {
        PlayerSnapshot ps = new PlayerSnapshot("Blue", 1, 2, 3, 4, 5, true, 6);
        assertEquals("Blue", ps.getSkin());
        assertEquals(1, ps.getRow());
        assertEquals(2, ps.getCol());
        assertEquals(3, ps.getDeaths());
        assertEquals(4, ps.getExtraLives());
        assertEquals(5, ps.getHitImmunityTicks());
        assertTrue(ps.isGreenShield());
        assertEquals(6, ps.getGreenInvulnerableTicks());
    }

    @Test
    public void testCoinSnapshotGetters() {
        CoinSnapshot cs = new CoinSnapshot(true, false);
        assertTrue(cs.isCollected());
        assertFalse(cs.isPermanent());
    }

    @Test
    public void testEnemySnapshotGetters() {
        EnemySnapshot es = new EnemySnapshot(1, 2, 3, 4, 5);
        assertEquals(1, es.getRow());
        assertEquals(2, es.getCol());
        assertEquals(3, es.getDeltaRow());
        assertEquals(4, es.getDeltaCol());
        assertEquals(5, es.getTickCount());
    }

    @Test
    public void testPowerUpSnapshotGetters() {
        PowerUpSnapshot pus = new PowerUpSnapshot("Bomb", true, false);
        assertEquals("Bomb", pus.getType());
        assertTrue(pus.isConsumed());
        assertFalse(pus.isPermanentlyUsed());
    }

    @Test
    public void testGameSnapshotGetters() {
        PlayerSnapshot p1 = new PlayerSnapshot("Red", 1, 1, 0, 0, 0, false, 0);
        PlayerSnapshot p2 = new PlayerSnapshot("Blue", 2, 2, 0, 0, 0, false, 0);
        
        List<CoinSnapshot> coins = new ArrayList<>();
        coins.add(new CoinSnapshot(false, true));
        
        List<EnemySnapshot> enemies = new ArrayList<>();
        enemies.add(new EnemySnapshot(2, 3, 0, 1, 1));
        
        List<PowerUpSnapshot> powerUps = new ArrayList<>();
        powerUps.add(new PowerUpSnapshot("Shield", false, true));
        
        GameSnapshot snap = new GameSnapshot(
            HardestGame.State.PLAYING,
            120,
            5,
            true,
            "CHECKPOINT",
            "Red",
            "Blue",
            "OriginalRed",
            "OriginalBlue",
            true,
            "Winner",
            p1,
            p2,
            coins,
            enemies,
            powerUps
        );

        assertEquals(HardestGame.State.PLAYING, snap.getState());
        assertEquals(120, snap.getTimeRemaining());
        assertEquals(5, snap.getCoinsCollected());
        assertTrue(snap.isIntermediateCheckpointReached());
        assertEquals("CHECKPOINT", snap.getRespawnZoneKind());
        assertEquals("Red", snap.getPlayerSkin());
        assertEquals("Blue", snap.getPlayer2Skin());
        assertEquals("OriginalRed", snap.getOriginalPlayerSkin());
        assertEquals("OriginalBlue", snap.getOriginalPlayer2Skin());
        assertTrue(snap.isTwoPlayerMode());
        assertEquals("Winner", snap.getWinnerName());
        assertSame(p1, snap.getPlayer1());
        assertSame(p2, snap.getPlayer2());
        
        assertEquals(1, snap.getCoins().size());
        assertFalse(snap.getCoins().get(0).isCollected());
        
        assertEquals(1, snap.getEnemies().size());
        assertEquals(2, snap.getEnemies().get(0).getRow());
        
        assertEquals(1, snap.getPowerUps().size());
        assertEquals("Shield", snap.getPowerUps().get(0).getType());
    }
}
