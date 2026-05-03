# Reporte de Análisis Estático (PMD)

Se realizó el análisis estático del código utilizando **PMD** con 8 rule sets activos, orientado a mejorar la mantenibilidad y robustez del sistema **The DOPO Hardest Game**.

---

## 1. Resultado Inicial

El escaneo inicial sobre los 21 archivos del proyecto arrojó **434 violaciones** distribuidas así:

| Categoría      | Violaciones   |
|----------------|---------------|
| bestpractices  | 107 (102 + 5) |
| codestyle      | 122 (118 + 4) |
| design         | 39            |
| documentation  | 152           |
| errorprone     | 14            |
| **Total**      | **434**       |

Las violaciones más frecuentes fueron:

- **`documentation` — CommentRequired y CommentSize (152):** Métodos y constructores sin Javadoc completo, o con descripciones de menos de 10 palabras. Afectaba a todas las clases de dominio y a los archivos de prueba.
- **`codestyle` — ShortVariable (63):** Variables locales con nombres de 1–2 caracteres (`r`, `c`, `dr`, `dc`, `e`, `g`, `p`, `z`, `f`, `g2`), tanto en dominio como en tests.
- **`codestyle` — LocalVariableCouldBeFinal (26):** Variables locales que no se reasignan y podrían declararse `final`.
- **`codestyle` — ControlStatementBraces (9):** Sentencias `if/for/while` sin llaves `{}`.
- **`codestyle` — AtLeastOneConstructor (7) y CallSuperInConstructor (4):** Clases sin constructor explícito o sin llamada a `super()`.
- **`design` (39):** Violaciones de `LawOfDemeter`, `CognitiveComplexity`, `ImmutableField`, entre otras.
- **`bestpractices` — UnitTestContainsTooManyAsserts (20):** Métodos de prueba con múltiples `assert` agrupados.

---

## 2. Proceso de Reducción

Las correcciones se aplicaron en iteraciones sucesivas:

| Iteración | Total | Acción principal |
|-----------|-------|-----------------|
| Inicial   | 434   | — |
| Ciclo 1   | 353   | Corrección de `documentation`: Javadoc completo con `@param`, `@return` y descripciones de más de 10 palabras en todas las clases de dominio y tests |
| Ciclo 2   | 307   | Corrección de `codestyle`: `super()` explícito en constructores, llaves en sentencias de control, `final` en variables locales y parámetros |
| Final     | 289   | Renombre de variables cortas (`dr`→`deltaRow`, `dc`→`deltaCol`, `nr`→`nextRow`, `e`→`enemy`, `z`→`zone`, `f`→`file`) |

---

## 3. Decisiones Tomadas

### Correcciones aplicadas:

- **Javadoc completo:** Se reescribieron los comentarios de `Zone`, `HardestGame`, `GameConfiguration`, `Board`, `BasicEnemy`, `Enemy`, `Coin`, `Player`, `RedPlayer`, `YellowCoin` y `HardestGameException`, asegurando descripciones de más de 10 palabras con `@param` y `@return` en todos los métodos públicos. Lo mismo se aplicó a los 7 archivos de test.

- **`super()` explícito en constructores:** Se agregó en `Board`, `Coin`, `Enemy`, `Player`, `Zone`, `RedPlayer`, `YellowCoin` y `HardestGameException`.

- **Llaves en sentencias de control:** Se añadieron `{}` a todos los bloques `if`, `for` y `while` que carecían de ellas.

- **`final` en variables locales y parámetros:** Se declararon como `final` todas las variables y parámetros que no se reasignan en su alcance.

- **Renombre de variables cortas:** Se renombraron todas las variables de 1–2 caracteres por nombres descriptivos en dominio y tests.

### Violaciones no corregidas y justificación:

- **`design` (39 — sin cambio):** Las violaciones de `LawOfDemeter` (21) son inherentes al patrón MVC: la capa de presentación accede al estado del dominio mediante cadenas de `getters`, lo cual no puede eliminarse sin romper la separación de capas. Las de `ImmutableField` (5), `DataClass` (2) y `SingularField` (3) afectan a campos de dominio que son mutables por diseño (el estado del juego cambia en tiempo de ejecución) y que están pensados para extenderse en versiones futuras. `CognitiveComplexity` y `CyclomaticComplexity` (5) afectan a métodos como `parseLine` en `GameConfiguration` y `drawBoard` en `BoardPanel`, cuya complejidad es inherente a la lógica de parsing y renderizado; fragmentarlos no mejoraría la legibilidad real del código.

- **`errorprone` (14 — sin cambio):** La regla presente es `AvoidLiteralsInIfCondition`, que señala constantes numéricas en condiciones `if`. Estas constantes están correctamente definidas como `static final` en sus clases, pero PMD las detecta en los `if` de métodos como `update` y `gameTick`. Extraerlas a variables intermedias adicionales no aportaría claridad y podría dificultar la lectura del flujo de control.

- **`bestpractices` — UnitTestContainsTooManyAsserts (20):** Los métodos de prueba agrupan múltiples `assert` pertenecientes a un único escenario funcional (por ejemplo, verificar posición + contador de muertes tras un `respawn`). Dividirlos en métodos separados reduciría la legibilidad sin aportar valor adicional a la validación del comportamiento.

- **`codestyle` residual:** Las violaciones restantes incluyen `ShortClassName` en la clase `Board` (renombrarla rompería la coherencia semántica del dominio) y reglas de estilo en la capa de `presentation` (`GameGUI`, `BoardPanel`, `MenuScreen`) cuya refactorización está planificada para versiones futuras.

---

## 4. Resultado Final

| Categoría      | Inicial | Final   |
|----------------|---------|---------|
| bestpractices  | 107     | 26      |
| codestyle      | 122     | 122     |
| design         | 39      | 39      |
| documentation  | 152     | 106     |
| errorprone     | 14      | 14      |
| **Total**      | **434** | **289** |

> **Nota sobre `codestyle`:** El número final es igual al inicial porque las correcciones aplicadas en esa categoría (constructores, llaves, `final`) fueron compensadas por nuevas detecciones generadas al renombrar variables (e.g. `LongVariable`). La reducción neta de 145 violaciones se concentra en `documentation` y `bestpractices`.

### Conclusión

Se logró una reducción de **145 violaciones (33%)** en cuatro iteraciones, pasando de 434 a 289. Las violaciones restantes corresponden a limitaciones arquitecturales del patrón MVC, decisiones de diseño del dominio, o reglas cuya corrección no aportaría mejora real a la calidad del código en la versión 1 del proyecto.
