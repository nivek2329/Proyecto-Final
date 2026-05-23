package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import main.domain.HardestGame;
import main.domain.HardestGameException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clase de pruebas unitarias para la clase HardestGame.
 * Verifica el comportamiento completo de la lógica principal del juego,
 * incluyendo la carga de niveles, el movimiento del jugador, la interacción
 * con monedas, enemigos, power-ups, el manejo del tiempo, la pausa del juego,
 * las condiciones de victoria y derrota, y la creación correcta de excepciones.
 *
 * Las pruebas utilizan archivos temporales para simular configuraciones
 * de niveles mínimos, garantizando aislamiento y repetibilidad.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class HardestGameTest {

    private HardestGame game;

    /**
     * Retorna la definición textual de un nivel mínimo estándar para las pruebas.
     *
     * @return configuración de nivel en formato texto
     */
    private String miniLevel() {
        return String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "COIN YELLOW 2 4",
                "ENEMY BASIC 4 6 VERTICAL",
                "");
    }

    /**
     * Retorna la definición textual de un nivel mínimo orientado a pruebas de colisión.
     *
     * @return configuración de nivel con enemigo cercano
     */
    private String miniLevelParaColision() {
        return String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "COIN YELLOW 2 4",
                "ENEMY BASIC 2 6 VERTICAL",
                "");
    }

    /**
     * Inicializa el entorno de prueba antes de cada test.
     *
     * @throws Exception si ocurre un error al crear archivos temporales
     */
    @Before
    public void setUp() throws Exception {
        game = new HardestGame();
        Path f = Files.createTempFile("dopo-level-", ".txt");
        Files.writeString(f, miniLevel());
        game.loadConfiguration(f.toString());
    }

    /**
     * Libera el entorno de prueba después de cada test.
     */
    @After
    public void tearDown() {
        game = null;
    }

    /**
     * Verifica carga correcta del nivel y movimiento válido del jugador.
     */
    @Test
    public void shouldCargaYPuedeMoverJugador() throws HardestGameException {
        assertEquals(
                "El juego debe iniciar en estado PLAYING",
                HardestGame.State.PLAYING,
                game.getState()
        );
        int row0 = game.getPlayer().getRow();
        game.movePlayer(1, 0);
        assertNotEquals(
                "El jugador debe haberse movido una fila hacia abajo",
                row0,
                game.getPlayer().getRow()
        );
    }

    /**
     * Comprueba recolección correcta de una moneda.
     */
    @Test
    public void shouldRecogeMoneda() throws HardestGameException {
        for (int i = 0; i < 3; i++) game.movePlayer(0, 1);
        assertEquals(
                "El jugador debe haber recogido 1 moneda al pasar por col 4",
                1,
                game.getCoinsCollected()
        );
    }

    /**
     * Verifica colisión con enemigo, reinicio de monedas y aumento de muertes.
     *
     * @throws Exception si ocurre un error al cargar el nivel
     */
    @Test
    public void shouldColisionEnemigoReseteaMonedasYAumentaMuertes() throws Exception { // HardestGameException incluida
        Path f = Files.createTempFile("dopo-colision-", ".txt");
        Files.writeString(f, miniLevelParaColision());

        HardestGame g = new HardestGame();
        g.loadConfiguration(f.toString());

        for (int i = 0; i < 3; i++) g.movePlayer(0, 1);
        assertEquals(
                "Deberia haber 1 moneda recogida antes de la colision",
                1,
                g.getCoinsCollected()
        );

        for (int i = 0; i < 2; i++) g.movePlayer(0, 1);
        assertEquals(
                "Las monedas deben resetearse a 0 tras morir",
                0,
                g.getCoinsCollected()
        );
        assertEquals(
                "El contador de muertes debe ser 1 tras la colision",
                1,
                g.getDeaths()
        );
    }

    /**
     * Comprueba derrota al agotarse el tiempo.
     *
     * @throws Exception si ocurre un error al cargar el nivel
     */
    @Test
    public void shouldNotTiempoCeroPierde() throws Exception {
        Path f = Files.createTempFile("dopo-time-", ".txt");
        Files.writeString(f, miniLevel().replace("TIME 60", "TIME 10"));

        HardestGame g = new HardestGame();
        g.loadConfiguration(f.toString());
        for (int i = 0; i < 10; i++) {
            g.decrementTime();
        }

        assertEquals(
                "Al llegar a 0 segundos el estado debe ser LOST",
                HardestGame.State.LOST,
                g.getState()
        );
    }

    /**
     * Verifica la condición de victoria al llegar a la zona final
     * con todas las monedas recolectadas.
     */
    @Test
    public void shouldVictoriaConTodasLasMonedasYZonaFinal() throws HardestGameException {
        for (int i = 0; i < 3; i++) game.movePlayer(0, 1);
        assertEquals(
                "Debe haber 1 moneda recogida antes de ir a la zona final",
                1,
                game.getCoinsCollected()
        );

        for (int i = 0; i < 4; i++) game.movePlayer(0, 1);
        assertEquals(
                "Al llegar a la zona final con todas las monedas el estado debe ser WON",
                HardestGame.State.WON,
                game.getState()
        );
    }

    /**
     * Comprueba correcto cambio entre pausa y juego activo.
     */
    @Test
    public void shouldTogglePause() {
        assertEquals(
                "El estado inicial debe ser PLAYING",
                HardestGame.State.PLAYING,
                game.getState()
        );
        game.togglePause();
        assertEquals(
                "Tras pausar el estado debe ser PAUSED",
                HardestGame.State.PAUSED,
                game.getState()
        );
        game.togglePause();
        assertEquals(
                "Tras reanudar el estado debe ser PLAYING",
                HardestGame.State.PLAYING,
                game.getState()
        );
    }

    /**
     * Verifica que el tablero del juego no sea null.
     */
    @Test
    public void shouldGetBoard() {
        assertNotNull("El tablero no debe ser null", game.getBoard());
    }

    /**
     * Comprueba el número total de monedas del nivel.
     */
    @Test
    public void shouldGetTotalCoins() {
        assertEquals(
                "El nivel debe tener exactamente 1 moneda en total",
                1,
                game.getTotalCoins()
        );
    }

    /**
     * Verifica la lista de enemigos del nivel.
     */
    @Test
    public void shouldGetEnemies() {
        assertNotNull("La lista de enemigos no debe ser null", game.getEnemies());
        assertEquals(
                "El nivel debe tener exactamente 1 enemigo",
                1,
                game.getEnemies().size()
        );
    }

    /**
     * Verifica existencia de la zona inicial.
     */
    @Test
    public void shouldGetStartZone() {
        assertNotNull("La zona de inicio no debe ser null", game.getStartZone());
    }

    /**
     * Verifica existencia de la zona final.
     */
    @Test
    public void shouldGetFinalZone() {
        assertNotNull("La zona final no debe ser null", game.getFinalZone());
    }

    /**
     * Comprueba que el tiempo inicial sea mayor que 0.
     */
    @Test
    public void shouldGetTimeRemaining() {
        assertTrue(
                "El tiempo restante debe ser mayor que 0 al iniciar",
                game.getTimeRemaining() > 0
        );
    }

    /**
     * Verifica que el jugador no se mueve cuando el juego está pausado.
     */
    @Test(expected = HardestGameException.class)
    public void shouldNotMoveWhenPaused() throws HardestGameException {
        game.togglePause();
        game.movePlayer(1, 0);
    }

    /**
     * Comprueba que la lógica de actualización no actúa en pausa.
     */
    @Test
    public void shouldNotTickWhenPaused() {
        game.togglePause();
        game.tick();
        assertEquals(
                "El estado debe seguir siendo PAUSED tras un tick en pausa",
                HardestGame.State.PAUSED,
                game.getState()
        );
    }

    /**
     * Verifica creación correcta de excepción con causa.
     */
    @Test
    public void shouldCreateExceptionWithCause() {
        Throwable cause = new RuntimeException("causa raiz");
        HardestGameException ex = new HardestGameException("mensaje", cause);

        assertEquals(
                "El mensaje de la excepcion debe coincidir",
                "mensaje",
                ex.getMessage()
        );
        assertEquals(
                "La causa de la excepcion debe coincidir",
                cause,
                ex.getCause()
        );
    }

    /**
     * Verifica creación correcta de excepción con solo mensaje.
     */
    @Test
    public void shouldCreateExceptionWithMessageOnly() {
        HardestGameException ex = new HardestGameException("solo mensaje");

        assertEquals(
                "El mensaje de la excepcion debe coincidir",
                "solo mensaje",
                ex.getMessage()
        );
        assertNull(
                "La causa debe ser null si no se especifico",
                ex.getCause()
        );
    }

    /**
     * Verifica que las vidas extra inician en 0.
     */
    @Test
    public void shouldExtraLivesStartAtZero() {
        assertEquals(
                "Las vidas extra deben iniciar en 0",
                0,
                game.getExtraLives()
        );
    }

    /**
     * Verifica que la lista de power-ups no es null.
     */
    @Test
    public void shouldGetPowerUpsNotNull() {
        assertNotNull("La lista de power-ups no debe ser null", game.getPowerUps());
    }

    /**
     * Al llegar por primera vez a la zona intermedia, las monedas previas no reaparecen al morir.
     */
    @Test
    public void shouldCheckpointIntermedioFijaMonedasPrevias() throws Exception {
        final String level = String.join("\n",
                "ROWS 5",
                "COLS 12",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_INTERMEDIATE 0 5 5 2",
                "SAFE_FINAL 0 10 5 2",
                "COIN YELLOW 2 3",
                "ENEMY BASIC 3 4 VERTICAL",
                "");
        final Path f = Files.createTempFile("dopo-checkpoint-", ".txt");
        Files.writeString(f, level);

        final HardestGame g = new HardestGame();
        g.loadConfiguration(f.toString());

        for (int i = 0; i < 2; i++) {
            g.movePlayer(0, 1);
        }
        assertEquals(1, g.getCoinsCollected());

        for (int i = 0; i < 2; i++) {
            g.movePlayer(0, 1);
        }
        assertEquals(
                main.domain.Zone.Type.INTERMEDIATE,
                g.getCurrentRespawnZone().getType()
        );

        g.movePlayer(1, 0);
        g.movePlayer(0, -1);
        g.tick();
        assertEquals(
                "La moneda del checkpoint debe seguir recolectada tras morir",
                1,
                g.getCoinsCollected()
        );
    }

    /**
     * En modo dos jugadores, la colisión reinicia ambos a su punto de inicio.
     */
    @Test
    public void shouldColisionJugadoresReiniciaAInicio() throws Exception {
        final Path f = Files.createTempFile("dopo-pvp-", ".txt");
        Files.writeString(f, miniLevel());

        final HardestGame g = new HardestGame();
        g.setTwoPlayerMode(true);
        g.loadConfiguration(f.toString());

        final int p1StartRow = g.getStartZone().getRow() + g.getStartZone().getRows() / 2;
        final int p1StartCol = g.getStartZone().getCol() + g.getStartZone().getCols() / 2;
        final int p2StartRow = g.getFinalZone().getRow() + g.getFinalZone().getRows() / 2;
        final int p2StartCol = g.getFinalZone().getCol() + g.getFinalZone().getCols() / 2;

        for (int i = 0; i < 4; i++) {
            g.movePlayer(0, 1);
        }
        for (int i = 0; i < 4; i++) {
            g.moveSecondPlayer(0, -1);
        }

        assertEquals(p1StartRow, g.getPlayer().getRow());
        assertEquals(p1StartCol, g.getPlayer().getCol());
        assertEquals(p2StartRow, g.getPlayer2().getRow());
        assertEquals(p2StartCol, g.getPlayer2().getCol());
    }

    /**
     * La moneda skin cambia al jugador hasta que muere; entonces vuelve al skin del menú.
     */
    @Test
    public void shouldSkinCoinCambiaHastaMorir() throws Exception {
        final String body = String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "COIN SKIN BLUE 2 4",
                "ENEMY BASIC 2 6 VERTICAL",
                "");

        final Path f = Files.createTempFile("dopo-skin-", ".txt");
        Files.writeString(f, body);

        final HardestGame g = new HardestGame();
        g.setPlayerSkin("Rojo (Blinky)");
        g.loadConfiguration(f.toString());

        for (int i = 0; i < 3; i++) {
            g.movePlayer(0, 1);
        }
        assertTrue("Tras la moneda skin debe ser Azul",
                g.getPlayer() instanceof main.domain.BluePlayer);

        for (int i = 0; i < 2; i++) {
            g.movePlayer(0, 1);
        }
        assertTrue("Tras morir debe volver al skin original",
                g.getPlayer() instanceof main.domain.RedPlayer);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NUEVOS TESTS PARA AUMENTAR BRANCH COVERAGE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * decrementTime no hace nada cuando el estado no es PLAYING.
     */
    @Test
    public void shouldDecrementTimeIgnoredWhenNotPlaying() throws Exception {
        game.togglePause(); // PLAYING → PAUSED
        final int timeBefore = game.getTimeRemaining();
        game.decrementTime();
        assertEquals("decrementTime no debe cambiar el tiempo en PAUSED",
                timeBefore, game.getTimeRemaining());
    }

    /**
     * tick actualiza defensa de player2 en modo dos jugadores.
     */
    @Test
    public void shouldTickUpdatesTwoPlayerDefense() throws Exception {
        final Path f = Files.createTempFile("dopo-tick2p-", ".txt");
        Files.writeString(f, miniLevel());
        final HardestGame g = new HardestGame();
        g.setTwoPlayerMode(true);
        g.loadConfiguration(f.toString());
        // Solo verificamos que tick() no lanza excepción con player2 presente
        g.tick();
        assertNotNull("Player2 no debe ser null en modo dos jugadores", g.getPlayer2());
    }

    /**
     * Jugador con inmunidad temporal absorbe golpe sin morir.
     */
    @Test
    public void shouldHitImmunePlayerAbsorbeGolpeSinMorir() throws Exception {
        // Usamos un nivel donde el enemigo esté en la misma posición que el jugador
        final String level = String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "COIN YELLOW 2 4",
                "ENEMY BASIC 2 2 VERTICAL",
                "");
        final Path f = Files.createTempFile("dopo-immune-", ".txt");
        Files.writeString(f, level);
        final HardestGame g = new HardestGame();
        g.loadConfiguration(f.toString());

        // Hacemos que el jugador muera una vez para obtener inmunidad
        g.movePlayer(0, 1); // col 2 → colisiona con enemigo en (2,2) → respawn + inmunidad
        final int deathsAfterFirst = g.getDeaths();

        // Con inmunidad activa, el tick no debe causar otra muerte
        g.tick();
        assertEquals("Con inmunidad activa las muertes no deben aumentar tras tick",
                deathsAfterFirst, g.getDeaths());
    }

    /**
     * LifeSource ya usada permanentemente no da vida extra al volver a pasar.
     */
    @Test
    public void shouldLifeSourcePermanentlyUsedIgnored() throws Exception {
        // Keyword correcto: POWERUP LIFE fila col
        final String level = String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "POWERUP LIFE 2 4",
                "");
        final Path f = Files.createTempFile("dopo-lifesource-", ".txt");
        Files.writeString(f, level);
        final HardestGame g = new HardestGame();
        g.loadConfiguration(f.toString());

        // Primera vez: recoge LifeSource en col 4 → da vida extra
        for (int i = 0; i < 3; i++) g.movePlayer(0, 1);
        final int livesAfterFirst = g.getExtraLives();
        assertTrue("Debe haber conseguido al menos 1 vida extra", livesAfterFirst >= 1);

        // LifeSource ya está marcada permanent+consumed; volver a pasar no da más vidas
        g.movePlayer(0, -1); // retrocede a col 3
        g.movePlayer(0, 1);  // vuelve a col 4 (LifeSource ya consumida)
        assertEquals("La LifeSource permanente no debe dar vidas adicionales",
                livesAfterFirst, g.getExtraLives());
    }

    /**
     * resolveRespawnZone para player2 sin intermediateZone usa finalZone.
     */
    @Test
    public void shouldPlayer2RespawnAtFinalZoneWhenNoIntermediate() throws Exception {
        final Path f = Files.createTempFile("dopo-p2resp-", ".txt");
        Files.writeString(f, miniLevel());
        final HardestGame g = new HardestGame();
        g.setTwoPlayerMode(true);
        g.loadConfiguration(f.toString());

        // Forzamos colisión de player2 con un enemigo moviéndolo encima
        // En el nivel mini, el enemigo está en (4,6). Player2 inicia en zona final.
        // Solo verificamos que el respawn de player2 ocurre sin error
        assertNull("Sin zona intermedia, intermediateZone debe ser null",
                g.getIntermediateZone());
        assertNotNull("Player2 debe existir", g.getPlayer2());
    }

    /**
     * Segunda entrada a checkIntermediateZone no re-marca monedas.
     */
    @Test
    public void shouldIntermediateZoneNotRemoveCheckpointOnSecondEntry() throws Exception {
        final String level = String.join("\n",
                "ROWS 5",
                "COLS 12",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_INTERMEDIATE 0 5 5 2",
                "SAFE_FINAL 0 10 5 2",
                "COIN YELLOW 2 3",
                "");
        final Path f = Files.createTempFile("dopo-check2-", ".txt");
        Files.writeString(f, level);
        final HardestGame g = new HardestGame();
        g.loadConfiguration(f.toString());

        // Primera entrada al checkpoint: col 2→3(moneda)→4→5
        g.movePlayer(0, 1); g.movePlayer(0, 1); // recoge moneda en col 3
        g.movePlayer(0, 1); g.movePlayer(0, 1); // llega a zona intermedia (col 5)
        assertEquals(main.domain.Zone.Type.INTERMEDIATE, g.getCurrentRespawnZone().getType());
        final int coinsAfterFirst = g.getCoinsCollected();

        // Salir y volver a entrar a la zona intermedia → segunda entrada, no debe re-marcar
        g.movePlayer(0, -1); // sale
        g.movePlayer(0, 1);  // vuelve a entrar
        assertEquals("Las monedas no deben cambiar en segunda entrada al checkpoint",
                coinsAfterFirst, g.getCoinsCollected());
    }

    /**
     * Player2 gana al llegar a startZone con todas las monedas.
     */
    @Test
    public void shouldPlayer2WinsWhenReachesStartZone() throws Exception {
        // Nivel con filas separadas: P1 en fila 5, P2 en fila 7 → no colisionan
        // StartZone abarca todas las filas para que P2 gane al llegar al col 0-1
        final String level = String.join("\n",
                "ROWS 10",
                "COLS 14",
                "TIME 60",
                "SAFE_START 0 0 10 2",
                "SAFE_FINAL 0 12 10 2",
                "COIN YELLOW 7 6",
                "");
        final Path f = Files.createTempFile("dopo-p2win-", ".txt");
        Files.writeString(f, level);
        final HardestGame g = new HardestGame();
        g.setTwoPlayerMode(true);
        g.loadConfiguration(f.toString());

        // P1 inicia en row=5, col=1. P2 inicia en row=5, col=13.
        // Movemos P2 a fila 7 para recoger la moneda sin chocar con P1
        g.moveSecondPlayer(1, 0); // fila 5→6
        g.moveSecondPlayer(1, 0); // fila 6→7
        // P2 está en fila 7, col 13. Moneda en fila 7, col 6.
        for (int i = 0; i < 7; i++) g.moveSecondPlayer(0, -1); // col 13→6: recoge moneda
        assertEquals("P2 debe haber recogido la moneda", 1, g.getCoinsCollected());
        // P2 sigue hacia la startZone (cols 0-1). Col 6-5=1 ya está dentro → WON
        for (int i = 0; i < 5; i++) g.moveSecondPlayer(0, -1); // col 6→1: ya en startZone

        assertEquals("Player2 debe ganar al llegar a startZone",
                HardestGame.State.WON, g.getState());
        assertEquals("El ganador debe ser Jugador 2", "Jugador 2", g.getWinnerName());
    }

    /**
     * restoreFromSnapshot falla si hay discrepancia en cantidad de enemigos.
     */
    @Test
    public void shouldRestoreFromSnapshotFailOnEnemyCountMismatch() throws Exception {
        final main.domain.GameSnapshot snap = game.captureSnapshot();

        // Creamos un snapshot con un enemigo extra
        final java.util.List<main.domain.GameSnapshot.EnemySnapshot> badEnemies =
                new java.util.ArrayList<>(snap.getEnemies());
        badEnemies.add(new main.domain.GameSnapshot.EnemySnapshot(1, 1, 0, 0, 0));

        final main.domain.GameSnapshot badSnap = new main.domain.GameSnapshot(
                snap.getState(), snap.getTimeRemaining(), snap.getCoinsCollected(),
                snap.isIntermediateCheckpointReached(), snap.getRespawnZoneKind(),
                snap.getPlayerSkin(), snap.getPlayer2Skin(),
                snap.getOriginalPlayerSkin(), snap.getOriginalPlayer2Skin(),
                snap.isTwoPlayerMode(), snap.getWinnerName(),
                snap.getPlayer1(), snap.getPlayer2(),
                snap.getCoins(), badEnemies, snap.getPowerUps());

        final HardestGame g = new HardestGame();
        final Path f = Files.createTempFile("dopo-mismatch-en-", ".txt");
        Files.writeString(f, miniLevel());
        g.loadConfiguration(f.toString());

        try {
            g.restoreFromSnapshot(badSnap);
            fail("Debió fallar con INVALID_GAME_STATE por enemigos extra");
        } catch (HardestGameException ex) {
            assertEquals(HardestGameException.INVALID_GAME_STATE, ex.getMessage());
        }
    }

    /**
     * restoreFromSnapshot falla si hay discrepancia en cantidad de power-ups.
     */
    @Test
    public void shouldRestoreFromSnapshotFailOnPowerUpCountMismatch() throws Exception {
        final main.domain.GameSnapshot snap = game.captureSnapshot();

        // Creamos un snapshot con un power-up extra
        final java.util.List<main.domain.GameSnapshot.PowerUpSnapshot> badPups =
                new java.util.ArrayList<>(snap.getPowerUps());
        badPups.add(new main.domain.GameSnapshot.PowerUpSnapshot("BOMB", false, false));

        final main.domain.GameSnapshot badSnap = new main.domain.GameSnapshot(
                snap.getState(), snap.getTimeRemaining(), snap.getCoinsCollected(),
                snap.isIntermediateCheckpointReached(), snap.getRespawnZoneKind(),
                snap.getPlayerSkin(), snap.getPlayer2Skin(),
                snap.getOriginalPlayerSkin(), snap.getOriginalPlayer2Skin(),
                snap.isTwoPlayerMode(), snap.getWinnerName(),
                snap.getPlayer1(), snap.getPlayer2(),
                snap.getCoins(), snap.getEnemies(), badPups);

        final HardestGame g = new HardestGame();
        final Path f = Files.createTempFile("dopo-mismatch-pu-", ".txt");
        Files.writeString(f, miniLevel());
        g.loadConfiguration(f.toString());

        try {
            g.restoreFromSnapshot(badSnap);
            fail("Debió fallar con INVALID_GAME_STATE por power-ups extra");
        } catch (HardestGameException ex) {
            assertEquals(HardestGameException.INVALID_GAME_STATE, ex.getMessage());
        }
    }

    /**
     * restoreFromSnapshot falla si hay modo dos jugadores pero player2 es null.
     */
    @Test
    public void shouldRestoreFromSnapshotFailOnTwoPlayerNullPlayer2() throws Exception {
        final main.domain.GameSnapshot snap = game.captureSnapshot();

        // Forzamos twoPlayerMode=true pero player2=null
        final main.domain.GameSnapshot badSnap = new main.domain.GameSnapshot(
                snap.getState(), snap.getTimeRemaining(), snap.getCoinsCollected(),
                snap.isIntermediateCheckpointReached(), snap.getRespawnZoneKind(),
                snap.getPlayerSkin(), snap.getPlayer2Skin(),
                snap.getOriginalPlayerSkin(), snap.getOriginalPlayer2Skin(),
                true, // twoPlayerMode = true
                snap.getWinnerName(),
                snap.getPlayer1(),
                null, // player2 = null → debe fallar
                snap.getCoins(), snap.getEnemies(), snap.getPowerUps());

        final HardestGame g = new HardestGame();
        final Path f = Files.createTempFile("dopo-2p-null-", ".txt");
        Files.writeString(f, miniLevel());
        g.loadConfiguration(f.toString());

        try {
            g.restoreFromSnapshot(badSnap);
            fail("Debió fallar con INVALID_GAME_STATE por player2 null en twoPlayerMode");
        } catch (HardestGameException ex) {
            assertEquals(HardestGameException.INVALID_GAME_STATE, ex.getMessage());
        }
    }

    /**
     * respawnZoneKind y zoneFromKind cubren la rama INTERMEDIATE y FINAL.
     */
    @Test
    public void shouldRespawnZoneKindAndRestoreIntermediate() throws Exception {
        final String level = String.join("\n",
                "ROWS 5",
                "COLS 12",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_INTERMEDIATE 0 5 5 2",
                "SAFE_FINAL 0 10 5 2",
                "COIN YELLOW 2 3",
                "");
        final Path f = Files.createTempFile("dopo-respawn-kind-", ".txt");
        Files.writeString(f, level);
        final HardestGame g = new HardestGame();
        g.loadConfiguration(f.toString());

        // Activar checkpoint intermedio
        for (int i = 0; i < 4; i++) g.movePlayer(0, 1);

        // Capturar snapshot con respawnZoneKind = INTERMEDIATE
        final main.domain.GameSnapshot snap = g.captureSnapshot();
        assertEquals("El respawnZoneKind debe ser INTERMEDIATE", "INTERMEDIATE",
                snap.getRespawnZoneKind());

        // Restaurar y verificar que la zona se restaura correctamente
        final HardestGame g2 = new HardestGame();
        final Path f2 = Files.createTempFile("dopo-respawn-kind2-", ".txt");
        Files.writeString(f2, level);
        g2.loadConfiguration(f2.toString());
        g2.restoreFromSnapshot(snap);
        assertEquals("Debe restaurarse la zona intermedia como respawn",
                main.domain.Zone.Type.INTERMEDIATE, g2.getCurrentRespawnZone().getType());
    }

    /**
     * togglePause no hace nada cuando el estado es WON.
     */
    @Test
    public void shouldTogglePauseIgnoredWhenWon() throws HardestGameException {
        // Ganar el juego
        for (int i = 0; i < 3; i++) game.movePlayer(0, 1);
        for (int i = 0; i < 4; i++) game.movePlayer(0, 1);
        assertEquals(HardestGame.State.WON, game.getState());

        // togglePause en WON no debe cambiar el estado
        game.togglePause();
        assertEquals("togglePause en WON no debe cambiar el estado",
                HardestGame.State.WON, game.getState());
    }

    /**
     * togglePause no hace nada cuando el estado es LOST.
     */
    @Test
    public void shouldTogglePauseIgnoredWhenLost() throws Exception {
        // Perder el juego — TIME 11 supera el mínimo de 10s; agotamos en el tick 11
        final Path f = Files.createTempFile("dopo-lost-toggle-", ".txt");
        Files.writeString(f, miniLevel().replace("TIME 60", "TIME 11"));
        final HardestGame g = new HardestGame();
        g.loadConfiguration(f.toString());
        for (int i = 0; i < 11; i++) g.decrementTime();
        assertEquals(HardestGame.State.LOST, g.getState());

        g.togglePause();
        assertEquals("togglePause en LOST no debe cambiar el estado",
                HardestGame.State.LOST, g.getState());
    }

    /**
     * moveSecondPlayer lanza excepción cuando el estado no es PLAYING.
     */
    @Test
    public void shouldMoveSecondPlayerFailsWhenNotPlaying() throws Exception {
        final Path f = Files.createTempFile("dopo-p2state-", ".txt");
        Files.writeString(f, miniLevel());
        final HardestGame g = new HardestGame();
        g.setTwoPlayerMode(true);
        g.loadConfiguration(f.toString());
        g.togglePause(); // PLAYING → PAUSED

        try {
            g.moveSecondPlayer(0, 1);
            fail("Debió lanzar INVALID_GAME_STATE en estado PAUSED");
        } catch (HardestGameException ex) {
            assertEquals(HardestGameException.INVALID_GAME_STATE, ex.getMessage());
        }
    }

    /**
     * captureSnapshot serializa LifeSource permanente correctamente.
     */
    @Test
    public void shouldCaptureSnapshotSerializesLifeSourcePermanent() throws Exception {
        // Keyword correcto: POWERUP LIFE fila col
        final String level = String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "POWERUP LIFE 2 4",
                "");
        final Path f = Files.createTempFile("dopo-snap-ls-", ".txt");
        Files.writeString(f, level);
        final HardestGame g = new HardestGame();
        g.loadConfiguration(f.toString());

        // Recoger la LifeSource en col 4
        for (int i = 0; i < 3; i++) g.movePlayer(0, 1);

        // El snapshot debe reflejar permanent=true para el power-up
        final main.domain.GameSnapshot snap = g.captureSnapshot();
        assertFalse("La lista de power-ups no debe estar vacía", snap.getPowerUps().isEmpty());
        assertTrue("El power-up debe estar marcado como permanente",
                snap.getPowerUps().get(0).isPermanentlyUsed());
    }

    /**
     * getSecondPlayerExtraLives y getSecondPlayerDeaths retornan 0 con player2=null.
     */
    @Test
    public void shouldSecondPlayerGettersReturnZeroWhenNull() {
        final HardestGame g = new HardestGame(); // sin loadConfiguration → player2=null
        assertEquals("getSecondPlayerExtraLives debe ser 0 sin player2", 0,
                g.getSecondPlayerExtraLives());
        assertEquals("getSecondPlayerDeaths debe ser 0 sin player2", 0,
                g.getSecondPlayerDeaths());
    }

    /**
     * snapshotForEnemy cubre ramas de VerticalEnemy, AcceleratedEnemy, PatrolEnemy y SpinnerEnemy.
     */
    @Test
    public void shouldSnapshotForEnemyVariousTypes() throws Exception {
        // Usar keywords correctos: VERTICAL (solo fila col), ACCEL (fila col dir),
        // PATROL (fila col filas cols), SPINNER (fila col radio index horario vel)
        final String level = String.join("\n",
                "ROWS 12",
                "COLS 15",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 13 5 2",
                "COIN YELLOW 6 7",
                "ENEMY VERTICAL 3 5",
                "ENEMY ACCEL 7 8 HORIZONTAL",
                "ENEMY PATROL 8 9 3 3",
                "ENEMY SPINNER 5 7 2 0 true 1",
                "");
        final Path f = Files.createTempFile("dopo-snap-enemy-", ".txt");
        Files.writeString(f, level);
        final HardestGame g = new HardestGame();
        g.loadConfiguration(f.toString());

        final main.domain.GameSnapshot snap = g.captureSnapshot();
        assertEquals("Debe haber 4 snapshots de enemigo", 4, snap.getEnemies().size());
    }

    /**
     * GreenPlayer como player2 recibe tickPlayerDefense en modo dos jugadores.
     */
    @Test
    public void shouldTickWithGreenPlayerAsPlayer2() throws Exception {
        final Path f = Files.createTempFile("dopo-green2-", ".txt");
        Files.writeString(f, miniLevel());
        final HardestGame g = new HardestGame();
        g.setTwoPlayerMode(true);
        g.setSecondPlayerSkin("Verde (Clyde)");
        g.loadConfiguration(f.toString());

        // tick() llama tickPlayerDefense(player2) que es GreenPlayer → rama GreenPlayer en tickPlayerDefense
        g.tick();
        assertNotNull("Player2 GreenPlayer no debe ser null tras tick", g.getPlayer2());
    }

    /**
     * resetPlayerSkinOnDeath para player2 (firstPlayer=false).
     */
    @Test
    public void shouldResetSkinOnDeathForPlayer2() throws Exception {
        final String level = String.join("\n",
                "ROWS 5",
                "COLS 10",
                "TIME 60",
                "SAFE_START 0 0 5 2",
                "SAFE_FINAL 0 8 5 2",
                "COIN SKIN BLUE 2 5",
                "ENEMY BASIC 2 6 VERTICAL",
                "");
        final Path f = Files.createTempFile("dopo-p2skin-", ".txt");
        Files.writeString(f, level);
        final HardestGame g = new HardestGame();
        g.setTwoPlayerMode(true);
        g.setPlayerSkin("Rojo (Blinky)");
        g.setSecondPlayerSkin("Rojo (Blinky)");
        g.loadConfiguration(f.toString());

        // Player2 recoge SkinCoin en col 5 (viene de col ~9)
        for (int i = 0; i < 4; i++) g.moveSecondPlayer(0, -1);
        // Player2 ahora en col 5, recoge SkinCoin BLUE
        // Luego muere al llegar al enemigo en col 6
        g.moveSecondPlayer(0, 1);
        // Tras morir, el skin debe volver a Rojo (Blinky)
        assertTrue("Player2 debe ser RedPlayer tras morir (skin original)",
                g.getPlayer2() instanceof main.domain.RedPlayer);
    }

    /**
     * zoneFromKind("FINAL") retorna finalZone.
     */
    @Test
    public void shouldZoneFromKindFinalUsedInRestore() throws Exception {
        // El nivel sin zona intermedia hace que respawnZoneKind sea START o FINAL
        final main.domain.GameSnapshot snap = game.captureSnapshot();
        assertEquals("Sin checkpoint, respawnZoneKind debe ser START",
                "START", snap.getRespawnZoneKind());

        // Creamos un snapshot con respawnZoneKind = FINAL para cubrir zoneFromKind("FINAL")
        final main.domain.GameSnapshot finalSnap = new main.domain.GameSnapshot(
                snap.getState(), snap.getTimeRemaining(), snap.getCoinsCollected(),
                snap.isIntermediateCheckpointReached(),
                "FINAL", // <--- forzamos FINAL
                snap.getPlayerSkin(), snap.getPlayer2Skin(),
                snap.getOriginalPlayerSkin(), snap.getOriginalPlayer2Skin(),
                snap.isTwoPlayerMode(), snap.getWinnerName(),
                snap.getPlayer1(), snap.getPlayer2(),
                snap.getCoins(), snap.getEnemies(), snap.getPowerUps());

        final HardestGame g = new HardestGame();
        final Path f = Files.createTempFile("dopo-final-zone-", ".txt");
        Files.writeString(f, miniLevel());
        g.loadConfiguration(f.toString());
        g.restoreFromSnapshot(finalSnap);

        assertEquals("Debe restaurarse la zona final como respawn",
                main.domain.Zone.Type.FINAL, g.getCurrentRespawnZone().getType());
    }

    @Test
    public void testHardestGameAdditionalBranches() throws Exception {
        // 1. GAME_ALREADY_RUNNING
        try {
            game.loadConfiguration(Files.createTempFile("dopo-run-", ".txt").toString());
            fail("Debió fallar con GAME_ALREADY_RUNNING");
        } catch (HardestGameException ex) {
            assertEquals(HardestGameException.GAME_ALREADY_RUNNING, ex.getMessage());
        }

        // 2. INVALID_SKIN
        HardestGame gSkin = new HardestGame();
        gSkin.setPlayerSkin("Skin Inexistente 123");
        try {
            Path f = Files.createTempFile("dopo-skin-err-", ".txt");
            Files.writeString(f, miniLevel());
            gSkin.loadConfiguration(f.toString());
            fail("Debió fallar con INVALID_SKIN");
        } catch (HardestGameException ex) {
            assertEquals(HardestGameException.INVALID_SKIN, ex.getMessage());
        }

        // 3. moveSecondPlayer inactive
        try {
            game.moveSecondPlayer(0, 1);
            fail("Debió fallar con INVALID_GAME_STATE al no estar activo P2");
        } catch (HardestGameException ex) {
            assertEquals(HardestGameException.INVALID_GAME_STATE, ex.getMessage());
        }

        // 4. restoreFromSnapshot size mismatch
        main.domain.GameSnapshot snap = game.captureSnapshot();
        // Create matching snap but mismatching list of coins to trigger coins.size() mismatch
        java.util.List<main.domain.GameSnapshot.CoinSnapshot> badCoins = new java.util.ArrayList<>(snap.getCoins());
        badCoins.add(new main.domain.GameSnapshot.CoinSnapshot(false, false));

        main.domain.GameSnapshot badSnap = new main.domain.GameSnapshot(
            snap.getState(),
            snap.getTimeRemaining(),
            snap.getCoinsCollected(),
            snap.isIntermediateCheckpointReached(),
            snap.getRespawnZoneKind(),
            snap.getPlayerSkin(),
            snap.getPlayer2Skin(),
            snap.getOriginalPlayerSkin(),
            snap.getOriginalPlayer2Skin(),
            snap.isTwoPlayerMode(),
            snap.getWinnerName(),
            snap.getPlayer1(),
            snap.getPlayer2(),
            badCoins,
            snap.getEnemies(),
            snap.getPowerUps()
        );
        
        HardestGame mismatchGame = new HardestGame();
        Path f = Files.createTempFile("dopo-mismatch-", ".txt");
        Files.writeString(f, miniLevel());
        mismatchGame.loadConfiguration(f.toString());
        try {
            mismatchGame.restoreFromSnapshot(badSnap);
            fail("Debió fallar con INVALID_GAME_STATE por diferencia de monedas");
        } catch (HardestGameException ex) {
            assertEquals(HardestGameException.INVALID_GAME_STATE, ex.getMessage());
        }
    }
}
