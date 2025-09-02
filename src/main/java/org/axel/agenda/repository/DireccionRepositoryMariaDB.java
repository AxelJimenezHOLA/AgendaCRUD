package org.axel.agenda.repository;

import org.axel.agenda.config.ConexionBaseDatos;
import org.axel.agenda.model.Direccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class DireccionRepositoryMariaDB implements DireccionRepository {
    @Override
    public void guardar(Direccion direccion) {
        String sql = "INSERT INTO direccion (calle) VALUES (?) ";
        try (
            Connection conn = ConexionBaseDatos.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, direccion.getCalle());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo insertar la dirección. ", e);
        }
    }

    @Override
    public void actualizar(Direccion direccion) {
        String sql = "UPDATE direccion SET calle = ? WHERE id = ? ";
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, direccion.getCalle());
            stmt.setInt(2, direccion.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo actualizar dirección. ", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM direccion WHERE id = ? ";
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo eliminar dirección. ", e);
        }
    }

    @Override
    public Optional<Direccion> obtenerPorId(int id) {
        String sql = "SELECT * FROM direccion WHERE id = ?";
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Direccion(rs.getInt("id"), rs.getString("calle")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo obtener la dirección. ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Direccion> obtenerTodos() {
        String sql = "SELECT * FROM direccion";
        ArrayList<Direccion> direcciones = new ArrayList<>();
        try (
                Connection conn = ConexionBaseDatos.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                direcciones.add(new Direccion(rs.getInt("id"), rs.getString("calle")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error: no se pudo obtener las direcciones. ", e);
        }
        return direcciones;
    }
}