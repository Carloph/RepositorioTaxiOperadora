package com.taxioperadora.APIChoferes;

/**
 * Created by carlos on 21/02/17.
 */

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by carlos on 08/01/17.
 */

public interface APIDrivers {

    @GET("/obtener_ubicacion_choferes.php")
    Call<ListDriver>getDrivers();

}
