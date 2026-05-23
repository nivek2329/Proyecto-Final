package main.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Instantánea serializable del estado de una partida en curso.
 * Almacena el estado del tablero, jugadores, monedas, enemigos y power-ups.
 *
 * @author Angel-Garcia
 * @version 2026-1
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
     * Crea una instantánea del estado completo del juego.
     *
     * @param state estado actual del juego
     * @param timeRemaining tiempo restante en segundos
     * @param coinsCollected cantidad de monedas recolectadas hasta el momento
     * @param intermediateCheckpointReached indica si se alcanzó el checkpoint intermedio
     * @param respawnZoneKind identificador del tipo de zona de reaparición activa
     * @param playerSkin skin actual del jugador principal
     * @param player2Skin skin actual del segundo jugador
     * @param originalPlayerSkin skin original del jugador principal
     * @param originalPlayer2Skin skin original del segundo jugador
     * @param twoPlayerMode indica si el modo de dos jugadores está activo
     * @param winnerName nombre del jugador ganador
     * @param player1 instantánea de estado del jugador principal
     * @param player2 instantánea de estado del segundo jugador
     * @param coins lista con las instantáneas de cada moneda
     * @param enemies lista con las instantáneas de cada enemigo
     * @param powerUps lista con las instantáneas de cada power-up
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

    /**
     * Obtiene el estado actual de la partida.
     *
     * @return estado del juego
     */
    public HardestGame.State getState() {
        return state;
    }

    /**
     * Obtiene el tiempo restante en segundos.
     *
     * @return segundos restantes de la partida
     */
    public int getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Obtiene el total de monedas recolectadas.
     *
     * @return monedas recolectadas
     */
    public int getCoinsCollected() {
        return coinsCollected;
    }

    /**
     * Verifica si se alcanzó el checkpoint intermedio en esta instantánea.
     *
     * @return true si se ha alcanzado; false en caso contrario
     */
    public boolean isIntermediateCheckpointReached() {
        return intermediateCheckpointReached;
    }

    /**
     * Obtiene la zona de reaparición activa.
     *
     * @return cadena con el tipo de la zona de reaparición
     */
    public String getRespawnZoneKind() {
        return respawnZoneKind;
    }

    /**
     * Obtiene la skin activa del jugador principal.
     *
     * @return skin del jugador 1
     */
    public String getPlayerSkin() {
        return playerSkin;
    }

    /**
     * Obtiene la skin activa del segundo jugador.
     *
     * @return skin del jugador 2
     */
    public String getPlayer2Skin() {
        return player2Skin;
    }

    /**
     * Obtiene la skin original configurada para el jugador principal.
     *
     * @return skin original del jugador 1
     */
    public String getOriginalPlayerSkin() {
        return originalPlayerSkin;
    }

    /**
     * Obtiene la skin original configurada para el segundo jugador.
     *
     * @return skin original del jugador 2
     */
    public String getOriginalPlayer2Skin() {
        return originalPlayer2Skin;
    }

    /**
     * Verifica si el modo de dos jugadores está activo en la instantánea.
     *
     * @return true si hay dos jugadores; false en caso contrario
     */
    public boolean isTwoPlayerMode() {
        return twoPlayerMode;
    }

    /**
     * Obtiene el nombre del ganador de la partida.
     *
     * @return nombre del ganador
     */
    public String getWinnerName() {
        return winnerName;
    }

    /**
     * Obtiene la instantánea correspondiente al jugador principal.
     *
     * @return instantánea del jugador 1
     */
    public PlayerSnapshot getPlayer1() {
        return player1;
    }

    /**
     * Obtiene la instantánea correspondiente al segundo jugador.
     *
     * @return instantánea del jugador 2; puede ser null
     */
    public PlayerSnapshot getPlayer2() {
        return player2;
    }

    /**
     * Obtiene la lista inmutable de instantáneas de monedas.
     *
     * @return lista de monedas guardadas
     */
    public List<CoinSnapshot> getCoins() {
        return coins;
    }

    /**
     * Obtiene la lista inmutable de instantáneas de enemigos.
     *
     * @return lista de enemigos guardados
     */
    public List<EnemySnapshot> getEnemies() {
        return enemies;
    }

    /**
     * Obtiene la lista inmutable de instantáneas de power-ups.
     *
     * @return lista de power-ups guardados
     */
    public List<PowerUpSnapshot> getPowerUps() {
        return powerUps;
    }

    /**
     * Estado estructurado de un jugador para su persistencia en la instantánea.
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

        /**
         * Crea una instantánea detallada de un jugador.
         *
         * @param skin skin del jugador
         * @param row fila actual
         * @param col columna actual
         * @param deaths número de muertes acumuladas
         * @param extraLives número de vidas extra
         * @param hitImmunityTicks ticks de inmunidad restantes
         * @param greenShield estado del escudo de Clyde (GreenPlayer)
         * @param greenInvulnerableTicks ticks de invulnerabilidad de Clyde
         */
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

        /**
         * Obtiene la skin del jugador.
         *
         * @return skin del jugador
         */
        public String getSkin() {
            return skin;
        }

        /**
         * Obtiene la fila actual del jugador.
         *
         * @return fila actual
         */
        public int getRow() {
            return row;
        }

        /**
         * Obtiene la columna actual del jugador.
         *
         * @return columna actual
         */
        public int getCol() {
            return col;
        }

        /**
         * Obtiene el número acumulado de muertes del jugador.
         *
         * @return número de muertes
         */
        public int getDeaths() {
            return deaths;
        }

        /**
         * Obtiene el número de vidas extra del jugador.
         *
         * @return vidas extra disponibles
         */
        public int getExtraLives() {
            return extraLives;
        }

        /**
         * Obtiene el número de ticks de inmunidad restantes del jugador.
         *
         * @return ticks de inmunidad
         */
        public int getHitImmunityTicks() {
            return hitImmunityTicks;
        }

        /**
         * Indica si el jugador verde (Clyde) posee su escudo activo.
         *
         * @return true si el escudo está activo; false en caso contrario
         */
        public boolean isGreenShield() {
            return greenShield;
        }

        /**
         * Obtiene el número de ticks de invulnerabilidad de Clyde.
         *
         * @return ticks de invulnerabilidad
         */
        public int getGreenInvulnerableTicks() {
            return greenInvulnerableTicks;
        }
    }

    /**
     * Estado estructurado de una moneda en la instantánea.
     */
    public static final class CoinSnapshot {
        private final boolean collected;
        private final boolean permanent;

        /**
         * Crea una instantánea de una moneda.
         *
         * @param collected indica si fue recolectada
         * @param permanent indica si se recolectó permanentemente
         */
        public CoinSnapshot(final boolean collected, final boolean permanent) {
            this.collected = collected;
            this.permanent = permanent;
        }

        /**
         * Verifica si la moneda está recolectada.
         *
         * @return true si ya fue recolectada; false en caso contrario
         */
        public boolean isCollected() {
            return collected;
        }

        /**
         * Verifica si la moneda fue recolectada permanentemente mediante checkpoint.
         *
         * @return true si es permanente; false en caso contrario
         */
        public boolean isPermanent() {
            return permanent;
        }
    }

    /**
     * Estado estructurado de un enemigo en la instantánea.
     */
    public static final class EnemySnapshot {
        private final int row;
        private final int col;
        private final int deltaRow;
        private final int deltaCol;
        private final int tickCount;

        /**
         * Crea una instantánea de un enemigo.
         *
         * @param row fila actual
         * @param col columna actual
         * @param deltaRow desplazamiento en filas
         * @param deltaCol desplazamiento en columnas
         * @param tickCount contador interno de ticks
         */
        public EnemySnapshot(final int row, final int col,
                             final int deltaRow, final int deltaCol,
                             final int tickCount) {
            this.row = row;
            this.col = col;
            this.deltaRow = deltaRow;
            this.deltaCol = deltaCol;
            this.tickCount = tickCount;
        }

        /**
         * Obtiene la fila actual del enemigo.
         *
         * @return fila del enemigo
         */
        public int getRow() {
            return row;
        }

        /**
         * Obtiene la columna actual del enemigo.
         *
         * @return columna del enemigo
         */
        public int getCol() {
            return col;
        }

        /**
         * Obtiene el desplazamiento en filas del enemigo.
         *
         * @return delta de filas
         */
        public int getDeltaRow() {
            return deltaRow;
        }

        /**
         * Obtiene el desplazamiento en columnas del enemigo.
         *
         * @return delta de columnas
         */
        public int getDeltaCol() {
            return deltaCol;
        }

        /**
         * Obtiene el contador interno de ticks del enemigo.
         *
         * @return número de ticks acumulados
         */
        public int getTickCount() {
            return tickCount;
        }
    }

    /**
     * Estado estructurado de un power-up en la instantánea.
     */
    public static final class PowerUpSnapshot {
        private final String type;
        private final boolean consumed;
        private final boolean permanentlyUsed;

        /**
         * Crea una instantánea del power-up.
         *
         * @param type tipo del power-up
         * @param consumed indica si ya fue consumido
         * @param permanentlyUsed indica si su uso es permanente (no reaparece)
         */
        public PowerUpSnapshot(final String type, final boolean consumed,
                               final boolean permanentlyUsed) {
            this.type = type;
            this.consumed = consumed;
            this.permanentlyUsed = permanentlyUsed;
        }

        /**
         * Obtiene el tipo del power-up.
         *
         * @return tipo del power-up en mayúsculas
         */
        public String getType() {
            return type;
        }

        /**
         * Verifica si el power-up ya fue consumido.
         *
         * @return true si está consumido; false en caso contrario
         */
        public boolean isConsumed() {
            return consumed;
        }

        /**
         * Verifica si el power-up se ha usado de forma permanente.
         *
         * @return true si es permanente; false en caso contrario
         */
        public boolean isPermanentlyUsed() {
            return permanentlyUsed;
        }
    }
}
