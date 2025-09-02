package org.axel.agenda.repository;

import org.axel.agenda.model.Persona;

import java.util.List;
import java.util.Optional;

public interface PersonaRepository {
    void guardar(Persona persona);
    void actualizar(Persona persona);
    void eliminar(int id);
    Optional<Persona> obtenerPorId(int id);
    List<Persona> obtenerTodos();
}