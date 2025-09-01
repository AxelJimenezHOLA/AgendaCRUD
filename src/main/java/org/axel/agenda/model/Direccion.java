package org.axel.agenda.model;

public class Direccion {
    private int id;
    private String calle;

    public Direccion(int id, String calle) {
        this.id = id;
        this.calle = calle;
    }

    public Direccion(String calle) {
        this.calle = calle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }
}