package presentation;

import domain.HardestGame;
import domain.HardestGameException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Ventana principal que controla la interfaz gráfica completa del juego.
 * Gestiona la transición entre el menú inicial y el modo de juego,
 * la entrada por teclado y los temporizadores del ciclo de juego.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public final class GameGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final int  TIMER_GAME_MS      = 50;
    private static final int  TIMER_COUNTDOWN_MS = 1000;
    private static final int  PLAYER_MOVE_EVERY  = 2;

    private static final Color COLOR_STATUS_FG  = Color.WHITE;
    private static final Color COLOR_STATUS_BG  = Color.BLACK;
    private static final Color COLOR_BTN_PANEL  = new Color(30, 30, 30);
    private static final Color COLOR_BTN_BG     = new Color(50, 50, 50);

    private HardestGame  game;
    private BoardPanel   boardPanel;
    private JLabel       statusLabel;
    private MenuScreen   menuScreen;
    private Timer        gameTimer;
    private Timer        countdownTimer;

    private final Set<Integer> pressedKeys = new HashSet<>();
    private int playerTick;
    private String lastConfig = "the world hast dopo Documentado/configs/level1.txt";

    /**
     * Crea y muestra la ventana principal del juego con el menú inicial.
     */
    public GameGUI() {
        game = new HardestGame();
        buildFrame();
        setupTimers();
        showMenu();
    }

    private void buildFrame() {
        setTitle("The DOPO Hardest Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
    }

    private void showMenu() {
        getContentPane().removeAll();
        removeKeyListeners();
        menuScreen = new MenuScreen(this::startGame);
        add(menuScreen, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private void removeKeyListeners() {
        for (final KeyListener kl : getKeyListeners()) {
            removeKeyListener(kl);
        }
    }

    private void startGame() {
        getContentPane().removeAll();
        statusLabel = null;
        boardPanel  = null;

        statusLabel = new JLabel("  Muertes: 0 | Monedas: 0/0 | Tiempo: 0s");
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        statusLabel.setForeground(COLOR_STATUS_FG);
        statusLabel.setBackground(COLOR_STATUS_BG);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        add(statusLabel, BorderLayout.NORTH);

        final JPanel btnPanel = new JPanel();
        btnPanel.setBackground(COLOR_BTN_PANEL);
        addBtn(btnPanel, "Pausar [P]",    e -> togglePauseAndRepaint());
        addBtn(btnPanel, "Reiniciar [R]", e -> loadLevel(lastConfig));
        addBtn(btnPanel, "Menu",          e -> returnToMenu());
        addBtn(btnPanel, "Salir",         e -> System.exit(0));
        add(btnPanel, BorderLayout.SOUTH);

        removeKeyListeners();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    togglePauseAndRepaint();
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
        loadLevel(lastConfig);
    }

    private void togglePauseAndRepaint() {
        game.togglePause();
        if (boardPanel != null) {
            boardPanel.repaint();
        }
    }

    private void returnToMenu() {
        game = new HardestGame();
        showMenu();
    }

    private void addBtn(final JPanel panel, final String text,
                        final ActionListener listener) {
        final JButton btn = new JButton(text);
        btn.setBackground(COLOR_BTN_BG);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(listener);
        panel.add(btn);
    }

    private void loadLevel(final String path) {
        try {
            game.loadConfiguration(path);
            lastConfig = path;

            if (boardPanel != null) {
                remove(boardPanel);
            }
            boardPanel = new BoardPanel(game);
            add(boardPanel, BorderLayout.CENTER);

            pack();
            setLocationRelativeTo(null);
            updateStatus();
            revalidate();
            repaint();
            requestFocusInWindow();
        } catch (HardestGameException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error cargando nivel:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupTimers() {
        gameTimer = new Timer(TIMER_GAME_MS, e -> gameTick());
        gameTimer.start();

        countdownTimer = new Timer(TIMER_COUNTDOWN_MS, e -> {
            if (game.getState() == HardestGame.State.PLAYING) {
                game.decrementTime();
                updateStatus();
                if (boardPanel != null) {
                    boardPanel.repaint();
                }
            }
        });
        countdownTimer.start();
    }

    private void gameTick() {
        if (boardPanel == null || game.getState() != HardestGame.State.PLAYING) {
            return;
        }

        playerTick++;
        if (playerTick >= PLAYER_MOVE_EVERY) {
            playerTick = 0;
            int dr = 0;
            int dc = 0;

            if (pressedKeys.contains(KeyEvent.VK_UP)
                    || pressedKeys.contains(KeyEvent.VK_W)) {
                dr = -1;
            } else if (pressedKeys.contains(KeyEvent.VK_DOWN)
                    || pressedKeys.contains(KeyEvent.VK_S)) {
                dr = 1;
            }

            if (pressedKeys.contains(KeyEvent.VK_LEFT)
                    || pressedKeys.contains(KeyEvent.VK_A)) {
                dc = -1;
            } else if (pressedKeys.contains(KeyEvent.VK_RIGHT)
                    || pressedKeys.contains(KeyEvent.VK_D)) {
                dc = 1;
            }

            if (dr != 0 || dc != 0) {
                game.movePlayer(dr, dc);
            }
        }

        game.tick();
        updateStatus();
        boardPanel.repaint();
    }

    private void updateStatus() {
        if (statusLabel == null) {
            return;
        }
        statusLabel.setText(String.format(
                "  Muertes: %d  |  Monedas: %d / %d  |  Tiempo: %ds",
                game.getDeaths(),
                game.getCoinsCollected(),
                game.getTotalCoins(),
                game.getTimeRemaining()));
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