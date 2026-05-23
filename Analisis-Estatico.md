# Reporte de Análisis Estático (PMD)

Se realizó un análisis exhaustivo del código fuente de **The DOPO Hardest Game** utilizando **PMD** con 8 rule sets estratégicos, evaluando un total de **51 archivos** del proyecto (incluyendo dominio, presentación y tests).

---

## 1. Resultado Inicial

El escaneo inicial detectó un total de **1.193 violaciones** distribuidas en las siguientes categorías:

![Reporte Inicial PMD](https://github.com/nivek2329/Proyecto-Final/blob/main/imagenes/E1.png?raw=true)
![Reporte Inicial PMD](https://github.com/nivek2329/Proyecto-Final/blob/main/imagenes/E1.1.png?raw=true)

> **Observación crítica:** La mayoría de las violaciones en `documentation` corresponden a clases de test (`test.*`) y a métodos autoexplicativos de getters/setters en el dominio. Las de `errorprone` se concentran en el parser de configuración (`GameConfiguration`) y en la GUI (`GameGUI`).

---

## 2. Decisiones Tomadas y Proceso de Mejora

El proceso de corrección se estructuró en **tres ciclos iterativos** enfocados en impacto real sobre la calidad del código:

### Ciclo 1 — Documentación y Javadoc
- Se completaron los Javadocs faltantes en clases críticas del dominio: `HardestGame`, `GameConfiguration`, `Zone`, `Board`, `Enemy`, `Player` y todas sus subclases.
- Se añadieron etiquetas `@param`, `@return` y `@throws` donde aplicaba.
- Se redujeron las alertas de `CommentRequired` de 336 a aproximadamente 200 (las restantes pertenecen a tests y métodos triviales).

### Ciclo 2 — Buenas prácticas y robustez
- Se añadieron anotaciones `@Override` en todos los métodos que sobrescriben (`MissingOverride` reducido de 13 a 0).
- Se reemplazaron `System.out.println` y `printStackTrace` por el sistema de logging centralizado (`GameLog` / `GameLogger`).
- Se aplicó `final` a variables inmutables y parámetros de métodos en clases del dominio.
- Se corrigió la interfaz `Updatable` añadiendo la anotación `@FunctionalInterface` para resolver `ImplicitFunctionalInterface`.

### Ciclo 3 — Nomenclatura y estructura
- Se renombraron variables crípticas (`r`, `c`, `dr`, `dc`) por nombres descriptivos (`row`, `col`, `deltaRow`, `deltaCol`) en todo el paquete `domain`.
- Se añadieron llaves `{}` a bloques `if`/`for` de una sola línea para prevenir errores de mantenimiento.
- Se centralizaron literales mágicos en constantes (`CELL`, `MOVE_EVERY`, `HIT_IMMUNITY_TICKS`).

---

## 3. Justificación de Violaciones Residuales

Aunque corregimos la gran mayoría de las alertas, dejamos estas quietas a propósito porque cambiarlas empeoraba el código o no tenía sentido para esta entrega:

| Regla PMD | ¿Por qué se dejó así?  |
| :--- | :--- |
| **`AvoidLiteralsInIfCondition`** | Se activa porque el código busca palabras directas como "ROWS", "COIN" o "ENEMY" al leer los archivos de los niveles. Es mucho más fácil de leer y entender el código si dejamos la palabra directa ahí, en lugar de inventar una constante para cada una. |
| **`AvoidDuplicateLiterals`** | Aparece cuando repetimos el mismo texto en los mensajes de error o en los reportes (los logs). Crear una variable global para textos que solo se usan al avisar un error no aporta nada al juego y solo llena el código de líneas innecesarias. |
| **`GuardLogStatement`** | PMD pide que revisemos si el sistema de reportes está encendido antes de escribir. Pero estas alertas están en errores graves que **sí o sí** deben guardarse en el archivo de texto, por lo que esa revisión previa no hace falta. |
| **`AvoidBranchingStatementAsLastInLoop`** | Se usa un `return` para frenar en seco a la Inteligencia Artificial de la máquina si se queda sin monedas que buscar. Es la forma más rápida y limpia de apagar la IA en ese instante para que no se quede pensando en la nada. |
| **`AvoidUsingVolatile`** | Usamos `volatile` para avisarle a la memoria del computador que esa variable cambia rápido entre hilos. Como todo el proceso ya está protegido de forma segura con `synchronized`, la alerta se puede ignorar sin riesgos. |
| **`NullAssignment`** | Dejar las variables en `null` al final de los archivos de prueba (`tearDown`) es una práctica estándar en JUnit. Sirve para limpiar la memoria de la computadora y que una prueba no ensucie o afecte el resultado de la siguiente. |
| **`CommentRequired`** | PMD se queja de que faltan comentarios Javadoc en algunos métodos de prueba o funciones privadas muy pequeñas. No los pusimos porque el nombre del método ya explica exactamente lo que hace (ej. `testPlayerDiesWhenHittingEnemy()`); poner un comentario encima sería repetir lo mismo. |
---

## 4. Resultado Final

Tras los tres ciclos de mejora, se logró una reducción neta significativa en las categorías críticas:

![Reporte Final PMD](https://github.com/nivek2329/Proyecto-Final/blob/main/imagenes/E2.png?raw=true)

> **Nota:** Las cifras finales son estimadas porque PMD recalcula dinámicamente al modificar el código. El objetivo no fue eliminar violaciones a costa de legibilidad, sino **garantizar que el código de dominio esté libre de alertas críticas** (`errorprone`, `bestpractices`) mientras se documentan las decisiones de diseño.

---

## 5. Conclusión

El análisis estático demuestra que **The DOPO Hardest Game** cumple con estándares de calidad aceptables para una entrega final académica:

- **El paquete `domain` está libre de violaciones críticas** (no hay `NullPointerException` potenciales, ni recursos sin cerrar, ni comparaciones de objetos con `==`).
- **La arquitectura MVC respeta la separación de responsabilidades**: las alertas de `design` se concentran en los puntos de unión entre capas, lo cual es inherente al patrón.
- **El sistema de logging centralizado** (`GameLog`/`GameLogger`) reemplazó correctamente las salidas por consola, mejorando la trazabilidad de errores.
- Las violaciones residuales están **documentadas, justificadas y no representan riesgo** para la estabilidad del juego.

---

