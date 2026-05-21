package main.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Instantánea serializable del estado de una partida en curso.
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
     * Crea una instantánea del estado del juego.
     *
     * @param state                         estado actual del juego
     * @param timeRemaining                 tiempo restante en segundos
     * @param coinsCollected                cantidad de monedas recolectadas
     * @param intermediateCheckpointReached indica si se alcanzó el checkpoint intermedio
     * @param respawnZoneKind               tipo de zona de reaparición activa
     * @param playerSkin                    skin del jugador 1
     * @param player2Skin                   skin del jugador 2
     * @param originalPlayerSkin            skin original del jugador 1
     * @param originalPlayer2Skin           skin original del jugador 2
     * @param twoPlayerMode                 indica si se está jugando en modo multijugador
     * @param winnerName                    nombre del jugador ganador si aplica
     * @param player1                       instantánea del estado del jugador 1
     * @param player2                       instantánea del estado del jugador 2
     * @param coins                         lista de instantáneas de las monedas
     * @param enemies                       lista de instantáneas de los enemigos
     * @param powerUps                      lista de instantáneas de los power-ups
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
     * Retorna el estado del juego en la instantánea.
     *
     * @return estado del juego
     */
    public HardestGame.State getState() {
        return state;
    }

    /**
     * Retorna el tiempo restante en la instantánea.
     *
     * @return tiempo restante en segundos
     */
    public int getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Retorna el número de monedas recolectadas.
     *
     * @return cantidad de monedas
     */
    public int getCoinsCollected() {
        return coinsCollected;
    }

    /**
     * Consulta si se había alcanzado el checkpoint intermedio.
     *
     * @return true si se activó el checkpoint intermedio
     */
    public boolean isIntermediateCheckpointReached() {
        return intermediateCheckpointReached;
    }

    /**
     * Retorna el tipo de zona de reaparición del jugador.
     *
     * @return tipo de zona de reaparición
     */
    public String getRespawnZoneKind() {
        return respawnZoneKind;
    }

    /**
     * Retorna el skin activo del jugador 1.
     *
     * @return skin del jugador 1
     */
    public String getPlayerSkin() {
        return playerSkin;
    }

    /**
     * Retorna el skin activo del jugador 2.
     *
     * @return skin del jugador 2
     */
    public String getPlayer2Skin() {
        return player2Skin;
    }

    /**
     * Retorna el skin original del jugador 1.
     *
     * @return skin original del jugador 1
     */
    public String getOriginalPlayerSkin() {
        return originalPlayerSkin;
    }

    /**
     * Retorna el skin original del jugador 2.
     *
     * @return skin original del jugador 2
     */
    public String getOriginalPlayer2Skin() {
        return originalPlayer2Skin;
    }

    /**
     * Consulta si el modo de juego es multijugador.
     *
     * @return true si es de dos jugadores
     */
    public boolean isTwoPlayerMode() {
        return twoPlayerMode;
    }

    /**
     * Retorna el nombre del ganador en la instantánea si aplica.
     *
     * @return nombre del ganador
     */
    public String getWinnerName() {
        return winnerName;
    }

    /**
     * Retorna la instantánea del jugador 1.
     *
     * @return instantánea del jugador 1
     */
    public PlayerSnapshot getPlayer1() {
        return player1;
    }

    /**
     * Retorna la instantánea del jugador 2.
     *
     * @return instantánea del jugador 2 o null si no aplica
     */
    public PlayerSnapshot getPlayer2() {
        return player2;
    }

    /**
     * Retorna la lista de instantáneas de monedas del nivel.
     *
     * @return lista de instantáneas de monedas
     */
    public List<CoinSnapshot> getCoins() {
        return coins;
    }

    /**
     * Retorna la lista de instantáneas de enemigos del nivel.
     *
     * @return lista de instantáneas de enemigos
     */
    public List<EnemySnapshot> getEnemies() {
        return enemies;
    }

    /**
     * Retorna la lista de instantáneas de power-ups del nivel.
     *
     * @return lista de instantáneas de power-ups
     */
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

        /**
         * Crea una instantánea del estado del jugador.
         *
         * @param skin                   nombre identificador del skin del jugador
         * @param row                    fila actual del jugador
         * @param col                    columna actual del jugador
         * @param deaths                 cantidad de muertes del jugador
         * @param extraLives             vidas extra del jugador
         * @param hitImmunityTicks       ticks restantes de inmunidad por golpe
         * @param greenShield            indica si posee el escudo del skin verde
         * @param greenInvulnerableTicks ticks restantes de invulnerabilidad verde
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
         * Retorna el skin asignado al jugador.
         *
         * @return skin del jugador
         */
        public String getSkin() {
            return skin;
        }

        /**
         * Retorna la fila actual en el tablero.
         *
         * @return fila del jugador
         */
        public int getRow() {
            return row;
        }

        /**
         * Retorna la columna actual en el tablero.
         *
         * @return columna del jugador
         */
        public int getCol() {
            return col;
        }

        /**
         * Retorna el acumulado de muertes.
         *
         * @return muertes del jugador
         */
        public int getDeaths() {
            return deaths;
        }

        /**
         * Retorna la cantidad de vidas extra.
         *
         * @return vidas extra
         */
        public int getExtraLives() {
            return extraLives;
        }

        /**
         * Retorna los ticks restantes de inmunidad tras ser golpeado.
         *
         * @return ticks de inmunidad
         */
        public int getHitImmunityTicks() {
            return hitImmunityTicks;
        }

        /**
         * Consulta si posee el escudo verde activado.
         *
         * @return true si tiene escudo
         */
        public boolean isGreenShield() {
            return greenShield;
        }

        /**
         * Retorna los ticks restantes de la invulnerabilidad del skin verde.
         *
         * @return ticks de invulnerabilidad verde
         */
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

        /**
         * Crea una instantánea del estado de la moneda.
         *
         * @param collected indica si la moneda está recolectada en el momento del guardado
         * @param permanent indica si la recolección fue fijada en un checkpoint
         */
        public CoinSnapshot(final boolean collected, final boolean permanent) {
            this.collected = collected;
            this.permanent = permanent;
        }

        /**
         * Consulta si la moneda está recolectada.
         *
         * @return true si está recolectada
         */
        public boolean isCollected() {
            return collected;
        }

        /**
         * Consulta si la recolección es permanente.
         *
         * @return true si es permanente
         */
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

        /**
         * Crea una instantánea del estado del enemigo.
         *
         * @param row      fila actual del enemigo
         * @param col      columna actual del enemigo
         * @param deltaRow desplazamiento del vector dirección en filas
         * @param deltaCol desplazamiento del vector dirección en columnas
         * @param tickCount acumulado de ticks para el siguiente paso del enemigo
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
         * Retorna la fila actual del enemigo.
         *
         * @return fila
         */
        public int getRow() {
            return row;
        }

        /**
         * Retorna la columna actual del enemigo.
         *
         * @return columna
         */
        public int getCol() {
            return col;
        }

        /**
         * Retorna el delta de movimiento en filas.
         *
         * @return delta en filas
         */
        public int getDeltaRow() {
            return deltaRow;
        }

        /**
         * Retorna el delta de movimiento en columnas.
         *
         * @return delta en columnas
         */
        public int getDeltaCol() {
            return deltaCol;
        }

        /**
         * Retorna el contador de ticks del enemigo.
         *
         * @return contador de ticks
         */
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

        /**
         * Crea una instantánea del estado del power-up.
         *
         * @param type            tipo del power-up en formato texto
         * @param consumed        indica si el power-up ya fue consumido
         * @param permanentlyUsed indica si el uso es permanente (no reaparece)
         */
        public PowerUpSnapshot(final String type, final boolean consumed,
                               final boolean permanentlyUsed) {
            this.type = type;
            this.consumed = consumed;
            this.permanentlyUsed = permanentlyUsed;
        }

        /**
         * Retorna el tipo de power-up en formato texto.
         *
         * @return tipo de power-up
         */
        public String getType() {
            return type;
        }

        /**
         * Consulta si el power-up ha sido consumido.
         *
         * @return true si ya fue consumido
         */
        public boolean isConsumed() {
            return consumed;
        }

        /**
         * Consulta si el power-up está permanentemente usado.
         *
         * @return true si fue consumido permanentemente
         */
        public boolean isPermanentlyUsed() {
            return permanentlyUsed;
        }
    }
}
