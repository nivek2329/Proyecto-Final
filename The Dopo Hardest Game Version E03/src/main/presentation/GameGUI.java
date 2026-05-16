package main.presentation;

import main.domain.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;

/**
 * Ventana principal que controla la interfaz gráfica completa del juego.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public final class GameGUI extends JFrame {

    private static final Logger LOGGER = GameLog.getLogger(GameGUI.class);
    private static final long serialVersionUID = 1L;

    private static final int  TIMER_GAME_MS      = 50;
    private static final int  TIMER_COUNTDOWN_MS = 1000;

    private static final Color COLOR_STATUS_FG = Color.WHITE;
    private static final Color COLOR_STATUS_BG = Color.BLACK;
    private static final Color COLOR_BTN_PANEL = new Color(30, 30, 30);
    private static final Color COLOR_BTN_BG    = new Color(50, 50, 50);

    private HardestGame game;
    private BoardPanel boardPanel;
    private JLabel       statusLabel;
    private Timer        gameTimer;
    private Timer        countdownTimer;
    private GameSetup currentSetup;

    private final Set<Integer> pressedKeys = new HashSet<>();

    private int playerTick;
    private int player2Tick;
    private boolean machineMode;
    private boolean machineExpert;
    private final List<int[]> machineTargets = new ArrayList<>();
    private final Random random = new Random();

    private String lastConfig = "configs/level1.txt";

    /**
     * Crea y muestra la ventana principal del juego con el menú inicial.
     */
    public GameGUI() {
        game = new HardestGame();
        buildFrame();
        setupTimers();
        showMenu();
    }

    /**
     * Configura las propiedades básicas de la ventana.
     */
    private void buildFrame() {
        setTitle("The DOPO Hardest Game — Entrega final");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
    }

    /**
     * Muestra la pantalla de menú principal.
     */
    private void showMenu() {
        getContentPane().removeAll();
        removeKeyListeners();
        add(new MenuScreen(this::startGame, this::loadSavedGame), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    /**
     * Inicia una nueva partida con la configuración seleccionada.
     *
     * @param setup configuración de la partida elegida en el menú
     */
    private void startGame(final GameSetup setup) {
        beginGameSession(setup, null);
    }

    /**
     * Carga una partida guardada desde el menú principal.
     */
    private void loadSavedGame() {
        final java.nio.file.Path path = GameSaveDialog.showOpenDialog(this);
        if (path == null) {
            return;
        }
        try {
            final GameSaveIO.SavedGameData data = GameSaveIO.load(path);
            beginGameSession(data.getSetup(), data.getSnapshot());
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Cargar partida",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Guarda la partida actual en disco.
     */
    private void saveCurrentGame() {
        if (currentSetup == null || boardPanel == null) {
            JOptionPane.showMessageDialog(this,
                    "Inicia una partida antes de guardar.",
                    "Guardar partida",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (game.getState() == HardestGame.State.WON
                || game.getState() == HardestGame.State.LOST) {
            JOptionPane.showMessageDialog(this,
                    "La partida ya terminó. Reinicia el nivel o vuelve al menú.",
                    "Guardar partida",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        final java.nio.file.Path path = GameSaveDialog.showSaveDialog(this);
        if (path == null) {
            return;
        }
        if (java.nio.file.Files.exists(path)) {
            final int choice = JOptionPane.showConfirmDialog(this,
                    "El archivo ya existe. ¿Deseas reemplazarlo?",
                    "Guardar partida",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {
            GameSaveIO.save(game, currentSetup, path);
            JOptionPane.showMessageDialog(this,
                    "Partida guardada en:\n" + path.toAbsolutePath(),
                    "Guardar partida",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar: " + ex.getMessage(),
                    "Guardar partida",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inicia la sesión de juego con la configuración indicada y, opcionalmente,
     * restaura un guardado previo.
     */
    private void beginGameSession(final GameSetup setup, final GameSnapshot snapshot) {
        this.currentSetup = setup;
        playerTick = 0;
        player2Tick = 0;
        getContentPane().removeAll();
        statusLabel = null;
        boardPanel = null;

        statusLabel = new JLabel("  Muertes: 0 | Monedas: 0/0 | Vidas+: 0 | Tiempo: 0s");
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        statusLabel.setForeground(COLOR_STATUS_FG);
        statusLabel.setBackground(COLOR_STATUS_BG);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        add(statusLabel, BorderLayout.NORTH);

        final JPanel btnPanel = new JPanel();
        btnPanel.setBackground(COLOR_BTN_PANEL);
        addBtn(btnPanel, "Pausar [P]",       e -> togglePauseAndRepaint());
        addBtn(btnPanel, "Guardar partida",  e -> saveCurrentGame());
        addBtn(btnPanel, "Reportar [E]",     e -> ErrorReportDialog.show(this, lastConfig));
        addBtn(btnPanel, "Reiniciar [R]",    e -> loadLevel(lastConfig));
        addBtn(btnPanel, "Menu",             e -> returnToMenu());
        addBtn(btnPanel, "Salir",            e -> System.exit(0));
        add(btnPanel, BorderLayout.SOUTH);

        removeKeyListeners();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    togglePauseAndRepaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_E) {
                    ErrorReportDialog.show(GameGUI.this, lastConfig);
                }
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    loadLevel(lastConfig);
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });
        setFocusable(true);

        game.setPlayerSkin(setup.getSkin1());
        game.setSecondPlayerSkin(setup.getSkin2());
        machineMode = "Jugador vs máquina".equals(setup.getModalidad());
        machineExpert = machineMode && "Máquina experta".equals(setup.getMaquina());
        game.setTwoPlayerMode(machineMode || "Jugador vs jugador".equals(setup.getModalidad()));
        machineTargets.clear();

        if ("Jugador vs jugador".equals(setup.getModalidad())
                && setup.getSkin1().equals(setup.getSkin2())) {
            handleGameError("inicio de partida",
                    new InvalidMultiplayerConfigException(
                            "En PvP cada jugador debe elegir un skin distinto."));
            return;
        }

        loadLevel(setup.getLevelPath(), snapshot);
    }

    /**
     * Carga un nivel desde el archivo de configuración indicado.
     *
     * @param path ruta del archivo de configuración del nivel
     */
    private void loadLevel(final String path) {
        loadLevel(path, null);
    }

    /**
     * Carga un nivel y, si se indica, restaura una partida guardada.
     *
     * @param path     ruta del archivo de configuración del nivel
     * @param snapshot instantánea opcional a restaurar
     */
    private void loadLevel(final String path, final GameSnapshot snapshot) {
        try {
            game.prepareForLoad();
            if (currentSetup != null) {
                game.setPlayerSkin(currentSetup.getSkin1());
                game.setSecondPlayerSkin(currentSetup.getSkin2());
            }
            game.loadConfiguration(path);
            if (snapshot != null) {
                game.restoreFromSnapshot(snapshot);
            }
            lastConfig = path;
            playerTick = 0;
            player2Tick = 0;
            machineTargets.clear();

            if (boardPanel != null) {
                remove(boardPanel);
            }

            final Color border = (currentSetup != null)
                    ? currentSetup.getBorder1()
                    : Color.BLACK;
            final Color border2 = (currentSetup != null)
                    ? currentSetup.getBorder2()
                    : Color.BLACK;

            boardPanel = new BoardPanel(game, border, border2);
            add(boardPanel, BorderLayout.CENTER);

            pack();
            setLocationRelativeTo(null);
            updateStatus();
            revalidate();
            repaint();
            requestFocusInWindow();

        } catch (HardestGameException ex) {
            handleGameError(snapshot != null ? "carga de partida guardada" : "carga de nivel: " + path, ex);
        } catch (Exception ex) {
            handleGameError(snapshot != null ? "carga de partida guardada" : "carga de nivel: " + path, ex);
        }
    }

    /**
     * Registra el error, informa al usuario y deja el juego en estado consistente.
     */
    private void handleGameError(final String operation, final Exception ex) {
        LOGGER.log(Level.SEVERE, "Error en " + operation + ": " + ex.getMessage(), ex);
        if (ex instanceof HardestGameException) {
            GameLogger.logGameException(operation, (HardestGameException) ex);
        } else {
            GameLogger.logError("Error en " + operation, ex);
        }
        final String userMsg = ex instanceof HardestGameException
                ? ex.getMessage()
                : "Ocurrió un error inesperado. Revisa logs/game_errors.log";
        JOptionPane.showMessageDialog(this, userMsg, "Error", JOptionPane.ERROR_MESSAGE);
        game.prepareForLoad();
    }

    /**
     * Configura los timers de juego y cuenta regresiva.
     */
    private void setupTimers() {
        gameTimer = new Timer(TIMER_GAME_MS, e -> gameTick());
        gameTimer.start();

        countdownTimer = new Timer(TIMER_COUNTDOWN_MS, e -> {
            if (game.getState() == HardestGame.State.PLAYING) {
                game.decrementTime();
                updateStatus();
                if (boardPanel != null) { boardPanel.repaint(); }
            }
        });
        countdownTimer.start();
    }

    /**
     * Ejecuta un tick de lógica de juego: procesa entrada, movimiento y actualización.
     */
    private void gameTick() {
        if (boardPanel == null || game.getState() != HardestGame.State.PLAYING) {
            return;
        }

        playerTick++;
        final int moveTicks = game.getPlayer().getMoveTicks();

        if (playerTick >= moveTicks) {
            playerTick = 0;
            int dr = 0;
            int dc = 0;

            if (pressedKeys.contains(KeyEvent.VK_UP)) {
                dr = -1;
            } else if (pressedKeys.contains(KeyEvent.VK_DOWN)) {
                dr = 1;
            }
            if (pressedKeys.contains(KeyEvent.VK_LEFT)) {
                dc = -1;
            } else if (pressedKeys.contains(KeyEvent.VK_RIGHT)) {
                dc = 1;
            }

            if (!game.isTwoPlayerMode()) {
                if (pressedKeys.contains(KeyEvent.VK_W)) {
                    dr = -1;
                } else if (pressedKeys.contains(KeyEvent.VK_S)) {
                    dr = 1;
                }
                if (pressedKeys.contains(KeyEvent.VK_A)) {
                    dc = -1;
                } else if (pressedKeys.contains(KeyEvent.VK_D)) {
                    dc = 1;
                }
            }

            if (dr != 0 || dc != 0) {
                try {
                    game.movePlayer(dr, dc);
                } catch (HardestGameException ex) {
                    LOGGER.log(Level.SEVERE, "movePlayer: " + ex.getMessage(), ex);
                }
            }
        }

        if (game.isTwoPlayerMode() && game.getPlayer2() != null) {
            player2Tick++;
            final int moveTicks2 = game.getPlayer2().getMoveTicks();
            if (player2Tick >= moveTicks2) {
                player2Tick = 0;
                int dr2 = 0;
                int dc2 = 0;
                if (machineMode) {
                    final int[] machineMove = computeMachineMove();
                    dr2 = machineMove[0];
                    dc2 = machineMove[1];
                } else {
                    if (pressedKeys.contains(KeyEvent.VK_W)) {
                        dr2 = -1;
                    } else if (pressedKeys.contains(KeyEvent.VK_S)) {
                        dr2 = 1;
                    }
                    if (pressedKeys.contains(KeyEvent.VK_A)) {
                        dc2 = -1;
                    } else if (pressedKeys.contains(KeyEvent.VK_D)) {
                        dc2 = 1;
                    }
                }
                if (dr2 != 0 || dc2 != 0) {
                    try {
                        game.moveSecondPlayer(dr2, dc2);
                    } catch (HardestGameException ex) {
                        LOGGER.log(Level.SEVERE, "moveSecondPlayer: " + ex.getMessage(), ex);
                    }
                }
            }
        }

        game.tick();
        updateStatus();
        boardPanel.repaint();
    }

    /**
     * Alterna el estado de pausa del juego y repinta el tablero.
     */
    private void togglePauseAndRepaint() {
        game.togglePause();
        if (boardPanel != null) { boardPanel.repaint(); }
    }

    /**
     * Regresa al menú principal reiniciando el estado del juego.
     */
    private void returnToMenu() {
        game         = new HardestGame();
        currentSetup = null;
        machineMode = false;
        machineExpert = false;
        machineTargets.clear();
        showMenu();
    }

    /**
     * Elimina todos los listeners de teclado registrados.
     */
    private void removeKeyListeners() {
        for (final KeyListener kl : getKeyListeners()) {
            removeKeyListener(kl);
        }
    }

    /**
     * Crea y añade un botón al panel indicado.
     *
     * @param panel    panel donde se añadirá el botón
     * @param text     texto del botón
     * @param listener acción a ejecutar al presionar el botón
     */
    private void addBtn(final JPanel panel, final String text,
                        final ActionListener listener) {
        final JButton btn = new JButton(text);
        btn.setBackground(COLOR_BTN_BG);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(listener);
        panel.add(btn);
    }

    /**
     * Actualiza el texto de la barra de estado con la información actual del juego.
     */
    private void updateStatus() {
        if (statusLabel == null) { return; }
        if (game.isTwoPlayerMode()) {
            statusLabel.setText(String.format(
                    "  J1 M:%d V+:%d  |  J2 M:%d V+:%d  |  Monedas: %d/%d  |  Tiempo: %ds",
                    game.getDeaths(),
                    game.getExtraLives(),
                    game.getSecondPlayerDeaths(),
                    game.getSecondPlayerExtraLives(),
                    game.getCoinsCollected(),
                    game.getTotalCoins(),
                    game.getTimeRemaining()));
        } else {
            statusLabel.setText(String.format(
                    "  Muertes: %d  |  Monedas: %d/%d  |  Vidas+: %d  |  Tiempo: %ds",
                    game.getDeaths(),
                    game.getCoinsCollected(),
                    game.getTotalCoins(),
                    game.getExtraLives(),
                    game.getTimeRemaining()));
        }
    }

    /**
     * Calcula el siguiente movimiento de la máquina hacia el objetivo actual.
     *
     * @return arreglo con desplazamiento en filas y columnas
     */
    private int[] computeMachineMove() {
        final Player bot = game.getPlayer2();
        if (bot == null) {
            return new int[]{0, 0};
        }
        if (!hasPendingCoinTarget() && hasRemainingCoins()) {
            machineTargets.clear();
        }
        if (machineTargets.isEmpty()) {
            buildMachineTargets();
        }
        while (!machineTargets.isEmpty()) {
            final int[] target = machineTargets.get(0);
            if (bot.getRow() == target[0] && bot.getCol() == target[1]) {
                machineTargets.remove(0);
                continue;
            }
            if (isCollectedCoinCell(target[0], target[1])) {
                machineTargets.remove(0);
                continue;
            }
            final int[] step = findNextStep(bot.getRow(), bot.getCol(), target[0], target[1], true);
            if (step[0] == 0 && step[1] == 0) {
                final int[] fallback = findNextStep(bot.getRow(), bot.getCol(), target[0], target[1], false);
                if (fallback[0] == 0 && fallback[1] == 0) {
                    machineTargets.remove(0);
                    continue;
                }
                return fallback;
            }
            return step;
        }
        return new int[]{0, 0};
    }

    /**
     * Construye la lista de objetivos de la máquina: monedas restantes y zona final.
     */
    private void buildMachineTargets() {
        final List<int[]> remainingCoins = new ArrayList<>();
        for (final Coin coin : game.getCoins()) {
            if (!coin.isCollected()) {
                remainingCoins.add(new int[]{coin.getRow(), coin.getCol()});
            }
        }
        if (machineExpert) {
            buildExpertTargets(remainingCoins);
        } else {
            Collections.shuffle(remainingCoins, random);
            machineTargets.addAll(remainingCoins);
        }
        final Zone goal = game.getStartZone();
        machineTargets.add(new int[]{goal.getRow() + goal.getRows() / 2,
                goal.getCol() + goal.getCols() / 2});
    }

    /**
     * Ordena las monedas restantes por distancia óptima para el modo experto.
     *
     * @param coins lista de coordenadas de monedas pendientes
     */
    private void buildExpertTargets(final List<int[]> coins) {
        int currentRow = game.getPlayer2().getRow();
        int currentCol = game.getPlayer2().getCol();
        final List<int[]> pending = new ArrayList<>(coins);
        while (!pending.isEmpty()) {
            int bestIdx = -1;
            int bestDist = Integer.MAX_VALUE;
            for (int i = 0; i < pending.size(); i++) {
                final int[] c = pending.get(i);
                final int dist = shortestDistance(currentRow, currentCol, c[0], c[1], true);
                if (dist >= 0 && dist < bestDist) {
                    bestDist = dist;
                    bestIdx = i;
                }
            }
            if (bestIdx < 0) {
                machineTargets.addAll(pending);
                break;
            }
            final int[] next = pending.remove(bestIdx);
            machineTargets.add(next);
            currentRow = next[0];
            currentCol = next[1];
        }
    }

    /**
     * Calcula la distancia más corta entre dos celdas usando BFS.
     *
     * @param startRow    fila de inicio
     * @param startCol    columna de inicio
     * @param goalRow     fila destino
     * @param goalCol     columna destino
     * @param avoidDanger true para evitar celdas cercanas a enemigos
     * @return distancia en pasos, o -1 si no hay camino
     */
    private int shortestDistance(final int startRow, final int startCol,
                                 final int goalRow, final int goalCol,
                                 final boolean avoidDanger) {
        final Board board = game.getBoard();
        final int rows = board.getRows();
        final int cols = board.getCols();
        final int[][] dist = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                dist[r][c] = -1;
            }
        }
        final Deque<int[]> q = new ArrayDeque<>();
        q.add(new int[]{startRow, startCol});
        dist[startRow][startCol] = 0;
        while (!q.isEmpty()) {
            final int[] cur = q.removeFirst();
            if (cur[0] == goalRow && cur[1] == goalCol) {
                return dist[cur[0]][cur[1]];
            }
            for (final int[] d : directions()) {
                final int nr = cur[0] + d[0];
                final int nc = cur[1] + d[1];
                if (!board.isValidPosition(nr, nc) || dist[nr][nc] >= 0) {
                    continue;
                }
                if (avoidDanger && isDangerCell(nr, nc)) {
                    continue;
                }
                dist[nr][nc] = dist[cur[0]][cur[1]] + 1;
                q.addLast(new int[]{nr, nc});
            }
        }
        return -1;
    }

    /**
     * Encuentra el siguiente paso hacia el objetivo usando BFS con backtracking.
     *
     * @param startRow    fila de inicio
     * @param startCol    columna de inicio
     * @param goalRow     fila destino
     * @param goalCol     columna destino
     * @param avoidDanger true para evitar celdas cercanas a enemigos
     * @return arreglo con desplazamiento en filas y columnas para el siguiente paso
     */
    private int[] findNextStep(final int startRow, final int startCol,
                               final int goalRow, final int goalCol,
                               final boolean avoidDanger) {
        final Board board = game.getBoard();
        final int rows = board.getRows();
        final int cols = board.getCols();
        final boolean[][] visited = new boolean[rows][cols];
        final int[][] prevR = new int[rows][cols];
        final int[][] prevC = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                prevR[r][c] = -1;
                prevC[r][c] = -1;
            }
        }
        final Deque<int[]> q = new ArrayDeque<>();
        q.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        boolean found = false;
        while (!q.isEmpty()) {
            final int[] cur = q.removeFirst();
            if (cur[0] == goalRow && cur[1] == goalCol) {
                found = true;
                break;
            }
            for (final int[] d : directions()) {
                final int nr = cur[0] + d[0];
                final int nc = cur[1] + d[1];
                if (!board.isValidPosition(nr, nc) || visited[nr][nc]) {
                    continue;
                }
                if (avoidDanger && isDangerCell(nr, nc) && !(nr == goalRow && nc == goalCol)) {
                    continue;
                }
                visited[nr][nc] = true;
                prevR[nr][nc] = cur[0];
                prevC[nr][nc] = cur[1];
                q.addLast(new int[]{nr, nc});
            }
        }
        if (!found) {
            return new int[]{0, 0};
        }
        int tr = goalRow;
        int tc = goalCol;
        while (prevR[tr][tc] != startRow || prevC[tr][tc] != startCol) {
            final int pr = prevR[tr][tc];
            final int pc = prevC[tr][tc];
            if (pr < 0 || pc < 0) {
                return new int[]{0, 0};
            }
            tr = pr;
            tc = pc;
        }
        return new int[]{tr - startRow, tc - startCol};
    }

    /**
     * Indica si la celda indicada contiene una moneda ya recolectada.
     *
     * @param row fila a evaluar
     * @param col columna a evaluar
     * @return true si la moneda en esa celda está recolectada
     */
    private boolean isCollectedCoinCell(final int row, final int col) {
        for (final Coin coin : game.getCoins()) {
            if (coin.getRow() == row && coin.getCol() == col) {
                return coin.isCollected();
            }
        }
        return false;
    }

    /**
     * Indica si quedan monedas sin recolectar.
     *
     * @return true si hay monedas pendientes
     */
    private boolean hasRemainingCoins() {
        for (final Coin coin : game.getCoins()) {
            if (!coin.isCollected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indica si algún objetivo pendiente corresponde a una moneda no recolectada.
     *
     * @return true si hay al menos un objetivo con moneda válida
     */
    private boolean hasPendingCoinTarget() {
        for (final int[] target : machineTargets) {
            for (final Coin coin : game.getCoins()) {
                if (!coin.isCollected() && coin.getRow() == target[0] && coin.getCol() == target[1]) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Indica si la celda indicada está en peligro por proximidad a enemigos.
     *
     * @param row fila a evaluar
     * @param col columna a evaluar
     * @return true si hay un enemigo adyacente
     */
    private boolean isDangerCell(final int row, final int col) {
        for (final Enemy enemy : game.getEnemies()) {
            final int er = enemy.getRow();
            final int ec = enemy.getCol();
            final int manhattan = Math.abs(er - row) + Math.abs(ec - col);
            if (manhattan <= 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna las cuatro direcciones cardinales posibles de movimiento.
     *
     * @return lista de desplazamientos en filas y columnas
     */
    private List<int[]> directions() {
        return List.of(
                new int[]{-1, 0},
                new int[]{1, 0},
                new int[]{0, -1},
                new int[]{0, 1}
        );
    }

    /**
     * Punto de entrada de la aplicación.
     *
     * @param args argumentos de la línea de comandos (no se usan)
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> new GameGUI().setVisible(true));
    }
}
