# The DOPO Hardest Game — Entrega final

Clon en Java (Swing) del clásico *The World's Hardest Game*, con tablero por archivos de texto, varios tipos de enemigos, jugadores con habilidades distintas, multijugador local y guardado de partida.

**Curso:** EPRE · **Versión:** 2026-1  
**Autor en código:** Angel-Garcia

---

## Cómo ejecutar

1. Abrir el módulo `epre/` en IntelliJ IDEA (o IDE compatible con Java).
2. Ejecutar `main.presentation.GameGUI`.
3. Los niveles están en `configs/`. Ejecutar desde la raíz del repositorio para que las rutas relativas funcionen.

**Pruebas unitarias (JUnit 4):**

```bash
cd epre
javac -encoding UTF-8 -sourcepath "src/main;src/test" -d out -cp "out;%USERPROFILE%\.m2\repository\junit\junit\4.13.1\junit-4.13.1.jar"
java -cp "out;..." org.junit.runner.JUnitCore test.HardestGameTest
```

---

## Arquitectura (resumen)

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| Dominio | `main.domain` | Reglas del juego, entidades, excepciones, carga de niveles |
| Presentación | `main.presentation` | Menú, ventana, tablero gráfico, guardado/carga, reportes |
| Pruebas | `test` | Unitarias e integración por componente |

---

## Ciclos de desarrollo

El proyecto se construyó de forma incremental. Cada **ciclo** agrupa entregables funcionales; cada **mini-ciclo** es un bloque verificable (código + pruebas + nivel de ejemplo).

### Ciclo 1 — Tablero, jugador y victoria básica

**Objetivo:** Tener un juego jugable en una sola pantalla: mover al jugador, recolectar monedas, ganar o perder por tiempo, con niveles definidos en archivo.

| Mini-ciclo | Objetivo | Entregables principales |
|------------|----------|------------------------|
| **1.1** Modelo del tablero | Representar celdas, muros y zonas seguras. | `Board`, `Zone`, validación de movimiento |
| **1.2** Jugador y movimiento | Un jugador controlable con reglas de desplazamiento. | `Player`, `RedPlayer`, `HardestGame.movePlayer` |
| **1.3** Carga de niveles | Leer dimensiones, tiempo, muros y monedas desde `.txt`. | `GameConfiguration`, excepciones de formato |
| **1.4** Monedas y victoria | Recolectar monedas amarillas y llegar a la zona final. | `YellowCoin`, condición de victoria, contador |
| **1.5** Interfaz mínima | Ver el tablero y jugar con teclado. | `GameGUI`, `BoardPanel`, barra de estado |

---

### Ciclo 2 — Enemigos, muerte y robustez

**Objetivo:** Añadir peligro real (enemigos), ciclo muerte–respawn, reinicio de progreso y manejo de errores sin romper la partida.

| Mini-ciclo | Objetivo | Entregables principales |
|------------|----------|------------------------|
| **2.1** Enemigo básico | Patrulla horizontal o vertical con rebote. | `Enemy`, `BasicEnemy` |
| **2.2** Colisión y muerte | Choque con enemigo incrementa muertes y respawnea. | `checkEnemyCollisionForPlayer`, `respawn` |
| **2.3** Tiempo y derrota | Cuenta regresiva; al llegar a 0 → `LOST`. | `decrementTime`, estados `WON` / `LOST` |
| **2.4** Excepciones de dominio | Errores tipados y mensajes claros al usuario. | Jerarquía `HardestGameException` |
| **2.5** Registro de errores | Log en disco de fallos internos. | `GameLog`, `GameLogger`, `logs/game_errors.log` |
| **2.6** Pausa y reinicio | Pausar partida y recargar nivel actual. | `togglePause`, tecla `P` / botones |

---

### Ciclo 3 — Jugadores especiales, power-ups y checkpoint

**Objetivo:** Diversificar mecánicas: skins, monedas especiales, trampas, vidas extra y punto de control intermedio.

| Mini-ciclo | Objetivo | Entregables principales |
|------------|----------|------------------------|
| **3.1** Jugador azul | Hitbox ampliada (colisión en cruz). | `BluePlayer`, `Enemy.collidesWith` |
| **3.2** Jugador verde (Clyde) | Escudo que absorbe un golpe; ralentización tras usarlo. | `GreenPlayer`, inmunidad temporal |
| **3.3** Moneda skin | Cambio temporal de personaje hasta morir. | `SkinCoin`, `applyCoinEffect`, restauración al morir |
| **3.4** Power-ups | Bomba (muerte) y fuente de vida (vida extra permanente). | `Bomb`, `LifeSource` |
| **3.5** Checkpoint intermedio | Zona `SAFE_INTERMEDIATE`; monedas previas quedan fijas. | `checkIntermediateZone`, `markPermanent` |
| **3.6** Nivel 3 (diseño U) | Ruta en U con formaciones en X y monedas skin abajo. | `configs/level3.txt` |

---

### Ciclo 4 — Menú, multijugador e IA de maquinas

**Objetivo:** Pantalla de inicio configurable, modos 1J / PvP / PvM y máquina que compite por monedas.

| Mini-ciclo | Objetivo | Entregables principales |
|------------|----------|------------------------|
| **4.1** Menú principal | Elegir nivel, modalidad, skins y colores de borde. | `MenuScreen`, `GameSetup` |
| **4.2** Multijugador local | Dos jugadores (flechas vs WASD); meta cruzada. | `twoPlayerMode`, `moveSecondPlayer` |
| **4.3** Colisión entre jugadores | Misma celda → ambos vuelven a su inicio. | `checkPlayerCollision` |
| **4.4** Modo vs máquina | Segundo jugador controlado por IA. | `computeMachineMove`, BFS de rutas |
| **4.5** Máquina experta | Orden de monedas por distancia mínima. | `buildExpertTargets` |
| **4.6** Reporte de errores | El usuario describe fallos desde el juego. | `ErrorReportDialog`, `logs/user_reports.log` |

---

### Ciclo 5 — Enemigos avanzados, persistencia y entrega final

**Objetivo:** Completar el catálogo de enemigos del enunciado, guardar/cargar partida y consolidar niveles de demostración.

| Mini-ciclo | Objetivo | Entregables principales |
|------------|----------|------------------------|
| **5.1** Deslizador vertical (Tipo V) | Solo movimiento vertical; rebote; ritmo lento. | `VerticalEnemy` |
| **5.2** Acelerado (Tipo A) | Línea recta al doble de velocidad. | `AcceleratedEnemy` |
| **5.3** Patrullero azul | Recorrido del perímetro de una zona (figura geométrica). | `PatrolEnemy`, filtrado de waypoints en zonas prohibidas |
| **5.4** Guardar / cargar partida | Archivo `.dopo` elegido por el usuario (diálogo del sistema). | `GameSaveIO`, `GameSaveDialog`, `GameSnapshot` |
| **5.5** Niveles y demo | Pack de niveles + `demo_final` con todos los elementos. | `configs/*.txt`, `LoadAllLevelsTest` |
| **5.6** Pruebas automatizadas | Cobertura de dominio y configuración. | `src/test/*` |

---

## Requisitos no contemplados en esta entrega

Los siguientes puntos **no se implementaron** (por alcance, tiempo o porque quedaron fuera del enunciado acordado). No se evalúan como parte de este repositorio:

| Área | Requisito no considerado |
|------|-------------------------|
| **Gráficos** | Sprites, animaciones de personajes o tiles artísticos (solo formas geométricas: cuadrados, círculos, colores). |
| **Audio** | Música, efectos de sonido o voz. |
| **Enemigos** | Formaciones en X que **giran como un solo cuerpo**; cada enemigo se mueve con su propia lógica. |

---

## Niveles incluidos

| Archivo | Descripción breve |
|---------|-------------------|
| `level1.txt` | Referencia tipo TWHG (tablero ancho) |
| `level2.txt` | Compacto con skin coins y enemigos variados |
| `level3.txt` | Ruta en U, checkpoint, formaciones en X |
| `demo_final.txt` | Demostración de todos los elementos |
| `nivelsupremo.txt` | Desafío con ramas laterales |
| `level_gemas.txt` | Medio, checkpoint |
| `level_relampago.txt` | Corto, poco tiempo |
| `level_duelo.txt` | Orientado a PvP / PvM |

---

## Formato de archivo de nivel (resumen)

```
ROWS <filas>
COLS <columnas>
TIME <segundos>
SAFE_START <fila> <col> <alto> <ancho>
SAFE_INTERMEDIATE ...   (opcional)
SAFE_FINAL ...
COIN YELLOW <fila> <col>
COIN SKIN <RED|BLUE|GREEN> <fila> <col>
POWERUP BOMB|LIFE <fila> <col>
ENEMY BASIC <fila> <col> HORIZONTAL|VERTICAL [LEFT|RIGHT]
ENEMY VERTICAL <fila> <col>
ENEMY ACCEL <fila> <col> HORIZONTAL|VERTICAL [LEFT|RIGHT]
ENEMY PATROL <fila> <col> [<zonaFila> <zonaCol>] <filasZona> <colsZona>
WALL <fila> <col>
```

---

## Estructura del repositorio

```
epre 2/
├── README.md
├── configs/          # Niveles (.txt)
├── logs/             # Errores y reportes de usuario (generados al jugar)
├── saves/            # Partidas guardadas (.dopo), opcional
└── epre/
    └── src/
        ├── main/domain/
        ├── main/presentation/
        └── test/
```

---

## Licencia y uso académico

Proyecto con fines educativos (EPRE 2026-1). Consultar con el docente del curso antes de reutilizar o publicar fuera del contexto académico.
