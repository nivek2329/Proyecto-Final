package main.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Instantánea serializable del estado de una partida en curso.
 */
public final class GameSnapshot {

    private final HardestGame.State state;
    private final int timeRemaining;
    private final int coinsCollected;
    private final boolean intermediateCheckpointReached;
    private final String respawnZoneKind;
    private final String playerSkin;
    private final String player2Skin;
    private final String originalPlayerSkin;
    private final String originalPlayer2Skin;
    private final boolean twoPlayerMode;
    private final String winnerName;
    private final PlayerSnapshot player1;
    private final PlayerSnapshot player2;
    private final List<CoinSnapshot> coins;
    private final List<EnemySnapshot> enemies;
    private final List<PowerUpSnapshot> powerUps;

    /**
     * Crea una instantánea del estado del juego.
     */
    public GameSnapshot(final HardestGame.State state, final int timeRemaining,
                        final int coinsCollected, final boolean intermediateCheckpointReached,
                        final String respawnZoneKind, final String playerSkin,
                        final String player2Skin, final String originalPlayerSkin,
                        final String originalPlayer2Skin, final boolean twoPlayerMode,
                        final String winnerName, final PlayerSnapshot player1,
                        final PlayerSnapshot player2, final List<CoinSnapshot> coins,
                        final List<EnemySnapshot> enemies,
                        final List<PowerUpSnapshot> powerUps) {
        this.state = state;
        this.timeRemaining = timeRemaining;
        this.coinsCollected = coinsCollected;
        this.intermediateCheckpointReached = intermediateCheckpointReached;
        this.respawnZoneKind = respawnZoneKind;
        this.playerSkin = playerSkin;
        this.player2Skin = player2Skin;
        this.originalPlayerSkin = originalPlayerSkin;
        this.originalPlayer2Skin = originalPlayer2Skin;
        this.twoPlayerMode = twoPlayerMode;
        this.winnerName = winnerName;
        this.player1 = player1;
        this.player2 = player2;
        this.coins = Collections.unmodifiableList(new ArrayList<>(coins));
        this.enemies = Collections.unmodifiableList(new ArrayList<>(enemies));
        this.powerUps = Collections.unmodifiableList(new ArrayList<>(powerUps));
    }

    public HardestGame.State getState() {
        return state;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public int getCoinsCollected() {
        return coinsCollected;
    }

    public boolean isIntermediateCheckpointReached() {
        return intermediateCheckpointReached;
    }

    public String getRespawnZoneKind() {
        return respawnZoneKind;
    }

    public String getPlayerSkin() {
        return playerSkin;
    }

    public String getPlayer2Skin() {
        return player2Skin;
    }

    public String getOriginalPlayerSkin() {
        return originalPlayerSkin;
    }

    public String getOriginalPlayer2Skin() {
        return originalPlayer2Skin;
    }

    public boolean isTwoPlayerMode() {
        return twoPlayerMode;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public PlayerSnapshot getPlayer1() {
        return player1;
    }

    public PlayerSnapshot getPlayer2() {
        return player2;
    }

    public List<CoinSnapshot> getCoins() {
        return coins;
    }

    public List<EnemySnapshot> getEnemies() {
        return enemies;
    }

    public List<PowerUpSnapshot> getPowerUps() {
        return powerUps;
    }

    /**
     * Estado de un jugador en la instantánea.
     */
    public static final class PlayerSnapshot {
        private final String skin;
        private final int row;
        private final int col;
        private final int deaths;
        private final int extraLives;
        private final int hitImmunityTicks;
        private final boolean greenShield;
        private final int greenInvulnerableTicks;

        public PlayerSnapshot(final String skin, final int row, final int col,
                              final int deaths, final int extraLives,
                              final int hitImmunityTicks,
                              final boolean greenShield,
                              final int greenInvulnerableTicks) {
            this.skin = skin;
            this.row = row;
            this.col = col;
            this.deaths = deaths;
            this.extraLives = extraLives;
            this.hitImmunityTicks = hitImmunityTicks;
            this.greenShield = greenShield;
            this.greenInvulnerableTicks = greenInvulnerableTicks;
        }

        public String getSkin() {
            return skin;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public int getDeaths() {
            return deaths;
        }

        public int getExtraLives() {
            return extraLives;
        }

        public int getHitImmunityTicks() {
            return hitImmunityTicks;
        }

        public boolean isGreenShield() {
            return greenShield;
        }

        public int getGreenInvulnerableTicks() {
            return greenInvulnerableTicks;
        }
    }

    /**
     * Estado de una moneda en la instantánea.
     */
    public static final class CoinSnapshot {
        private final boolean collected;
        private final boolean permanent;

        public CoinSnapshot(final boolean collected, final boolean permanent) {
            this.collected = collected;
            this.permanent = permanent;
        }

        public boolean isCollected() {
            return collected;
        }

        public boolean isPermanent() {
            return permanent;
        }
    }

    /**
     * Estado de un enemigo en la instantánea.
     */
    public static final class EnemySnapshot {
        private final int row;
        private final int col;
        private final int deltaRow;
        private final int deltaCol;
        private final int tickCount;

        public EnemySnapshot(final int row, final int col,
                             final int deltaRow, final int deltaCol,
                             final int tickCount) {
            this.row = row;
            this.col = col;
            this.deltaRow = deltaRow;
            this.deltaCol = deltaCol;
            this.tickCount = tickCount;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public int getDeltaRow() {
            return deltaRow;
        }

        public int getDeltaCol() {
            return deltaCol;
        }

        public int getTickCount() {
            return tickCount;
        }
    }

    /**
     * Estado de un power-up en la instantánea.
     */
    public static final class PowerUpSnapshot {
        private final String type;
        private final boolean consumed;
        private final boolean permanentlyUsed;

        public PowerUpSnapshot(final String type, final boolean consumed,
                               final boolean permanentlyUsed) {
            this.type = type;
            this.consumed = consumed;
            this.permanentlyUsed = permanentlyUsed;
        }

        public String getType() {
            return type;
        }

        public boolean isConsumed() {
            return consumed;
        }

        public boolean isPermanentlyUsed() {
            return permanentlyUsed;
        }
    }
}
