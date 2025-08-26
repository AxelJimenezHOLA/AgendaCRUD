package org.axel.agenda;

import java.util.List;

public class Persona {
    private int id;
    private String nombre;
    private List<String> direcciones;

    public Persona(int id, String nombre, List<String> direccion) {
        this.id = id;
        this.nombre = nombre;
        this.direcciones = direccion;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public List<String> getDirecciones() { return direcciones; }

    public String getDireccionesTexto() {
        return String.join(", ", direcciones);
    }
}