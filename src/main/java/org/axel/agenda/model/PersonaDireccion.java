package org.axel.agenda.model;

public class PersonaDireccion {
    private int personaId;
    private int direccionId;

    public PersonaDireccion(int personaId, int direccionId) {
        this.personaId = personaId;
        this.direccionId = direccionId;
    }

    public int getPersonaId() {
        return personaId;
    }

    public void setPersonaId(int personaId) {
        this.personaId = personaId;
    }

    public int getDireccionId() {
        return direccionId;
    }

    public void setDireccionId(int direccionId) {
        this.direccionId = direccionId;
    }
}