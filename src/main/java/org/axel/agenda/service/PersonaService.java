package org.axel.agenda.service;

import org.axel.agenda.model.Persona;
import org.axel.agenda.repository.PersonaRepository;

import java.util.List;
import java.util.Optional;

public class PersonaService {
    private final PersonaRepository personaRepository;
    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    public void crearPersona(String nombre) {
        personaRepository.guardar(new Persona(nombre));
    }

    public List<Persona> listarPersonas() {
        return personaRepository.obtenerTodos();
    }

    public Optional<Persona> obtenerPorId(int id) {
        return personaRepository.obtenerPorId(id);
    }

    public void actualizarPersona(int id, String nombre) {
        personaRepository.actualizar(new Persona(id, nombre));
    }

    public void eliminarPersona(int id) {
        personaRepository.eliminar(id);
    }
}