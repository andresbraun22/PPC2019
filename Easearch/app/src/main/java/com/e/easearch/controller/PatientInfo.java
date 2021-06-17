package com.e.easearch.controller;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.easearch.R;
import com.e.easearch.model.Patient;
import com.google.gson.Gson;

import java.text.DecimalFormat;

public class PatientInfo extends AppCompatActivity {

    TextView descripcion,detalle,distancia,domicilio, telefono,valor, tipo;
    Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        descripcion = findViewById(R.id.descripcionWebService);
        detalle = findViewById(R.id.detalleWebService);
        distancia = findViewById(R.id.distanciaWebService);
        domicilio = findViewById(R.id.domicilioWebService);
        telefono = findViewById(R.id.telefonoWebService);
        valor = findViewById(R.id.valorWebService);
        tipo = findViewById(R.id.tipoWebService);

        Gson gson = new Gson();
        patient = gson.fromJson(getIntent().getStringExtra("myjson"), Patient.class);

        DecimalFormat dfKM = new DecimalFormat("###.#");
        DecimalFormat dfMT = new DecimalFormat("###");
        double distance = patient.getDistanciaUbicacion();
        String resultado;
        if(distance >= 1000){
            resultado = dfKM.format(distance/1000) + " Kms";
        } else {
            resultado = dfMT.format(distance) + " Mts";
        }

        descripcion.setText(patient.getApellidoYNombre());
        distancia.setText(resultado);
        domicilio.setText(patient.getDomicilioPaciente());
        telefono.setText(patient.getTelefonoCelular());
        valor.setText(patient.getDetalleClinico());
        tipo.setText(String.valueOf(patient.getValorConsulta()) + " $");
    }


    private Patient getPatient(){
        return this.patient;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){

            case R.id.icon_indications:
                patientMapsIndication();

            default:break;
        }
        return true;
    }

    private void patientMapsIndication(){
        String latUser = getIntent().getStringExtra("latUser");
        String longUser = getIntent().getStringExtra("longUser");
        Uri uri = Uri.parse("http://www.google.co.in/maps/dir/" + latUser + "," + longUser + "/" + getPatient().getLatitudActual() + "," + getPatient().getLongitudActual());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}