package org.axel.agenda;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings("unchecked")
public class JavaSQLController {
    // La base de datos
    ConexionBaseDatos conexionBaseDatos = new ConexionBaseDatos();

    // Campos de texto
    @FXML private TextField nombreTextField;
    @FXML private TextField telefonoTextField;
    @FXML private TextField direccionTextField;

    // Botones
    @FXML private Button agregarPersonaButton;
    @FXML private Button borrarButton;
    @FXML private Button modificarButton;
    @FXML private Button agregarTelefonoButton;

    // Tablas
    @FXML private TableView personaTableView;
    @FXML private TableView telefonoTableView;

    // Columnas de tablas
    @FXML private TableColumn idTableColumn;
    @FXML private TableColumn nombreTableColumn;
    @FXML private TableColumn direccionTableColumn;
    @FXML private TableColumn telefonoIDTableColumn;
    @FXML private TableColumn foreignKeyTableColumn;
    @FXML private TableColumn numeroTableColumn;

    // Labels
    @FXML private Label idSeleccionadoLabel;


    @FXML public void initialize() {
        // Verificar si la libreria conectora de MariaDB está instalada e intentar establecer conexión
        System.out.println("Inicializando programa");
        conexionBaseDatos.registrarDriver();
        conexionBaseDatos.establecerConexion();

        // Crear fábricas de celdas para los campos de las tablas.
        idTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreTableColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        direccionTableColumn.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        telefonoIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        foreignKeyTableColumn.setCellValueFactory(new PropertyValueFactory<>("personaId"));
        numeroTableColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        // Listener de tabla personas
        personaTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                Persona persona = (Persona) newSel;
                nombreTextField.setText(persona.getNombre());
                direccionTextField.setText(persona.getDireccion());
                telefonoTextField.clear();
                idSeleccionadoLabel.setText("Persona seleccionada: " + persona.getId());
            }
        });

        // Listener de telefonos
        telefonoTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                Telefono tel = (Telefono) newSel;
                telefonoTextField.setText(tel.getTelefono());
                nombreTextField.clear();
                direccionTextField.clear();
                idSeleccionadoLabel.setText("Teléfono seleccionado: " + tel.getId());
            }
        });

        // Cerrar recursos cuando se cierra la aplicación
        Platform.runLater(() -> {
            Stage stage = (Stage) nombreTextField.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                System.out.println("Cerrando la base de datos...");
                conexionBaseDatos.cerrarRecursos();
            });
        });

        actualizarTablas();
    }

    @FXML protected void onAgregarButtonClick() {
        String nombre = nombreTextField.getText();
        String telefono = telefonoTextField.getText();
        String direccion = direccionTextField.getText();

        try {
            // Insertar persona
            String sqlPersona = "INSERT INTO Persona (nombre, direccion) VALUES (?, ?)";
            PreparedStatement pstmt = conexionBaseDatos.getConnection().prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, nombre);
            pstmt.setString(2, direccion);
            pstmt.executeUpdate();

            // Obtener ID generado
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            int personaId = 0;
            if (generatedKeys.next()) {
                personaId = generatedKeys.getInt(1);
            }

            // Insertar teléfono
            String sqlTelefono = "INSERT INTO Telefono (personaId, numero) VALUES (?, ?)";
            PreparedStatement pstmtTel = conexionBaseDatos.getConnection().prepareStatement(sqlTelefono);
            pstmtTel.setInt(1, personaId);
            pstmtTel.setString(2, telefono);
            pstmtTel.executeUpdate();

            pstmt.close();
            pstmtTel.close();

            actualizarTablas();
            nombreTextField.clear();
            telefonoTextField.clear();
            direccionTextField.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void onBorrarButtonClick() {
        Persona personaSeleccionada = (Persona) personaTableView.getSelectionModel().getSelectedItem();
        Telefono telefonoSeleccionado = (Telefono) telefonoTableView.getSelectionModel().getSelectedItem();

        try {
            if (telefonoSeleccionado != null) {
                String sqlTel = "DELETE FROM Telefono WHERE id = ?";
                PreparedStatement pstmtTel = conexionBaseDatos.getConnection().prepareStatement(sqlTel);
                pstmtTel.setInt(1, telefonoSeleccionado.getId());
                pstmtTel.executeUpdate();
                pstmtTel.close();
                System.out.println("Teléfono eliminado");
            }
            else if (personaSeleccionada != null) {
                String sql = "DELETE FROM Persona WHERE id = ?";
                PreparedStatement pstmt = conexionBaseDatos.getConnection().prepareStatement(sql);
                pstmt.setInt(1, personaSeleccionada.getId());
                pstmt.executeUpdate();
                pstmt.close();
                System.out.println("Persona eliminada");
            }

            actualizarTablas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void onModificarButtonClick() {
        Persona personaSeleccionada = (Persona) personaTableView.getSelectionModel().getSelectedItem();
        Telefono telefonoSeleccionado = (Telefono) telefonoTableView.getSelectionModel().getSelectedItem();

        try {
            if (telefonoSeleccionado != null) {
                // Modificar teléfono
                String sqlTel = "UPDATE Telefono SET numero = ? WHERE id = ?";
                PreparedStatement pstmtTel = conexionBaseDatos.getConnection().prepareStatement(sqlTel);
                pstmtTel.setString(1, telefonoTextField.getText());
                pstmtTel.setInt(2, telefonoSeleccionado.getId());
                pstmtTel.executeUpdate();
                pstmtTel.close();
                System.out.println("Teléfono modificado");
            }
            else if (personaSeleccionada != null) {
                // Modificar persona
                String sql = "UPDATE Persona SET nombre = ?, direccion = ? WHERE id = ?";
                PreparedStatement pstmt = conexionBaseDatos.getConnection().prepareStatement(sql);
                pstmt.setString(1, nombreTextField.getText());
                pstmt.setString(2, direccionTextField.getText());
                pstmt.setInt(3, personaSeleccionada.getId());
                pstmt.executeUpdate();
                pstmt.close();
                System.out.println("Persona modificada");
            }

            actualizarTablas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void onAgregarTelefonoClick() {
        Persona personaSeleccionada = (Persona) personaTableView.getSelectionModel().getSelectedItem();
        String telefono = telefonoTextField.getText();

        if (personaSeleccionada != null && telefono != null && !telefono.isEmpty()) {
            try {
                String sql = "INSERT INTO Telefono (personaId, numero) VALUES (?, ?)";
                PreparedStatement pstmt = conexionBaseDatos.getConnection().prepareStatement(sql);
                pstmt.setInt(1, personaSeleccionada.getId());
                pstmt.setString(2, telefono);
                pstmt.executeUpdate();
                pstmt.close();

                actualizarTablas();
                telefonoTextField.clear();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Debe seleccionar una persona y escribir un teléfono.");
        }
    }

    @FXML protected void actualizarTablas() {
        ObservableList<Persona> personas = FXCollections.observableArrayList();
        ObservableList<Telefono> telefonos = FXCollections.observableArrayList();

        try {
            // Cargar personas
            String sqlPersonas = "SELECT * FROM Persona";
            Statement stmt = conexionBaseDatos.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sqlPersonas);
            while (rs.next()) {
                personas.add(new Persona(rs.getInt("id"), rs.getString("nombre"), rs.getString("direccion")));
            }

            // Cargar teléfonos
            String sqlTelefonos = "SELECT * FROM Telefono";
            rs = stmt.executeQuery(sqlTelefonos);
            while (rs.next()) {
                telefonos.add(new Telefono(rs.getInt("id"), rs.getInt("personaId"), rs.getString("numero")));
            }

            personaTableView.setItems(personas);
            telefonoTableView.setItems(telefonos);

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        idSeleccionadoLabel.setText("Ningún ID seleccionado.");
    }

}