package org.axel.agenda.service;

import org.axel.agenda.model.PersonaDireccion;
import org.axel.agenda.repository.PersonaDireccionRepository;

import java.util.List;

public class PersonaDireccionService {
    private final PersonaDireccionRepository personaDireccionRepository;

    public PersonaDireccionService(PersonaDireccionRepository personaDireccionRepository) {
        this.personaDireccionRepository = personaDireccionRepository;
    }

    public void asignarDireccionAPersona(int personaId, int direccionId) {
        personaDireccionRepository.guardar(new PersonaDireccion(personaId, direccionId));
    }

    public List<PersonaDireccion> listarRelaciones() {
        return personaDireccionRepository.obtenerTodos();
    }

    public List<PersonaDireccion> listarDireccionesDePersona(int personaId) {
        return personaDireccionRepository.obtenerPorPersonaId(personaId);
    }

    public List<PersonaDireccion> listarPersonasEnDireccion(int direccionId) {
        return personaDireccionRepository.obtenerPorDireccionId(direccionId);
    }

    public void eliminarRelacion(int personaId, int direccionId) {
        personaDireccionRepository.eliminar(personaId, direccionId);
    }
}