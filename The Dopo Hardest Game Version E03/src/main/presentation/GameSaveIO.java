package main.presentation;

import main.domain.GameSnapshot;
import main.domain.GameSnapshot.CoinSnapshot;
import main.domain.GameSnapshot.EnemySnapshot;
import main.domain.GameSnapshot.PlayerSnapshot;
import main.domain.GameSnapshot.PowerUpSnapshot;
import main.domain.HardestGame;
import main.domain.HardestGame.State;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Lectura y escritura de partidas guardadas en formato texto.
 */
public final class GameSaveIO {

  private static final int FORMAT_VERSION = 1;

  private GameSaveIO() {
  }

  /**
   * Datos completos de una partida guardada (menú + estado).
   */
  public static final class SavedGameData {
    private final GameSetup setup;
    private final GameSnapshot snapshot;

    public SavedGameData(final GameSetup setup, final GameSnapshot snapshot) {
      this.setup = setup;
      this.snapshot = snapshot;
    }

    public GameSetup getSetup() {
      return setup;
    }

    public GameSnapshot getSnapshot() {
      return snapshot;
    }
  }

  /**
   * Guarda la configuración de menú y el estado actual del juego.
   *
   * @param game  instancia del juego
   * @param setup configuración de la partida
   * @param path  ruta del archivo de guardado
   */
  public static void save(final HardestGame game, final GameSetup setup, final Path file)
      throws IOException {
    final Path parent = file.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    final GameSnapshot snapshot = game.captureSnapshot();
    try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
      w.write("VERSION " + FORMAT_VERSION);
      w.newLine();
      w.write("LEVEL " + setup.getLevelPath());
      w.newLine();
      w.write("MODALIDAD " + setup.getModalidad());
      w.newLine();
      w.write("SKIN1 " + setup.getSkin1());
      w.newLine();
      w.write("SKIN2 " + setup.getSkin2());
      w.newLine();
      w.write("BORDE1 " + colorToName(setup.getBorder1()));
      w.newLine();
      w.write("BORDE2 " + colorToName(setup.getBorder2()));
      w.newLine();
      w.write("MAQUINA " + setup.getMaquina());
      w.newLine();
      writeSnapshot(w, snapshot);
    }
  }

  /**
   * Carga una partida desde disco.
   *
   * @param path ruta del archivo
   * @return datos de menú y estado
   */
  public static SavedGameData load(final Path file) throws IOException {
    if (!Files.isRegularFile(file)) {
      throw new IOException("No se encontró el archivo: " + file);
    }
    String level = null;
    String modalidad = null;
    String skin1 = null;
    String skin2 = null;
    String borde1 = "Negro";
    String borde2 = "Negro";
    String maquina = "Máquina aleatoria";
    GameSnapshot snapshot = null;
    try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
      String line;
      while ((line = r.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) {
          continue;
        }
        if (line.startsWith("VERSION ")) {
          final int ver = Integer.parseInt(line.substring(8).trim());
          if (ver != FORMAT_VERSION) {
            throw new IOException("Versión de guardado no compatible: " + ver);
          }
        } else if (line.startsWith("LEVEL ")) {
          level = line.substring(6).trim();
        } else if (line.startsWith("MODALIDAD ")) {
          modalidad = line.substring(10).trim();
        } else if (line.startsWith("SKIN1 ")) {
          skin1 = line.substring(6).trim();
        } else if (line.startsWith("SKIN2 ")) {
          skin2 = line.substring(6).trim();
        } else if (line.startsWith("BORDE1 ")) {
          borde1 = line.substring(7).trim();
        } else if (line.startsWith("BORDE2 ")) {
          borde2 = line.substring(7).trim();
        } else if (line.startsWith("MAQUINA ")) {
          maquina = line.substring(8).trim();
        } else if (line.startsWith("STATE ")) {
          snapshot = readSnapshotUntilEnd(line, r);
        }
      }
    }
    if (level == null || modalidad == null || skin1 == null || skin2 == null || snapshot == null) {
      throw new IOException("Archivo de guardado incompleto o corrupto");
    }
    final GameSetup setup = new GameSetup(
        level, modalidad, skin1, skin2,
        nameToColor(borde1), nameToColor(borde2), maquina);
    return new SavedGameData(setup, snapshot);
  }

  private static void writeSnapshot(final BufferedWriter w, final GameSnapshot s)
      throws IOException {
    w.write("STATE " + s.getState().name());
    w.newLine();
    w.write("TIME " + s.getTimeRemaining());
    w.newLine();
    w.write("COINS_COLLECTED " + s.getCoinsCollected());
    w.newLine();
    w.write("CHECKPOINT " + s.isIntermediateCheckpointReached());
    w.newLine();
    w.write("RESPAWN " + s.getRespawnZoneKind());
    w.newLine();
    w.write("PLAYER_SKIN " + s.getPlayerSkin());
    w.newLine();
    w.write("PLAYER2_SKIN " + s.getPlayer2Skin());
    w.newLine();
    w.write("ORIGINAL_SKIN " + s.getOriginalPlayerSkin());
    w.newLine();
    w.write("ORIGINAL2_SKIN " + s.getOriginalPlayer2Skin());
    w.newLine();
    w.write("TWO_PLAYER " + s.isTwoPlayerMode());
    w.newLine();
    w.write("WINNER " + (s.getWinnerName() == null ? "" : s.getWinnerName()));
    w.newLine();
    writePlayer(w, "PLAYER1", s.getPlayer1());
    if (s.getPlayer2() != null) {
      writePlayer(w, "PLAYER2", s.getPlayer2());
    } else {
      w.write("PLAYER2 none");
      w.newLine();
    }
    for (int i = 0; i < s.getCoins().size(); i++) {
      final CoinSnapshot c = s.getCoins().get(i);
      w.write("COIN " + i + " " + c.isCollected() + " " + c.isPermanent());
      w.newLine();
    }
    for (int i = 0; i < s.getEnemies().size(); i++) {
      final EnemySnapshot e = s.getEnemies().get(i);
      w.write("ENEMY " + i + " " + e.getRow() + " " + e.getCol() + " "
          + e.getDeltaRow() + " " + e.getDeltaCol() + " " + e.getTickCount());
      w.newLine();
    }
    for (int i = 0; i < s.getPowerUps().size(); i++) {
      final PowerUpSnapshot p = s.getPowerUps().get(i);
      w.write("POWERUP " + i + " " + p.getType() + " "
          + p.isConsumed() + " " + p.isPermanentlyUsed());
      w.newLine();
    }
  }

  private static void writePlayer(final BufferedWriter w, final String tag,
                                  final PlayerSnapshot p) throws IOException {
    w.write(tag + " " + p.getSkin() + " " + p.getRow() + " " + p.getCol() + " "
        + p.getDeaths() + " " + p.getExtraLives() + " " + p.getHitImmunityTicks()
        + " " + p.isGreenShield() + " " + p.getGreenInvulnerableTicks());
    w.newLine();
  }

  private static GameSnapshot readSnapshotUntilEnd(final String stateLine,
                                                   final BufferedReader r)
      throws IOException {
    final State state = State.valueOf(stateLine.substring(6).trim());
    int time = 0;
    int coinsCollected = 0;
    boolean checkpoint = false;
    String respawn = "START";
    String playerSkin = "";
    String player2Skin = "";
    String originalSkin = "";
    String original2Skin = "";
    boolean twoPlayer = false;
    String winner = "";
    PlayerSnapshot player1 = null;
    PlayerSnapshot player2 = null;
    final List<CoinSnapshot> coins = new ArrayList<>();
    final List<EnemySnapshot> enemies = new ArrayList<>();
    final List<PowerUpSnapshot> powerUps = new ArrayList<>();

    String line = stateLine;
    while (line != null) {
      line = line.trim();
      if (!line.isEmpty() && !line.startsWith("#")) {
        if (line.startsWith("TIME ")) {
          time = Integer.parseInt(line.substring(5).trim());
        } else if (line.startsWith("COINS_COLLECTED ")) {
          coinsCollected = Integer.parseInt(line.substring(16).trim());
        } else if (line.startsWith("CHECKPOINT ")) {
          checkpoint = Boolean.parseBoolean(line.substring(11).trim());
        } else if (line.startsWith("RESPAWN ")) {
          respawn = line.substring(8).trim();
        } else if (line.startsWith("PLAYER_SKIN ")) {
          playerSkin = line.substring(12).trim();
        } else if (line.startsWith("PLAYER2_SKIN ")) {
          player2Skin = line.substring(13).trim();
        } else if (line.startsWith("ORIGINAL_SKIN ")) {
          originalSkin = line.substring(14).trim();
        } else if (line.startsWith("ORIGINAL2_SKIN ")) {
          original2Skin = line.substring(15).trim();
        } else if (line.startsWith("TWO_PLAYER ")) {
          twoPlayer = Boolean.parseBoolean(line.substring(11).trim());
        } else if (line.startsWith("WINNER ")) {
          winner = line.substring(7).trim();
        } else if (line.startsWith("PLAYER1 ")) {
          player1 = parsePlayer(line.substring(8).trim());
        } else if (line.startsWith("PLAYER2 ")) {
          final String rest = line.substring(8).trim();
          if (!"none".equals(rest)) {
            player2 = parsePlayer(rest);
          }
        } else if (line.startsWith("COIN ")) {
          coins.add(parseCoin(line.substring(5).trim()));
        } else if (line.startsWith("ENEMY ")) {
          enemies.add(parseEnemy(line.substring(6).trim()));
        } else if (line.startsWith("POWERUP ")) {
          powerUps.add(parsePowerUp(line.substring(8).trim()));
        }
      }
      line = r.readLine();
    }

    if (player1 == null) {
      throw new IOException("Guardado sin datos del jugador 1");
    }
    return new GameSnapshot(state, time, coinsCollected, checkpoint, respawn,
        playerSkin, player2Skin, originalSkin, original2Skin, twoPlayer, winner,
        player1, player2, coins, enemies, powerUps);
  }

  private static PlayerSnapshot parsePlayer(final String data) throws IOException {
    final String[] p = data.split(" ");
    if (p.length < 8) {
      throw new IOException("Línea de jugador inválida: " + data);
    }
    final int n = p.length;
    final int greenInvuln = Integer.parseInt(p[n - 1]);
    final boolean greenShield = Boolean.parseBoolean(p[n - 2]);
    final int hitImmunity = Integer.parseInt(p[n - 3]);
    final int extraLives = Integer.parseInt(p[n - 4]);
    final int deaths = Integer.parseInt(p[n - 5]);
    final int col = Integer.parseInt(p[n - 6]);
    final int row = Integer.parseInt(p[n - 7]);
    final StringBuilder skin = new StringBuilder(p[0]);
    for (int i = 1; i < n - 7; i++) {
      skin.append(' ').append(p[i]);
    }
    return new PlayerSnapshot(skin.toString(), row, col, deaths, extraLives,
        hitImmunity, greenShield, greenInvuln);
  }

  private static CoinSnapshot parseCoin(final String data) throws IOException {
    final String[] p = data.split(" ");
    if (p.length < 3) {
      throw new IOException("Línea de moneda inválida: " + data);
    }
    return new CoinSnapshot(Boolean.parseBoolean(p[1]), Boolean.parseBoolean(p[2]));
  }

  private static EnemySnapshot parseEnemy(final String data) throws IOException {
    final String[] p = data.split(" ");
    if (p.length < 6) {
      throw new IOException("Línea de enemigo inválida: " + data);
    }
    return new EnemySnapshot(Integer.parseInt(p[1]), Integer.parseInt(p[2]),
        Integer.parseInt(p[3]), Integer.parseInt(p[4]), Integer.parseInt(p[5]));
  }

  private static PowerUpSnapshot parsePowerUp(final String data) throws IOException {
    final String[] p = data.split(" ");
    if (p.length < 4) {
      throw new IOException("Línea de power-up inválida: " + data);
    }
    return new PowerUpSnapshot(p[1], Boolean.parseBoolean(p[2]), Boolean.parseBoolean(p[3]));
  }

  private static String colorToName(final Color color) {
    if (Color.WHITE.equals(color)) {
      return "Blanco";
    }
    if (Color.YELLOW.equals(color)) {
      return "Amarillo";
    }
    if (Color.CYAN.equals(color)) {
      return "Cian";
    }
    if (Color.MAGENTA.equals(color)) {
      return "Magenta";
    }
    return "Negro";
  }

  private static Color nameToColor(final String name) {
    switch (name) {
      case "Blanco":
        return Color.WHITE;
      case "Amarillo":
        return Color.YELLOW;
      case "Cian":
        return Color.CYAN;
      case "Magenta":
        return Color.MAGENTA;
      default:
        return Color.BLACK;
    }
  }
}
