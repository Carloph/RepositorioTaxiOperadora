package com.taxioperadora;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationListener;
import android.Manifest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.taxioperadora.APIChoferes.APIClient;
import com.taxioperadora.APIChoferes.APIDrivers;
import com.taxioperadora.APIChoferes.APIService;
import com.taxioperadora.APIChoferes.ListDriver;
import com.taxioperadora.APIChoferes.MSG;
import com.taxioperadora.APIChoferes.ObjectDriver;
import com.taxioperadora.DirectionsMaps.DirectionFinder;
import com.taxioperadora.DirectionsMaps.DirectionFinderListener;
import com.taxioperadora.DirectionsMaps.Route;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,LocationListener, DirectionFinderListener {

    private ListView lv_taxis;

    static public ArrayList<ObjectDriver> array_drivers;
    static public ArrayList<ObjectDriver> array_drivers_availables;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    public GoogleMap mMap;
    public SupportMapFragment mapFragment;
    private LocationManager locationManager;

    private Button btn_origin,btn_destination;

    private final static int  CODE_ORIGIN = 1;
    private final static int  CODE_DESTINATION = 2;
    public static String coordinates_origin="";
    public static String coordinates_destination="";


    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();
    public int nCounter = 0;

    public TextView tv_distance,tv_time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btn_origin = (Button) findViewById(R.id.button_origin);
        btn_destination = (Button) findViewById(R.id.button_destination);
        lv_taxis = (ListView) findViewById(R.id.list_view_inside_nav);
        tv_distance = (TextView) findViewById(R.id.tvDistance);
        tv_time = (TextView) findViewById(R.id.tvDuration);

        btn_destination.setEnabled(false);

        btn_origin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent_filter =  new Intent(Home.this,DirectionFilter.class);
                 intent_filter.putExtra("CODE",CODE_ORIGIN);
                 startActivityForResult(intent_filter,CODE_ORIGIN);
                 stopTask();
                }
        });

        btn_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                     Intent intent_filter =  new Intent(Home.this,DirectionFilter.class);
                     intent_filter.putExtra("CODE",CODE_DESTINATION);
                     startActivityForResult(intent_filter,CODE_DESTINATION);
                 }

        });

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
            array_drivers_availables = new ArrayList<>();

            doTimerTask();


        } catch (Exception e) {

            Toast.makeText(getApplication(), "Hubo un error al obtener la ubicación .1 " + e, Toast.LENGTH_LONG).show();

        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        try{

        this.mMap = googleMap;

        if(array_drivers.isEmpty()){
            Toast.makeText(getApplication(),"Cargando taxis",Toast.LENGTH_SHORT).show();
            mMap.clear();
        }else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) return;
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.clear();
            for (int i= 0;i<array_drivers.size();i++){

                if(array_drivers.get(i).getESTATUS().equals("0")){

                }
                else if (array_drivers.get(i).getESTATUS().equals("1")){

                  Marker marker_green =
                          mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(array_drivers.get(i).getLATITUD()),Double.parseDouble(array_drivers.get(i).getLONGITUD())))
                        .title(array_drivers.get(i).getID_CHOFER()).icon(BitmapDescriptorFactory.fromResource(R.drawable.taxiverde)));
                  marker_green.hideInfoWindow();

                }else if(array_drivers.get(i).getESTATUS().equals("2")){

                    Marker marker_yellow=
                         mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(array_drivers.get(i).getLATITUD()),Double.parseDouble(array_drivers.get(i).getLONGITUD())))
                            .title(array_drivers.get(i).getID_CHOFER()).icon(BitmapDescriptorFactory.fromResource(R.drawable.taxiamarillo)));
                    marker_yellow.hideInfoWindow();

                }else if(array_drivers.get(i).getESTATUS().equals("3")){

                   Marker marker_orange =
                          mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(array_drivers.get(i).getLATITUD()),Double.parseDouble(array_drivers.get(i).getLONGITUD())))
                            .title(array_drivers.get(i).getID_CHOFER()).icon(BitmapDescriptorFactory.fromResource(R.drawable.taxinaranja)));
                    marker_orange.hideInfoWindow();

                }else if(array_drivers.get(i).getESTATUS().equals("4")){

                    Marker marker_red =
                           mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(array_drivers.get(i).getLATITUD()),Double.parseDouble(array_drivers.get(i).getLONGITUD())))
                                   .title(array_drivers.get(i).getID_CHOFER()).icon(BitmapDescriptorFactory.fromResource(R.drawable.taxirojo)));
                    marker_red.hideInfoWindow();

                }else{
                    Toast.makeText(getApplication(),"No se encontraron taxis",Toast.LENGTH_SHORT).show();
                }
            }
        }


        //mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(getApplication())));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                stopTask();
                /// Puede que aquí haya un problema debido al título que no sea el id_chofer
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                doTimerTask();
                /*if(btn_origin.getText().toString().equals(R.string.origin) || btn_destination.getText().toString().equals(R.string.destination)){

                }else{
                    btn_origin.setText(R.string.origin);
                    btn_destination.setText(R.string.destination);
                    btn_destination.setEnabled(false);
                }*/
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(final Marker marker) {
                    try {
                        stopTask();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this);

                        final EditText input = new EditText(Home.this);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        dialog.setView(input);
                        input.setHint("Ingresa un mensaje para el conductor");

                        dialog.setCancelable(false);
                        dialog.setTitle("Solicitar viaje");
                        dialog.setMessage("¿Estás seguro de asignar este viaje al taxista?" );
                        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //Action for "Delete".
                                if(coordinates_origin.equals("") || coordinates_destination.equals("")){
                                    Toast.makeText(getApplicationContext(),"Primero elija el origen y destino",Toast.LENGTH_SHORT).show();
                                    doTimerTask();
                                }else{


                                    int id_driver = Integer.parseInt(marker.getTitle());

                                    String [] split_origin =  coordinates_origin.split(",");
                                    String var1 = split_origin [0];
                                    double origin_lat = Double.parseDouble(var1);
                                    String var2 = split_origin [1];
                                    double origin_lng = Double.parseDouble(var2);

                                    String [] split_destination =  coordinates_destination.split(",");
                                    String var01 = split_destination [0];
                                    double destination_lat = Double.parseDouble(var01);
                                    String var02 = split_destination [1];
                                    double destination_lng = Double.parseDouble(var02);
                                    String message = input.getText().toString();
                                    if (message.equals(null) || message.equals("")){
                                        Toast.makeText(getApplication(),"Para poder enviar la petición escribe un mensaje al conductor",Toast.LENGTH_LONG).show();
                                    }else{
                                        insert_petition(id_driver,origin_lat,origin_lng,destination_lat,destination_lng,message);
                                        doTimerTask();
                                    }

                                }


                            }
                        }).setNegativeButton("Cancelar ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Action for "Cancel".
                                doTimerTask();
                            }
                        });

                        final AlertDialog alert = dialog.create();
                        alert.show();

                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.e("Error", " Occured");
                    }

                }
            });


        }catch (Exception e){
            Toast.makeText(getApplication(),"Hubo un error"+e,Toast.LENGTH_SHORT).show();
        }


    }

    private void sendRequest(String orign,String destin) {
       // String origin = lat + "," + lng;

       // String destination = latitude + "," + longitude;
        //String ruta = rut;
        stopTask();

        if (orign.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese la dirección de origen!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destin.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese la dirección de destino!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            new DirectionFinder(this, orign, destin).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {

        progressDialog = ProgressDialog.show(this, "Por favor espere.",
                "Localizando dirección..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        try {
            progressDialog.dismiss();
            polylinePaths = new ArrayList<>();
            originMarkers = new ArrayList<>();
            destinationMarkers = new ArrayList<>();

            for (Route route : routes) {
                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 13));
                ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
                ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

                originMarkers.add(mMap.addMarker(new MarkerOptions()
           //             .title(nombre)
                        .position(route.startLocation)));
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(route.endAddress)
                        .position(route.endLocation)));

                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.BLUE).
                        width(10);

                for (int i = 0; i < route.points.size(); i++)
                    polylineOptions.add(route.points.get(i));

                polylinePaths.add(mMap.addPolyline(polylineOptions));
            }

            Double origin_lat,origin_lng;
            Double destination_lat,destination_lng;

            String cadena[] = coordinates_origin.split(",");
            origin_lat = Double.parseDouble(cadena[0]);
            origin_lng = Double.parseDouble(cadena[1]);

            String cadena2[] = coordinates_destination.split(",");
            destination_lat = Double.parseDouble(cadena2[0]);
            destination_lng = Double.parseDouble(cadena2[1]);

            LatLng var_origen = new LatLng(origin_lat,origin_lng);
            LatLng var_destino = new LatLng(destination_lat,destination_lng);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(var_origen);
            builder.include(var_destino);
            LatLngBounds bounds = builder.build();

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
            mMap.animateCamera(cu, new GoogleMap.CancelableCallback() {
                public void onCancel() {
                }

                public void onFinish() {
                    CameraUpdate zout = CameraUpdateFactory.zoomBy(-1.0f);
                    mMap.animateCamera(zout);
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplication(), "Hubo un error al obtener la ubicación .2" + e, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void startActivityForResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=50){

            if(requestCode==CODE_ORIGIN){

                coordinates_origin=data.getExtras().getString("data");
                String address = data.getExtras().getString("address");
                btn_origin.setText(address);
                btn_destination.setEnabled(true);

            }
            else if(requestCode == CODE_DESTINATION){
                coordinates_destination=data.getExtras().getString("data");
                String address = data.getExtras().getString("address");
                btn_destination.setText(address);
                sendRequest(coordinates_origin,coordinates_destination);

            }

        }else{
            Toast.makeText(getApplicationContext(),"No eligió ningún viaje",Toast.LENGTH_SHORT).show();
            doTimerTask();
        }
}

    public void doTimerTask(){

        mTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        nCounter++;
                        // update TextView


                        Retrofit retrofit =  new Retrofit.Builder().baseUrl("http://seec.com.mx/taxaApp/")
                                .addConverterFactory(GsonConverterFactory.create()).build();

                        APIDrivers service=  retrofit.create(APIDrivers.class);
                        Call<ListDriver> calltel = service.getDrivers();
                        calltel.enqueue(new Callback<ListDriver>() {

                            @Override
                            public void onResponse(Call<ListDriver> call, Response<ListDriver> response) {



                                if(response.isSuccessful()){


                                    if(array_drivers.isEmpty() || array_drivers.size()!= response.body().getUbicaciones().size() ){
                                        array_drivers.clear();
                                        array_drivers = response.body().getUbicaciones();
                                        mapFragment.getMapAsync(Home.this);
                                    }else
                                    {
                                        for (int i = 0; i<response.body().getUbicaciones().size();i++){
                                            array_drivers.get(i).setESTATUS(response.body().getUbicaciones().get(i).getESTATUS());
                                            array_drivers.get(i).setLATITUD(response.body().getUbicaciones().get(i).getLATITUD());
                                            array_drivers.get(i).setLONGITUD(response.body().getUbicaciones().get(i).getLONGITUD());
                                        }
                                        mapFragment.getMapAsync(Home.this);
                                    }

                                    Log.e("Se ha actualizado"," la ubicacion de todos los taxis");

                                }else {

                                    Log.e("Hubo un error","al obtener la ubicación de los taxis");

                                    Toast.makeText(getApplication(),"Hubo un error al obtener los datos",Toast.LENGTH_SHORT).show();

                                }

                            }

                            @Override
                            public void onFailure(Call<ListDriver> call, Throwable t) {
                            }
                        });

                        Log.e("TIMER", "TimerTask run");
                    }
                });
            }};

        // public void schedule (TimerTask task, long delay, long period)
        t.schedule(mTimerTask, 0, 10000);  //

    }

    public void stopTask(){

        if(mTimerTask!=null){

            Log.e("Se ha cancelado", "la ubicación de los taxis");

            mTimerTask.cancel();
        }

    }
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        stopTask();
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

    public void insert_petition(int id, double var_orig_lat, double var_orig_lng, double var_dest_lat, double var_dest_lng, String mensaje){

        APIService service = APIClient.getClient().create(APIService.class);
        //User user = new User(name, email, password);


        Call<MSG> userCall = service.insertPetition(id, var_orig_lat, var_orig_lng, var_dest_lat, var_dest_lng,mensaje);

        userCall.enqueue(new Callback<MSG>() {
            @Override
            public void onResponse(Call<MSG> call, Response<MSG> response) {

                Log.d("onResponse", "" + response.body().getMessage());


                if(response.body().getSuccess() == 1) {
                    tv_time.setText("0 min");
                    tv_distance.setText("0 km");
                    btn_origin.setText(R.string.origin);
                    btn_destination.setText(R.string.destination);
                    Log.e("onResponse", "" + "Se ha registrado la solicitud de taxi");
                    coordinates_origin = "";
                    coordinates_destination = "";
                    Toast.makeText(getApplicationContext(),"Se ha asignado el viaje al taxi espere el cambio de color",Toast.LENGTH_SHORT).show();

                    // startActivity(new Intent(SignupActivity.this, MainActivity.class));
                }else {
                    Toast.makeText(Home.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("onResponse", "" + "Ha habido un error");

                }
            }

            @Override
            public void onFailure(Call<MSG> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });

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
