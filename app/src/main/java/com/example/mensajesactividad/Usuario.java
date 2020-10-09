package com.example.mensajesactividad;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String telefono;
    private String nombre;


    public Usuario(String telefono, String nombre) {
        this.telefono = telefono;
        this.nombre = nombre;
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

    @Override
    public String toString() {
        return "Usuario{" +
                "telefono='" + telefono + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
