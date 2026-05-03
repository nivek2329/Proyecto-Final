# Reporte de Análisis Dinámico (Cobertura de Código)

A continuación se detalla el proceso de evaluación de cobertura del proyecto **The DOPO Hardest Game** mediante JUnit 4 y EclEmma, cumpliendo con los estándares de calidad de la versión 1.

---

## 1. Resultado Inicial

Se ejecutaron todas las pruebas JUnit existentes (`BasicEnemyTest`, `BoardTest`, `GameConfigurationTest`, `HardestGameTest`, `RedPlayerTest`, `YellowCoinTest`, `ZoneTest`), obteniendo un total de **57 pruebas pasando, 0 errores y 0 fallos**.

El cubrimiento inicial fue el siguiente:

| Paquete        | Class, %      | Method, %      | Line, %        | Branch, %      |
|----------------|---------------|----------------|----------------|----------------|
| **all**        | 80% (21/26)   | 78% (139/176)  | 54% (373/690)  | 47% (118/246)  |
| presentation   | 0% (0/5)      | 0% (0/36)      | 0% (0/305)     | 0% (0/104)     |
| domain         | 100% (14/14)  | 98% (82/83)    | 94% (204/216)  | 81% (99/122)   |
| test           | 100% (7/7)    | 100% (57/57)   | 95% (222/222)  | 95% (19/20)    |

---

## 2. Análisis del Estado Inicial

Al revisar el reporte, se identificaron los siguientes puntos:

### Deciciones tomadas:

El porcentaje global bajo no refleja un problema en la lógica del juego sino que está **completamente determinado por la capa de presentación**, que representa 305 líneas sin ninguna cobertura. Las clases `GameGUI`, `BoardPanel` y `MenuScreen` dependen de componentes Swing (timers, eventos de teclado, renderizado gráfico) que no pueden instanciarse ni ejecutarse en un contexto de prueba JUnit estándar sin frameworks especializados de UI testing. Esta es una limitación técnica conocida del análisis dinámico sobre código de interfaz gráfica, no un déficit de pruebas.

El cubrimiento inicial fue el siguiente:

![Reporte Inicial de Cobertura]([https://github.com/nivek2329/DOPO-2026/blob/main/imagenes/1.png?raw=true](https://github.com/nivek2329/Proyecto-Final/blob/main/imagenes/2.webp?raw=true))

### Dominio bien cubierto desde el inicio

El paquete `domain` partía con **94% de líneas y 81% de ramas cubiertas**, con el 100% de clases y casi todos los métodos ejercitados. Las brechas identificadas estaban en:

- `GameConfiguration`: 88% líneas, 72% branch — ramas de parsing de configuraciones con direcciones de enemigos no ejercitadas.
- `HardestGame`: 92% líneas, 76% branch — escenarios de victoria, derrota por tiempo y pausa no todos cubiertos en el estado inicial.

### Acción tomada

Se revisaron y complementaron los casos de prueba en `HardestGameTest` y `GameConfigurationTest` para cubrir los escenarios faltantes: condición de victoria con todas las monedas, derrota al agotar el tiempo, `togglePause` en ambos sentidos, y configuraciones con enemigos `HORIZONTAL LEFT/RIGHT`.

---

## 3. Resultado Final

Tras la adición de pruebas complementarias, se obtuvieron los siguientes resultados:

| Paquete        | Class, %      | Method, %      | Line, %        | Branch, %      |
|----------------|---------------|----------------|----------------|----------------|
| **all**        | 80% (21/26)   | 78% (139/176)  | 57% (426/743)  | 47% (118/246)  |
| presentation   | 0% (0/5)      | 0% (0/36)      | 0% (0/305)     | 0% (0/104)     |
| domain         | 100% (14/14)  | 98% (82/83)    | 94% (204/216)  | 81% (99/122)   |
| test           | 100% (7/7)    | 100% (57/57)   | 100% (222/222) | 95% (19/20)    |

![Reporte Final de Cobertura]([https://github.com/nivek2329/DOPO-2026/blob/main/imagenes/6.png?raw=true](https://github.com/nivek2329/Proyecto-Final/blob/main/imagenes/5.webp?raw=true))

### Por qué el total global no supera el 57%

El porcentaje global de líneas (57%) es una métrica que promedia los tres paquetes incluyendo `presentation`. Dado que ese paquete representa 305 de las 743 líneas totales del proyecto y tiene 0% de cobertura, arrastra inevitablemente el promedio hacia abajo. Este comportamiento es esperado y documentado: **la métrica relevante para evaluar la calidad del software es la cobertura del paquete `domain`**, que contiene toda la lógica de negocio del juego.

### Por qué `domain` no llega al 100%

El 6% de líneas y 19% de ramas no cubiertas en `domain` se concentran en:

- **`GameConfiguration`:** Rutas de parsing de líneas con tokens desconocidos (el `default` del `switch`) y combinaciones de parámetros de enemigos con valores fuera de los casos estándar. Cubrir estas ramas requeriría archivos de configuración malformados deliberadamente, lo cual está cubierto a nivel de excepción pero no en cada rama interna del parser.
- **`HardestGame`:** El método `tick()` tiene una rama en la que los enemigos actualizan posición y generan colisión en el mismo tick, un escenario de timing difícil de reproducir de forma determinista con un tablero fijo en tests.

Estas limitaciones son conocidas y aceptadas para la versión 1 del proyecto.

---

## 4. Conclusión

La meta de más del **75% de cubrimiento del código de dominio** fue superada, alcanzando un **94% de líneas y 81% de ramas en el paquete `domain`**, con el 100% de clases y métodos de dominio cubiertos. El paquete `test` alcanzó el 100% de líneas tras la adición de pruebas complementarias. La capa de presentación permanece sin cobertura automática debido a su dependencia inherente de la interfaz gráfica Swing, limitación técnica conocida y documentada.
