package main.domain;

import java.awt.*;

/**
 * Fuente de vida: otorga una vida extra al jugador que la toca.
 * Desaparece permanentemente del nivel tras usarse.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class LifeSource extends PowerUp {

    public static final Color CORE_COLOR   = new Color(255, 70, 120);
    public static final Color GLOW_COLOR   = new Color(255, 180, 210, 90);
    public static final Color CROSS_COLOR  = new Color(255, 255, 255, 220);

    private boolean permanentlyUsed;

    /**
     * Crea una fuente de vida en la posición indicada.
     *
     * @param row fila del tablero
     * @param col columna del tablero
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
     * Consulta si la fuente de vida ha sido usada permanentemente.
     *
     * @return true si ya no puede ser recolectada
     */
    public boolean isPermanentlyUsed() {
        return permanentlyUsed;
    }

    /**
     * Restaura el estado de uso permanente desde una partida guardada.
     *
     * @param wasConsumed  indica si la fuente de vida fue consumida en el ciclo actual
     * @param wasPermanent indica si la fuente de vida ya había sido consumida permanentemente
     */
    void restoreLifeSourceSavedState(final boolean wasConsumed, final boolean wasPermanent) {
        consumed = wasConsumed;
        permanentlyUsed = wasPermanent;
    }

    /**
     * Indica si debe mostrarse en el tablero.
     *
     * @return true si aún no fue usada permanentemente
     */
    public boolean isVisibleOnBoard() {
        return !permanentlyUsed;
    }

    @Override
    public void reset() {
        if (!permanentlyUsed) {
            super.reset();
        }
    }

    @Override
    public Color getColor() {
        return CORE_COLOR;
    }

    @Override
    public String getType() {
        return "LIFE_SOURCE";
    }
}
