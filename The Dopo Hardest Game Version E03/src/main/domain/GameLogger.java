package main.domain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servicio de registro estructurado de eventos y errores del juego.
 *
 * <p>Escribe dos archivos en la carpeta logs:
 * <ul>
 *   <li> errores internos capturados por el programa
 *       (excepciones, estados inválidos, etc.).</li>
 *   <li> reportes escritos por el usuario desde la
 *       pantalla de error, para que el programador los revise luego.</li>
 * </ul>
 *
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public final class GameLogger {

    private static final String LOGS_DIR        = "logs";
    private static final String ERROR_LOG_FILE  = LOGS_DIR + "/game_errors.log";
    private static final String REPORT_LOG_FILE = LOGS_DIR + "/user_reports.log";


    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String SEP_SECTION =
            "════════════════════════════════════════════════════════════";
    private static final String SEP_ENTRY   =
            "────────────────────────────────────────────────────────────";

    /** Constructor privado — clase utilitaria no instanciable. */
    private GameLogger() { }


    /**
     * Registra un error interno del juego sin excepción asociada.
     *
     * @param message descripción del error
     */
    public static void logError(final String message) {
        logError(message, null, null);
    }

    /**
     * Registra un error interno del juego con la excepción que lo causó.
     *
     * @param message   descripción del error
     * @param exception excepción capturada, puede ser null
     */
    public static void logError(final String message, final Throwable exception) {
        logError(message, exception, null);
    }

    /**
     * Registra una HardestGameException con su tipo y contexto completos.
     * @param context   descripción de dónde ocurrió el error
     * @param exception excepción del juego
     */
    public static void logGameException(final String context,
                                        final HardestGameException exception) {
        final String type = exception.getErrorType().name();
        logError("[" + type + "] en " + context + ": " + exception.getMessage(),
                exception, null);
    }

    /**
     * Registra un reporte de error escrito por el usuario desde la pantalla de error.
     * Este método es el punto de entrada para los mensajes que el usuario redacta
     * describiendo qué salió mal, para que el programador los revise.
     *
     * @param userMessage  texto escrito por el usuario
     * @param levelContext nombre del nivel o contexto donde ocurrió el error
     */
    public static void logUserReport(final String userMessage,
                                     final String levelContext) {
        ensureLogsDir();
        final String timestamp = now();
        final StringBuilder sb = new StringBuilder();
        sb.append("\n").append(SEP_SECTION).append("\n");
        sb.append("REPORTE DE USUARIO — ").append(timestamp).append("\n");
        sb.append(SEP_ENTRY).append("\n");
        sb.append("Contexto : ").append(levelContext != null ? levelContext : "desconocido").append("\n");
        sb.append("Mensaje  : ").append(userMessage != null ? userMessage.trim() : "(sin mensaje)").append("\n");
        sb.append(SEP_SECTION).append("\n");

        writeToFile(REPORT_LOG_FILE, sb.toString());
    }

    /**
     * Registra una entrada informativa (INFO) para seguimiento del flujo normal.
     *
     * @param message texto informativo
     */
    public static void logInfo(final String message) {
        writeToFile(ERROR_LOG_FILE,
                "\n[INFO] " + now() + " — " + message + "\n");
    }


    /**
     * Implementación central del registro de errores.
     *
     * @param message   descripción del error
     * @param exception excepción asociada, puede ser null
     * @param extra     información adicional libre, puede ser null
     */
    private static void logError(final String message,
                                 final Throwable exception,
                                 final String extra) {
        ensureLogsDir();
        final StringBuilder sb = new StringBuilder();
        sb.append("\n").append(SEP_SECTION).append("\n");
        sb.append("[ERROR] ").append(now()).append("\n");
        sb.append(SEP_ENTRY).append("\n");
        sb.append("Mensaje    : ").append(message).append("\n");

        if (extra != null) {
            sb.append("Contexto   : ").append(extra).append("\n");
        }

        if (exception != null) {
            sb.append("Excepción  : ").append(exception.getClass().getName()).append("\n");
            sb.append("Detalle    : ").append(exception.getMessage()).append("\n");
            if (exception.getCause() != null) {
                sb.append("Causa raíz : ")
                        .append(exception.getCause().getMessage()).append("\n");
            }
            sb.append("StackTrace :\n");
            for (final StackTraceElement el : exception.getStackTrace()) {
                sb.append("  at ").append(el.toString()).append("\n");
            }
        }

        sb.append(SEP_SECTION).append("\n");
        writeToFile(ERROR_LOG_FILE, sb.toString());
    }

    /**
     * Escribe el contenido en el archivo indicado, creándolo si no existe.
     * Agrega el contenido al final del archivo (append).
     *
     * @param filePath ruta del archivo de log
     * @param content  texto a escribir
     */
    private static void writeToFile(final String filePath, final String content) {
        try {
            final Path path = Paths.get(filePath);
            final BufferedWriter writer = Files.newBufferedWriter(
                    path,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            System.err.println("[GameLogger] No se pudo escribir en " + filePath
                    + ": " + e.getMessage());
        }
    }

    /**
     * Crea el directorio de logs si no existe.
     */
    private static void ensureLogsDir() {
        try {
            Files.createDirectories(Paths.get(LOGS_DIR));
        } catch (IOException e) {
            System.err.println("[GameLogger] No se pudo crear carpeta logs: "
                    + e.getMessage());
        }
    }

    /**
     * Retorna la marca de tiempo actual formateada.
     *
     * @return cadena con fecha y hora
     */
    private static String now() {
        return LocalDateTime.now().format(FMT);
    }
}