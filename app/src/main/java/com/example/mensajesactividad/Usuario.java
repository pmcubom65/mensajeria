package com.example.mensajesactividad;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String telefono;
    private String nombre;
    private Bitmap foto;


    public Usuario(String telefono, String nombre) {
        this.telefono = telefono;
        this.nombre = nombre;
    //    this.foto=foto;
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

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "telefono='" + telefono + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
