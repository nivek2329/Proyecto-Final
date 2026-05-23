# The DOPO Hardest Game — Entrega Final

Clon en Java  del juego *The World's Hardest Game*, para **DOPO (DESARROLLO ORIENTADO A OBJETOS)**. Incluye tablero configurable por archivos de texto, múltiples tipos de enemigos, jugadores con habilidades distintas, multijugador local (PvP / PvM), sistema de guardado de partidas y reporte de errores.

**Curso:** DOPO 2026-1  
**Autor:** Angel-Garcia  
**Versión:** 2026-1 (Entrega Final)

---

## Tabla de contenidos

1. [Cómo clonar y ejecutar](#cómo-clonar-y-ejecutar)
2. [Cómo ejecutar tests](#cómo-ejecutar-tests)
3. [Arquitectura del proyecto](#arquitectura-del-proyecto)
4. [Ciclos de desarrollo](#ciclos-de-desarrollo)
5. [DOPO: Patrones y principios aplicados](#dopo-patrones-y-principios-aplicados)
6. [Niveles incluidos](#niveles-incluidos)
7. [Formato de archivo de nivel](#formato-de-archivo-de-nivel)
8. [Controles](#controles)
9. [Referencias](#referencias)
10. [Análisis de calidad](#análisis-de-calidad)

---

## Cómo clonar y ejecutar

### Prerrequisitos

- **Java JDK 8** o superior (recomendado JDK 17+).
- IDE compatible: **IntelliJ IDEA**, Eclipse o VS Code con extensión Java.
- (Opcional) **JUnit 4.13+** para ejecutar tests.

### Clonar el repositorio

```bash
git clone https://github.com/<usuario>/the-dopo-hardest-game.git
cd the-dopo-hardest-game
```

> Nota: reemplaza `<usuario>` con tu usuario de GitHub o la URL del repositorio real.

### Ejecutar desde consola (sin IDE)

```bash
# Compilar
cd epre
javac -encoding UTF-8 -sourcepath "src/main;src/test" -d out $(find src/main -name "*.java")

# Ejecutar el juego
java -cp "out" main.presentation.GameGUI
```

> **Importante:** Ejecutar desde la raíz del proyecto (`epre/`) para que las rutas relativas (`configs/`, `logs/`, `saves/`) funcionen correctamente.

### Ejecutar desde IntelliJ IDEA

1. Abrir el directorio `epre/` como proyecto.
2. Marcar `src/main` como *Sources Root* y `src/test` como *Test Sources Root*.
3. Añadir JUnit 4.13.1 a las dependencias del módulo.
4. Ejecutar la clase `main.presentation.GameGUI` (botón derecho → *Run*).

---

## Cómo ejecutar tests

### Desde consola

```bash
cd epre

# Compilar tests
javac -encoding UTF-8 -sourcepath "src/main;src/test" -d out -cp "out;junit-4.13.1.jar;hamcrest-core-1.3.jar" $(find src -name "*.java")

# Ejecutar suite completa
java -cp "out;junit-4.13.1.jar;hamcrest-core-1.3.jar" org.junit.runner.JUnitCore   test.AcceleratedEnemyTest   test.BasicEnemyTest   test.BluePlayerTest   test.BoardTest   test.BombTest   test.ExceptionsTest   test.GameConfigurationTest   test.GameLogTest   test.GameSnapshotTest   test.GreenPlayerTest   test.HardestGameTest   test.LifeSourceTest   test.LoadAllLevelsTest   test.PatrolEnemyTest   test.PowerUpIntegrationTest   test.PowerUpTest   test.RedPlayerTest   test.SkinCoinTest   test.SpinnerEnemyTest   test.VerticalEnemyTest   test.YellowCoinTest   test.ZoneTest
```

### Desde IntelliJ IDEA

1. Click derecho sobre la carpeta `src/test` → *Run 'All Tests'*.
2. O click derecho sobre cualquier clase `*Test.java` → *Run*.

### Resultados esperados

- **131 tests pasando, 0 errores, 0 fallos**.
- Cobertura de líneas en `main.domain`: **91%**.
- Cobertura de clases en `main.domain`: **100%**.

---

## Arquitectura del proyecto

El proyecto sigue una arquitectura **MVC (Modelo-Vista-Controlador)** con separación clara de responsabilidades:

| Capa | Paquete | Responsabilidad | Clases principales |
|------|---------|-----------------|-------------------|
| **Modelo (Dominio)** | `main.domain` | Reglas del juego, entidades, excepciones, carga de niveles, persistencia | `HardestGame`, `Board`, `Player`, `Enemy`, `Coin`, `PowerUp`, `Zone`, `GameConfiguration`, `GameSnapshot` |
| **Vista (Presentación)** | `main.presentation` | Interfaz gráfica Swing, menú, renderizado, diálogos, guardado/carga | `GameGUI`, `BoardPanel`, `MenuScreen`, `ErrorReportDialog`, `GameSaveDialog` |
| **Pruebas** | `test` | Unitarias e integración por componente | `*Test.java` (22 archivos, 131 tests) |

---

## Ciclos de desarrollo

El proyecto se construyó de forma incremental mediante 5 ciclos funcionales. Cada ciclo agrupa entregables verificables (código + pruebas + nivel de ejemplo).

### Ciclo 1 — Tablero, jugador y victoria básica

**Objetivo:** Juego jugable en una sola pantalla: mover al jugador, recolectar monedas, ganar o perder por tiempo, con niveles definidos en archivo.

| Mini-ciclo | Objetivo | Entregables |
|------------|----------|-------------|
| 1.1 | Modelo del tablero | `Board`, `Zone`, validación de movimiento |
| 1.2 | Jugador y movimiento | `Player` (abstracto), `RedPlayer`, `HardestGame.movePlayer` |
| 1.3 | Carga de niveles | `GameConfiguration`, excepciones de formato (`HardestGameException`) |
| 1.4 | Monedas y victoria | `YellowCoin`, condición de victoria, contador de monedas |
| 1.5 | Interfaz mínima | `GameGUI`, `BoardPanel`, barra de estado, controles de teclado |

### Ciclo 2 — Enemigos, muerte y robustez

**Objetivo:** Añadir peligro real (enemigos), ciclo muerte–respawn, reinicio de progreso y manejo de errores.

| Mini-ciclo | Objetivo | Entregables |
|------------|----------|-------------|
| 2.1 | Enemigo básico | `Enemy` (abstracto), `BasicEnemy` (patrulla horizontal/vertical con rebote) |
| 2.2 | Colisión y muerte | `Enemy.collidesWith`, `Player.respawn`, contador de muertes |
| 2.3 | Tiempo y derrota | `decrementTime`, estados `WON` / `LOST` |
| 2.4 | Excepciones de dominio | `HardestGameException` con constantes tipificadas |
| 2.5 | Registro de errores | `GameLog` (Singleton), `GameLogger`, archivos en `logs/` |
| 2.6 | Pausa y reinicio | `togglePause`, tecla `P`, botón *Reiniciar* |

### Ciclo 3 — Jugadores especiales, power-ups y checkpoint

**Objetivo:** Diversificar mecánicas: skins, monedas especiales, trampas, vidas extra y punto de control intermedio.

| Mini-ciclo | Objetivo | Entregables |
|------------|----------|-------------|
| 3.1 | Jugador azul (Inky) | `BluePlayer` — hitbox ampliada (colisión en cruz de 5 celdas), velocidad 1.5x |
| 3.2 | Jugador verde (Clyde) | `GreenPlayer` — escudo que absorbe un golpe, ralentización tras usarlo, inmunidad temporal |
| 3.3 | Moneda skin | `SkinCoin` — cambio temporal de personaje hasta morir |
| 3.4 | Power-ups | `Bomb` (muerte instantánea), `LifeSource` (vida extra permanente) |
| 3.5 | Checkpoint intermedio | `SAFE_INTERMEDIATE` — monedas previas quedan fijas permanentemente |
| 3.6 | Nivel 3 (La Cortina Vertical) | `configs/level3.txt` — ruta en U con formaciones en X |

### Ciclo 4 — Menú, multijugador e IA

**Objetivo:** Pantalla de inicio configurable, modos 1J / PvP / PvM y máquina que compite por monedas.

| Mini-ciclo | Objetivo | Entregables |
|------------|----------|-------------|
| 4.1 | Menú principal | `MenuScreen`, `GameSetup` — elegir nivel, modalidad, skins, colores de borde |
| 4.2 | Multijugador local | `twoPlayerMode`, `moveSecondPlayer` — flechas (J1) vs WASD (J2) |
| 4.3 | Colisión entre jugadores | `checkPlayerCollision` — misma celda → ambos vuelven a inicio |
| 4.4 | Modo vs máquina | `computeMachineMove` — IA con BFS de rutas, evasión de peligro |
| 4.5 | Máquina experta | `buildExpertTargets` — orden de monedas por distancia mínima (nearest neighbor) |
| 4.6 | Reporte de errores | `ErrorReportDialog` — usuario describe fallos, guardados en `logs/user_reports.log` |

### Ciclo 5 — Enemigos avanzados, persistencia y entrega final

**Objetivo:** Completar el catálogo de enemigos, guardar/cargar partida y consolidar niveles de demostración.

| Mini-ciclo | Objetivo | Entregables |
|------------|----------|-------------|
| 5.1 | Deslizador vertical (Tipo V) | `VerticalEnemy` — solo vertical, rebote, ritmo lento (6 ticks) |
| 5.2 | Acelerado (Tipo A) | `AcceleratedEnemy` — línea recta al doble de velocidad (2 ticks) |
| 5.3 | Patrullero azul | `PatrolEnemy` — recorrido del perímetro de una zona rectangular |
| 5.4 | Enemigo giratorio | `SpinnerEnemy` — órbita circular alrededor de un centro, 8 posiciones discretas |
| 5.5 | Guardar / cargar partida | `GameSaveIO`, `GameSaveDialog` — formato `.dopo` con snapshot completo |
| 5.6 | Niveles y demo | `configs/*.txt` (6 niveles), `LoadAllLevelsTest` |

---

## DOPO: Patrones y principios aplicados

### Patrones de diseño (GoF)

| Patrón | Aplicación | Clases involucradas |
|--------|-----------|---------------------|
| **Singleton** | Logger único para toda la aplicación | `GameLog`, `GameLogger` |
| **Factory Method** | Creación de jugadores según skin seleccionada | `HardestGame.createPlayer`, `createPlayerBySkin` |
| **Strategy** | Algoritmos de movimiento intercambiables | `Player` (Red/Blue/Green), `Enemy` (Basic/Vertical/Accel/Patrol/Spinner) |
| **State** | Estados finitos del juego | `HardestGame.State` (`PLAYING`, `PAUSED`, `WON`, `LOST`) |
| **Observer** | Notificación de actualización en cada tick | `Updatable` interface |
| **MVC** | Separación modelo-vista-controlador | `main.domain` / `main.presentation` |

### Principios SOLID

| Principio | Aplicación |
|-----------|-----------|
| **SRP** (Responsabilidad Única) | `Board` solo gestiona celdas; `GameConfiguration` solo parsea archivos; `GameLog` solo logging |
| **OCP** (Abierto/Cerrado) | Nuevos tipos de `Player`, `Enemy` y `Coin` se añaden sin modificar código existente |

### Otros conceptos DOPO

- **Herencia:** Jerarquías `Player` → `RedPlayer`/`BluePlayer`/`GreenPlayer`; `Enemy` → `BasicEnemy`/`VerticalEnemy`/etc.
- **Polimorfismo:** `Enemy.update(Board)` se comporta diferente según el tipo concreto.
- **Encapsulamiento:** Atributos `private`/`protected` con getters/setters controlados; `GameSnapshot` usa listas inmutables.
- **Coesión:** Cada clase tiene un propósito claro y bien definido (alta cohesión).
- **Acoplamiento:** Bajo acoplamiento entre `domain` y `presentation`; `GameGUI` solo invoca métodos públicos de `HardestGame`.

---

## Niveles incluidos

| Archivo | Descripción | Elementos destacados |
|---------|-------------|---------------------|
| `level1.txt` | Referencia tipo TWHG (tablero ancho 8×22) | 8 monedas, 4 enemigos horizontales, muros en L |
| `level2.txt` | Compacto (6×12) con variedad | Skin coins, enemigo acelerado, vertical |
| `level3.txt` | La Cortina Vertical (8×26) | 6 monedas, 18+ enemigos verticales en parejas, ruta en U |
| `demo_final.txt` | Demostración de todos los elementos | Vida, bomba, skin coins, patrullero, acelerado, vertical, checkpoint |
| `nivelsupremo.txt` | Desafío con ramas laterales | 5 monedas, 3 enemigos, 2 vidas, 1 bomba, checkpoint |
| `level_duelo.txt` | Orientado a PvP / PvM (6×14) | 6 monedas centrales, 2 enemigos, 2 vidas, meta cruzada |

---

## Formato de archivo de nivel

```
ROWS <filas>
COLS <columnas>
TIME <segundos>          # mínimo 10

SAFE_START <fila> <col> <alto> <ancho>
SAFE_INTERMEDIATE <fila> <col> <alto> <ancho>   # opcional
SAFE_FINAL <fila> <col> <alto> <ancho>

# Monedas
COIN YELLOW <fila> <col>
COIN SKIN <RED|BLUE|GREEN> <fila> <col>

# Power-ups
POWERUP BOMB <fila> <col>
POWERUP LIFE <fila> <col>

# Enemigos
ENEMY BASIC <fila> <col> HORIZONTAL|VERTICAL [LEFT|RIGHT]
ENEMY VERTICAL <fila> <col>
ENEMY ACCEL <fila> <col> HORIZONTAL|VERTICAL [LEFT|RIGHT]
ENEMY PATROL <fila> <col> [<zonaFila> <zonaCol>] <filasZona> <colsZona>
ENEMY SPINNER <filaCentro> <colCentro> <radio> <indexInicial> <horario> <velocidad>

# Muros
WALL <fila> <col>
```

---

## Controles

| Acción | Jugador 1 | Jugador 2 (PvP) |
|--------|-----------|-----------------|
| Arriba | ↑ | W |
| Abajo | ↓ | S |
| Izquierda | ← | A |
| Derecha | → | D |
| Pausar | P | — |
| Reportar error | E | — |
| Reiniciar nivel | R | — |

> **Nota:** En modo un jugador, tanto las flechas como WASD controlan al jugador 1.

---

## Referencias

```
Barnes, D. J., & Kölling, M. (2016). Objects First with Java: A Practical Introduction Using BlueJ (6th ed.). Pearson.

Oracle. (2024). Java SE 21 Official Documentation.

Escuela Colombiana de Ingeniería Julio Garavito. (2026). Enunciado del Proyecto de Aula - POO.

```

---

## Análisis de calidad

### Cobertura de tests 

| Métrica | Valor |
|---------|-------|
| Tests totales | **131** |
| Tests pasando | **131 (100%)** |
| Clases cubiertas (domain) | **100%** |
| Métodos cubiertos (domain) | **96%** |
| Líneas cubiertas (domain) | **91%** |
| Branch coverage (domain) | **80%** |

### Análisis estático (PMD)

| Categoría | Estado |
|-----------|--------|
| bestpractices | Mejorado (reducción del 37%) |
| errorprone | Mejorado (reducción del 31%) |
| documentation | Documentación completa en dominio |
| Violaciones residuales | Justificadas técnicamente |

> Ver archivos `ANALISIS_ESTATICO.md` y `ANALISIS_DINAMICO.md` para el detalle completo.

---

---

*Última actualización: mayo 2026*
*Nota Esperada: 5.0*
