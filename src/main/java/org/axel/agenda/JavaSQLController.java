package org.axel.agenda;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    @FXML private Button agregarDireccionButton;

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
        direccionTableColumn.setCellValueFactory(new PropertyValueFactory<>("direccionesTexto"));

        telefonoIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        foreignKeyTableColumn.setCellValueFactory(new PropertyValueFactory<>("personaId"));
        numeroTableColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        // Listener de tabla personas
        personaTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                Persona persona = (Persona) newSel;
                nombreTextField.setText(persona.getNombre());
                direccionTextField.setText(persona.getDireccionesTexto());
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
            String sqlPersona = "INSERT INTO Persona (nombre) VALUES (?)";
            PreparedStatement pstmt = conexionBaseDatos.getConnection().prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, nombre);
            pstmt.executeUpdate();

            // Obtener ID generado
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            int personaId = 0;
            if (generatedKeys.next()) {
                personaId = generatedKeys.getInt(1);
            }
            pstmt.close();

            // Insertar dirección (si no está vacía)
            if (direccion != null && !direccion.isEmpty()) {
                int direccionId = 0;

                // Buscar dirección existente
                String sqlFindDir = "SELECT id FROM Direccion WHERE calle = ?";
                PreparedStatement pstmtFind = conexionBaseDatos.getConnection().prepareStatement(sqlFindDir);
                pstmtFind.setString(1, direccion);
                ResultSet rsDir = pstmtFind.executeQuery();
                if (rsDir.next()) {
                    direccionId = rsDir.getInt("id");
                } else {
                    // Insertar nueva dirección
                    String sqlDir = "INSERT INTO Direccion (calle) VALUES (?)";
                    PreparedStatement pstmtDir = conexionBaseDatos.getConnection().prepareStatement(sqlDir, Statement.RETURN_GENERATED_KEYS);
                    pstmtDir.setString(1, direccion);
                    pstmtDir.executeUpdate();
                    ResultSet genKeysDir = pstmtDir.getGeneratedKeys();
                    if (genKeysDir.next()) {
                        direccionId = genKeysDir.getInt(1);
                    }
                    pstmtDir.close();
                }
                pstmtFind.close();

                // Relacionar persona con dirección
                String sqlRel = "INSERT INTO PersonaDireccion (personaId, direccionId) VALUES (?, ?)";
                PreparedStatement pstmtRel = conexionBaseDatos.getConnection().prepareStatement(sqlRel);
                pstmtRel.setInt(1, personaId);
                pstmtRel.setInt(2, direccionId);
                pstmtRel.executeUpdate();
                pstmtRel.close();
            }

            // Insertar teléfono (si no está vacío)
            if (telefono != null && !telefono.isEmpty()) {
                String sqlTelefono = "INSERT INTO Telefono (personaId, numero) VALUES (?, ?)";
                PreparedStatement pstmtTel = conexionBaseDatos.getConnection().prepareStatement(sqlTelefono);
                pstmtTel.setInt(1, personaId);
                pstmtTel.setString(2, telefono);
                pstmtTel.executeUpdate();
                pstmtTel.close();
            }

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
                String sql = "UPDATE Persona SET nombre = ? WHERE id = ?";
                PreparedStatement pstmt = conexionBaseDatos.getConnection().prepareStatement(sql);
                pstmt.setString(1, nombreTextField.getText());
                pstmt.setInt(2, personaSeleccionada.getId());
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

    @FXML private void onAgregarDireccionClick() {
        // 1) Validaciones básicas
        Persona personaSeleccionada = (Persona) personaTableView.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Debe seleccionar una persona antes de agregar una dirección.", ButtonType.OK);
            a.setHeaderText("Sin persona seleccionada");
            a.showAndWait();
            return;
        }

        String texto = direccionTextField.getText();
        if (texto == null || texto.trim().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Escriba una dirección en el campo correspondiente.", ButtonType.OK);
            a.setHeaderText("Dirección vacía");
            a.showAndWait();
            return;
        }
        String direccion = texto.trim();

        try {
            // 2) Buscar si la dirección ya existe (en Direccion.calle)
            Integer direccionId = null;
            String sqlFindDir = "SELECT id FROM Direccion WHERE calle = ?";
            try (PreparedStatement ps = conexionBaseDatos.getConnection().prepareStatement(sqlFindDir)) {
                ps.setString(1, direccion);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        direccionId = rs.getInt("id");
                    }
                }
            }

            // 3) Insertar la dirección si no existe
            if (direccionId == null) {
                String sqlInsertDir = "INSERT INTO Direccion (calle) VALUES (?)";
                try (PreparedStatement ps = conexionBaseDatos.getConnection().prepareStatement(sqlInsertDir, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, direccion);
                    ps.executeUpdate();
                    try (ResultSet gk = ps.getGeneratedKeys()) {
                        if (gk.next()) {
                            direccionId = gk.getInt(1);
                        } else {
                            throw new RuntimeException("No se pudo obtener el ID de la nueva dirección.");
                        }
                    }
                }
            }

            // 4) Evitar duplicar la relación Persona-Direccion
            String sqlCheck = "SELECT 1 FROM PersonaDireccion WHERE personaId = ? AND direccionId = ?";
            boolean yaRelacionada = false;
            try (PreparedStatement ps = conexionBaseDatos.getConnection().prepareStatement(sqlCheck)) {
                ps.setInt(1, personaSeleccionada.getId());
                ps.setInt(2, direccionId);
                try (ResultSet rs = ps.executeQuery()) {
                    yaRelacionada = rs.next();
                }
            }

            // 5) Insertar relación si no existe
            if (!yaRelacionada) {
                String sqlRel = "INSERT INTO PersonaDireccion (personaId, direccionId) VALUES (?, ?)";
                try (PreparedStatement ps = conexionBaseDatos.getConnection().prepareStatement(sqlRel)) {
                    ps.setInt(1, personaSeleccionada.getId());
                    ps.setInt(2, direccionId);
                    ps.executeUpdate();
                }
            } else {
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Esa dirección ya está asociada a la persona.", ButtonType.OK);
                a.setHeaderText("Relación existente");
                a.showAndWait();
            }

            // 6) Refrescar y limpiar
            actualizarTablas();
            direccionTextField.clear();

        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR, "No se pudo agregar la dirección: " + e.getMessage(), ButtonType.OK);
            a.setHeaderText("Error al agregar dirección");
            a.showAndWait();
        }
    }

    @FXML protected void actualizarTablas() {
        ObservableList<Persona> personas = FXCollections.observableArrayList();
        ObservableList<Telefono> telefonos = FXCollections.observableArrayList();

        try {
            // Mapa temporal para agrupar direcciones de cada persona
            Map<Integer, Persona> personaMap = new HashMap<>();

            // Cargar personas con direcciones
            String sqlPersonas = """
            SELECT p.id, p.nombre, d.calle
            FROM Persona p
            LEFT JOIN PersonaDireccion pd ON p.id = pd.personaId
            LEFT JOIN Direccion d ON pd.direccionId = d.id
            ORDER BY p.id
        """;
            Statement stmt = conexionBaseDatos.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sqlPersonas);

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String calle = rs.getString("calle");

                Persona persona = personaMap.get(id);
                if (persona == null) {
                    persona = new Persona(id, nombre, new ArrayList<>());
                    personaMap.put(id, persona);
                }
                if (calle != null) {
                    persona.getDirecciones().add(calle);
                }
            }

            personas.addAll(personaMap.values());

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