package com.example.valhallasoft.getbyid;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Sandoval on 24/02/2017.
 */

public interface GetTodos {
    @GET("obtener_todo.php")
    Call<Result> all();

    @GET("obtener_todo_por_id.php")
    Call<Todo> select(@Query("id") int id);

    @GET("obtener_choferes_por_user.php")
    Call<Usuario> getLogin(@Query("USUARIO") String usuario);
}
