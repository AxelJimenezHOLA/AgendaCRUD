package org.axel.agenda.service;

import org.axel.agenda.model.Direccion;
import org.axel.agenda.repository.DireccionRepository;

import java.util.List;
import java.util.Optional;

public class DireccionService {
    private final DireccionRepository direccionRepository;

    public DireccionService(DireccionRepository direccionRepository) {
        this.direccionRepository = direccionRepository;
    }

    public void crearDireccion(String calle) {
        direccionRepository.guardar(new Direccion(calle));
    }

    public List<Direccion> listarDirecciones() {
        return direccionRepository.obtenerTodos();
    }

    public Optional<Direccion> obtenerPorId(int id) {
        return direccionRepository.obtenerPorId(id);
    }

    public void actualizarDireccion(int id, String nuevaCalle) {
        direccionRepository.actualizar(new Direccion(id, nuevaCalle));
    }

    public void eliminarDireccion(int id) {
        direccionRepository.eliminar(id);
    }
}