package org.axel.agenda.repository;

import org.axel.agenda.model.PersonaDireccion;
import org.axel.agenda.model.Telefono;

import java.util.List;

public interface PersonaDireccionRepository {
    void guardar(PersonaDireccion personaDireccion);
    void eliminar(int personaId, int direccionId);
    List<PersonaDireccion> obtenerTodos();
    List<PersonaDireccion> obtenerPorPersonaId(int personaId);
    List<PersonaDireccion> obtenerPorDireccionId(int direccionId);
}