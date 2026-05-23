package main.domain;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import main.domain.HardestGameException;

/**
 * Servicio de carga y validación de archivos de nivel (.txt).
 * Lee las dimensiones, zonas (SAFE_START, SAFE_FINAL, SAFE_INTERMEDIATE),
 * muros, monedas, enemigos y power-ups del archivo de texto y construye
 * el estado inicial del dominio. Aplica validaciones semánticas estrictas.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public final class GameConfiguration {

    private static final Logger LOGGER = GameLog.getLogger(GameConfiguration.class);
    private static final int MIN_TIME_SECONDS = 10;

    private final Random random = new Random();

    private int rows;
    private int cols;
    private int timeLimit;
    private Zone startZone;
    private Zone intermediateZone;
    private Zone finalZone;

    private final List<Coin> coins = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final List<int[]> walls = new ArrayList<>();

    /**
     * Carga y valida la configuración de nivel desde un archivo de texto.
     *
     * @param filename ruta del archivo de configuración del nivel
     * @throws HardestGameException si ocurre cualquier error de lectura, permisos o inconsistencia
     */
    public void load(final String filename) throws HardestGameException {
        final Path path = Paths.get(filename);
        try {
            final List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            int lineNum = 0;
            for (final String line : lines) {
                lineNum++;
                final String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                final String[] parts = trimmed.split("\\s+");
                try {
                    parseLine(parts);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    final String msg = "Error de formato numérico en fila " + lineNum;
                    LOGGER.log(java.util.logging.Level.SEVERE, msg, e);
                    throw new HardestGameException(HardestGameException.INVALID_LEVEL_FORMAT, e);
                }
            }
            validate();
        } catch (java.nio.file.NoSuchFileException e) {
            final String msg = "Archivo no encontrado: " + filename;
            LOGGER.log(java.util.logging.Level.SEVERE, msg, e);
            throw new HardestGameException(HardestGameException.LEVEL_NOT_FOUND, e);
        } catch (AccessDeniedException e) {
            final String msg = "Acceso denegado al archivo: " + filename;
            LOGGER.log(java.util.logging.Level.SEVERE, msg, e);
            throw new HardestGameException(HardestGameException.LEVEL_ACCESS_DENIED, e);
        } catch (IOException e) {
            final String msg = "Error de E/S al leer " + filename;
            LOGGER.log(java.util.logging.Level.SEVERE, msg, e);
            throw new HardestGameException(HardestGameException.LEVEL_NOT_FOUND, e);
        }
    }

    /**
     * Procesa una línea de comando de la configuración.
     *
     * @param parts partes de la línea parseada
     * @throws HardestGameException si la línea tiene formato inválido
     */
    private void parseLine(final String[] parts) throws HardestGameException {
        final String command = parts[0].toUpperCase();
        switch (command) {
            case "ROWS":
            case "COLS":
            case "TIME":
                parseDimension(parts);
                break;
            case "SAFE_START":
            case "SAFE_INTERMEDIATE":
            case "SAFE_FINAL":
                parseZoneLine(parts);
                break;
            case "WALL":
                walls.add(new int[]{
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2])});
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
            default:
                if (LOGGER.isLoggable(java.util.logging.Level.WARNING)) {
                    LOGGER.warning("Comando desconocido en archivo: " + command);
                }
        }
    }

    /**
     * Procesa una línea que define una dimensión (ROWS, COLS, TIME).
     *
     * @param parts partes de la línea
     * @throws HardestGameException si faltan valores numéricos
     */
    private void parseDimension(final String[] parts) throws HardestGameException {
        if (parts.length < 2) {
            final String msg = "ROWS o COLS requiere un valor numérico";
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.INVALID_LEVEL_FORMAT);
        }
        final String command = parts[0].toUpperCase();
        final int value = Integer.parseInt(parts[1]);
        if ("ROWS".equals(command)) {
            rows = value;
        } else if ("COLS".equals(command)) {
            cols = value;
        } else if ("TIME".equals(command)) {
            timeLimit = value;
        }
    }

    /**
     * Procesa una línea que define una zona segura (SAFE_START, SAFE_INTERMEDIATE, SAFE_FINAL).
     *
     * @param parts partes de la línea
     * @throws HardestGameException si la zona no cuenta con los parámetros requeridos
     */
    private void parseZoneLine(final String[] parts) throws HardestGameException {
        if (parts.length < 5) {
            final String msg = "ZONA requiere fila, col, filas y cols";
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.INVALID_LEVEL_FORMAT);
        }
        final String command = parts[0].toUpperCase();
        if ("SAFE_START".equals(command)) {
            startZone = parseZone(parts, Zone.Type.INITIAL);
        } else if ("SAFE_INTERMEDIATE".equals(command)) {
            intermediateZone = parseZone(parts, Zone.Type.INTERMEDIATE);
        } else if ("SAFE_FINAL".equals(command)) {
            finalZone = parseZone(parts, Zone.Type.FINAL);
        }
    }

    /**
     * Construye un objeto Zone a partir de los datos leídos.
     *
     * @param parts partes de la línea conteniendo las coordenadas
     * @param type tipo de la zona segura
     * @return nueva zona creada
     */
    private Zone parseZone(final String[] parts, final Zone.Type type) {
        return new Zone(
                Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]),
                type);
    }

    /**
     * Parsea una moneda a partir de la línea leída.
     *
     * @param parts partes de la línea de configuración
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
     * Parsea y crea un enemigo según su tipo e información provista.
     *
     * @param parts partes de la línea de configuración
     * @throws HardestGameException si el formato de movimiento es inválido
     */
    private void parseEnemy(final String[] parts) throws HardestGameException {
        if (parts.length < 4) {
            final String msg = "ENEMY requiere tipo, fila y columna";
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.INVALID_MOVEMENT);
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
            case "SPINNER":
                parseSpinnerEnemy(parts);
                break;
            default:
                final String msg = "Tipo de enemigo desconocido: " + parts[1];
                LOGGER.severe(msg);
                throw new HardestGameException(HardestGameException.INVALID_MOVEMENT);
        }
    }

    /**
     * Parsea un enemigo tipo Spinner (giratorio).
     *
     * @param parts partes de la línea conteniendo parámetros
     * @throws HardestGameException si faltan argumentos necesarios
     */
    private void parseSpinnerEnemy(final String[] parts) throws HardestGameException {
        if (parts.length < 8) {
            final String msg = "ENEMY SPINNER requiere filaCentro colCentro radio indexInicial horario velocidad";
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.INVALID_MOVEMENT);
        }
        enemies.add(new SpinnerEnemy(
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]),
                Integer.parseInt(parts[4]),
                Integer.parseInt(parts[5]),
                Boolean.parseBoolean(parts[6]),
                Integer.parseInt(parts[7])));
    }

    /**
     * Parsea un enemigo tipo básico.
     *
     * @param parts partes de la línea conteniendo parámetros
     * @throws HardestGameException si faltan parámetros o si la configuración es incorrecta
     */
    private void parseBasicEnemy(final String[] parts) throws HardestGameException {
        if (parts.length < 5) {
            final String msg = "ENEMY BASIC requiere fila, columna y dirección";
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.INVALID_MOVEMENT);
        }
        final BasicEnemy.Direction dir = parseDirectionToken(parts[4]);
        int horizontalStep = 1;
        if (parts.length > 5) {
            if (dir == BasicEnemy.Direction.VERTICAL) {
                final String msg = "horizontalStep no aplica con dirección VERTICAL: " + parts[5];
                LOGGER.severe(msg);
                throw new HardestGameException(HardestGameException.INVALID_MOVEMENT);
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
     * Parsea un enemigo acelerado.
     *
     * @param parts partes de la línea conteniendo parámetros
     * @throws HardestGameException si hay inconsistencias o faltan datos
     */
    private void parseAcceleratedEnemy(final String[] parts) throws HardestGameException {
        if (parts.length < 5) {
            final String msg = "ENEMY ACCEL requiere fila, columna y dirección";
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.INVALID_MOVEMENT);
        }
        final String dirToken = parts[4].toUpperCase();
        final boolean horizontal = "HORIZONTAL".equals(dirToken);
        if (!horizontal && !"VERTICAL".equals(dirToken)) {
            final String msg = "Dirección ACCEL inválida: " + parts[4];
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.INVALID_MOVEMENT);
        }
        int horizontalStep = 1;
        if (parts.length > 5) {
            if (!horizontal) {
                final String msg = "horizontalStep no aplica con dirección VERTICAL: " + parts[5];
                LOGGER.severe(msg);
                throw new HardestGameException(HardestGameException.INVALID_MOVEMENT);
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
     * Parsea un enemigo patrulla.
     *
     * @param parts partes de la línea conteniendo parámetros
     * @throws HardestGameException si el formato de patrulla no es correcto
     */
    private void parsePatrolEnemy(final String[] parts) throws HardestGameException {
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
        throw new HardestGameException(HardestGameException.INVALID_MOVEMENT);
    }

    /**
     * Parsea la dirección especificada por un token a la enumeración correspondiente.
     *
     * @param token cadena de texto con la dirección ("HORIZONTAL" o "VERTICAL")
     * @return enumeración representativa
     * @throws HardestGameException si el token no es válido
     */
    private BasicEnemy.Direction parseDirectionToken(final String token)
            throws HardestGameException {
        if ("HORIZONTAL".equalsIgnoreCase(token)) {
            return BasicEnemy.Direction.HORIZONTAL;
        }
        if ("VERTICAL".equalsIgnoreCase(token)) {
            return BasicEnemy.Direction.VERTICAL;
        }
        final String msg = "Dirección de enemigo inválida: " + token;
        LOGGER.severe(msg);
        throw new HardestGameException(HardestGameException.INVALID_MOVEMENT);
    }

    /**
     * Parsea el sentido de paso horizontal especificado por un token.
     *
     * @param token cadena conteniendo la orientación del movimiento ("LEFT" o "RIGHT")
     * @return paso representado por un entero (1 o -1)
     * @throws HardestGameException si la dirección es inválida
     */
    private int parseHorizontalStep(final String token) throws HardestGameException {
        if ("RIGHT".equalsIgnoreCase(token)) {
            return -1;
        }
        if ("LEFT".equalsIgnoreCase(token)) {
            return 1;
        }
        final String msg = "Sentido horizontal inválido: " + token;
        LOGGER.severe(msg);
        throw new HardestGameException(HardestGameException.INVALID_MOVEMENT);
    }

    /**
     * Parsea un objeto PowerUp de la línea.
     *
     * @param parts partes conteniendo datos del power-up
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
     * Valida semánticamente toda la configuración del nivel una vez cargada.
     *
     * @throws HardestGameException si hay inconsistencias o errores en la carga
     */
    private void validate() throws HardestGameException {
        if (rows <= 0 || cols <= 0) {
            final String msg = "Dimensiones inválidas: " + rows + "x" + cols;
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.MISSING_ENTITY);
        }
        if (startZone == null) {
            final String msg = "Falta SAFE_START en la configuración";
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.MISSING_ENTITY);
        }
        if (finalZone == null) {
            final String msg = "Falta SAFE_FINAL en la configuración";
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.MISSING_ENTITY);
        }
        if (timeLimit < MIN_TIME_SECONDS) {
            final String msg = "Tiempo " + timeLimit + "s menor al mínimo (" + MIN_TIME_SECONDS + "s)";
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.INSUFFICIENT_TIME);
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
     * Valida que un elemento no se superponga con un muro.
     *
     * @param row fila del elemento
     * @param col columna del elemento
     * @param label etiqueta identificadora del tipo de elemento
     * @throws HardestGameException si el elemento está sobre un muro
     */
    private void validateNotOnWall(final int row, final int col, final String label)
            throws HardestGameException {
        if (isWallAt(row, col)) {
            final String msg = label + " colocado sobre un muro en (" + row + "," + col + ")";
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.INVALID_LEVEL_FORMAT);
        }
    }

    /**
     * Verifica si existe un muro en las coordenadas indicadas.
     *
     * @param row fila a verificar
     * @param col columna a verificar
     * @return true si hay un muro; false en caso contrario
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
     * Valida que toda la zona segura esté dentro de los límites del tablero.
     *
     * @param zone zona a verificar
     * @param label nombre descriptivo de la zona
     * @throws HardestGameException si parte de la zona está fuera del tablero
     */
    private void validateZoneInBounds(final Zone zone, final String label)
            throws HardestGameException {
        for (int r = zone.getRow(); r < zone.getRow() + zone.getRows(); r++) {
            for (int c = zone.getCol(); c < zone.getCol() + zone.getCols(); c++) {
                if (!inBoard(r, c)) {
                    final String msg = label + " fuera del tablero en (" + r + "," + c + ")";
                    LOGGER.severe(msg);
                    throw new HardestGameException(HardestGameException.OUT_OF_BOUNDS);
                }
            }
        }
    }

    /**
     * Valida que una celda específica esté dentro de los límites del tablero.
     *
     * @param row fila a verificar
     * @param col columna a verificar
     * @param label nombre descriptivo del elemento
     * @throws HardestGameException si está fuera del tablero
     */
    private void validateCellInBounds(final int row, final int col, final String label)
            throws HardestGameException {
        if (!inBoard(row, col)) {
            final String msg = label + " fuera del tablero en (" + row + "," + col + ")";
            LOGGER.severe(msg);
            throw new HardestGameException(HardestGameException.OUT_OF_BOUNDS);
        }
    }

    /**
     * Determina si la celda se encuentra dentro del tablero.
     *
     * @param row fila a verificar
     * @param col columna a verificar
     * @return true si está dentro del tablero; false en caso contrario
     */
    private boolean inBoard(final int row, final int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * Obtiene el número total de filas del nivel.
     *
     * @return número de filas
     */
    public int getRows() { 
        return rows; 
    }

    /**
     * Obtiene el número total de columnas del nivel.
     *
     * @return número de columnas
     */
    public int getCols() { 
        return cols; 
    }

    /**
     * Obtiene el límite de tiempo del nivel en segundos.
     *
     * @return límite de tiempo
     */
    public int getTimeLimit() { 
        return timeLimit; 
    }

    /**
     * Obtiene la zona inicial segura.
     *
     * @return zona de inicio
     */
    public Zone getStartZone() { 
        return startZone; 
    }

    /**
     * Obtiene la zona intermedia segura (puede ser nula).
     *
     * @return zona intermedia
     */
    public Zone getIntermediateZone() { 
        return intermediateZone; 
    }

    /**
     * Obtiene la zona final segura.
     *
     * @return zona final
     */
    public Zone getFinalZone() { 
        return finalZone; 
    }

    /**
     * Obtiene una copia de la lista de monedas del nivel.
     *
     * @return lista de monedas
     */
    public List<Coin> getCoins() { 
        return new ArrayList<>(coins); 
    }

    /**
     * Obtiene una copia de la lista de enemigos del nivel.
     *
     * @return lista de enemigos
     */
    public List<Enemy> getEnemies() { 
        return new ArrayList<>(enemies); 
    }

    /**
     * Obtiene una copia de la lista de power-ups del nivel.
     *
     * @return lista de power-ups
     */
    public List<PowerUp> getPowerUps() { 
        return new ArrayList<>(powerUps); 
    }

    /**
     * Obtiene una copia de la lista de muros del nivel.
     *
     * @return lista de coordenadas de muros [fila, columna]
     */
    public List<int[]> getWalls() { 
        return new ArrayList<>(walls); 
    }
}
