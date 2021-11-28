package com.example.blessflag.model;

public class Restaurante {

    private String nombre, sucursal, ubicacion, gerente, imagen;
    private int id, cp, telefono;

    public Restaurante() {
    }

    public Restaurante(String imagen) {
        this.imagen = imagen;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getGerente() {
        return gerente;
    }

    public void setGerente(String gerente) {
        this.gerente = gerente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        String datos = "ID: " +String.valueOf(id) + "\n" +
                "Nombre: " +nombre+ "\n"+
                "Sucursal: " +sucursal+"\n"+
                "Direccion: " +ubicacion+"\n"+
                "Codigo Postal: " +cp+"\n"+
                "Telefono: " +telefono+"\n"+
                "Gerente: " +gerente;
        return datos;
    }
}
