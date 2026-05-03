package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal que representa la lógica central del juego.
 * Coordina el estado completo de una partida incluyendo tablero,
 * jugador, enemigos, monedas, zonas seguras y condiciones de
 * victoria, derrota y pausa.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class HardestGame {

    /**
     * Estados posibles del juego.
     */
    public enum State {PLAYING, PAUSED, WON, LOST}

    private Board board;
    private Player player;
    private List<Coin> coins;
    private List<Enemy> enemies;
    private Zone startZone;
    private Zone finalZone;
    private int timeRemaining;
    private int coinsCollected;
    private State state;

    /**
     * Crea el juego en estado de pausa inicial.
     */
    public HardestGame() {
        state = State.PAUSED;
    }

    /**
     * Carga la configuración del nivel desde un archivo y reinicia la partida.
     *
     * @param filename ruta del archivo de configuración del nivel
     * @throws HardestGameException si el archivo es inválido
     */
    public void loadConfiguration(final String filename) throws HardestGameException {
        final GameConfiguration cfg = new GameConfiguration();
        cfg.load(filename);

        board = new Board(cfg.getRows(), cfg.getCols());
        for (final int[] wall : cfg.getWalls()) {
            board.setWall(wall[0], wall[1]);
        }

        startZone = cfg.getStartZone();
        finalZone = cfg.getFinalZone();
        board.addEnemyForbiddenZone(startZone);
        board.addEnemyForbiddenZone(finalZone);

        coins = cfg.getCoins();
        enemies = cfg.getEnemies();
        timeRemaining = cfg.getTimeLimit();
        coinsCollected = 0;

        player = new RedPlayer(
                startZone.getRow() + startZone.getRows() / 2,
                startZone.getCol() + startZone.getCols() / 2);

        state = State.PLAYING;
    }

    /**
     * Mueve al jugador en la dirección indicada si el juego está activo.
     *
     * @param dr desplazamiento en filas
     * @param dc desplazamiento en columnas
     */
    public void movePlayer(final int deltaRow, final int deltaCol) {
        if (state != State.PLAYING) {
            return;
        }
        player.move(deltaRow, deltaCol, board);
        checkCoins();
        checkEnemyCollision();
        checkWin();
    }

    /**
     * Actualiza el movimiento de los enemigos y verifica colisiones.
     */
    public void tick() {
        if (state != State.PLAYING) {
            return;
        }
        for (final Enemy enemy : enemies) {
            enemy.update(board);
        }
        checkEnemyCollision();
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
     * Alterna el estado entre PLAYING y PAUSED.
     */
    public void togglePause() {
        if (state == State.PLAYING) {
            state = State.PAUSED;
        } else if (state == State.PAUSED) {
            state = State.PLAYING;
        }
    }

    private void checkCoins() {
        for (final Coin coin : coins) {
            if (!coin.isCollected()
                    && coin.getRow() == player.getRow()
                    && coin.getCol() == player.getCol()) {
                coin.collect();
                coinsCollected++;
            }
        }
    }

    private void checkEnemyCollision() {
        for (final Enemy enemy : enemies) {
            if (enemy.collidesWith(player)) {
                player.respawn(startZone);
                resetAllCoins();
                return;
            }
        }
    }

    private void resetAllCoins() {
        for (final Coin coin : coins) {
            coin.reset();
        }
        coinsCollected = 0;
    }

    private void checkWin() {
        if (coinsCollected == coins.size()
                && finalZone.contains(player.getRow(), player.getCol())) {
            state = State.WON;
        }
    }

    /**
     * Retorna el tablero lógico activo de la partida actual.
     *
     * @return tablero actual del juego
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Retorna el jugador que participa en la partida actual.
     *
     * @return jugador actual de la partida
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Retorna la lista de monedas presentes en el nivel actual.
     *
     * @return lista de monedas del nivel
     */
    public List<Coin> getCoins() {
        return coins;
    }

    /**
     * Retorna la lista de enemigos presentes en el nivel actual.
     *
     * @return lista de enemigos del nivel
     */
    public List<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Retorna la zona segura inicial donde aparece el jugador al morir.
     *
     * @return zona segura inicial del nivel
     */
    public Zone getStartZone() {
        return startZone;
    }

    /**
     * Retorna la zona segura final que el jugador debe alcanzar para ganar.
     *
     * @return zona segura final del nivel
     */
    public Zone getFinalZone() {
        return finalZone;
    }

    /**
     * Retorna el tiempo restante en segundos para completar el nivel.
     *
     * @return tiempo restante del nivel en segundos
     */
    public int getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Retorna el número de monedas recolectadas en el intento actual.
     *
     * @return número de monedas recolectadas en este intento
     */
    public int getCoinsCollected() {
        return coinsCollected;
    }

    /**
     * Retorna el total de monedas que contiene el nivel cargado.
     *
     * @return número total de monedas del nivel
     */
    public int getTotalCoins() {
        return coins != null ? coins.size() : 0;
    }

    /**
     * Retorna el estado actual en que se encuentra la partida.
     *
     * @return estado actual del juego
     */
    public State getState() {
        return state;
    }

    /**
     * Retorna el número acumulado de muertes del jugador en esta partida.
     *
     * @return número total de muertes del jugador
     */
    public int getDeaths() {
        return player != null ? player.getDeaths() : 0;
    }
}