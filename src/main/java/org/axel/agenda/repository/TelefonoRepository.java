package org.axel.agenda.repository;

import org.axel.agenda.model.Telefono;

import java.util.List;
import java.util.Optional;

public interface TelefonoRepository {
    void guardar(Telefono telefono);
    void actualizar(Telefono telefono);
    void eliminar(int id);
    Optional<Telefono> obtenerPorId(int id);
    List<Telefono> obtenerPorPersonaId(int personaId);
    List<Telefono> obtenerTodos();
}