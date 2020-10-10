package com.example.mensajesactividad;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String telefono;
    private String nombre;
    private String uri;


    public Usuario(String telefono, String nombre, String uri) {
        this.telefono = telefono;
        this.nombre = nombre;
        this.uri=uri;
    }


    public String getTelefono() {
        return telefono;
    }

    public String getNombre() {
        return nombre;
    }


    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "telefono='" + telefono + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
