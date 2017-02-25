package com.taxioperadora.APIChoferes;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by carlos on 24/02/17.
 */

public interface APIService {

  @FormUrlEncoded
    @POST("services/insert_driver_petition.php")
    Call<MSG> insertPetition(@Field("ID_CHOFER") int idchofer,
                              @Field("LATITUD_CLIENTE") double latitud_cliente,
                              @Field("LONGITUD_CLIENTE") double longitud_cliente,
                              @Field("LATITUD_DESTINO") double latitud_destino,
                              @Field("LONGITUD_DESTINO") double longitud_destino);
}
