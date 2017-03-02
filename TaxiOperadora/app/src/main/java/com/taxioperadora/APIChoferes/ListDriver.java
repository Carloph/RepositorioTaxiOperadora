package com.taxioperadora.APIChoferes;

import java.util.ArrayList;

/**
 * Created by carlos on 21/02/17.
 */

public class ListDriver {

    private ArrayList<ObjectDriver> ubicaciones = new ArrayList<>();

    public ArrayList<ObjectDriver> getUbicaciones()  {
        return ubicaciones;
    }

    public void setUbicaciones(ArrayList<ObjectDriver> ubicaciones) {
        this.ubicaciones = ubicaciones;
    }
}
