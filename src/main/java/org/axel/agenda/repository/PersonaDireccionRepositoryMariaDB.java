package org.axel.agenda.repository;

import org.axel.agenda.config.ConexionBaseDatos;
import org.axel.agenda.model.PersonaDireccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonaDireccionRepositoryMariaDB implements PersonaDireccionRepository {
    @Override
    public void guardar(PersonaDireccion personaDireccion) {
        String sql = "INSERT INTO personadireccion (personaId, direccionId) VALUES (?, ?)";
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, personaDireccion.getPersonaId());
            stmt.setInt(2, personaDireccion.getDireccionId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo relacionar la dirección con la persona. ", e);
        }
    }

    @Override
    public void eliminar(int personaId, int direccionId) {
        String sql = "DELETE FROM personadireccion WHERE personaId = ? AND direccionId = ?";
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, personaId);
            stmt.setInt(2, direccionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo listar las relaciones de persona/dirección. ", e);
        }
    }

    @Override
    public List<PersonaDireccion> obtenerTodos() {
        List<PersonaDireccion> relaciones = new ArrayList<>();
        String sql = "SELECT * FROM personadireccion";
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                relaciones.add(new PersonaDireccion(
                        rs.getInt("personaId"),
                        rs.getInt("direccionId")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo listar las relaciones de persona/dirección. ", e);
        }
        return relaciones;
    }

    @Override
    public List<PersonaDireccion> obtenerPorPersonaId(int personaId) {
        List<PersonaDireccion> relaciones = new ArrayList<>();
        String sql = "SELECT * FROM personadireccion WHERE personaId = ?";
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, personaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                relaciones.add(new PersonaDireccion(
                        rs.getInt("personaId"),
                        rs.getInt("direccionId")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo obtener direcciones según la persona. ", e);
        }
        return relaciones;
    }

    @Override
    public List<PersonaDireccion> obtenerPorDireccionId(int direccionId) {
        List<PersonaDireccion> relaciones = new ArrayList<>();
        String sql = "SELECT * FROM personadireccion WHERE direccionId = ?";
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, direccionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                relaciones.add(new PersonaDireccion(
                        rs.getInt("personaId"),
                        rs.getInt("direccionId")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo obtener personas según la dirección. ", e);
        }
        return relaciones;
    }
}