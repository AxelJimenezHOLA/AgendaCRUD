package org.axel.agenda.repository;

import org.axel.agenda.config.ConexionBaseDatos;
import org.axel.agenda.model.Telefono;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class TelefonoRepositoryMariaDB implements TelefonoRepository {

    @Override
    public void guardar(Telefono telefono) {
        String sql = "INSERT INTO telefono (personaId, numero) VALUES (?,?)";
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, telefono.getPersonaId());
            stmt.setString(2, telefono.getNumero());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo insertar persona. ", e);
        }
    }

    @Override
    public void actualizar(Telefono telefono) {
        String sql = "UPDATE telefono SET numero=?, personaId=? WHERE id=?";
        try (Connection conn = ConexionBaseDatos.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, telefono.getNumero());
            stmt.setInt(2, telefono.getPersonaId());
            stmt.setInt(3, telefono.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar teléfono", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM telefono WHERE id=?";
        try (Connection conn = ConexionBaseDatos.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar teléfono", e);
        }
    }

    @Override
    public Optional<Telefono> obtenerPorId(int id) {
        String sql = "SELECT * FROM telefono WHERE id = ?";
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Telefono(
                        rs.getInt("id"),
                        rs.getInt("personaId"),
                        rs.getString("numero")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo insertar persona. ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Telefono> obtenerPorPersonaId(int personaId) {
        String sql = "SELECT * FROM telefono WHERE personaId = ?";
        List<Telefono> telefonos = new ArrayList<>();
        try (
            Connection conn = ConexionBaseDatos.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, personaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                telefonos.add(new Telefono(
                        rs.getInt("id"),
                        rs.getInt("personaId"),
                        rs.getString("numero")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo insertar persona. ", e);
        }
        return telefonos;
    }

    @Override
    public List<Telefono> obtenerTodos() {
        String sql = "SELECT * FROM telefono";
        ArrayList<Telefono> telefonos = new ArrayList<>();
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                telefonos.add(new Telefono(
                        rs.getInt("id"),
                        rs.getInt("personaId"),
                        rs.getString("numero")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo insertar persona. ", e);
        }
        return telefonos;
    }
}