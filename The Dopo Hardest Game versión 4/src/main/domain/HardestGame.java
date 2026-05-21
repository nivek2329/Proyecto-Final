package main.domain;

import main.domain.GameSnapshot.CoinSnapshot;
import main.domain.GameSnapshot.EnemySnapshot;
import main.domain.GameSnapshot.PlayerSnapshot;
import main.domain.GameSnapshot.PowerUpSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import main.domain.HardestGameException.*;


/**
 * Clase principal que representa la lógica central del juego.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class HardestGame {

    private static final Logger LOGGER = GameLog.getLogger(HardestGame.class);

    public enum State { PLAYING, PAUSED, WON, LOST }

    private Board board;
    private Player player;
    private Player player2;
    private List<Coin>  coins;
    private List<Enemy> enemies;
    private List<PowerUp> powerUps;
    private Zone startZone;
    private Zone        intermediateZone;
    private Zone        finalZone;
    private Zone        currentRespawnZone;
    private int         timeRemaining;
    private int         coinsCollected;
    private State       state;
    private String      playerSkin = "Rojo (Blinky)";
    private String      player2Skin = "Azul (Inky)";
    private String      originalPlayerSkin = "Rojo (Blinky)";
    private String      originalPlayer2Skin = "Azul (Inky)";
    private boolean     twoPlayerMode;
    private String      winnerName = "";
    private boolean     intermediateCheckpointReached;

    /**
     * Crea el juego en estado de pausa inicial.
     */
    public HardestGame() {
        state = State.PAUSED;
    }

    /**
     * Pone el juego en pausa para permitir cargar un nivel nuevo.
     */
    public void prepareForLoad() {
        state = State.PAUSED;
    }

    /**
     * Establece el skin del jugador antes de cargar el nivel.
     *
     * @param skin nombre del skin seleccionado en el menú
     */
    public void setPlayerSkin(final String skin) {
        this.playerSkin = skin;
        this.originalPlayerSkin = skin;
    }

    /**
     * Establece el skin del segundo jugador antes de cargar el nivel.
     *
     * @param skin nombre del skin seleccionado en el menú
     */
    public void setSecondPlayerSkin(final String skin) {
        this.player2Skin = skin;
        this.originalPlayer2Skin = skin;
    }

    /**
     * Activa o desactiva el modo de dos jugadores.
     *
     * @param enabled true para activar modo dos jugadores
     */
    public void setTwoPlayerMode(final boolean enabled) {
        this.twoPlayerMode = enabled;
    }

    /**
     * Crea un jugador en la posición indicada según el skin seleccionado.
     *
     * @param row fila inicial del jugador
     * @param col columna inicial del jugador
     * @return instancia del jugador creado
     */
    private Player createPlayer(final int row, final int col) throws InvalidSkinException {
        switch (playerSkin) {
            case "Azul (Inky)":   return new BluePlayer(row, col);
            case "Verde (Clyde)": return new GreenPlayer(row, col);
            case "Rojo (Blinky)": return new RedPlayer(row, col);
            default:
                final String msg = "Skin no registrado: " + playerSkin;
                LOGGER.severe(msg);
                throw new InvalidSkinException(msg);
        }
    }

    /**
     * Carga la configuración del nivel y reinicia la partida.
     *
     * @param filename ruta del archivo de configuración
     * @throws HardestGameException si el archivo es inválido
     */
    public void loadConfiguration(final String filename) throws HardestGameException {
        if (state != State.PAUSED) {
            final String msg = "No se puede cargar nivel: partida en estado " + state;
            LOGGER.severe(msg);
            throw new GameAlreadyRunningException(msg);
        }
        final GameConfiguration cfg = new GameConfiguration();
        cfg.load(filename);

        board = new Board(cfg.getRows(), cfg.getCols());
        for (final int[] wall : cfg.getWalls()) {
            board.setWall(wall[0], wall[1]);
        }

        startZone        = cfg.getStartZone();
        intermediateZone = cfg.getIntermediateZone();
        finalZone        = cfg.getFinalZone();

        board.addEnemyForbiddenZone(startZone);
        board.addEnemyForbiddenZone(finalZone);
        if (intermediateZone != null) {
            board.addEnemyForbiddenZone(intermediateZone);
        }

        currentRespawnZone = startZone;
        intermediateCheckpointReached = false;

        coins          = cfg.getCoins();
        enemies        = cfg.getEnemies();
        powerUps       = cfg.getPowerUps();
        timeRemaining  = cfg.getTimeLimit();
        coinsCollected = 0;

        final int spawnRow = startZone.getRow() + startZone.getRows() / 2;
        final int spawnCol = startZone.getCol() + startZone.getCols() / 2;
        player = createPlayer(spawnRow, spawnCol);
        if (twoPlayerMode) {
            final int spawn2Row = finalZone.getRow() + finalZone.getRows() / 2;
            final int spawn2Col = finalZone.getCol() + finalZone.getCols() / 2;
            player2 = createPlayerBySkin(player2Skin, spawn2Row, spawn2Col);
        } else {
            player2 = null;
        }
        winnerName = "";

        state = State.PLAYING;
    }

    /**
     * Mueve al jugador principal y evalúa monedas, power-ups, colisiones y victoria.
     *
     * @param deltaRow desplazamiento en filas
     * @param deltaCol desplazamiento en columnas
     */
    public void movePlayer(final int deltaRow, final int deltaCol) throws HardestGameException {
        if (state != State.PLAYING) {
            final String msg = "movePlayer no permitido en estado " + state;
            LOGGER.severe(msg);
            throw new InvalidGameStateException(msg);
        }
        player.move(deltaRow, deltaCol, board);
        checkCoinsForPlayer(true);
        checkPowerUpsForPlayer(true);
        checkIntermediateZone();
        checkEnemyCollisionForPlayer(true);
        checkPlayerCollision();
        checkWinForPlayer(true);
    }

    /**
     * Mueve al segundo jugador y evalúa monedas, power-ups, colisiones y victoria.
     *
     * @param deltaRow desplazamiento en filas
     * @param deltaCol desplazamiento en columnas
     */
    public void moveSecondPlayer(final int deltaRow, final int deltaCol) throws HardestGameException {
        if (!twoPlayerMode || player2 == null) {
            final String msg = "moveSecondPlayer sin modo dos jugadores activo";
            LOGGER.severe(msg);
            throw new InvalidGameStateException(msg);
        }
        if (state != State.PLAYING) {
            final String msg = "moveSecondPlayer no permitido en estado " + state;
            LOGGER.severe(msg);
            throw new InvalidGameStateException(msg);
        }
        player2.move(deltaRow, deltaCol, board);
        checkCoinsForPlayer(false);
        checkPowerUpsForPlayer(false);
        checkEnemyCollisionForPlayer(false);
        checkPlayerCollision();
        checkWinForPlayer(false);
    }

    /**
     * Actualiza los enemigos y verifica colisiones.
     * También decrementa la invulnerabilidad del GreenPlayer si aplica.
     */
    public void tick() {
        if (state != State.PLAYING) {
            return;
        }
        for (final Enemy enemy : enemies) {
            enemy.update(board);
        }
        tickPlayerDefense(player);
        if (twoPlayerMode && player2 != null) {
            tickPlayerDefense(player2);
        }
        checkEnemyCollisionForPlayer(true);
        if (twoPlayerMode && player2 != null) {
            checkEnemyCollisionForPlayer(false);
        }
    }

    /**
     * Decrementa el tiempo restante. Si llega a cero el estado pasa a LOST.
     */
    public void decrementTime() {
        if (state != State.PLAYING) {
            return;
        }
        timeRemaining--;
        if (timeRemaining <= 0) {
            state = State.LOST;
        }
    }

    /**
     * Alterna el estado del juego entre PLAYING y PAUSED.
     */
    public void togglePause() {
        if (state == State.PLAYING) {
            state = State.PAUSED;
        } else if (state == State.PAUSED) {
            state = State.PLAYING;
        }
    }

    /**
     * Verifica si el jugador indicado recolectó alguna moneda.
     *
     * @param firstPlayer true para jugador 1, false para jugador 2
     */
    private void checkCoinsForPlayer(final boolean firstPlayer)
            throws CoinAlreadyCollectedException, InvalidSkinException {
        final Player activePlayer = firstPlayer ? player : player2;
        for (final Coin coin : coins) {
            if (!coin.isCollected()
                    && coin.getRow() == activePlayer.getRow()
                    && coin.getCol() == activePlayer.getCol()) {
                coin.collect();
                coinsCollected++;
                applyCoinEffect(activePlayer, coin, firstPlayer);
            }
        }
    }

    /**
     * Verifica si el jugador indicado tocó algún power-up.
     *
     * @param firstPlayer true para jugador 1, false para jugador 2
     */
    private void checkPowerUpsForPlayer(final boolean firstPlayer) {
        final Player activePlayer = firstPlayer ? player : player2;
        for (final PowerUp powerUp : powerUps) {
            if (!powerUp.isConsumed()
                    && powerUp.getRow() == activePlayer.getRow()
                    && powerUp.getCol() == activePlayer.getCol()) {
                applyPowerUpEffect(activePlayer, powerUp, firstPlayer);
            }
        }
    }

    /**
     * Aplica el efecto de un power-up al jugador que lo tocó.
     * Bomb: muerte instantánea del jugador.
     * LifeSource: otorga un punto de vida extra y desaparece permanentemente.
     *
     * @param activePlayer jugador que tocó el power-up
     * @param powerUp      power-up tocado
     * @param firstPlayer  true para jugador 1, false para jugador 2
     */
    private void applyPowerUpEffect(final Player activePlayer, final PowerUp powerUp,
                                     final boolean firstPlayer) {
        if (powerUp instanceof Bomb) {
            powerUp.consume();
            if (!tryAbsorbDamage(activePlayer, firstPlayer)) {
                applyFatalHit(firstPlayer, activePlayer, true);
            }
        } else if (powerUp instanceof LifeSource) {
            final LifeSource lifeSource = (LifeSource) powerUp;
            if (!lifeSource.isPermanentlyUsed()) {
                activePlayer.addExtraLife();
                lifeSource.consume();
                lifeSource.markPermanentlyUsed();
                GameLogger.logInfo("Vida extra para "
                        + (firstPlayer ? "Jugador 1" : "Jugador 2")
                        + " (total: " + activePlayer.getExtraLives() + ")");
            }
        }
    }

    /**
     * Intenta absorber daño letal (bomba u otro efecto instantáneo).
     */
    private boolean tryAbsorbDamage(final Player activePlayer, final boolean firstPlayer) {
        return tryAbsorbDamage(activePlayer, firstPlayer, false);
    }

    /**
     * Intenta absorber colisión con enemigo.
     */
    private boolean tryAbsorbEnemyHit(final Player activePlayer, final boolean firstPlayer) {
        return tryAbsorbDamage(activePlayer, firstPlayer, true);
    }

    /**
     * Intenta absorber daño: escudo de Clyde, vidas extra o inmunidad temporal.
     *
     * @param honorClydeOverlap si true, la inmunidad post-escudo evita otro golpe enemigo
     */
    private boolean tryAbsorbDamage(final Player activePlayer, final boolean firstPlayer,
                                    final boolean honorClydeOverlap) {
        if (activePlayer.isHitImmune()) {
            return true;
        }
        if (activePlayer instanceof GreenPlayer) {
            final GreenPlayer gp = (GreenPlayer) activePlayer;
            if (honorClydeOverlap && gp.hasOverlapImmunity()) {
                return true;
            }
            if (gp.hasShield() && gp.absorbHit()) {
                GameLogger.logInfo("Clyde absorbió un golpe con su escudo");
                return true;
            }
        }
        if (activePlayer.consumeExtraLife()) {
            activePlayer.grantHitImmunity();
            GameLogger.logInfo("Vida extra absorbió un golpe para "
                    + (firstPlayer ? "Jugador 1" : "Jugador 2")
                    + " (restantes: " + activePlayer.getExtraLives() + ")");
            return true;
        }
        return false;
    }

    /**
     * Aplica muerte completa: respawn, reinicio de monedas y skin si corresponde.
     */
    private void applyFatalHit(final boolean firstPlayer, final Player activePlayer,
                               final boolean resetCoinsOnFullDeath) {
        final Zone respawnZone = resolveRespawnZone(firstPlayer);
        activePlayer.respawn(respawnZone);
        if (resetCoinsOnFullDeath) {
            resetAllCoins();
            resetPlayerSkinOnDeath(firstPlayer, activePlayer);
        }
    }

    private Zone resolveRespawnZone(final boolean firstPlayer) {
        if (firstPlayer) {
            return currentRespawnZone;
        }
        return intermediateZone != null ? intermediateZone : finalZone;
    }

    /**
     * Aplica el efecto de una moneda de skin al jugador que la recolectó.
     *
     * @param activePlayer jugador que recolectó la moneda
     * @param coin         moneda recolectada
     * @param firstPlayer  true para jugador 1, false para jugador 2
     */
    private void applyCoinEffect(final Player activePlayer, final Coin coin, final boolean firstPlayer)
            throws InvalidSkinException {
        if (!(coin instanceof SkinCoin)) {
            return;
        }
        final SkinCoin skinCoin = (SkinCoin) coin;
        final String mapped = mapSkinName(skinCoin.getSkinName());
        if (firstPlayer) {
            player = replacePlayerSkin(activePlayer, mapped);
            playerSkin = mapped;
        } else {
            player2 = replacePlayerSkin(activePlayer, mapped);
            player2Skin = mapped;
        }
        GameLogger.logInfo("Moneda skin: "
                + (firstPlayer ? "Jugador 1" : "Jugador 2")
                + " cambió a " + mapped + " hasta morir");
    }

    /**
     * Actualiza el punto de reaparición si el jugador entra en la zona intermedia.
     * La primera vez que llega, las monedas ya recolectadas quedan permanentes.
     */
    private void checkIntermediateZone() {
        if (intermediateZone == null
                || !intermediateZone.contains(player.getRow(), player.getCol())) {
            return;
        }
        currentRespawnZone = intermediateZone;
        if (!intermediateCheckpointReached) {
            intermediateCheckpointReached = true;
            for (final Coin coin : coins) {
                if (coin.isCollected()) {
                    coin.markPermanent();
                }
            }
            recalculateCoinsCollected();
            GameLogger.logInfo("Checkpoint intermedio activado — monedas previas fijadas");
        }
    }

    /**
     * Si ambos jugadores ocupan la misma celda, vuelven a su punto de inicio.
     */
    private void checkPlayerCollision() {
        if (!twoPlayerMode || player2 == null || state != State.PLAYING) {
            return;
        }
        if (player.getRow() == player2.getRow() && player.getCol() == player2.getCol()) {
            player.resetToZone(startZone);
            player2.resetToZone(finalZone);
            GameLogger.logInfo("Colisión jugador-jugador: ambos reiniciados a inicio");
        }
    }

    /**
     * Recalcula el contador de monedas según las recolectadas actualmente.
     */
    private void recalculateCoinsCollected() {
        int count = 0;
        for (final Coin coin : coins) {
            if (coin.isCollected()) {
                count++;
            }
        }
        coinsCollected = count;
    }

    /**
     * Detecta colisión enemigo-jugador y aplica las consecuencias correspondientes.
     * Verde con escudo: absorbe el golpe sin morir y gana invulnerabilidad temporal.
     * Verde sin escudo pero invulnerable: ignora la colisión.
     * Con vida extra disponible: consume una vida extra y respawnea sin perder monedas.
     * Cualquier otro caso: muere, reaparece y reinicia todas las monedas.
     *
     * @param firstPlayer true para jugador 1, false para jugador 2
     */
    private void checkEnemyCollisionForPlayer(final boolean firstPlayer) {
        final Player activePlayer = firstPlayer ? player : player2;
        if (activePlayer == null) {
            return;
        }
        for (final Enemy enemy : enemies) {
            if (enemy.collidesWith(activePlayer)) {
                if (tryAbsorbEnemyHit(activePlayer, firstPlayer)) {
                    return;
                }
                applyFatalHit(firstPlayer, activePlayer, true);
                return;
            }
        }
    }

    /**
     * Actualiza inmunidad temporal y escudo de Clyde en cada tick.
     */
    private void tickPlayerDefense(final Player p) {
        if (p == null) {
            return;
        }
        if (p instanceof GreenPlayer) {
            ((GreenPlayer) p).tickInvulnerability();
        } else {
            p.tickHitImmunity();
        }
    }

    /**
     * Restaura el skin original del jugador tras morir.
     *
     * @param firstPlayer  true para jugador 1, false para jugador 2
     * @param activePlayer jugador que murió
     */
    private void resetPlayerSkinOnDeath(final boolean firstPlayer, final Player activePlayer) {
        try {
            if (firstPlayer) {
                player = replacePlayerSkin(activePlayer, originalPlayerSkin);
                playerSkin = originalPlayerSkin;
            } else {
                player2 = replacePlayerSkin(activePlayer, originalPlayer2Skin);
                player2Skin = originalPlayer2Skin;
            }
        } catch (InvalidSkinException e) {
            if (LOGGER.isLoggable(java.util.logging.Level.SEVERE)) {
                LOGGER.log(java.util.logging.Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * Reinicia las monedas no fijadas por checkpoint y actualiza el contador.
     */
    private void resetAllCoins() {
        for (final Coin coin : coins) {
            coin.reset();
        }
        recalculateCoinsCollected();
    }

    /**
     * Verifica si el jugador indicado cumple la condición de victoria.
     *
     * @param firstPlayer true para jugador 1, false para jugador 2
     */
    private void checkWinForPlayer(final boolean firstPlayer) {
        final Player activePlayer = firstPlayer ? player : player2;
        if (activePlayer == null) {
            return;
        }
        final Zone playerGoalZone = firstPlayer ? finalZone : startZone;
        if (coinsCollected == coins.size()
                && playerGoalZone.contains(activePlayer.getRow(), activePlayer.getCol())) {
            state = State.WON;
            winnerName = firstPlayer ? "Jugador 1" : "Jugador 2";
        }
    }

    /**
     * Mapea un nombre corto de skin al nombre completo del menú.
     *
     * @param shortSkinName nombre corto del skin
     * @return nombre completo del skin
     */
    private String mapSkinName(final String shortSkinName) {
        if ("BLUE".equalsIgnoreCase(shortSkinName)) {
            return "Azul (Inky)";
        }
        if ("GREEN".equalsIgnoreCase(shortSkinName)) {
            return "Verde (Clyde)";
        }
        return "Rojo (Blinky)";
    }

    /**
     * Crea un jugador con el skin indicado preservando el skin principal.
     *
     * @param skin nombre del skin a usar
     * @param row  fila inicial del jugador
     * @param col  columna inicial del jugador
     * @return instancia del jugador creado
     */
    private Player createPlayerBySkin(final String skin, final int row, final int col)
            throws InvalidSkinException {
        final String previous = playerSkin;
        playerSkin = skin;
        final Player created = createPlayer(row, col);
        playerSkin = previous;
        return created;
    }

    /**
     * Reemplaza un jugador por otro con el skin indicado preservando las muertes.
     *
     * @param previousPlayer jugador actual
     * @param skin             nombre del skin a aplicar
     * @return nuevo jugador con el skin cambiado
     */
    private Player replacePlayerSkin(final Player previousPlayer, final String skin)
            throws InvalidSkinException {
        final Player replaced = createPlayerBySkin(skin, previousPlayer.getRow(), previousPlayer.getCol());
        replaced.deaths = previousPlayer.getDeaths();
        replaced.extraLives = previousPlayer.getExtraLives();
        replaced.hitImmunityTicks = previousPlayer.hitImmunityTicks;
        if (replaced instanceof GreenPlayer && previousPlayer instanceof GreenPlayer) {
            ((GreenPlayer) replaced).copyShieldStateFrom((GreenPlayer) previousPlayer);
        }
        return replaced;
    }

    /**
     * Retorna el tablero del juego.
     *
     * @return tablero actual
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Retorna el jugador principal.
     *
     * @return jugador 1
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Retorna el segundo jugador.
     *
     * @return jugador 2, puede ser null
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
     * Retorna la lista de monedas del nivel.
     *
     * @return lista de monedas
     */
    public List<Coin> getCoins() {
        return coins;
    }

    /**
     * Retorna la lista de enemigos del nivel.
     *
     * @return lista de enemigos
     */
    public List<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Retorna la lista de power-ups del nivel.
     *
     * @return lista de power-ups
     */
    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    /**
     * Retorna la zona segura inicial.
     *
     * @return zona de inicio
     */
    public Zone getStartZone() {
        return startZone;
    }

    /**
     * Retorna la zona segura intermedia.
     *
     * @return zona intermedia, puede ser null
     */
    public Zone getIntermediateZone() {
        return intermediateZone;
    }

    /**
     * Retorna la zona segura final.
     *
     * @return zona final
     */
    public Zone getFinalZone() {
        return finalZone;
    }

    /**
     * Retorna la zona de reaparición actual.
     *
     * @return zona de reaparición activa
     */
    public Zone getCurrentRespawnZone() {
        return currentRespawnZone;
    }

    /**
     * Retorna el tiempo restante en segundos.
     *
     * @return tiempo restante
     */
    public int getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Retorna la cantidad de monedas recolectadas.
     *
     * @return monedas recolectadas
     */
    public int         getCoinsCollected()    {
        return coinsCollected;
    }

    /**
     * Retorna el total de monedas del nivel.
     *
     * @return cantidad total de monedas
     */
    public int getTotalCoins() {
        return coins != null ? coins.size() : 0;
    }

    /**
     * Retorna la cantidad de vidas extra disponibles.
     *
     * @return vidas extra acumuladas
     */
    public int getExtraLives() {
        return player != null ? player.getExtraLives() : 0;
    }

    /**
     * Retorna las vidas extra del segundo jugador.
     *
     * @return vidas extra del jugador 2
     */
    public int getSecondPlayerExtraLives() {
        return player2 != null ? player2.getExtraLives() : 0;
    }

    /**
     * Retorna el estado actual del juego.
     *
     * @return estado del juego
     */
    public State getState() {
        return state;
    }

    /**
     * Retorna la cantidad de muertes del jugador principal.
     *
     * @return muertes del jugador 1
     */
    public int getDeaths() {
        return player != null ? player.getDeaths() : 0;
    }

    /**
     * Retorna la cantidad de muertes del segundo jugador.
     *
     * @return muertes del jugador 2
     */
    public int getSecondPlayerDeaths() {
        return player2 != null ? player2.getDeaths() : 0;
    }

    /**
     * Indica si el modo de dos jugadores está activo.
     *
     * @return true si hay dos jugadores
     */
    public boolean isTwoPlayerMode() {
        return twoPlayerMode;
    }

    /**
     * Retorna el nombre del jugador ganador.
     *
     * @return nombre del ganador
     */
    public String getWinnerName() {
        return winnerName;
    }

    /**
     * Captura el estado actual de la partida para guardarlo en disco.
     *
     * @return instantánea del juego
     */
    public GameSnapshot captureSnapshot() {
        final List<CoinSnapshot> coinSnaps = new ArrayList<>();
        for (final Coin coin : coins) {
            coinSnaps.add(new CoinSnapshot(coin.isCollected(), coin.isPermanentlyCollected()));
        }
        final List<EnemySnapshot> enemySnaps = new ArrayList<>();
        for (final Enemy enemy : enemies) {
            enemySnaps.add(snapshotForEnemy(enemy));
        }
        final List<PowerUpSnapshot> powerSnaps = new ArrayList<>();
        for (final PowerUp powerUp : powerUps) {
            final boolean permanent = powerUp instanceof LifeSource
                    && ((LifeSource) powerUp).isPermanentlyUsed();
            powerSnaps.add(new PowerUpSnapshot(powerUp.getType(),
                    powerUp.isConsumed(), permanent));
        }
        return new GameSnapshot(
                state,
                timeRemaining,
                coinsCollected,
                intermediateCheckpointReached,
                respawnZoneKind(),
                playerSkin,
                player2Skin,
                originalPlayerSkin,
                originalPlayer2Skin,
                twoPlayerMode,
                winnerName,
                playerSnapshot(player, playerSkin),
                player2 != null ? playerSnapshot(player2, player2Skin) : null,
                coinSnaps,
                enemySnaps,
                powerSnaps);
    }

    /**
     * Restaura una partida previamente guardada sobre un nivel ya cargado.
     *
     * @param snap instantánea a aplicar
     * @throws HardestGameException si los datos no coinciden con el nivel
     */
    public void restoreFromSnapshot(final GameSnapshot snap) throws HardestGameException {
        if (board == null || coins == null) {
            throw new InvalidGameStateException("No hay nivel cargado para restaurar la partida");
        }
        playerSkin = snap.getPlayerSkin();
        player2Skin = snap.getPlayer2Skin();
        originalPlayerSkin = snap.getOriginalPlayerSkin();
        originalPlayer2Skin = snap.getOriginalPlayer2Skin();
        twoPlayerMode = snap.isTwoPlayerMode();
        winnerName = snap.getWinnerName() != null ? snap.getWinnerName() : "";
        timeRemaining = snap.getTimeRemaining();
        coinsCollected = snap.getCoinsCollected();
        intermediateCheckpointReached = snap.isIntermediateCheckpointReached();
        currentRespawnZone = zoneFromKind(snap.getRespawnZoneKind());
        state = snap.getState();

        player = restorePlayerFromSnapshot(snap.getPlayer1(), true);
        if (snap.isTwoPlayerMode()) {
            if (snap.getPlayer2() == null) {
                throw new InvalidGameStateException("Guardado PvP sin datos del jugador 2");
            }
            player2 = restorePlayerFromSnapshot(snap.getPlayer2(), false);
        } else {
            player2 = null;
        }

        if (snap.getCoins().size() != coins.size()) {
            throw new InvalidGameStateException("El guardado no coincide con las monedas del nivel");
        }
        for (int i = 0; i < coins.size(); i++) {
            final CoinSnapshot cs = snap.getCoins().get(i);
            coins.get(i).restoreSavedState(cs.isCollected(), cs.isPermanent());
        }

        if (snap.getEnemies().size() != enemies.size()) {
            throw new InvalidGameStateException("El guardado no coincide con los enemigos del nivel");
        }
        for (int i = 0; i < enemies.size(); i++) {
            restoreEnemyFromSnapshot(enemies.get(i), snap.getEnemies().get(i));
        }

        if (snap.getPowerUps().size() != powerUps.size()) {
            throw new InvalidGameStateException("El guardado no coincide con los power-ups del nivel");
        }
        for (int i = 0; i < powerUps.size(); i++) {
            final PowerUpSnapshot ps = snap.getPowerUps().get(i);
            final PowerUp powerUp = powerUps.get(i);
            if (powerUp instanceof LifeSource) {
                ((LifeSource) powerUp).restoreLifeSourceSavedState(
                        ps.isConsumed(), ps.isPermanentlyUsed());
            } else {
                powerUp.restoreSavedState(ps.isConsumed());
            }
        }
    }

    /**
     * Retorna el tipo de zona de reaparición activa como texto.
     *
     * @return tipo de zona ("START", "INTERMEDIATE" o "FINAL")
     */
    private String respawnZoneKind() {
        if (currentRespawnZone == startZone) {
            return "START";
        }
        if (intermediateZone != null && currentRespawnZone == intermediateZone) {
            return "INTERMEDIATE";
        }
        return "FINAL";
    }

    /**
     * Resuelve el nombre del tipo de zona de reaparición a la instancia de la zona correspondiente.
     *
     * @param kind tipo de zona en texto
     * @return zona correspondiente del tablero
     */
    private Zone zoneFromKind(final String kind) {
        if ("INTERMEDIATE".equals(kind) && intermediateZone != null) {
            return intermediateZone;
        }
        if ("FINAL".equals(kind)) {
            return finalZone;
        }
        return startZone;
    }

    /**
     * Construye la instantánea del jugador a partir de su instancia y skin.
     *
     * @param p    el jugador a fotografiar
     * @param skin el skin asignado
     * @return instantánea del estado del jugador
     */
    private PlayerSnapshot playerSnapshot(final Player p, final String skin) {
        boolean greenShield = false;
        int greenInvuln = 0;
        if (p instanceof GreenPlayer) {
            final GreenPlayer gp = (GreenPlayer) p;
            greenShield = gp.hasShield();
            greenInvuln = gp.getInvulnerableTicksForSave();
        }
        return new PlayerSnapshot(skin, p.getRow(), p.getCol(), p.getDeaths(),
                p.getExtraLives(), p.hitImmunityTicks, greenShield, greenInvuln);
    }

    /**
     * Construye la instantánea del estado de un enemigo de forma segura.
     *
     * @param enemy enemigo a fotografiar
     * @return instantánea del enemigo
     */
    private EnemySnapshot snapshotForEnemy(final Enemy enemy) {
        if (enemy instanceof BasicEnemy) {
            return ((BasicEnemy) enemy).toSnapshot();
        }
        if (enemy instanceof VerticalEnemy) {
            return ((VerticalEnemy) enemy).toSnapshot();
        }
        if (enemy instanceof AcceleratedEnemy) {
            return ((AcceleratedEnemy) enemy).toSnapshot();
        }
        if (enemy instanceof PatrolEnemy) {
            return ((PatrolEnemy) enemy).toSnapshot();
        }
        return new EnemySnapshot(enemy.getRow(), enemy.getCol(), 0, 0, 0);
    }

    /**
     * Restaura el estado interno del enemigo a partir de una instantánea.
     *
     * @param enemy enemigo a restaurar
     * @param es    instantánea con el estado previo
     */
    private void restoreEnemyFromSnapshot(final Enemy enemy, final EnemySnapshot es) {
        if (enemy instanceof BasicEnemy) {
            ((BasicEnemy) enemy).restoreSavedState(es.getRow(), es.getCol(),
                    es.getDeltaRow(), es.getDeltaCol(), es.getTickCount());
        } else if (enemy instanceof VerticalEnemy) {
            ((VerticalEnemy) enemy).restoreSavedState(es.getRow(), es.getCol(),
                    es.getDeltaRow(), es.getTickCount());
        } else if (enemy instanceof AcceleratedEnemy) {
            ((AcceleratedEnemy) enemy).restoreSavedState(es.getRow(), es.getCol(),
                    es.getDeltaRow(), es.getDeltaCol(), es.getTickCount());
        } else if (enemy instanceof PatrolEnemy) {
            ((PatrolEnemy) enemy).restoreSavedState(es.getRow(), es.getCol(),
                    es.getDeltaRow(), es.getTickCount());
        }
    }

    /**
     * Restaura un jugador a partir de su instantánea, instanciando el tipo correcto según el skin.
     *
     * @param snap        la instantánea a restaurar
     * @param firstPlayer true para jugador 1, false para jugador 2
     * @return nueva instancia del jugador restaurado
     * @throws InvalidSkinException si el skin guardado no es reconocido
     */
    private Player restorePlayerFromSnapshot(final PlayerSnapshot snap,
                                             final boolean firstPlayer)
            throws InvalidSkinException {
        final Player restored = createPlayerBySkin(snap.getSkin(), snap.getRow(), snap.getCol());
        restored.restoreSavedState(snap.getRow(), snap.getCol(), snap.getDeaths(),
                snap.getExtraLives(), snap.getHitImmunityTicks());
        if (restored instanceof GreenPlayer) {
            ((GreenPlayer) restored).restoreGreenSavedState(
                    snap.isGreenShield(), snap.getGreenInvulnerableTicks());
        }
        if (firstPlayer) {
            playerSkin = snap.getSkin();
        } else {
            player2Skin = snap.getSkin();
        }
        return restored;
    }
}
