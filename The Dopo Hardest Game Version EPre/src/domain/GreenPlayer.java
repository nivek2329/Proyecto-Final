package domain;

import java.awt.Color;

/**
 * Jugador verde (Clyde). Absorbe el primer golpe de enemigo sin morir,
 * pero pierde velocidad tras recibirlo (de 2 ticks pasa a 3 ticks,
 * equivalente a bajar de 1.0x a ~0.67x de velocidad).
 *
 * Tras absorber un golpe, tiene un período de invulnerabilidad
 * para evitar morir inmediatamente por permanecer en la misma celda.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public class GreenPlayer extends Player {

    private static final int TICKS_NORMAL       = 2;
    private static final int TICKS_SLOWED       = 3;
    private static final int INVULNERABLE_TICKS = 30;

    private boolean shieldActive;
    private int invulnerableTicks;

    /**
     * Crea un jugador verde en la posición indicada con escudo activo.
     *
     * @param row fila inicial del jugador en el tablero
     * @param col columna inicial del jugador en el tablero
     */
    public GreenPlayer(final int row, final int col) {
        super(row, col);
        this.shieldActive = true;
        this.invulnerableTicks = 0;
    }

    /**
     * Intenta absorber un golpe enemigo.
     * Si el escudo está activo no muere: pierde el escudo, se ralentiza
     * y gana invulnerabilidad temporal.
     *
     * @return true si el golpe fue absorbido (no debe morir),
     *         false si debe morir normalmente
     */
    public boolean absorbHit() {
        if (shieldActive) {
            shieldActive = false;
            invulnerableTicks = INVULNERABLE_TICKS;
            return true;
        }
        return false;
    }

    /**
     * Indica si el escudo sigue activo.
     *
     * @return true si aún puede absorber un golpe
     */
    public boolean hasShield() { 
        return shieldActive; 
    }

    /**
     * Indica si el jugador es invulnerable (post-golpe absorbido).
     *
     * @return true si no puede recibir daño en este momento
     */
    public boolean isInvulnerable() {
        return invulnerableTicks > 0;
    }

    /**
     * Decrementa el contador de invulnerabilidad. Llamar en cada tick.
     */
    public void tickInvulnerability() {
        if (invulnerableTicks > 0) {
            invulnerableTicks--;
        }
    }

    /**
     * Reubica al jugador en la zona indicada, recupera el escudo
     * y limpia el estado de invulnerabilidad.
     *
     * @param zone zona donde reaparecerá el jugador
     */
    @Override
    public void respawn(final Zone zone) {
        super.respawn(zone);
        shieldActive = true;
        invulnerableTicks = 0;
    }

    /**
     * Retorna el color verde del jugador.
     *
     * @return Color.GREEN
     */
    @Override public Color  getColor() { 
        return Color.GREEN; 
    }

    /**
     * Retorna el nombre identificador del jugador verde.
     *
     * @return "Clyde"
     */
    @Override public String getName()  { 
        return "Clyde";     
    }

    /**
     * Retorna el tamaño base del jugador.
     *
     * @return 1.0
     */
    @Override public double getSize()  { 
        return 1.0;         
    }

    /**
     * Retorna el intervalo de ticks entre movimientos.
     * Velocidad normal (2 ticks) mientras tiene escudo,
     * ralentizado (3 ticks) después de absorber el primer golpe.
     *
     * @return ticks entre movimientos según estado del escudo
     */
    @Override
    public int getMoveTicks() {
        return shieldActive ? TICKS_NORMAL : TICKS_SLOWED;
    }
}
