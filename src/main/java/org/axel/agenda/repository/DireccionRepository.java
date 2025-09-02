package org.axel.agenda.repository;

import org.axel.agenda.model.Direccion;

import java.util.List;
import java.util.Optional;

public interface DireccionRepository {
    void guardar(Direccion direccion);
    void actualizar(Direccion direccion);
    void eliminar(int id);
    Optional<Direccion> obtenerPorId(int id);
    List<Direccion> obtenerTodos();
}