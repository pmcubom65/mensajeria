package com.example.mensajesactividad;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class Mensaje {

    private String contenido;
    private String fecha;
    private String telefono;


    public Mensaje(String contenido, String fecha, String telefono) {
        this.contenido = contenido;
        this.fecha = fecha;
        this.telefono=telefono;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getContenido() {
        return contenido;
    }

    public String getFecha() {
        return fecha;
    }


    @Override
    public String toString() {
        return "Mensaje{" +
                "contenido='" + contenido + '\'' +
                ", fecha='" + fecha + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mensaje mensaje = (Mensaje) o;
        return Objects.equals(contenido, mensaje.contenido) &&
                Objects.equals(fecha, mensaje.fecha) &&
                Objects.equals(telefono, mensaje.telefono);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(contenido, fecha, telefono);
    }
}
