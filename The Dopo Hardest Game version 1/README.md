# DOPO-2026 — The DOPO Hardest Game

Versión del videojuego *The World's Hardest Game*, desarrollada como proyecto de la asignatura **Programación Orientada a Objetos** de la **Escuela Colombiana de Ingeniería Julio Garavito**.

**Autores:** Kevin Angel · Santiago Garcia  
**Versión:** 1.0 — 2026-1

---

##  Descripción del proyecto
El jugador controla un cuadrado rojo que debe recolectar todas las monedas amarillas del tablero y llegar a la zona segura final antes de que se agote el tiempo, evitando el contacto con los enemigos azules. 

*   **Mecánica:** Cada colisión reinicia al jugador en la zona de inicio y suma una muerte al contador. 
*   **Victoria:** El nivel se completa cuando todas las monedas han sido recolectadas y el jugador alcanza la zona segura final.

### Elementos del juego
| Elemento | Descripción |
| :--- | :--- |
| **RedPlayer** | Jugador estándar, velocidad y tamaño normales. |
| **BasicEnemy** | Enemigo que se desplaza en línea recta, rebotando en paredes y zonas seguras. |
| **YellowCoin** | Moneda que debe recolectarse para completar el nivel. |
| **Zona inicial** | Punto de aparición y reaparición del jugador. |
| **Zona final** | Destino que el jugador debe alcanzar con todas las monedas. |

---

## Pasos para iniciar el proyecto en IntelliJ IDEA

Para poner en marcha el proyecto correctamente, siga estas instrucciones:

1.  **Abrir el proyecto:** Importe la carpeta raíz en IntelliJ IDEA.
2.  **Configurar dependencias:** Asegúrese de tener **JUnit 4** configurado en el *Build Path*.
3.  **Ejecutar la aplicación:** Localice la clase `GameGUI` y ejecute el método `main`.
4.  **Ejecutar pruebas con cobertura:** Haga clic derecho sobre el paquete `test` y seleccione **Run 'All Tests' with Coverage**.

 ---

## Análisis de calidad

- **Análisis dinámico:** ver [`Analisis-Dinamico.md`](./Analisis-Dinamico.md)
- **Análisis estático:** ver [`Analisis-Estatico.md`](./Analisis-Estatico.md)

---


---
##  Referencias
* Barnes, D. J., & Kölling, M. (2016). *Objects First with Java: A Practical Introduction Using BlueJ*.
* Oracle. (2024). *Java SE 21 Documentation*.
* Wikipedia. (2025). *The World's Hardest Game*.
* Escuela Colombiana de Ingeniería. (2026). *Enunciado The DOPO Hardest Game*.
* PMD. (2024). *PMD for Eclipse Plugin*.
