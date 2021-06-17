package com.e.easearch.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.easearch.R;
import com.e.easearch.model.Patient;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context context;
    private ArrayList<Patient> listItems;


    public ListViewAdapter(Context context, ArrayList<Patient> listItems) {
        this.context = context;
        this.listItems = listItems;

        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount(){
        return getListItems().size();
    }

    @Override
    public Object getItem(int position){
        return getListItems().get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent){

        final View vista = inflater.inflate(R.layout.item_list, null);

        TextView tvTitulo = (TextView) vista.findViewById(R.id.tvNombre);
        TextView tvContenido = (TextView) vista.findViewById(R.id.tvDescripcion);

        Patient p  = (Patient) getItem(i);

        tvTitulo.setText(p.getApellidoYNombre());

        DecimalFormat dfKM = new DecimalFormat("###.#");
        DecimalFormat dfMT = new DecimalFormat("###");
        double distance = p.getDistanciaUbicacion();
        String resultado;
        if(distance >= 1000){
            resultado = dfKM.format(distance/1000) + " Kms";
        } else {
            resultado = dfMT.format(distance) + " Mts";
        }
        tvContenido.setText(String.valueOf(resultado));

        return vista;
    }

    public ArrayList<Patient> getListItems() {
        return listItems;
    }

}


