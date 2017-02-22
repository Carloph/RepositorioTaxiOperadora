package com.taxioperadora.APIChoferes;

import android.os.AsyncTask;
import com.taxioperadora.Home;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by carlos on 21/02/17.
 */

public class AsynctaskDrivers extends AsyncTask<String, Void, ArrayList<ObjectDriver>> {
    public AsyncResponseValidator delegate = null;
    Home activity;
    ArrayList<ObjectDriver> driversList = new ArrayList<>();

    public interface AsyncResponseValidator {
        void processFinish(ArrayList<ObjectDriver> output);
    }

    public AsynctaskDrivers(Home activity, AsyncResponseValidator delegate) {
        this.activity = activity;
        this.delegate = delegate;

    }
    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(ArrayList<ObjectDriver>result) {

//        if(result.equals("error")){
          //  delegate.processFinish("error");
//        }else {
            delegate.processFinish(result);
//        }
    }

    @Override
    protected ArrayList<ObjectDriver> doInBackground(String... params) {

        try{

        Retrofit retrofittel =  new Retrofit.Builder().baseUrl("http://easytaxi.pe.hu/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        APIDrivers servicetel=  retrofittel.create(APIDrivers.class);
        Call<ListDriver> calltel = servicetel.getDrivers();
        calltel.enqueue(new Callback<ListDriver>() {

            @Override
            public void onResponse(Call<ListDriver> call, Response<ListDriver> response) {

                driversList = response.body().getUbicaciones();

            }

            @Override
            public void onFailure(Call<ListDriver> call, Throwable t) {

            }
        });


        } catch (Exception e) {
            e.printStackTrace();
        }

        return driversList;
    }
}