package presentation;

import domain.Board;
import domain.Coin;
import domain.Enemy;
import domain.HardestGame;
import domain.Player;
import domain.Zone;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Panel gráfico que representa visualmente el tablero principal del juego.
 * Muestra el escenario, el jugador, los enemigos, las monedas y los
 * indicadores de estado de la partida.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public final class BoardPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final int CELL = 40;

    private static final Color BG_LAVENDER   = new Color(0xCC, 0xCC, 0xFF);
    private static final Color ZONE_GREEN    = new Color(0xB2, 0xFF, 0xB2);
    private static final Color CHECKER_WHITE = new Color(250, 252, 255);
    private static final Color CHECKER_BLUE  = new Color(210, 218, 238);
    private static final Color BORDER_COLOR  = Color.BLACK;
    private static final Color ENEMY_BLUE    = Color.BLUE;
    private static final Color PLAYER_RED    = new Color(0xFF, 0x00, 0x00);
    private static final Color COIN_YELLOW   = new Color(220, 185, 20);

    private static final float STROKE_BOARD  = 3f;
    private static final float STROKE_COIN   = 1.5f;
    private static final float STROKE_ENEMY  = 2f;
    private static final float STROKE_PLAYER = 2f;
    private static final int   PLAYER_MARGIN = 6;
    private static final int   ENEMY_PAD     = CELL / 5;

    private final HardestGame game;
    private final Color       playerBorderColor;
    private final Color       player2BorderColor;

    /**
     * Crea el panel gráfico asociado al estado de juego y color de borde indicados.
     *
     * @param game instancia del juego que contiene el estado actual
     * @param playerBorderColor color del borde del cuadrado del jugador 1
     * @param player2BorderColor color del borde del cuadrado del jugador 2
     */
    public BoardPanel(final HardestGame game, final Color playerBorderColor,
                      final Color player2BorderColor) {
        this.game              = game;
        this.playerBorderColor = playerBorderColor;
        this.player2BorderColor = player2BorderColor;
        final Board board = game.getBoard();
        setPreferredSize(new Dimension(board.getCols() * CELL, board.getRows() * CELL));
    }

    /**
     * Dibuja todos los componentes visuales del tablero.
     *
     * @param graphics contexto gráfico del panel
     */
    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        final Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        drawBackground(g2);
        drawBoard(g2);
        drawCoins(g2);
        drawEnemies(g2);
        drawPlayers(g2);
        drawOverlay(g2);
    }

    /**
     * Dibuja el fondo del panel con color uniforme.
     *
     * @param g2 contexto gráfico 2D
     */
    private void drawBackground(final Graphics2D g2) {
        g2.setColor(BG_LAVENDER);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    /**
     * Dibuja el tablero con celdas, zonas seguras y muros.
     *
     * @param g2 contexto gráfico 2D
     */
    private void drawBoard(final Graphics2D g2) {
        final Board board  = game.getBoard();
        final Zone  startZ = game.getStartZone();
        final Zone  interZ = game.getIntermediateZone();
        final Zone  finalZ = game.getFinalZone();
        final int   rows   = board.getRows();
        final int   cols   = board.getCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!board.isWall(r, c)) {
                    final int x = c * CELL;
                    final int y = r * CELL;
                    final boolean inGreen = startZ.contains(r, c)
                            || finalZ.contains(r, c)
                            || (interZ != null && interZ.contains(r, c));
                    g2.setColor(inGreen ? ZONE_GREEN
                            : ((r + c) % 2 == 0) ? CHECKER_WHITE : CHECKER_BLUE);
                    g2.fillRect(x, y, CELL, CELL);
                }
            }
        }

        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(STROKE_BOARD,
                BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!board.isWall(r, c)) {
                    final int x = c * CELL;
                    final int y = r * CELL;
                    if (r == 0 || board.isWall(r - 1, c)) {
                        g2.drawLine(x, y, x + CELL, y);
                    }
                    if (r == rows - 1 || board.isWall(r + 1, c)) {
                        g2.drawLine(x, y + CELL, x + CELL, y + CELL);
                    }
                    if (c == 0 || board.isWall(r, c - 1)) {
                        g2.drawLine(x, y, x, y + CELL);
                    }
                    if (c == cols - 1 || board.isWall(r, c + 1)) {
                        g2.drawLine(x + CELL, y, x + CELL, y + CELL);
                    }
                }
            }
        }
    }

    /**
     * Dibuja las monedas no recolectadas en el tablero.
     *
     * @param g2 contexto gráfico 2D
     */
    private void drawCoins(final Graphics2D g2) {
        final BasicStroke strokeCoin = new BasicStroke(STROKE_COIN);
        for (final Coin coin : game.getCoins()) {
            if (!coin.isCollected()) {
                final int x = coin.getCol() * CELL + CELL / 4;
                final int y = coin.getRow() * CELL + CELL / 4;
                final int s = CELL / 2;
                g2.setColor(COIN_YELLOW);
                g2.fillOval(x, y, s, s);
                g2.setColor(COIN_YELLOW.darker());
                g2.setStroke(strokeCoin);
                g2.drawOval(x, y, s, s);
            }
        }
    }

    /**
     * Dibuja los enemigos en sus posiciones actuales.
     *
     * @param g2 contexto gráfico 2D
     */
    private void drawEnemies(final Graphics2D g2) {
        final BasicStroke strokeEnemy = new BasicStroke(STROKE_ENEMY);
        for (final Enemy enemy : game.getEnemies()) {
            final int x = enemy.getCol() * CELL + ENEMY_PAD;
            final int y = enemy.getRow() * CELL + ENEMY_PAD;
            final int s = CELL - ENEMY_PAD * 2;
            g2.setColor(ENEMY_BLUE);
            g2.fillOval(x, y, s, s);
            g2.setColor(ENEMY_BLUE.darker());
            g2.setStroke(strokeEnemy);
            g2.drawOval(x, y, s, s);
        }
    }

    /**
     * Dibuja ambos jugadores en el tablero.
     *
     * @param g2 contexto gráfico 2D
     */
    private void drawPlayers(final Graphics2D g2) {
        drawSinglePlayer(g2, game.getPlayer(), playerBorderColor);
        if (game.isTwoPlayerMode() && game.getPlayer2() != null) {
            drawSinglePlayer(g2, game.getPlayer2(), player2BorderColor);
        }
    }

    /**
     * Dibuja un único jugador como cuadrado con borde.
     *
     * @param g2          contexto gráfico 2D
     * @param p           jugador a dibujar
     * @param borderColor color del borde del jugador
     */
    private void drawSinglePlayer(final Graphics2D g2, final Player p, final Color borderColor) {
        final int    size = (int) (CELL * p.getSize()) - PLAYER_MARGIN * 2;
        final int    x    = p.getCol() * CELL + PLAYER_MARGIN;
        final int    y    = p.getRow() * CELL + PLAYER_MARGIN;
        g2.setColor(p.getColor());
        g2.fillRect(x, y, size, size);
        g2.setColor(borderColor != null ? borderColor : Color.BLACK);
        g2.setStroke(new BasicStroke(STROKE_PLAYER));
        g2.drawRect(x, y, size, size);
    }

    /**
     * Dibuja la capa de superposición con mensajes de estado.
     *
     * @param g2 contexto gráfico 2D
     */
    private void drawOverlay(final Graphics2D g2) {
        final HardestGame.State st = game.getState();
        if (st == HardestGame.State.PLAYING) {
            return;
        }

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, getWidth(), getHeight());

        final String msg;
        final Color  color;

        if (st == HardestGame.State.WON) {
            msg   = game.isTwoPlayerMode()
                    ? "¡GANA " + game.getWinnerName().toUpperCase() + "!"
                    : "¡GANASTE!";
            color = new Color(100, 255, 100);
        } else if (st == HardestGame.State.LOST) {
            msg   = "TIEMPO AGOTADO";
            color = new Color(255, 80, 80);
        } else {
            msg   = "PAUSA";
            color = Color.WHITE;
        }

        g2.setColor(color);
        g2.setFont(new Font("Arial", Font.BOLD, 40));
        final FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);

        if (st != HardestGame.State.PAUSED) {
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            final String sub = "Presiona Reiniciar para jugar de nuevo";
            final FontMetrics fm2 = g2.getFontMetrics();
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(sub,
                    (getWidth() - fm2.stringWidth(sub)) / 2,
                    getHeight() / 2 + 40);
        }
    }
}