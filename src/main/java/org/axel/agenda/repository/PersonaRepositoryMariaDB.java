package org.axel.agenda.repository;

import org.axel.agenda.config.ConexionBaseDatos;
import org.axel.agenda.model.Persona;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonaRepositoryMariaDB implements PersonaRepository {
    @Override
    public void guardar(Persona persona) {
        String sql = "INSERT INTO persona (nombre) VALUES (?)";
        try (
            Connection conn = ConexionBaseDatos.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, persona.getNombre());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo insertar persona. ", e);
        }
    }

    @Override
    public void actualizar(Persona persona) {
        String sql = "UPDATE persona SET nombre = ? WHERE id = ?";
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, persona.getNombre());
            stmt.setInt(2, persona.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo actualizar persona. ", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM persona WHERE id = ?";
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo eliminar persona. ", e);
        }
    }

    @Override
    public Optional<Persona> obtenerPorId(int id) {
        String sql = "SELECT * FROM persona WHERE id = ?";
        try (
            Connection conn = ConexionBaseDatos.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Persona(rs.getInt("id"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo encontrar la persona. ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Persona> obtenerTodos() {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM persona";
        try (
            Connection conn = ConexionBaseDatos.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                personas.add(new Persona(rs.getInt("id"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo listar a las personas. ", e);
        }
        return personas;
    }
}