package main.presentation;

import main.domain.GameLogger;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo modal que permite al usuario describir un error o problema
 * encontrado durante la partida. El reporte queda registrado para revisión del programador.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public final class ErrorReportDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final int  DIALOG_W    = 520;
    private static final int  DIALOG_H    = 400;
    private static final int  TEXT_ROWS   = 8;
    private static final int  MAX_CHARS   = 800;

    private static final Color BG_DARK     = new Color(18,  20,  28);
    private static final Color BG_PANEL    = new Color(28,  32,  44);
    private static final Color ACCENT_RED  = new Color(220, 60,  60);
    private static final Color ACCENT_GOLD = new Color(220, 185, 20);
    private static final Color TEXT_MAIN   = new Color(230, 232, 240);
    private static final Color TEXT_DIM    = new Color(140, 145, 165);
    private static final Color BTN_SEND_BG = new Color(60,  130, 80);
    private static final Color BTN_SEND_FG = new Color(220, 255, 220);
    private static final Color BTN_CANCEL  = new Color(55,  58,  75);
    private static final Color BORDER_COL  = new Color(55,  60,  80);

    // ── Fuentes ────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Courier New", Font.BOLD,  15);
    private static final Font FONT_BODY   = new Font("Courier New", Font.PLAIN, 12);
    private static final Font FONT_BTN    = new Font("Courier New", Font.BOLD,  13);
    private static final Font FONT_HINT   = new Font("Courier New", Font.ITALIC, 11);
    private static final Font FONT_CHARS  = new Font("Courier New", Font.PLAIN, 10);

    private final String levelContext;
    private final Throwable exception;
    private JTextArea    textArea;
    private JLabel       charCountLabel;

    /**
     * Crea el diálogo pero no lo hace visible.
     *
     * @param owner        ventana propietaria
     * @param levelContext ruta o nombre del nivel activo
     */
    private ErrorReportDialog(final Window owner, final String levelContext) {
        this(owner, levelContext, null);
    }

    /**
     * Crea el diálogo con soporte opcional de excepción no controlada.
     *
     * @param owner        ventana propietaria
     * @param levelContext ruta o nombre del nivel activo
     * @param exception    excepción causante del crash, puede ser null
     */
    private ErrorReportDialog(final Window owner, final String levelContext, final Throwable exception) {
        super(owner, exception != null ? "⚠ Fallo Inesperado (Crash)" : "Reportar problema", ModalityType.APPLICATION_MODAL);
        this.levelContext = levelContext;
        this.exception = exception;
        buildUI();
        if (exception != null) {
            final String exName = exception.getClass().getSimpleName();
            final String exMsg = exception.getMessage() != null ? exception.getMessage() : "sin mensaje";
            textArea.setText("[FALLO INESPERADO - CRASH]\n"
                    + "Excepción: " + exName + " (" + exMsg + ")\n\n"
                    + "Por favor, describe brevemente qué estabas haciendo cuando ocurrió el problema:\n");
            // Posicionar cursor al final
            textArea.setCaretPosition(textArea.getText().length());
        }
        setSize(DIALOG_W, DIALOG_H);
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    /**
     * Crea y muestra el diálogo de reporte de error estándar.
     *
     * @param owner        ventana propietaria (puede ser null)
     * @param levelContext ruta o nombre del nivel activo, para incluir en el log
     */
    public static void show(final Window owner, final String levelContext) {
        show(owner, levelContext, null);
    }

    /**
     * Crea y muestra el diálogo de reporte de error automático ante un crash.
     *
     * @param owner        ventana propietaria (puede ser null)
     * @param levelContext ruta o nombre del nivel activo, para incluir en el log
     * @param exception    excepción causante del crash
     */
    public static void show(final Window owner, final String levelContext, final Throwable exception) {
        final ErrorReportDialog dlg = new ErrorReportDialog(owner, levelContext, exception);
        dlg.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════
    // Construcción de la interfaz
    // ══════════════════════════════════════════════════════════════════════

    /** Construye y ensambla todos los paneles del diálogo. */
    private void buildUI() {
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    /**
     * Construye el encabezado con ícono, título y subtítulo.
     *
     * @return panel de encabezado
     */
    private JPanel buildHeader() {
        final JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(18, 20, 10, 20));

        // Barra de color superior
        final JPanel bar = new JPanel();
        bar.setBackground(ACCENT_RED);
        bar.setPreferredSize(new Dimension(DIALOG_W, 4));
        header.add(bar, BorderLayout.NORTH);

        final String titleText = exception != null ? "⚠  Fallo Inesperado Detectado" : "⚠  Reportar problema";
        final JLabel icon = new JLabel(titleText, SwingConstants.LEFT);
        icon.setFont(FONT_TITLE);
        icon.setForeground(ACCENT_RED);
        header.add(icon, BorderLayout.CENTER);

        final String subText = exception != null 
                ? "El crash ha sido registrado. Por favor, describe qué pasó para ayudar a solucionarlo."
                : "Tu reporte quedará guardado para que el programador lo revise.";
        final JLabel sub = new JLabel(subText, SwingConstants.LEFT);
        sub.setFont(FONT_HINT);
        sub.setForeground(TEXT_DIM);
        header.add(sub, BorderLayout.SOUTH);

        return header;
    }

    /**
     * Construye el área central con el campo de texto del usuario.
     *
     * @return panel central
     */
    private JPanel buildCenter() {
        final JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG_PANEL);
        center.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_COL),
                BorderFactory.createEmptyBorder(14, 20, 10, 20)));

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;

        // Etiqueta instrucción
        final JLabel instruction = new JLabel(
                "¿Qué pasó? Describe el problema o tu opinión:");
        instruction.setFont(FONT_BODY);
        instruction.setForeground(ACCENT_GOLD);
        gbc.gridy  = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        center.add(instruction, gbc);

        // Área de texto
        textArea = new JTextArea(TEXT_ROWS, 40);
        textArea.setFont(FONT_BODY);
        textArea.setBackground(new Color(22, 25, 36));
        textArea.setForeground(TEXT_MAIN);
        textArea.setCaretColor(ACCENT_GOLD);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        // Limitar caracteres y actualizar contador
        textArea.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    @Override public void insertUpdate(final javax.swing.event.DocumentEvent e) { cap(); }
                    @Override public void removeUpdate(final javax.swing.event.DocumentEvent e) { cap(); }
                    @Override public void changedUpdate(final javax.swing.event.DocumentEvent e) { cap(); }
                    private void cap() {
                        final String text = textArea.getText();
                        if (text.length() > MAX_CHARS) {
                            javax.swing.SwingUtilities.invokeLater(() ->
                                    textArea.setText(text.substring(0, MAX_CHARS)));
                        }
                        updateCharCount();
                    }
                });

        final JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(460, 160));
        gbc.gridy  = 1;
        gbc.insets = new Insets(0, 0, 6, 0);
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        center.add(scroll, gbc);

        // Contador de caracteres
        charCountLabel = new JLabel("0 / " + MAX_CHARS + " caracteres");
        charCountLabel.setFont(FONT_CHARS);
        charCountLabel.setForeground(TEXT_DIM);
        gbc.gridy  = 2;
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.insets = new Insets(2, 0, 0, 0);
        center.add(charCountLabel, gbc);

        return center;
    }

    /**
     * Construye el pie con los botones de envío y cancelación.
     *
     * @return panel de pie
     */
    private JPanel buildFooter() {
        final JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_DARK);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 20, 16, 20));

        // Info del nivel
        final String ctxShort = levelContext != null
                ? levelContext.replaceAll(".*/", "")
                : "desconocido";
        final JLabel ctxLabel = new JLabel("Nivel: " + ctxShort);
        ctxLabel.setFont(FONT_HINT);
        ctxLabel.setForeground(TEXT_DIM);
        footer.add(ctxLabel, BorderLayout.WEST);

        // Botones
        final JPanel btnRow = new JPanel();
        btnRow.setBackground(BG_DARK);

        final JButton cancelBtn = makeButton("Cancelar", BTN_CANCEL, TEXT_DIM);
        cancelBtn.addActionListener(e -> dispose());

        final JButton sendBtn = makeButton("Enviar reporte", BTN_SEND_BG, BTN_SEND_FG);
        sendBtn.addActionListener(e -> submitReport());

        btnRow.add(cancelBtn);
        btnRow.add(sendBtn);
        footer.add(btnRow, BorderLayout.EAST);

        return footer;
    }

    /**
     * Valida el mensaje y lo envía al {@link GameLogger}.
     * Muestra confirmación al usuario y cierra el diálogo.
     */
    private void submitReport() {
        final String msg = textArea.getText().trim();
        if (msg.isEmpty()) {
            textArea.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_RED, 2),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)));
            textArea.requestFocus();
            return;
        }

        GameLogger.logUserReport(msg, levelContext);
        showConfirmation();
        dispose();
    }

    /**
     * Muestra una pantalla de confirmación breve antes de cerrar.
     */
    private void showConfirmation() {
        final JDialog confirm = new JDialog(this, "Reporte enviado", true);
        confirm.getContentPane().setBackground(BG_DARK);
        confirm.setLayout(new BorderLayout());
        confirm.setSize(320, 130);
        confirm.setLocationRelativeTo(this);
        confirm.setResizable(false);

        final JLabel msg = new JLabel(
                "<html><center>✓ Reporte guardado.<br>"
                        + "<span style='color:#8c91a5;font-size:10px;'>"
                        + "El programador lo revisará pronto.</span></center></html>",
                SwingConstants.CENTER);
        msg.setFont(FONT_BODY);
        msg.setForeground(new Color(130, 220, 130));
        msg.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        confirm.add(msg, BorderLayout.CENTER);

        final JButton ok = makeButton("Aceptar", BTN_SEND_BG, BTN_SEND_FG);
        ok.addActionListener(e -> confirm.dispose());
        final JPanel p = new JPanel();
        p.setBackground(BG_DARK);
        p.add(ok);
        confirm.add(p, BorderLayout.SOUTH);
        confirm.setVisible(true);
    }

    /** Actualiza el contador de caracteres en tiempo real. */
    private void updateCharCount() {
        final int len = textArea.getText().length();
        charCountLabel.setText(len + " / " + MAX_CHARS + " caracteres");
        charCountLabel.setForeground(len > MAX_CHARS * 0.9 ? ACCENT_RED : TEXT_DIM);
    }

    /**
     * Crea un botón estilizado con los colores indicados.
     *
     * @param text     etiqueta del botón
     * @param bg       color de fondo
     * @param fg       color del texto
     * @return botón configurado
     */
    private JButton makeButton(final String text, final Color bg, final Color fg) {
        final JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        btn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        return btn;
    }
}