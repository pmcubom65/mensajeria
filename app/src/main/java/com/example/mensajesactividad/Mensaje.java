package com.example.mensajesactividad;

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
}
