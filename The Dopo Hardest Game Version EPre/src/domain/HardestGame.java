package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal que representa la lógica central del juego.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class HardestGame {

    public enum State { PLAYING, PAUSED, WON, LOST }

    private Board       board;
    private Player      player;
    private Player      player2;
    private List<Coin>  coins;
    private List<Enemy> enemies;
    private Zone        startZone;
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

    /**
     * Crea el juego en estado de pausa inicial.
     */
    public HardestGame() {
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
    private Player createPlayer(final int row, final int col) {
        switch (playerSkin) {
            case "Azul (Inky)":   return new BluePlayer(row, col);
            case "Verde (Clyde)": return new GreenPlayer(row, col);
            default:              return new RedPlayer(row, col);
        }
    }

    /**
     * Carga la configuración del nivel y reinicia la partida.
     *
     * @param filename ruta del archivo de configuración
     * @throws HardestGameException si el archivo es inválido
     */
    public void loadConfiguration(final String filename) throws HardestGameException {
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

        coins          = cfg.getCoins();
        enemies        = cfg.getEnemies();
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
     * Mueve al jugador principal y evalúa monedas, colisiones y victoria.
     *
     * @param deltaRow desplazamiento en filas
     * @param deltaCol desplazamiento en columnas
     */
    public void movePlayer(final int deltaRow, final int deltaCol) {
        if (state != State.PLAYING) {
            return;
        }
        player.move(deltaRow, deltaCol, board);
        checkCoinsForPlayer(true);
        checkIntermediateZone();
        checkEnemyCollisionForPlayer(true);
        checkWinForPlayer(true);
    }

    /**
     * Mueve al segundo jugador y evalúa monedas, colisiones y victoria.
     *
     * @param deltaRow desplazamiento en filas
     * @param deltaCol desplazamiento en columnas
     */
    public void moveSecondPlayer(final int deltaRow, final int deltaCol) {
        if (state != State.PLAYING || !twoPlayerMode || player2 == null) {
            return;
        }
        player2.move(deltaRow, deltaCol, board);
        checkCoinsForPlayer(false);
        checkEnemyCollisionForPlayer(false);
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
        tickGreenPlayerInvulnerability(player);
        if (twoPlayerMode && player2 != null) {
            tickGreenPlayerInvulnerability(player2);
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
    private void checkCoinsForPlayer(final boolean firstPlayer) {
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
     * Aplica el efecto de una moneda de skin al jugador que la recolectó.
     *
     * @param activePlayer jugador que recolectó la moneda
     * @param coin         moneda recolectada
     * @param firstPlayer  true para jugador 1, false para jugador 2
     */
    private void applyCoinEffect(final Player activePlayer, final Coin coin, final boolean firstPlayer) {
        if (!(coin instanceof SkinCoin)) {
            return;
        }
        final SkinCoin skinCoin = (SkinCoin) coin;
        if (firstPlayer) {
            player = replacePlayerSkin(activePlayer, mapSkinName(skinCoin.getSkinName()));
            playerSkin = mapSkinName(skinCoin.getSkinName());
        } else {
            player2 = replacePlayerSkin(activePlayer, mapSkinName(skinCoin.getSkinName()));
            player2Skin = mapSkinName(skinCoin.getSkinName());
        }
    }

    /**
     * Actualiza el punto de reaparición si el jugador entra en la zona intermedia.
     */
    private void checkIntermediateZone() {
        if (intermediateZone != null
                && intermediateZone.contains(player.getRow(), player.getCol())) {
            currentRespawnZone = intermediateZone;
        }
    }

    /**
     * Detecta colisión enemigo-jugador y aplica las consecuencias correspondientes.
     * Verde con escudo: absorbe el golpe sin morir y gana invulnerabilidad temporal.
     * Verde sin escudo pero invulnerable: ignora la colisión.
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
                if (activePlayer instanceof GreenPlayer) {
                    final GreenPlayer gp = (GreenPlayer) activePlayer;
                    if (gp.isInvulnerable()) {
                        continue;
                    }
                    if (gp.absorbHit()) {
                        continue;
                    }
                }
                final Zone respawnZone = firstPlayer
                        ? currentRespawnZone
                        : (intermediateZone != null ? intermediateZone : finalZone);
                activePlayer.respawn(respawnZone);
                resetAllCoins();
                resetPlayerSkinOnDeath(firstPlayer, activePlayer);
                return;
            }
        }
    }

    /**
     * Decrementa el contador de invulnerabilidad de un GreenPlayer si aplica.
     *
     * @param p jugador a evaluar
     */
    private void tickGreenPlayerInvulnerability(final Player p) {
        if (p instanceof GreenPlayer) {
            ((GreenPlayer) p).tickInvulnerability();
        }
    }

    /**
     * Restaura el skin original del jugador tras morir.
     *
     * @param firstPlayer  true para jugador 1, false para jugador 2
     * @param activePlayer jugador que murió
     */
    private void resetPlayerSkinOnDeath(final boolean firstPlayer, final Player activePlayer) {
        if (firstPlayer) {
            player = replacePlayerSkin(activePlayer, originalPlayerSkin);
            playerSkin = originalPlayerSkin;
        } else {
            player2 = replacePlayerSkin(activePlayer, originalPlayer2Skin);
            player2Skin = originalPlayer2Skin;
        }
    }

    /**
     * Reinicia todas las monedas a estado no recolectado y el contador a cero.
     */
    private void resetAllCoins() {
        for (final Coin coin : coins) {
            coin.reset();
        }
        coinsCollected = 0;
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
    private Player createPlayerBySkin(final String skin, final int row, final int col) {
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
    private Player replacePlayerSkin(final Player previousPlayer, final String skin) {
        final Player replaced = createPlayerBySkin(skin, previousPlayer.getRow(), previousPlayer.getCol());
        replaced.deaths = previousPlayer.getDeaths();
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
}