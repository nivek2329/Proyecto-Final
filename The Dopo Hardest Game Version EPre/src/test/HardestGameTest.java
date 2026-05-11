package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import domain.HardestGame;
import domain.HardestGameException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clase de pruebas unitarias para la clase HardestGame.
 * Verifica el comportamiento completo de la lógica principal del juego,
 * incluyendo la carga de niveles, el movimiento del jugador, la interacción
 * con monedas y enemigos, el manejo del tiempo, la pausa del juego,
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
    public void shouldCargaYPuedeMoverJugador() {
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
    public void shouldRecogeMoneda() {
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
    public void shouldColisionEnemigoReseteaMonedasYAumentaMuertes() throws Exception {
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
        Files.writeString(f, miniLevel().replace("TIME 60", "TIME 1"));

        HardestGame g = new HardestGame();
        g.loadConfiguration(f.toString());
        g.decrementTime();

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
    public void shouldVictoriaConTodasLasMonedasYZonaFinal() {
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
    @Test
    public void shouldNotMoveWhenPaused() {
        int row0 = game.getPlayer().getRow();
        game.togglePause();
        game.movePlayer(1, 0);
        assertEquals(
                "El jugador no debe moverse mientras el juego esta pausado",
                row0,
                game.getPlayer().getRow()
        );
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
}