package presentation;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * Panel principal que representa la pantalla de inicio del videojuego.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public final class MenuScreen extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int   PANEL_WIDTH  = 1100;
    private static final int   PANEL_HEIGHT = 680;
    private static final int   GRID_SIZE    = 18;

    private static final Color COLOR_BG      = new Color(235, 242, 235);
    private static final Color COLOR_GRID    = new Color(180, 210, 190, 120);
    private static final Color COLOR_BORDER  = new Color(160, 190, 165);
    private static final Color COLOR_TITLE   = new Color(60, 115, 80);
    private static final Color COLOR_SUB     = new Color(80, 130, 90);
    private static final Color COLOR_VER     = new Color(120, 160, 100);
    private static final Color COLOR_LABEL   = new Color(60, 115, 80);
    private static final Color COLOR_COMBO_BG = new Color(200, 220, 205);

    private static final int COL_LEFT      = 50;
    private static final int COL_RIGHT     = 580;
    private static final int COMBO_W_LEFT  = 500;
    private static final int COMBO_W_RIGHT = 480;
    private static final int COMBO_H       = 30;

    private static final String[] LEVEL_PATHS = {
            "configs/level1.txt",
            "configs/level2.txt",
            "configs/level3.txt",
            "configs/demo_final.txt"
    };

    private final Consumer<GameSetup> onStart;

    private JComboBox<String> nivelCombo;
    private JComboBox<String> modalidadCombo;
    private JLabel            maquinaLabel;
    private JComboBox<String> maquinaCombo;
    private JComboBox<String> skin1Combo;
    private JComboBox<String> skin2Combo;
    private JComboBox<String> borde1Combo;
    private JComboBox<String> borde2Combo;

    /**
     * Crea la pantalla de menú con el listener de inicio indicado.
     *
     * @param onStart acción que recibe el GameSetup al presionar INICIAR
     */
    public MenuScreen(final Consumer<GameSetup> onStart) {
        this.onStart = onStart;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        setBackground(COLOR_BG);
        buildComponents();
    }

    /**
     * Construye y posiciona todos los componentes del menú.
     */
    private void buildComponents() {
        placeLabel("Nivel (.txt)", COL_LEFT, 268);
        nivelCombo = makeCombo(new String[]{
            "Nivel 1 (referencia TWHG)",
            "Nivel 2 (compacto)",
            "Nivel 3 (checkpoint)",
            "Demo final (todos los elementos)"
        });
        nivelCombo.setBounds(COL_LEFT, 290, COMBO_W_LEFT + COMBO_W_RIGHT + 30, COMBO_H);
        add(nivelCombo);

        placeLabel("Modalidad", COL_LEFT, 340);
        modalidadCombo = makeCombo(new String[]{
            "Un jugador", "Jugador vs jugador", "Jugador vs máquina"
        });
        modalidadCombo.setBounds(COL_LEFT, 362, COMBO_W_LEFT, COMBO_H);
        add(modalidadCombo);

        maquinaLabel = new JLabel("Máquina (PvM)");
        styleLabel(maquinaLabel);
        maquinaLabel.setBounds(COL_RIGHT, 340, 300, 20);
        add(maquinaLabel);
        maquinaCombo = makeCombo(new String[]{"Máquina aleatoria", "Máquina experta"});
        maquinaCombo.setBounds(COL_RIGHT, 362, COMBO_W_RIGHT, COMBO_H);
        add(maquinaCombo);

        placeLabel("Skin jugador 1", COL_LEFT, 412);
        skin1Combo = makeCombo(new String[]{
            "Rojo (Blinky)", "Azul (Inky)", "Verde (Clyde)"
        });
        skin1Combo.setBounds(COL_LEFT, 434, COMBO_W_LEFT, COMBO_H);
        add(skin1Combo);

        placeLabel("Skin jugador 2 / máquina", COL_RIGHT, 412);
        skin2Combo = makeCombo(new String[]{
            "Rojo (Blinky)", "Azul (Inky)", "Verde (Clyde)"
        });
        skin2Combo.setBounds(COL_RIGHT, 434, COMBO_W_RIGHT, COMBO_H);
        add(skin2Combo);

        placeLabel("Borde jugador 1", COL_LEFT, 482);
        borde1Combo = makeCombo(new String[]{
            "Negro", "Blanco", "Amarillo", "Cian", "Magenta"
        });
        borde1Combo.setBounds(COL_LEFT, 504, COMBO_W_LEFT, COMBO_H);
        add(borde1Combo);

        placeLabel("Borde jugador 2", COL_RIGHT, 482);
        borde2Combo = makeCombo(new String[]{
            "Negro", "Blanco", "Amarillo", "Cian", "Magenta"
        });
        borde2Combo.setBounds(COL_RIGHT, 504, COMBO_W_RIGHT, COMBO_H);
        add(borde2Combo);

        final JLabel info = new JLabel(
            "J1: flechas   |   J2 (PvP): W=arriba S=abajo A=izq D=der"
            + "   |   Diagonales combinando teclas");
        info.setFont(new Font("Courier New", Font.PLAIN, 12));
        info.setForeground(COLOR_SUB);
        info.setBounds(COL_LEFT, 552, 1000, 20);
        add(info);

        placeMenuButton("INICIAR\nPARTIDA", 270, 592, e -> fireStart());
        placeMenuButton("CARGAR\nPARTIDA",  590, 592, e -> notAvailable());
        placeMenuButton("SALIR",             880, 592, e -> System.exit(0));

        modalidadCombo.addActionListener(e -> updateMaquinaVisibility());
        updateMaquinaVisibility();
    }

    /**
     * Construye el GameSetup y lo envía al listener de inicio.
     */
    private void fireStart() {
        final int nivelIdx = nivelCombo.getSelectedIndex();
        final String path  = LEVEL_PATHS[nivelIdx];

        final String modalidad = (String) modalidadCombo.getSelectedItem();
        final String skin1     = (String) skin1Combo.getSelectedItem();
        final String skin2     = (String) skin2Combo.getSelectedItem();
        final Color  border1   = parseBorderColor((String) borde1Combo.getSelectedItem());
        final Color  border2   = parseBorderColor((String) borde2Combo.getSelectedItem());
        final String maquina   = (String) maquinaCombo.getSelectedItem();

        onStart.accept(new GameSetup(path, modalidad, skin1, skin2,
                                     border1, border2, maquina));
    }

    /**
     * Convierte el nombre de un color de borde a su instancia Color.
     *
     * @param name nombre del color de borde
     * @return instancia Color correspondiente
     */
    private Color parseBorderColor(final String name) {
        switch (name) {
            case "Blanco":   return Color.WHITE;
            case "Amarillo": return Color.YELLOW;
            case "Cian":     return Color.CYAN;
            case "Magenta":  return Color.MAGENTA;
            default:         return Color.BLACK;
        }
    }

    /**
     * Actualiza la visibilidad del selector de máquina según la modalidad.
     */
    private void updateMaquinaVisibility() {
        final boolean pvm = "Jugador vs máquina"
                .equals(modalidadCombo.getSelectedItem());
        maquinaLabel.setVisible(pvm);
        maquinaCombo.setVisible(pvm);
    }

    /**
     * Muestra un mensaje indicando que la función no está disponible.
     */
    private void notAvailable() {
        JOptionPane.showMessageDialog(
            SwingUtilities.getWindowAncestor(this),
            "Esta opción estará disponible próximamente.",
            "No disponible", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Crea y posiciona una etiqueta de texto estilizada.
     *
     * @param text texto de la etiqueta
     * @param x    coordenada horizontal
     * @param y    coordenada vertical
     */
    private void placeLabel(final String text, final int x, final int y) {
        final JLabel lbl = new JLabel(text);
        styleLabel(lbl);
        lbl.setBounds(x, y, 500, 20);
        add(lbl);
    }

    /**
     * Aplica el estilo visual a una etiqueta.
     *
     * @param lbl etiqueta a estilizar
     */
    private void styleLabel(final JLabel lbl) {
        lbl.setFont(new Font("Courier New", Font.BOLD, 13));
        lbl.setForeground(COLOR_LABEL);
    }

    /**
     * Crea un combo box estilizado con los elementos indicados.
     *
     * @param items elementos del combo box
     * @return combo box configurado
     */
    private JComboBox<String> makeCombo(final String[] items) {
        final JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Courier New", Font.PLAIN, 13));
        combo.setBackground(COLOR_COMBO_BG);
        combo.setForeground(COLOR_TITLE);
        return combo;
    }

    /**
     * Crea y posiciona un botón del menú.
     *
     * @param text     texto del botón
     * @param cx       coordenada horizontal del centro
     * @param y        coordenada vertical
     * @param listener acción al presionar
     */
    private void placeMenuButton(final String text, final int cx, final int y,
                                  final ActionListener listener) {
        final MenuButton btn = new MenuButton(text.split("\n"));
        btn.setBounds(cx - 75, y, 150, 55);
        btn.addActionListener(listener);
        add(btn);
    }

    /**
     * Dibuja el fondo decorativo del panel de menú.
     *
     * @param graphics contexto gráfico
     */
    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        final Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final int w = getWidth();
        final int h = getHeight();
        g2.setColor(COLOR_BG);
        g2.fillRect(0, 0, w, h);
        g2.setColor(COLOR_GRID);
        g2.setStroke(new BasicStroke(0.7f));
        for (int x = 0; x < w; x += GRID_SIZE) { 
            g2.drawLine(x, 0, x, h); 
        }
        for (int y = 0; y < h; y += GRID_SIZE) { 
            g2.drawLine(0, y, w, y); 
        }
        g2.setColor(COLOR_BORDER);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(20, 15, w - 40, h - 30);
        g2.setFont(new Font("Courier New", Font.PLAIN, 28));
        g2.setColor(COLOR_SUB);
        g2.drawString("THE DOPO...", 45, 65);
        g2.setFont(new Font("Courier New", Font.BOLD, 105));
        g2.setColor(new Color(60, 115, 80, 40));
        g2.drawString("HARDEST GAME", 47, 222);
        g2.setColor(COLOR_TITLE);
        g2.drawString("HARDEST GAME", 45, 220);
        g2.setFont(new Font("Courier New", Font.ITALIC, 16));
        g2.setColor(COLOR_VER);
        g2.drawString("Entrega final \u2014 The DOPO Hardest Game", w - 490, 242);
    }

    /**
     * Botón personalizado del menú con efecto hover.
     */
    private static final class MenuButton extends JButton {
        private static final long serialVersionUID = 1L;
        private static final Color HOVER_BG  = new Color(60, 115, 80, 30);
        private static final Color HOVER_FG  = new Color(40, 90, 55);
        private static final Color NORMAL_FG = new Color(80, 130, 90);
        private final String[] lines;

        /**
         * Crea un botón con las líneas de texto indicadas.
         *
         * @param lines líneas de texto del botón
         */
        MenuButton(final String[] lines) {
            this.lines = lines.clone();
            setOpaque(false); setContentAreaFilled(false);
            setBorderPainted(false); setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        /**
         * Dibuja el botón con efecto hover y texto centrado.
         *
         * @param graphics contexto gráfico
         */
        @Override
        protected void paintComponent(final Graphics graphics) {
            final Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            final boolean hover = getModel().isRollover();
            if (hover) {
                g2.setColor(HOVER_BG);
                g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 8, 8);
            }
            g2.setFont(new Font("Courier New", Font.BOLD, 16));
            g2.setColor(hover ? HOVER_FG : NORMAL_FG);
            final FontMetrics fm = g2.getFontMetrics();
            int startY = (getHeight() - lines.length * fm.getHeight()) / 2 + fm.getAscent();
            for (final String line : lines) {
                g2.drawString(line, (getWidth() - fm.stringWidth(line)) / 2, startY);
                startY += fm.getHeight();
            }
        }
    }
}