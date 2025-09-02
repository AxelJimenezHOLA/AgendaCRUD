package org.axel.agenda.service;

import org.axel.agenda.model.Telefono;
import org.axel.agenda.repository.TelefonoRepository;

import java.util.List;
import java.util.Optional;

public class TelefonoService {
    private final TelefonoRepository telefonoRepository;

    public TelefonoService(TelefonoRepository telefonoRepository) {
        this.telefonoRepository = telefonoRepository;
    }

    public void agregarTelefono(int personaId, String numero) {
        telefonoRepository.guardar(new Telefono(personaId, numero));
    }

    public List<Telefono> listarTelefonos() {
        return telefonoRepository.obtenerTodos();
    }

    public Optional<Telefono> obtenerPorId(int id) {
        return telefonoRepository.obtenerPorId(id);
    }

    public List<Telefono> listarTelefonosPorPersona(int personaId) {
        return telefonoRepository.obtenerPorPersonaId(personaId);
    }

    public void actualizarTelefono(int id, int personaId, String nuevoNumero) {
        telefonoRepository.actualizar(new Telefono(id, personaId, nuevoNumero));
    }

    public void eliminarTelefono(int id) {
        telefonoRepository.eliminar(id);
    }
}