package test;

import static org.junit.Assert.*;
import org.junit.Test;
import main.domain.SkinCoin;
import java.awt.Color;

/**
 * Clase de pruebas unitarias para la clase SkinCoin.
 * Verifica la correcta inicialización de los skins, colores y tipos.
 */
public class SkinCoinTest {

    @Test
    public void shouldResolveColorsCorrectly() {
        SkinCoin blueCoin = new SkinCoin(1, 2, "BLUE");
        assertEquals("El color para BLUE debe ser Color.BLUE", Color.BLUE, blueCoin.getColor());
        assertEquals("El nombre del skin debe ser BLUE", "BLUE", blueCoin.getSkinName());
        assertEquals("El tipo de moneda debe ser SKIN_BLUE", "SKIN_BLUE", blueCoin.getType());

        SkinCoin greenCoin = new SkinCoin(3, 4, "GREEN");
        assertEquals("El color para GREEN debe ser Color.GREEN", Color.GREEN, greenCoin.getColor());
        assertEquals("El tipo de moneda debe ser SKIN_GREEN", "SKIN_GREEN", greenCoin.getType());

        SkinCoin randomCoin = new SkinCoin(5, 6, "XYZ");
        assertEquals("Cualquier otro skin debe ser Color.RED por defecto", Color.RED, randomCoin.getColor());
        assertEquals("El tipo de moneda debe ser SKIN_XYZ", "SKIN_XYZ", randomCoin.getType());
    }
}
