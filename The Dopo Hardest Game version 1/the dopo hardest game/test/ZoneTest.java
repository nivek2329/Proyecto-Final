package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import domain.Zone;

/**
 * Clase de pruebas unitarias para la clase Zone.
 * Verifica el comportamiento correcto de las zonas del tablero,
 * comprobando la detección de coordenadas internas y externas,
 * así como la correcta asignación de los valores definidos
 * mediante el constructor.
 *
 * Estas pruebas aseguran que las zonas cumplan su función como
 * áreas especiales dentro del dominio del juego.
 *
 * @return conjunto de pruebas unitarias para la clase Zone
 * @author Angel-Garcia
 * @date 2026-1
 */
public class ZoneTest {

    private Zone zone;

    /**
     * @return inicialización de una zona de prueba antes de cada test
     */
    @Before
    public void setUp() {
        zone = new Zone(2, 0, 4, 4, Zone.Type.INITIAL);
    }

    /**
     * @return liberación de la zona de prueba después de cada test
     */
    @After
    public void tearDown() {
        zone = null;
    }

    /**
     * @return verificación de detección correcta de coordenadas dentro y fuera de la zona
     */
    @Test
    public void shouldContieneInteriorYRechazaFuera() {
        assertTrue("(2,0) debe estar dentro de la zona", zone.contains(2, 0));
        assertTrue("(5,3) debe estar dentro de la zona", zone.contains(5, 3));
        assertFalse("(1,0) debe estar fuera de la zona (fila anterior al inicio)", zone.contains(1, 0));
        assertFalse("(6,0) debe estar fuera de la zona (fila posterior al fin)", zone.contains(6, 0));
        assertFalse("(2,4) debe estar fuera de la zona (columna exactamente en el limite)", zone.contains(2, 4));
    }

    /**
     * @return comprobación de que los getters coinciden con los valores definidos en el constructor
     */
    @Test
    public void shouldGettersCoincidenConstructor() {
        Zone z = new Zone(1, 2, 3, 5, Zone.Type.FINAL);

        assertEquals("getRow() debe retornar la fila del constructor", 1, z.getRow());
        assertEquals("getCol() debe retornar la columna del constructor", 2, z.getCol());
        assertEquals("getRows() debe retornar el alto del constructor", 3, z.getRows());
        assertEquals("getCols() debe retornar el ancho del constructor", 5, z.getCols());
        assertEquals("getType() debe retornar Zone.Type.FINAL", Zone.Type.FINAL, z.getType());
    }
}