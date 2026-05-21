package main.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Configuración centralizada de {@link java.util.logging} para el juego.
 */
public final class GameLog {

    private static final String LOG_DIR  = "logs";
    private static final String LOG_FILE = LOG_DIR + "/game_errors.log";
    private static boolean initialized;

    private GameLog() { }

    /**
     * Obtiene el logger de la clase indicada, asegurando el handler de archivo.
     *
     * @param clazz clase que solicita el logger
     * @return logger configurado
     */
    public static Logger getLogger(final Class<?> clazz) {
        ensureInitialized();
        return Logger.getLogger(clazz.getName());
    }

    private static synchronized void ensureInitialized() {
        if (initialized) {
            return;
        }
        try {
            Files.createDirectories(Paths.get(LOG_DIR));
            final Logger root = Logger.getLogger("");
            final FileHandler handler = new FileHandler(LOG_FILE, true);
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.ALL);
            root.addHandler(handler);
            root.setLevel(Level.INFO);
            initialized = true;
        } catch (IOException e) {
            if (Logger.getGlobal().isLoggable(Level.SEVERE)) {
                Logger.getGlobal().log(Level.SEVERE, "[GameLog] No se pudo inicializar logging: " + e.getMessage(), e);
            }
        }
    }
}
