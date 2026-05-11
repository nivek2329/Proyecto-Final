package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de cargar y almacenar la configuración de un nivel del juego
 * a partir de un archivo de texto.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class GameConfiguration {

    private int rows;
    private int cols;
    private int timeLimit;
    private Zone startZone;
    private Zone intermediateZone;
    private Zone finalZone;
    private final List<Coin>  coins;
    private final List<Enemy> enemies;
    private final List<int[]> walls;

    /**
     * Crea una configuración vacía lista para ser cargada.
     */
    public GameConfiguration() {
        coins    = new ArrayList<>();
        enemies  = new ArrayList<>();
        walls    = new ArrayList<>();
    }

    /**
     * Carga la configuración desde un archivo de texto.
     *
     * @param filename ruta del archivo
     * @throws HardestGameException si el archivo no se puede leer o es inválido
     */
    public void load(final String filename) throws HardestGameException {
        try (BufferedReader br = Files.newBufferedReader(
                Paths.get(filename), StandardCharsets.UTF_8)) {
            String line = br.readLine();
            while (line != null) {
                final String trimmed = line.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                    parseLine(trimmed.split("\s+"));
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            throw new HardestGameException("No se pudo cargar: " + filename, e);
        }
        validate();
    }

    /**
     * Procesa una línea del archivo de configuración y actualiza el estado interno.
     *
     * @param parts elementos de la línea separados por espacios
     * @throws HardestGameException si la línea contiene datos inválidos
     */
    private void parseLine(final String[] parts) throws HardestGameException {
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
                startZone = new Zone(
                        Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]), Integer.parseInt(parts[4]),
                        Zone.Type.INITIAL);
                break;
            case "SAFE_INTER":
            case "SAFE_INTERMEDIATE":
                intermediateZone = new Zone(
                        Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]), Integer.parseInt(parts[4]),
                        Zone.Type.INTERMEDIATE);
                break;
            case "SAFE_FINAL":
                finalZone = new Zone(
                        Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]), Integer.parseInt(parts[4]),
                        Zone.Type.FINAL);
                break;
            case "COIN":
                parseCoin(parts);
                break;
            case "ENEMY":
                parseEnemy(parts);
                break;
            case "WALL":
                walls.add(new int[]{
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2])});
                break;
            default:
                break;
        }
    }

    /**
     * Crea una moneda a partir de los parámetros de configuración.
     *
     * @param parts elementos de la línea COIN separados por espacios
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
     * Crea un enemigo a partir de los parámetros de configuración.
     *
     * @param parts elementos de la línea ENEMY separados por espacios
     */
    private void parseEnemy(final String[] parts) {
        if (!"BASIC".equals(parts[1])) {
            return;
        }
        final BasicEnemy.Direction dir = "HORIZONTAL".equals(parts[4])
                ? BasicEnemy.Direction.HORIZONTAL
                : BasicEnemy.Direction.VERTICAL;
        int horizontalStep = 1;
        if (dir == BasicEnemy.Direction.HORIZONTAL && parts.length > 5
                && "RIGHT".equalsIgnoreCase(parts[5])) {
            horizontalStep = -1;
        }
        enemies.add(new BasicEnemy(
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]),
                dir,
                horizontalStep));
    }

    /**
     * Valida que los parámetros mínimos obligatorios estén presentes.
     *
     * @throws HardestGameException si faltan datos obligatorios o son inválidos
     */
    private void validate() throws HardestGameException {
        if (rows <= 0 || cols <= 0) {
            throw new HardestGameException("Dimensiones del tablero inválidas");
        }
        if (startZone == null) {
            throw new HardestGameException("Falta SAFE_START en la configuración");
        }
        if (finalZone == null) {
            throw new HardestGameException("Falta SAFE_FINAL en la configuración");
        }
        if (timeLimit <= 0) {
            throw new HardestGameException("Tiempo límite inválido");
        }
    }

    /**
     * Retorna el número de filas del tablero.
     *
     * @return número de filas
     */
    public int getRows() { 
        return rows;
    }

    /**
     * Retorna el número de columnas del tablero.
     *
     * @return número de columnas
     */
    public int getCols() { 
        return cols;             
    }

    /**
     * Retorna el tiempo límite del nivel en segundos.
     *
     * @return tiempo límite
     */
    public int getTimeLimit() { 
        return timeLimit;        
    }

    /**
     * Retorna la zona segura inicial del nivel.
     *
     * @return zona de inicio
     */
    public Zone getStartZone() { 
        return startZone;        
    }

    /**
     * Retorna la zona segura intermedia del nivel.
     *
     * @return zona intermedia, puede ser null si no está definida
     */
    public Zone getIntermediateZone() { 
        return intermediateZone; 
    }

    /**
     * Retorna la zona segura final del nivel.
     *
     * @return zona final
     */
    public Zone getFinalZone() { 
        return finalZone;        
    }

    /**
     * Retorna la lista de monedas del nivel.
     *
     * @return lista de monedas
     */
    public List<Coin> getCoins() { 
        return new ArrayList<>(coins);   
    }

    /**
     * Retorna la lista de enemigos del nivel.
     *
     * @return lista de enemigos
     */
    public List<Enemy> getEnemies() { 
        return new ArrayList<>(enemies); 
    }

    /**
     * Retorna la lista de coordenadas de muros del nivel.
     *
     * @return lista de muros
     */
    public List<int[]> getWalls() { 
        return new ArrayList<>(walls);   
    }
}