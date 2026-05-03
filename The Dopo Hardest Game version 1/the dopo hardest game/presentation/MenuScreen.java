package presentation;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Panel principal que representa la pantalla de inicio del videojuego.
 * Muestra el título, la versión y las opciones disponibles para el usuario.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public final class MenuScreen extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int    PANEL_WIDTH    = 860;
    private static final int    PANEL_HEIGHT   = 340;
    private static final int    GRID_SIZE      = 18;
    private static final int    BTN_WIDTH      = 150;
    private static final int    BTN_HEIGHT     = 55;
    private static final int    BTN_Y          = 250;
    private static final int[]  BTN_X          = {60, 230, 450, 660};

    private static final Color  COLOR_BG       = new Color(240, 245, 240);
    private static final Color  COLOR_GRID     = new Color(180, 210, 190, 120);
    private static final Color  COLOR_BORDER   = new Color(160, 190, 165);
    private static final Color  COLOR_SUBTITLE = new Color(80, 130, 90);
    private static final Color  COLOR_TITLE    = new Color(60, 115, 80);
    private static final Color  COLOR_VERSION  = new Color(120, 160, 100);

    private final Runnable onPlayGame;

    /**
     * Crea el panel de menú con la acción de inicio de partida indicada.
     *
     * @param onPlayGame acción que se ejecuta al seleccionar la opción de jugar
     */
    public MenuScreen(final Runnable onPlayGame) {
        this.onPlayGame = onPlayGame;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        setBackground(COLOR_BG);
        buildButtons();
    }

    private void buildButtons() {
        final String[][] allParts = {
                {"PLAY", "GAME"},
                {"PLAYER", "VS PLAYER"},
                {"PLAYER", "VS TIME"},
                {"LOAD", "GAME"}
        };

        final MenuButton btn0 = new MenuButton(allParts[0]);
        final MenuButton btn1 = new MenuButton(allParts[1]);
        final MenuButton btn2 = new MenuButton(allParts[2]);
        final MenuButton btn3 = new MenuButton(allParts[3]);
        final MenuButton[] btns = {btn0, btn1, btn2, btn3};

        for (int i = 0; i < btns.length; i++) {
            btns[i].setBounds(BTN_X[i], BTN_Y, BTN_WIDTH, BTN_HEIGHT);
            if (i == 0) {
                btns[i].addActionListener(e -> onPlayGame.run());
            } else {
                btns[i].addActionListener(e ->
                        JOptionPane.showMessageDialog(
                                SwingUtilities.getWindowAncestor(this),
                                "Esta opción estará disponible próximamente.",
                                "No disponible",
                                JOptionPane.INFORMATION_MESSAGE));
            }
            add(btns[i]);
        }
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        final Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        final int panelW = getWidth();
        final int panelH = getHeight();

        g2.setColor(new Color(235, 242, 235));
        g2.fillRect(0, 0, panelW, panelH);

        g2.setColor(COLOR_GRID);
        g2.setStroke(new BasicStroke(0.7f));
        for (int x = 0; x < panelW; x += GRID_SIZE) {
            g2.drawLine(x, 0, x, panelH);
        }
        for (int y = 0; y < panelH; y += GRID_SIZE) {
            g2.drawLine(0, y, panelW, y);
        }

        g2.setColor(COLOR_BORDER);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(20, 15, panelW - 40, panelH - 80);

        g2.setFont(loadFont(28f, Font.PLAIN));
        g2.setColor(COLOR_SUBTITLE);
        g2.drawString("THE DOPO...", 45, 60);

        g2.setFont(loadFont(105f, Font.BOLD));
        g2.setColor(new Color(60, 115, 80, 40));
        g2.drawString("HARDEST GAME", 47, 192);
        g2.setColor(COLOR_TITLE);
        g2.drawString("HARDEST GAME", 45, 190);

        g2.setFont(loadFont(18f, Font.ITALIC));
        g2.setColor(COLOR_VERSION);
        g2.drawString("Version 1.0", 655, 210);
    }

    private Font loadFont(final float size, final int style) {
        return new Font("Courier New", style, (int) size);
    }

    /**
     * Botón gráfico con estilo personalizado del menú.
     */
    private static final class MenuButton extends JButton {

        private static final long serialVersionUID = 1L;

        private static final Color COLOR_HOVER_BG   = new Color(60, 115, 80, 30);
        private static final Color COLOR_HOVER_TEXT  = new Color(40, 90, 55);
        private static final Color COLOR_NORMAL_TEXT = new Color(80, 130, 90);

        private final String[] lines;

        /**
         * Crea un botón de menú con las líneas de texto indicadas.
         *
         * @param lines líneas de texto que componen el botón
         */
        MenuButton(final String[] lines) {
            this.lines = lines.clone();
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(final Graphics graphics) {
            final Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            final boolean hover = getModel().isRollover();

            if (hover) {
                g2.setColor(COLOR_HOVER_BG);
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
            }

            g2.setFont(new Font("Courier New", Font.BOLD, 16));
            g2.setColor(hover ? COLOR_HOVER_TEXT : COLOR_NORMAL_TEXT);

            final FontMetrics fm  = g2.getFontMetrics();
            final int totalHeight = lines.length * fm.getHeight();
            int startY = (getHeight() - totalHeight) / 2 + fm.getAscent();

            for (final String line : lines) {
                final int tx = (getWidth() - fm.stringWidth(line)) / 2;
                g2.drawString(line, tx, startY);
                startY += fm.getHeight();
            }
        }
    }
}