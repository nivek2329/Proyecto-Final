package main.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import main.domain.HardestGameException.*;


/**
 * Carga y valida la configuración de un nivel desde archivo de texto.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class GameConfiguration {

    private static final Logger LOGGER = GameLog.getLogger(GameConfiguration.class);
    private static final int MIN_TIME_SECONDS = 10;

    private int rows;
    private int cols;
    private int timeLimit;
    private Zone startZone;
    private Zone intermediateZone;
    private Zone finalZone;
    private final List<Coin>    coins;
    private final List<Enemy>   enemies;
    private final List<PowerUp> powerUps;
    private final List<int[]>   walls;

    /**
     * Crea una instancia de configuración vacía con listas inicializadas.
     */
    public GameConfiguration() {
        coins    = new ArrayList<>();
        enemies  = new ArrayList<>();
        powerUps = new ArrayList<>();
        walls    = new ArrayList<>();
    }

    /**
     * Carga la configuración desde un archivo de texto.
     *
     * @param filename ruta del archivo
     * @throws HardestGameException si ocurre un error lógico de carga o validación
     */
    public void load(final String filename) throws HardestGameException {
        try (BufferedReader br = Files.newBufferedReader(
                Paths.get(filename), StandardCharsets.UTF_8)) {
            String line = br.readLine();
            int lineNumber = 1;
            while (line != null) {
                final String trimmed = line.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                    try {
                        parseLine(trimmed.split("\\s+"), lineNumber);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        final String msg = "Línea " + lineNumber + " inválida: " + trimmed;
                        LOGGER.log(java.util.logging.Level.SEVERE, msg, e);
                        throw new InvalidLevelFormatException(msg, e);
                    }
                }
                line = br.readLine();
                lineNumber++;
            }
        } catch (NoSuchFileException e) {
            final String msg = "Nivel no encontrado: " + filename;
            LOGGER.log(java.util.logging.Level.SEVERE, msg, e);
            throw new LevelNotFoundException(msg, e);
        } catch (AccessDeniedException e) {
            final String msg = "Sin permisos para leer: " + filename;
            LOGGER.log(java.util.logging.Level.SEVERE, msg, e);
            throw new LevelAccessDeniedException(msg, e);
        } catch (IOException e) {
            final String msg = "Error de E/S al cargar: " + filename;
            LOGGER.log(java.util.logging.Level.SEVERE, msg, e);
            throw new LevelNotFoundException(msg, e);
        }
        validate();
    }

    /**
     * Preprocesa los tokens de una línea para su análisis sintáctico.
     *
     * @param parts      los tokens de la línea
     * @param lineNumber número de la línea procesada
     * @throws HardestGameException si la línea está vacía o es inválida
     */
    private void parseLine(final String[] parts, final int lineNumber)
            throws HardestGameException {
        if (parts.length == 0) {
            final String msg = "Línea " + lineNumber + " vacía tras tokenizar";
            LOGGER.severe(msg);
            throw new InvalidLevelFormatException(msg);
        }
        parseLineContent(parts, lineNumber);
    }

    /**
     * Procesa la directiva u objeto de la línea de configuración.
     *
     * @param parts      los tokens de la línea
     * @param lineNumber número de la línea procesada
     * @throws HardestGameException si el formato o token es inválido
     */
    private void parseLineContent(final String[] parts, final int lineNumber)
            throws HardestGameException {
        switch (parts[0]) {
            case "ROWS":
                rows = Integer.parseInt(parts[1]);
                break;
            case "COLS":
                cols = Integer.parseInt(parts[1]);
                break;
            case "TIME":
                timeLimit = Integer.parseInt(parts[1]);
                break;
            case "SAFE_START":
                startZone = parseZone(parts, Zone.Type.INITIAL);
                break;
            case "SAFE_INTER":
            case "SAFE_INTERMEDIATE":
                intermediateZone = parseZone(parts, Zone.Type.INTERMEDIATE);
                break;
            case "SAFE_FINAL":
                finalZone = parseZone(parts, Zone.Type.FINAL);
                break;
            case "COIN":
                parseCoin(parts);
                break;
            case "ENEMY":
                parseEnemy(parts);
                break;
            case "POWERUP":
                parsePowerUp(parts);
                break;
            case "LIFE":
                parsePowerUp(new String[]{"POWERUP", "LIFE", parts[1], parts[2]});
                break;
            case "BOMB":
                parsePowerUp(new String[]{"POWERUP", "BOMB", parts[1], parts[2]});
                break;
            case "WALL":
                walls.add(new int[]{
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2])});
                break;
            default:
                final String msg = "Token desconocido en línea " + lineNumber + ": " + parts[0];
                LOGGER.severe(msg);
                throw new InvalidLevelFormatException(msg);
        }
    }

    /**
     * Construye una zona a partir de los parámetros de la línea.
     *
     * @param parts tokens de la línea
     * @param type  tipo funcional de la zona
     * @return la zona instanciada
     */
    private Zone parseZone(final String[] parts, final Zone.Type type) {
        return new Zone(
                Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]),
                type);
    }

    /**
     * Agrega una moneda (amarilla o de skin) a partir de los parámetros.
     *
     * @param parts tokens de la línea de la moneda
     */
    private void parseCoin(final String[] parts) {
        if ("YELLOW".equals(parts[1])) {
            coins.add(new YellowCoin(
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])));
            return;
        }
        if ("SKIN".equals(parts[1]) && parts.length >= 5) {
            coins.add(new SkinCoin(
                    Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4]),
                    parts[2]));
        }
    }

    /**
     * Procesa la creación de un enemigo según su tipo e información de movimiento.
     *
     * @param parts tokens de la línea del enemigo
     * @throws InvalidMovementException si la configuración de movimiento no es válida
     */
    private void parseEnemy(final String[] parts) throws InvalidMovementException {
        if (parts.length < 4) {
            final String msg = "ENEMY requiere tipo, fila y columna";
            LOGGER.severe(msg);
            throw new InvalidMovementException(msg);
        }
        final String kind = parts[1].toUpperCase();
        switch (kind) {
            case "BASIC":
                parseBasicEnemy(parts);
                break;
            case "VERTICAL":
                enemies.add(new VerticalEnemy(
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
                break;
            case "ACCEL":
                parseAcceleratedEnemy(parts);
                break;
            case "PATROL":
                parsePatrolEnemy(parts);
                break;
            default:
                final String msg = "Tipo de enemigo desconocido: " + parts[1];
                LOGGER.severe(msg);
                throw new InvalidMovementException(msg);
        }
    }

    /**
     * Procesa y añade un enemigo básico a la lista.
     *
     * @param parts tokens de la línea del enemigo básico
     * @throws InvalidMovementException si la dirección o parámetros de movimiento son incorrectos
     */
    private void parseBasicEnemy(final String[] parts) throws InvalidMovementException {
        if (parts.length < 5) {
            final String msg = "ENEMY BASIC requiere fila, columna y dirección";
            LOGGER.severe(msg);
            throw new InvalidMovementException(msg);
        }
        final BasicEnemy.Direction dir = parseDirectionToken(parts[4]);
        int horizontalStep = 1;
        if (parts.length > 5) {
            if (dir == BasicEnemy.Direction.VERTICAL) {
                final String msg = "horizontalStep no aplica con dirección VERTICAL: " + parts[5];
                LOGGER.severe(msg);
                throw new InvalidMovementException(msg);
            }
            horizontalStep = parseHorizontalStep(parts[5]);
        }
        enemies.add(new BasicEnemy(
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]),
                dir,
                horizontalStep));
    }

    /**
     * Procesa y añade un enemigo acelerado a la lista.
     *
     * @param parts tokens de la línea del enemigo acelerado
     * @throws InvalidMovementException si los parámetros de dirección son inválidos
     */
    private void parseAcceleratedEnemy(final String[] parts) throws InvalidMovementException {
        if (parts.length < 5) {
            final String msg = "ENEMY ACCEL requiere fila, columna y dirección";
            LOGGER.severe(msg);
            throw new InvalidMovementException(msg);
        }
        final String dirToken = parts[4].toUpperCase();
        final boolean horizontal = "HORIZONTAL".equals(dirToken);
        if (!horizontal && !"VERTICAL".equals(dirToken)) {
            final String msg = "Dirección ACCEL inválida: " + parts[4];
            LOGGER.severe(msg);
            throw new InvalidMovementException(msg);
        }
        int horizontalStep = 1;
        if (parts.length > 5) {
            if (!horizontal) {
                final String msg = "horizontalStep no aplica con dirección VERTICAL: " + parts[5];
                LOGGER.severe(msg);
                throw new InvalidMovementException(msg);
            }
            horizontalStep = parseHorizontalStep(parts[5]);
        }
        enemies.add(new AcceleratedEnemy(
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]),
                horizontal,
                horizontalStep));
    }

    /**
     * Procesa y añade un enemigo de patrulla a la lista.
     *
     * @param parts tokens de la línea del enemigo de patrulla
     * @throws InvalidMovementException si el número de parámetros es incorrecto
     */
    private void parsePatrolEnemy(final String[] parts) throws InvalidMovementException {
        if (parts.length == 6) {
            final int startRow = Integer.parseInt(parts[2]);
            final int startCol = Integer.parseInt(parts[3]);
            final int zoneRows = Integer.parseInt(parts[4]);
            final int zoneCols = Integer.parseInt(parts[5]);
            enemies.add(new PatrolEnemy(startRow, startCol, startRow, startCol,
                    zoneRows, zoneCols));
            return;
        }
        if (parts.length >= 8) {
            enemies.add(new PatrolEnemy(
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4]),
                    Integer.parseInt(parts[5]),
                    Integer.parseInt(parts[6]),
                    Integer.parseInt(parts[7])));
            return;
        }
        final String msg = "ENEMY PATROL requiere fila col filas cols "
                + "o fila col zonaFila zonaCol filasZona colsZona";
        LOGGER.severe(msg);
        throw new InvalidMovementException(msg);
    }

    /**
     * Resuelve el token de dirección a un valor del enumerado Direction.
     *
     * @param token cadena que representa la dirección
     * @return la dirección resuelta
     * @throws InvalidMovementException si la cadena no coincide con ninguna dirección válida
     */
    private BasicEnemy.Direction parseDirectionToken(final String token)
            throws InvalidMovementException {
        if ("HORIZONTAL".equalsIgnoreCase(token)) {
            return BasicEnemy.Direction.HORIZONTAL;
        }
        if ("VERTICAL".equalsIgnoreCase(token)) {
            return BasicEnemy.Direction.VERTICAL;
        }
        final String msg = "Dirección de enemigo inválida: " + token;
        LOGGER.severe(msg);
        throw new InvalidMovementException(msg);
    }

    /**
     * Resuelve el sentido de movimiento horizontal.
     *
     * @param token cadena que representa el sentido (LEFT o RIGHT)
     * @return entero que representa la dirección (1 para izquierda, -1 para derecha)
     * @throws InvalidMovementException si el token es desconocido
     */
    private int parseHorizontalStep(final String token) throws InvalidMovementException {
        if ("RIGHT".equalsIgnoreCase(token)) {
            return -1;
        }
        if ("LEFT".equalsIgnoreCase(token)) {
            return 1;
        }
        final String msg = "Sentido horizontal inválido: " + token;
        LOGGER.severe(msg);
        throw new InvalidMovementException(msg);
    }

    /**
     * Procesa y agrega un power-up (bomba o fuente de vida) a la lista.
     *
     * @param parts tokens de la línea del power-up
     */
    private void parsePowerUp(final String[] parts) {
        if ("BOMB".equals(parts[1])) {
            powerUps.add(new Bomb(
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])));
        } else if ("LIFE".equals(parts[1])) {
            powerUps.add(new LifeSource(
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])));
        }
    }

    /**
     * Valida que todos los componentes cargados estén dentro de los límites del tablero
     * y no haya colisiones lógicas como monedas en muros.
     *
     * @throws HardestGameException si alguna validación de límites o lógica falla
     */
    private void validate() throws HardestGameException {
        if (rows <= 0 || cols <= 0) {
            final String msg = "Dimensiones inválidas: " + rows + "x" + cols;
            LOGGER.severe(msg);
            throw new MissingEntityException(msg);
        }
        if (startZone == null) {
            final String msg = "Falta SAFE_START en la configuración";
            LOGGER.severe(msg);
            throw new MissingEntityException(msg);
        }
        if (finalZone == null) {
            final String msg = "Falta SAFE_FINAL en la configuración";
            LOGGER.severe(msg);
            throw new MissingEntityException(msg);
        }
        if (timeLimit < MIN_TIME_SECONDS) {
            final String msg = "Tiempo " + timeLimit + "s menor al mínimo (" + MIN_TIME_SECONDS + "s)";
            LOGGER.severe(msg);
            throw new InsufficientTimeException(msg);
        }
        validateZoneInBounds(startZone, "SAFE_START");
        if (intermediateZone != null) {
            validateZoneInBounds(intermediateZone, "SAFE_INTERMEDIATE");
        }
        validateZoneInBounds(finalZone, "SAFE_FINAL");
        for (final int[] wall : walls) {
            validateCellInBounds(wall[0], wall[1], "WALL");
        }
        for (final Coin coin : coins) {
            validateCellInBounds(coin.getRow(), coin.getCol(), "COIN");
            validateNotOnWall(coin.getRow(), coin.getCol(), "COIN");
        }
        for (final Enemy enemy : enemies) {
            validateCellInBounds(enemy.getRow(), enemy.getCol(), "ENEMY");
            validateNotOnWall(enemy.getRow(), enemy.getCol(), "ENEMY");
        }
        for (final PowerUp powerUp : powerUps) {
            validateCellInBounds(powerUp.getRow(), powerUp.getCol(), "POWERUP");
            validateNotOnWall(powerUp.getRow(), powerUp.getCol(), "POWERUP");
        }
    }

    /**
     * Valida que una entidad no se coloque sobre un muro del tablero.
     *
     * @param row   fila a evaluar
     * @param col   columna a evaluar
     * @param label identificador de la entidad para el mensaje
     * @throws InvalidLevelFormatException si la posición coincide con un muro
     */
    private void validateNotOnWall(final int row, final int col, final String label)
            throws InvalidLevelFormatException {
        if (isWallAt(row, col)) {
            final String msg = label + " colocado sobre un muro en (" + row + "," + col + ")";
            LOGGER.severe(msg);
            throw new InvalidLevelFormatException(msg);
        }
    }

    /**
     * Consulta si hay un muro definido en una celda del tablero.
     *
     * @param row fila a evaluar
     * @param col columna a evaluar
     * @return true si la celda contiene un muro
     */
    private boolean isWallAt(final int row, final int col) {
        for (final int[] wall : walls) {
            if (wall[0] == row && wall[1] == col) {
                return true;
            }
        }
        return false;
    }

    /**
     * Valida que la zona esté completamente contenida en los límites del tablero.
     *
     * @param zone  la zona a evaluar
     * @param label identificador de la zona para el mensaje
     * @throws OutOfBoundsException si alguna parte de la zona está fuera del tablero
     */
    private void validateZoneInBounds(final Zone zone, final String label)
            throws OutOfBoundsException {
        for (int r = zone.getRow(); r < zone.getRow() + zone.getRows(); r++) {
            for (int c = zone.getCol(); c < zone.getCol() + zone.getCols(); c++) {
                if (!inBoard(r, c)) {
                    final String msg = label + " fuera del tablero en (" + r + "," + c + ")";
                    LOGGER.severe(msg);
                    throw new OutOfBoundsException(msg);
                }
            }
        }
    }

    /**
     * Valida que una celda esté contenida en los límites del tablero.
     *
     * @param row   fila a evaluar
     * @param col   columna a evaluar
     * @param label identificador para el mensaje de error
     * @throws OutOfBoundsException si la celda queda fuera del tablero
     */
    private void validateCellInBounds(final int row, final int col, final String label)
            throws OutOfBoundsException {
        if (!inBoard(row, col)) {
            final String msg = label + " fuera del tablero en (" + row + "," + col + ")";
            LOGGER.severe(msg);
            throw new OutOfBoundsException(msg);
        }
    }

    /**
     * Consulta si la celda se encuentra dentro de los límites físicos del tablero.
     *
     * @param row fila
     * @param col columna
     * @return true si está dentro de los límites
     */
    private boolean inBoard(final int row, final int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * Retorna el número de filas del nivel.
     *
     * @return filas
     */
    public int getRows() { return rows; }

    /**
     * Retorna el número de columnas del nivel.
     *
     * @return columnas
     */
    public int getCols() { return cols; }

    /**
     * Retorna el límite de tiempo del nivel.
     *
     * @return límite de tiempo en segundos
     */
    public int getTimeLimit() { return timeLimit; }

    /**
     * Retorna la zona inicial segura.
     *
     * @return zona inicial
     */
    public Zone getStartZone() { return startZone; }

    /**
     * Retorna la zona intermedia segura (checkpoint).
     *
     * @return zona intermedia o null si no tiene
     */
    public Zone getIntermediateZone() { return intermediateZone; }

    /**
     * Retorna la zona final segura de meta.
     *
     * @return zona final
     */
    public Zone getFinalZone() { return finalZone; }

    /**
     * Retorna una copia de la lista de monedas cargadas.
     *
     * @return lista de monedas
     */
    public List<Coin> getCoins() { return new ArrayList<>(coins); }

    /**
     * Retorna una copia de la lista de enemigos cargados.
     *
     * @return lista de enemigos
     */
    public List<Enemy> getEnemies() { return new ArrayList<>(enemies); }

    /**
     * Retorna una copia de la lista de power-ups cargados.
     *
     * @return lista de power-ups
     */
    public List<PowerUp> getPowerUps() { return new ArrayList<>(powerUps); }

    /**
     * Retorna una copia de la lista de coordenadas de los muros.
     *
     * @return lista de muros
     */
    public List<int[]> getWalls() { return new ArrayList<>(walls); }
}
