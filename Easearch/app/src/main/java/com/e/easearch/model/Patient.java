package com.e.easearch.model;

import com.google.gson.annotations.SerializedName;

public class Patient {

    @SerializedName("descripcion")          // @SerializedName se utiliza para parsear los nombres de las claves JSON a los atributos
    private String apellidoYNombre;         // de la clase Java. Si no se pone @SerializedName, el nombre del atributo debe ser igual
                                            // al nombre de la clave en el JSON
    @SerializedName("domicilio")
    private String domicilioPaciente;

    @SerializedName("telefono")
    private String telefonoCelular;

    @SerializedName("detalle")
    private String detalleClinico;

    @SerializedName("tipo")
    private String tipoPersona;

    @SerializedName("latitud")
    private float latitudActual;

    @SerializedName("longitud")
    private float longitudActual;

    @SerializedName("valor")
    private float valorConsulta;

    @SerializedName("distancia")
    private Double distanciaUbicacion;


    public Patient() {
    }


    public String getApellidoYNombre() {
        return apellidoYNombre;
    }

    public void setApellidoYNombre(String apellidoYNombre) {
        this.apellidoYNombre = apellidoYNombre;
    }

    public String getDomicilioPaciente() {
        return domicilioPaciente;
    }

    public void setDomicilioPaciente(String domicilioPaciente) {
        this.domicilioPaciente = domicilioPaciente;
    }

    public String getTelefonoCelular() {
        return telefonoCelular;
    }

    public void setTelefonoCelular(String telefonoCelular) {
        this.telefonoCelular = telefonoCelular;
    }

    public String getDetalleClinico() {
        return detalleClinico;
    }

    public void setDetalleClinico(String detalleClinico) {
        this.detalleClinico = detalleClinico;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public float getLatitudActual() {
        return latitudActual;
    }

    public void setLatitudActual(float latitudActual) {
        this.latitudActual = latitudActual;
    }

    public float getLongitudActual() {
        return longitudActual;
    }

    public void setLongitudActual(float longitudActual) {
        this.longitudActual = longitudActual;
    }

    public float getValorConsulta() {
        return valorConsulta;
    }

    public void setValorConsulta(float valorConsulta) {
        this.valorConsulta = valorConsulta;
    }

    public Double getDistanciaUbicacion() {
        return distanciaUbicacion;
    }

    public void setDistanciaUbicacion(Double distanciaUbicacion) {
        this.distanciaUbicacion = distanciaUbicacion;
    }

}
