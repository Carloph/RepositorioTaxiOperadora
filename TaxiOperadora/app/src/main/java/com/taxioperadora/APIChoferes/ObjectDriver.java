package com.taxioperadora.APIChoferes;

/**
 * Created by carlos on 21/02/17.
 */

public class ObjectDriver {

    private String ID_UBICACION;
    private String ID_CHOFER;
    private String LATITUD;
    private String LONGITUD;
    private String ESTATUS;


    public String getID_UBICACION() {
        return ID_UBICACION;
    }

    public void setID_UBICACION(String ID_UBICACION) {
        this.ID_UBICACION = ID_UBICACION;
    }

    public String getID_CHOFER() {
        return ID_CHOFER;
    }

    public void setID_CHOFER(String ID_CHOFER) {
        this.ID_CHOFER = ID_CHOFER;
    }

    public String getLATITUD() {
        return LATITUD;
    }

    public void setLATITUD(String LATITUD) {
        this.LATITUD = LATITUD;
    }

    public String getLONGITUD() {
        return LONGITUD;
    }

    public void setLONGITUD(String LONGITUD) {
        this.LONGITUD = LONGITUD;
    }

    public String getESTATUS() {
        return ESTATUS;
    }

    public void setESTATUS(String ESTATUS) {
        this.ESTATUS = ESTATUS;
    }
}
