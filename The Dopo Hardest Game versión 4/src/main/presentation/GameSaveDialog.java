package main.presentation;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.nio.file.Path;

/**
 * Diálogos para que el usuario elija dónde guardar o abrir una partida.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public final class GameSaveDialog {

    private static final String EXTENSION = "dopo";
    private static final String DESCRIPTION = "Partida guardada (*.dopo)";

    private static File lastDirectory;

    /**
     * Constructor privado para evitar la instanciación de la clase de utilidad.
     */
    private GameSaveDialog() {
    }

    /**
     * Muestra el diálogo de guardar archivo.
     *
     * @param parent ventana padre
     * @return ruta elegida, o null si el usuario canceló
     */
    public static Path showSaveDialog(final Component parent) {
        final JFileChooser chooser = createChooser();
        chooser.setDialogTitle("Guardar partida");
        chooser.setSelectedFile(new File("mi_partida." + EXTENSION));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        final File file = ensureExtension(chooser.getSelectedFile());
        rememberDirectory(file);
        return file.toPath();
    }

    /**
     * Muestra el diálogo de abrir archivo.
     *
     * @param parent ventana padre
     * @return ruta elegida, o null si el usuario canceló
     */
    public static Path showOpenDialog(final Component parent) {
        final JFileChooser chooser = createChooser();
        chooser.setDialogTitle("Abrir partida guardada");
        if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        final File file = chooser.getSelectedFile();
        rememberDirectory(file);
        return file.toPath();
    }

    /**
     * Crea un selector de archivos preconfigurado con el filtro de extensión dopo.
     *
     * @return selector de archivos preconfigurado
     */
    private static JFileChooser createChooser() {
        final JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter(DESCRIPTION, EXTENSION));
        if (lastDirectory != null) {
            chooser.setCurrentDirectory(lastDirectory);
        } else {
            final File saves = new File("saves");
            if (saves.isDirectory()) {
                chooser.setCurrentDirectory(saves);
            }
        }
        return chooser;
    }

    /**
     * Asegura que el archivo tenga la extensión .dopo.
     *
     * @param file archivo original
     * @return archivo con la extensión correcta garantizada
     */
    private static File ensureExtension(final File file) {
        if (file == null) {
            return null;
        }
        final String name = file.getName().toLowerCase();
        if (name.endsWith("." + EXTENSION)) {
            return file;
        }
        return new File(file.getAbsolutePath() + "." + EXTENSION);
    }

    /**
     * Recuerda el directorio del último archivo seleccionado.
     *
     * @param file archivo seleccionado
     */
    private static void rememberDirectory(final File file) {
        if (file != null && file.getParentFile() != null) {
            lastDirectory = file.getParentFile();
        }
    }
}
