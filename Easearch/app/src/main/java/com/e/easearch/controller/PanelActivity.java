package com.e.easearch.controller;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.e.easearch.R;
import com.e.easearch.model.Patient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class PanelActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;

    // SHARED PREFERENCES
    SharedPreferences pref;
    Intent intent;

    // API
    ArrayList<Patient> pacientes = new ArrayList<Patient>();

    // UBICACIÓN
    LocationManager locationManager;
    Location loc;
    double longitudeBest, latitudeBest;
    double longitudeGPS, latitudeGPS;
    double longitudeNetwork, latitudeNetwork;
    TextView longitudeValueBest, latitudeValueBest;
    TextView longitudeValueGPS, latitudeValueGPS;
    TextView longitudeValueNetwork, latitudeValueNetwork;

    // LISTADO
    //String uid;
    private ListView lvItems;
    private ListViewAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        //Bundle b = getIntent().getExtras();
        //uid = b.getString("Uid");

        pref = getSharedPreferences("user_details", Context.MODE_PRIVATE);
        intent = new Intent(this, LoginActivity.class);







        // ------------- INICIO CONSULTA A LA API ----------------

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(this, "No hay conexión. Active sus datos móviles o conéctese a una red WiFi", Toast.LENGTH_LONG).show();
        }

        requestAllDestinos(new View(this));

        // -------------- FIN CONSULTA A LA API -------------------



        // ------------------ INICIO LISTADO ----------------------

        progressBar = findViewById(R.id.loadingResults);
        lvItems = findViewById(R.id.lvItems);

        // ------------------- FIN LISTADO ------------------------


        // ------------------ INICIO UBICACIÓN ------------------

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        toggleBestUpdates();

        // ------------------ FIN UBICACIÓN ----------------------

    }








    // ------------- INICIO GETTERS Y SETTERS ----------------

    public double getLongitudeBest() {
        return longitudeBest;
    }

    public double getLatitudeBest() {
        return latitudeBest;
    }

    public void setPacientes(ArrayList<Patient> pacientes) {
        this.pacientes = pacientes;
    }

    public ArrayList<Patient> getPacientes() {
        return pacientes;
    }
    // --------------- FIN GETTERS Y SETTERS -----------------



    // ------------- INICIO CONSULTA A LA API ----------------

    public void requestAllDestinos(View view) {

        String url = "http://ppc.edit.com.ar:8080/resources/datos/pacientes/-34.581727/-60.931513";

        Type type = new TypeToken<ArrayList<Patient>>(){}.getType();

        final GsonRequest gsonRequest = new GsonRequest(Request.Method.GET, url, new JSONObject(), type,
                new Response.Listener<ArrayList<Patient>>() {
                    @Override
                    public void onResponse(ArrayList<Patient> response) {

                        try {

                            loadListView(response);
                            progressBar.setVisibility(View.GONE);
                            // No se puede setear el ArrayList<Patient> patients; desde aca ya que esta La tarea asincrónica está
                            // trabajando en otro hilo. Entonces, si desea acceder a cualquier variable fuera de ese método, debe
                            // esperar hasta que se complete la tarea. De lo contrario, la variable será nula.


                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){

                    }

        });

        gsonRequest.setTag(1);

        MySingleton.getInstance(this).addToRequestQueue(gsonRequest);
    }

    // -------------- FIN CONSULTA A LA API -------------------


    // ------------------ INICIO LISTADO ----------------------

    public void loadListView(ArrayList<Patient> response){
        setPacientes(response);

        // Ordenar el array en base a la distancia
        for(Patient p : getPacientes()){
            int distancia = calcularDistancia(p.getLatitudActual(), p.getLongitudActual());
            Log.i("Latitud", String.valueOf(getLatitudeBest()));
            Log.i("Longitud", String.valueOf(getLongitudeBest()));
            p.setDistanciaUbicacion(distancia);
        }



        adapter = new ListViewAdapter(this, pacientes);
        lvItems.setAdapter(adapter);
    }

    // ------------------- FIN LISTADO ------------------------




    // ------ INICIO UBICACIÓN ------

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }


    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\n por favor actívela")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }


    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void toggleBestUpdates() {
        if (!checkLocation())
            return;
        locationManager.removeUpdates(locationListenerBest);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {

            // Seteo inicial con las últimas coordenadas conocidas (hasta obtener la actualizacion de la ubicación)
            loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if(loc != null){
                longitudeBest = loc.getLongitude();
                latitudeBest = loc.getLatitude();
            }
            // Fin seteo inicial

            locationManager.requestLocationUpdates(provider, 1 * 10 * 1000, 10, locationListenerBest);
            Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
        }
    }


    private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeBest = location.getLongitude();
            latitudeBest = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadListView(getPacientes());
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };


    public int calcularDistancia(double latitud, double longitud){

        Location locPaciente = new Location("");
        locPaciente.setLatitude(latitud);
        locPaciente.setLongitude(longitud);

        Location locPropia = new Location("");
        locPropia.setLatitude(getLatitudeBest());
        locPropia.setLongitude(getLongitudeBest());

        int distanceInMeters = (int) locPaciente.distanceTo(locPropia);

        return distanceInMeters;
    }

    // ------------------- FIN UBICACIÓN ----------------------



    @Override
    public void onClick(View view) {
        String username = getFromSharedPreferences("username");
        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
    }

    private String getFromSharedPreferences(String key) {
        SharedPreferences sharedPref = this.getSharedPreferences("user_details", Context.MODE_PRIVATE);    // Se utilizan las Shared Preferences con nombre clave para acceder a ellas desde cualquier Activity
        return sharedPref.getString(key, "");

    }

    // MENÚ
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){

            case R.id.icon_logout:
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
                startActivity(intent);
                finish();

            default:break;
        }
        return true;
    }

}
