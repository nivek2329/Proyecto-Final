package main.presentation;

import java.awt.*;

/**
 * Datos de configuración seleccionados en el menú antes de iniciar la partida.
 *
 * @author Angel-Garcia
 * @version 2026-1
 */
public final class GameSetup {

    private final String levelPath;
    private final String modalidad;
    private final String skin1;
    private final String skin2;
    private final Color  border1;
    private final Color  border2;
    private final String maquina;

    /**
     * Crea la configuración de partida con los parámetros seleccionados.
     *
     * @param levelPath  ruta del archivo de nivel
     * @param modalidad  modo de juego seleccionado
     * @param skin1      skin del jugador 1
     * @param skin2      skin del jugador 2 o máquina
     * @param border1    color del borde del jugador 1
     * @param border2    color del borde del jugador 2
     * @param maquina    tipo de máquina si aplica
     */
    public GameSetup(final String levelPath, final String modalidad,
                     final String skin1,    final String skin2,
                     final Color  border1,  final Color  border2,
                     final String maquina) {
        this.levelPath = levelPath;
        this.modalidad = modalidad;
        this.skin1     = skin1;
        this.skin2     = skin2;
        this.border1   = border1;
        this.border2   = border2;
        this.maquina   = maquina;
    }

    /**
     * Retorna la ruta del archivo de nivel seleccionado.
     *
     * @return ruta del nivel
     */
    public String getLevelPath() { 
        return levelPath; 
    }

    /**
     * Retorna la modalidad de juego seleccionada.
     *
     * @return modalidad de juego
     */
    public String getModalidad() { 
        return modalidad; 
    }

    /**
     * Retorna el skin seleccionado para el jugador 1.
     *
     * @return skin del jugador 1
     */
    public String getSkin1() { 
        return skin1;     
    }

    /**
     * Retorna el skin seleccionado para el jugador 2 o máquina.
     *
     * @return skin del jugador 2
     */
    public String getSkin2() { 
        return skin2;     
    }

    /**
     * Retorna el color de borde seleccionado para el jugador 1.
     *
     * @return color del borde del jugador 1
     */
    public Color  getBorder1() { 
        return border1;   
    }

    /**
     * Retorna el color de borde seleccionado para el jugador 2.
     *
     * @return color del borde del jugador 2
     */
    public Color  getBorder2() { 
        return border2;   
    }

    /**
     * Retorna el tipo de máquina seleccionado para el modo PvM.
     *
     * @return tipo de máquina
     */
    public String getMaquina() { 
        return maquina;   
    }
}