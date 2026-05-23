package main.domain;

import java.awt.*;

/**
 * Fuente de vida: otorga una vida extra al jugador que la toca.
 * Desaparece permanentemente del nivel tras ser utilizada.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class LifeSource extends PowerUp {

    /** Color del núcleo de la fuente de vida. */
    public static final Color CORE_COLOR   = new Color(255, 70, 120);
    /** Color de brillo exterior de la fuente de vida. */
    public static final Color GLOW_COLOR   = new Color(255, 180, 210, 90);
    /** Color de la cruz blanca en la fuente de vida. */
    public static final Color CROSS_COLOR  = new Color(255, 255, 255, 220);

    private boolean permanentlyUsed;

    /**
     * Crea una fuente de vida en la posición indicada.
     *
     * @param row fila en el tablero
     * @param col columna en el tablero
     */
    public LifeSource(final int row, final int col) {
        super(row, col);
        this.permanentlyUsed = false;
    }

    /**
     * Marca la fuente de vida como usada permanentemente.
     */
    public void markPermanentlyUsed() {
        permanentlyUsed = true;
    }

    /**
     * Indica si la fuente de vida ha sido usada permanentemente.
     *
     * @return true si ya fue usada de manera permanente; false en caso contrario
     */
    public boolean isPermanentlyUsed() {
        return permanentlyUsed;
    }

    /**
     * Restaura el estado de uso permanente y consumo desde una partida guardada.
     *
     * @param wasConsumed indica si el power-up estaba consumido
     * @param wasPermanent indica si el power-up estaba usado permanentemente
     */
    public void restoreLifeSourceSavedState(final boolean wasConsumed, final boolean wasPermanent) {
        consumed = wasConsumed;
        permanentlyUsed = wasPermanent;
    }

    /**
     * Indica si la fuente de vida debe mostrarse en el tablero.
     *
     * @return true si aún no fue usada permanentemente; false en caso contrario
     */
    public boolean isVisibleOnBoard() {
        return !permanentlyUsed;
    }

    /**
     * Restaura la fuente de vida a su estado inicial no consumido, salvo si ya fue usada permanentemente.
     */
    @Override
    public void reset() {
        if (!permanentlyUsed) {
            super.reset();
        }
    }

    /**
     * Obtiene el color de la fuente de vida para su renderizado.
     *
     * @return color del núcleo
     */
    @Override
    public Color getColor() {
        return CORE_COLOR;
    }

    /**
     * Retorna el tipo de power-up en formato de texto.
     *
     * @return tipo de power-up ("LIFE_SOURCE")
     */
    @Override
    public String getType() {
        return "LIFE_SOURCE";
    }
}
