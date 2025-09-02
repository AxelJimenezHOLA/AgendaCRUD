package org.axel.agenda.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBaseDatos {
    private static final String URL = "jdbc:mariadb://localhost:3307/agenda";
    private static final String USER = "usuario1";
    private static final String PASSWORD = "superpassword";

    private ConexionBaseDatos() {}

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}