package main.domain;

import java.awt.*;

/**
 * Clase concreta que representa una moneda de color amarillo.
 * El jugador debe recolectar todas las monedas para completar el nivel.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class YellowCoin extends Coin {

    /**
     * Crea una moneda amarilla en la posición indicada.
     *
     * @param row fila del tablero donde se ubica la moneda
     * @param col columna del tablero donde se ubica la moneda
     */
    public YellowCoin(final int row, final int col) {
        super(row, col);
    }

    /**
     * Retorna el color amarillo de la moneda.
     *
     * @return color amarillo de la moneda (Color.YELLOW)
     */
    @Override
    public Color getColor() { 
        return Color.YELLOW; 
    }

    /**
     * Retorna el tipo de moneda como cadena de texto.
     *
     * @return tipo de moneda ("YELLOW")
     */
    @Override
    public String getType() { 
        return "YELLOW"; 
    } 
}