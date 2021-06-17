package com.e.easearch.controller;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.e.easearch.R;
import com.e.easearch.model.Patient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PanelActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ProgressBar progressBar;

    private static final String URL = "http://ppc.edit.com.ar:8080/resources/datos/pacientes/-34.581727/-60.931513";
    boolean local = false;
    ArrayList<Patient> pacientes = new ArrayList<Patient>();

    // SHARED PREFERENCES
    SharedPreferences pref;
    Intent intent;

    // UBICACIÓN
    public static final String TAG = PanelActivity.class.getSimpleName();
    LocationManager locationManager;
    double latUser;
    double lonUser;

    // LISTADO
    private DrawerLayout drawer;
    private ListView lvItems;
    private ListViewAdapter adapter;
    private SwipeRefreshLayout l;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        pref = getSharedPreferences("user_details", Context.MODE_PRIVATE);
        intent = new Intent(this, LoginActivity.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        pref = getSharedPreferences("user_details", Context.MODE_PRIVATE);
        String userName = getFromSharedPreferences("name");
        View header = ((NavigationView)findViewById(R.id.navigation_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.textViewUserLog)).setText("¡" + getResources().getString(R.string.hola) + ", " + userName + "!");

        // ------------------ INICIO LISTADO ----------------------

        progressBar = findViewById(R.id.loadingResults);
        lvItems = findViewById(R.id.lvItems);

        l = findViewById(R.id.swiperefresh);


        // ------------------ INICIO UBICACIÓN ------------------

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // ------------------ FIN UBICACIÓN ----------------------

        refreshApp();


        // ------------- CHEQUEO DE CONEXIÓN A INTERNET ----------------

        if (!checkOnlineState()) {
            Intent settingsIntent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
            startActivity(settingsIntent);
        }


        // ------------- CHEQUEO DE CONEXIÓN A GPS ----------------

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(PanelActivity.this, PatientInfo.class);
                Gson gson = new Gson();
                String patient_json = gson.toJson(getPacientes().get(position));
                intent.putExtra("myjson", patient_json);
                intent.putExtra("latUser", String.valueOf(getLatUser()));
                intent.putExtra("longUser", String.valueOf(getLonUser()));
                startActivity(intent);
            }
        });
    }



    // -------------- GETTERS Y SETTERS ----------------

    public void setPacientes(ArrayList<Patient> pacientes) {
        this.pacientes = pacientes;
    }

    public ArrayList<Patient> getPacientes() {
        return pacientes;
    }

    public void setLatUser(double lat) {this.latUser = lat; }

    public double getLatUser(){
        return this.latUser;
    }

    public void setLonUser(double lon){
        this.lonUser = lon;
    }

    public double getLonUser(){
        return this.lonUser;
    }

    public String getUrl(){return this.URL;}



    // -------------- MENÚ IZQUIERDO

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menuHome){
            Intent intent = new Intent(PanelActivity.this, PanelActivity.class);
            startActivity(intent);
            finish();
        }

        if (id == R.id.menuMapa) {
            Uri uri = Uri.parse("geo:" + getLatUser() + "," + getLonUser());
            Intent intentMaps = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intentMaps);
        }

        if (id == R.id.menuDevelop) {
            openDialogDeveloperInfo();
        }

        if (id == R.id.menuSalir) {
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // -------------- CHEQUEO DE CONEXIÓN A INTERNET ----------------

    public boolean checkOnlineState() {
        ConnectivityManager CManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = CManager.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isConnectedOrConnecting()) {
            return true;
        }else{
            return false;
        }
    }


    // -------------- LOCALIZACIÓN ----------------

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setPanelActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        /*
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            mlocManager.requestLocationUpdates(provider, 0, 1000, (LocationListener) Local);
            Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
        }
        */

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1000, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1000, (LocationListener) Local);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public class Localizacion implements LocationListener {
        PanelActivity panelActivity;

        public PanelActivity getPanelActivity() {
            return panelActivity;
        }

        public void setPanelActivity(PanelActivity panelActivity) {
            this.panelActivity = panelActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas debido a la deteccion de un cambio de ubicacion del dispositivo
            this.panelActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "GPS Desactivado.");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "GPS Activado.");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    public void setLocation(Location loc) {
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    this.setLatUser(loc.getLatitude());
                    this.setLonUser(loc.getLongitude());
                    consultAPI();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // -------------- CONSULTA A LA API ----------------

    public void consultAPI() {

        Type type = new TypeToken<ArrayList<Patient>>(){}.getType();

        final GsonRequest gsonRequest = new GsonRequest(Request.Method.GET, getUrl(), new JSONObject(), type,
                new Response.Listener<ArrayList<Patient>>() {
                    @Override
                    public void onResponse(ArrayList<Patient> response) {

                        try {
                            Log.i(TAG,"CONEXION EXITOSA: "+URL);
                            local=false;
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
                        if (!local) {
                            Log.e(TAG, "ERROR DE CONEXION");
                        }
                    }

        });

        gsonRequest.setTag(1);

        MySingleton.getInstance(this).addToRequestQueue(gsonRequest);
    }

    // ------------------ LISTADO ----------------------

    public void loadListView(ArrayList<Patient> response){

        setPacientes(response);

        // Por cada paciente calculo su distancia respecto de mi
        for(Patient p : getPacientes()){
            double distancia = calcularDistancia(p.getLatitudActual(), p.getLongitudActual());
            p.setDistanciaUbicacion(distancia);
        }

        // Ordeno el array en base a la distancia de cada paciente respecto de mi
        Collections.sort(getPacientes(), new Comparator<Patient>(){

            public int compare(Patient p1, Patient p2)
            {
                return p1.getDistanciaUbicacion().compareTo(p2.getDistanciaUbicacion());
            }
        });

        adapter = new ListViewAdapter(this, getPacientes());
        lvItems.setAdapter(adapter);
    }


    public double calcularDistancia(double latitud, double longitud){

        Location locPaciente = new Location("");
        locPaciente.setLatitude(latitud);
        locPaciente.setLongitude(longitud);

        Location locPropia = new Location("");
        locPropia.setLatitude(getLatUser());
        locPropia.setLongitude(getLonUser());

        double distanceInMeters = locPaciente.distanceTo(locPropia);

        return distanceInMeters;
    }


    private String getFromSharedPreferences(String key) {
        SharedPreferences sharedPref = this.getSharedPreferences("user_details", Context.MODE_PRIVATE);    // Se utilizan las Shared Preferences con nombre clave para acceder a ellas desde cualquier Activity
        return sharedPref.getString(key, "");

    }


    // MENÚ DERECHO
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){

            case R.id.icon_refresh:
                consultAPI();

            default:break;
        }
        return true;
    }

    private void refreshApp(){
        l.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("", "Refrescado!");
                        consultAPI();
                        l.setRefreshing(false);
                    }
                }
        );
    }

    private void openDialogDeveloperInfo(){
        AlertDialog.Builder  builder = new AlertDialog.Builder(PanelActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_acerca_de, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button buttonAceptar = view.findViewById(R.id.buttonAceptar);
        buttonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
