package main.domain;

import java.awt.*;

/**
 * Moneda que cambia temporalmente el skin del jugador que la recolecta.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class SkinCoin extends Coin {

    private final String skinName;
    private final Color color;

    /**
     * Crea una moneda de skin en la posición indicada con el skin especificado.
     *
     * @param row      fila del tablero donde se ubica la moneda
     * @param col      columna del tablero donde se ubica la moneda
     * @param skinName nombre identificador del skin a otorgar
     */
    public SkinCoin(final int row, final int col, final String skinName) {
        super(row, col);
        this.skinName = skinName;
        this.color = resolveColor(skinName);
    }

    /**
     * Resuelve el color asociado al nombre del skin.
     *
     * @param skin nombre del skin
     * @return color correspondiente al skin
     */
    private Color resolveColor(final String skin) {
        if ("BLUE".equalsIgnoreCase(skin)) {
            return Color.BLUE;
        }
        if ("GREEN".equalsIgnoreCase(skin)) {
            return Color.GREEN;
        }
        return Color.RED;
    }

    /**
     * Retorna el nombre del skin otorgado por esta moneda.
     *
     * @return nombre del skin
     */
    public String getSkinName() {
        return skinName;
    }

    /**
     * Retorna el color visual de la moneda según el skin.
     *
     * @return color de la moneda
     */
    @Override
    public Color getColor() {
        return color;
    }

    /**
     * Retorna el tipo de moneda como cadena de texto.
     *
     * @return tipo de moneda con prefijo SKIN_
     */
    @Override
    public String getType() {
        return "SKIN_" + skinName.toUpperCase();
    }
}