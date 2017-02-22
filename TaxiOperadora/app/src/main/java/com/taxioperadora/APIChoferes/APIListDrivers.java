package com.taxioperadora.APIChoferes;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by carlos on 22/02/17.
 */

public interface APIListDrivers {

    @GET("obtener_ubicacion_choferes_libres.php")
    Call<ListDriver> getDrivers();

}
