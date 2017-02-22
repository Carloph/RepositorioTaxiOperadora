package com.taxioperadora;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.location.LocationListener;
import android.Manifest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taxioperadora.APIChoferes.APIDrivers;
import com.taxioperadora.APIChoferes.ListDriver;
import com.taxioperadora.APIChoferes.ObjectDriver;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,LocationListener {

    private ListView lv_taxis;

    public ArrayList<ObjectDriver> array_drivers;
    public ArrayList<LatLng> coordenadas;

    private GoogleMap mMap;
    public SupportMapFragment mapFragment;
    private LocationManager locationManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String values[] = new String[]{"Uno","Dos","Tres","Cuatro","Cinco","Seis","Siete","Ocho","Nueve"};

        lv_taxis = (ListView) findViewById(R.id.list_view_inside_nav);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, values);

        lv_taxis.setAdapter(adapter);

        lv_taxis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplication(),"Esta es la posición del list"+position,Toast.LENGTH_LONG).show();
            }
        });


            //Intanciar
            mapFragment
                    = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)return;
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                onLocationChanged(location);

            }
            locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

            //Hasta aquí

            array_drivers = new ArrayList<>();
            coordenadas = new ArrayList<>();


            final Handler handler = new Handler();
            Timer timer = new Timer();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                Retrofit retrofit =  new Retrofit.Builder().baseUrl("http://easytaxi.pe.hu/")
                                        .addConverterFactory(GsonConverterFactory.create()).build();

                                APIDrivers service=  retrofit.create(APIDrivers.class);
                                Call<ListDriver> calltel = service.getDrivers();
                                calltel.enqueue(new Callback<ListDriver>() {

                                    @Override
                                    public void onResponse(Call<ListDriver> call, Response<ListDriver> response) {

                                        array_drivers.clear();

                                        coordenadas.clear();

                                        array_drivers = response.body().getUbicaciones();

                                        mapFragment.getMapAsync(Home.this);

                                        Toast.makeText(getApplication(),"Actualizado",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<ListDriver> call, Throwable t) {
                                    }
                                });


                                Retrofit retrofitlist =  new Retrofit.Builder().baseUrl("http://easytaxi.pe.hu/")
                                        .addConverterFactory(GsonConverterFactory.create()).build();

                                APIDrivers servicelist=  retrofitlist.create(APIDrivers.class);
                                Call<ListDriver> calllist = servicelist.getDrivers();
                                calllist.enqueue(new Callback<ListDriver>() {

                                    @Override
                                    public void onResponse(Call<ListDriver> call, Response<ListDriver> response) {

                                        array_drivers.clear();

                                        coordenadas.clear();

                                        array_drivers = response.body().getUbicaciones();

                                        mapFragment.getMapAsync(Home.this);

                                        Toast.makeText(getApplication(),"Actualizado",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<ListDriver> call, Throwable t) {
                                    }
                                });

                               } catch (Exception e) {
                                Log.e("error", e.getMessage());
                            }
                        }
                    });
                }
            };

            timer.schedule(task, 0, 30000);

        } catch (Exception e) {

            Toast.makeText(getApplication(), "Hubo un error al obtener la ubicación .1 " + e, Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_configuration) {

            Toast.makeText(getApplication(),"Este es la configuración",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_sign_off) {

            Toast.makeText(getApplication(),"Esto es cerrar sesión",Toast.LENGTH_SHORT).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try{

        mMap = googleMap;
        mMap.clear();

        if(array_drivers.isEmpty()){

            Toast.makeText(getApplication(),"Cargando taxis",Toast.LENGTH_SHORT).show();

        }else{

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) return;
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);


            for (int i= 0;i<array_drivers.size();i++){

                if (array_drivers.get(i).getESTATUS().equals("1")){
                    Marker melbourne = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(array_drivers.get(i).getLATITUD()),Double.parseDouble(array_drivers.get(i).getLONGITUD())))
                        .title(array_drivers.get(i).getID_CHOFER().toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.taxiverde)));
                    melbourne.showInfoWindow();

                }else if(array_drivers.get(i).getESTATUS().equals("2")){
                    Marker melbourne = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(array_drivers.get(i).getLATITUD()),Double.parseDouble(array_drivers.get(i).getLONGITUD())))
                            .title(array_drivers.get(i).getID_CHOFER().toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.taxiamarillo)));
                    melbourne.showInfoWindow();

                }else if(array_drivers.get(i).getESTATUS().equals("3")){
                    Marker melbourne = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(array_drivers.get(i).getLATITUD()),Double.parseDouble(array_drivers.get(i).getLONGITUD())))
                            .title(array_drivers.get(i).getID_CHOFER().toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.taxinaranja)));
                    melbourne.showInfoWindow();

                }else if(array_drivers.get(i).getESTATUS().equals("4")){
                    Marker melbourne = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(array_drivers.get(i).getLATITUD()),Double.parseDouble(array_drivers.get(i).getLONGITUD())))
                            .title(array_drivers.get(i).getID_CHOFER().toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.taxirojo)));
                    melbourne.showInfoWindow();

                }else{
                    Toast.makeText(getApplication(),"No se encontraron taxis",Toast.LENGTH_SHORT).show();
                }
            }
        }
        }catch (Exception e){
            Toast.makeText(getApplication(),"Hubo un error"+e,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
