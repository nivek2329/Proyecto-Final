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

/**
 * Carga y valida la configuración de un nivel desde archivo de texto.
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
     * @throws LevelNotFoundException      si el archivo no existe
     * @throws LevelAccessDeniedException  si no hay permisos de lectura
     * @throws InvalidLevelFormatException si el contenido es inválido
     * @throws MissingEntityException      si faltan entidades obligatorias
     * @throws InsufficientTimeException   si el tiempo es menor al mínimo
     * @throws OutOfBoundsException        si hay entidades fuera del tablero
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

    private void parseLine(final String[] parts, final int lineNumber)
            throws HardestGameException {
        if (parts.length == 0) {
            final String msg = "Línea " + lineNumber + " vacía tras tokenizar";
            LOGGER.severe(msg);
            throw new InvalidLevelFormatException(msg);
        }
        parseLineContent(parts, lineNumber);
    }

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

    private Zone parseZone(final String[] parts, final Zone.Type type) {
        return new Zone(
                Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]),
                type);
    }

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

    private void validateNotOnWall(final int row, final int col, final String label)
            throws InvalidLevelFormatException {
        if (isWallAt(row, col)) {
            final String msg = label + " colocado sobre un muro en (" + row + "," + col + ")";
            LOGGER.severe(msg);
            throw new InvalidLevelFormatException(msg);
        }
    }

    private boolean isWallAt(final int row, final int col) {
        for (final int[] wall : walls) {
            if (wall[0] == row && wall[1] == col) {
                return true;
            }
        }
        return false;
    }

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

    private void validateCellInBounds(final int row, final int col, final String label)
            throws OutOfBoundsException {
        if (!inBoard(row, col)) {
            final String msg = label + " fuera del tablero en (" + row + "," + col + ")";
            LOGGER.severe(msg);
            throw new OutOfBoundsException(msg);
        }
    }

    private boolean inBoard(final int row, final int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getTimeLimit() { return timeLimit; }
    public Zone getStartZone() { return startZone; }
    public Zone getIntermediateZone() { return intermediateZone; }
    public Zone getFinalZone() { return finalZone; }
    public List<Coin> getCoins() { return new ArrayList<>(coins); }
    public List<Enemy> getEnemies() { return new ArrayList<>(enemies); }
    public List<PowerUp> getPowerUps() { return new ArrayList<>(powerUps); }
    public List<int[]> getWalls() { return new ArrayList<>(walls); }
}
