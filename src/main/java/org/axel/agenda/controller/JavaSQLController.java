package org.axel.agenda.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.axel.agenda.model.Direccion;
import org.axel.agenda.model.Persona;
import org.axel.agenda.model.PersonaDireccion;
import org.axel.agenda.model.Telefono;
import org.axel.agenda.repository.DireccionRepositoryMariaDB;
import org.axel.agenda.repository.PersonaDireccionRepositoryMariaDB;
import org.axel.agenda.repository.PersonaRepositoryMariaDB;
import org.axel.agenda.repository.TelefonoRepositoryMariaDB;
import org.axel.agenda.service.DireccionService;
import org.axel.agenda.service.PersonaDireccionService;
import org.axel.agenda.service.PersonaService;
import org.axel.agenda.service.TelefonoService;

public class JavaSQLController {
    // Servicios de SQL
    private final PersonaService personaService;
    private final TelefonoService telefonoService;
    private final DireccionService direccionService;
    private final PersonaDireccionService personaDireccionService;

    // FXML
    @FXML private Label idSeleccionadoLabel;
    @FXML private TextField nombreTextField;
    @FXML private TextField numeroDeTelefonoTextField;
    @FXML private TextField direccionTextField;
    @FXML private TableView<Persona> personaTableView;
    @FXML private TableView<Telefono> telefonoTableView;
    @FXML private TableView<Direccion> direccionTableView;
    @FXML private TableView<PersonaDireccion> personaDireccionTableView;
    @FXML private TableColumn<Persona, Integer> personaIdColumn;
    @FXML private TableColumn<Persona, String> personaNombreColumn;
    @FXML private TableColumn<Telefono, Integer> telefonoIdColumn;
    @FXML private TableColumn<Telefono, Integer> telefonoPersonaIdColumn;
    @FXML private TableColumn<Telefono, String> telefonoNumeroColumn;
    @FXML private TableColumn<Direccion, Integer> direccionIdColumn;
    @FXML private TableColumn<Direccion, String> direccionCalleColumn;
    @FXML private TableColumn<PersonaDireccion, Integer> personaDireccionPersonaIdColumn;
    @FXML private TableColumn<PersonaDireccion, Integer> personaDireccionDireccionIdColumn;


    public JavaSQLController() {
        this.personaService = new PersonaService(new PersonaRepositoryMariaDB());
        this.telefonoService = new TelefonoService(new TelefonoRepositoryMariaDB());
        this.direccionService = new DireccionService(new DireccionRepositoryMariaDB());
        this.personaDireccionService = new PersonaDireccionService(new PersonaDireccionRepositoryMariaDB());
    }

    @FXML public void initialize() {
        // Configuración de columnas Persona
        personaIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        personaNombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Configuración de columnas Teléfono
        telefonoIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        telefonoPersonaIdColumn.setCellValueFactory(new PropertyValueFactory<>("personaId"));
        telefonoNumeroColumn.setCellValueFactory(new PropertyValueFactory<>("numero"));

        // Configuración de columnas Dirección
        direccionIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        direccionCalleColumn.setCellValueFactory(new PropertyValueFactory<>("calle"));

        // Configuración de columnas PersonaDireccion
        personaDireccionPersonaIdColumn.setCellValueFactory(new PropertyValueFactory<>("personaId"));
        personaDireccionDireccionIdColumn.setCellValueFactory(new PropertyValueFactory<>("direccionId"));

        // Ahora carga los datos
        actualizarDatos();

        // Listener de selección de persona
        personaTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                idSeleccionadoLabel.setText("Persona seleccionada: " + newSel.getId());
                telefonoTableView.getSelectionModel().clearSelection();
                direccionTableView.getSelectionModel().clearSelection();
                personaDireccionTableView.getSelectionModel().clearSelection();
            }
        });

        // Listener de selección de teléfono
        telefonoTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                idSeleccionadoLabel.setText("Teléfono seleccionado: " + newSel.getId());
                personaTableView.getSelectionModel().clearSelection();
                direccionTableView.getSelectionModel().clearSelection();
                personaDireccionTableView.getSelectionModel().clearSelection();
            }
        });

        // Listener de selección de dirección
        direccionTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                idSeleccionadoLabel.setText("Dirección seleccionada: " + newSel.getId());
                personaTableView.getSelectionModel().clearSelection();
                telefonoTableView.getSelectionModel().clearSelection();
                personaDireccionTableView.getSelectionModel().clearSelection();
            }
        });
    }

    private void actualizarDatos() {
        personaTableView.setItems(FXCollections.observableArrayList(personaService.listarPersonas()));
        telefonoTableView.setItems(FXCollections.observableArrayList(telefonoService.listarTelefonos()));
        direccionTableView.setItems(FXCollections.observableArrayList(direccionService.listarDirecciones()));
        personaDireccionTableView.setItems(FXCollections.observableArrayList(personaDireccionService.listarRelaciones()));
    }

    private void actualizarSelecciones() {
        personaTableView.getSelectionModel().clearSelection();
        telefonoTableView.getSelectionModel().clearSelection();
        direccionTableView.getSelectionModel().clearSelection();
        personaDireccionTableView.getSelectionModel().clearSelection();
    }

    @FXML protected void onAgregarPersonaButtonClick() {
        try {
            String nombre = nombreTextField.getText();
            String numero = numeroDeTelefonoTextField.getText();
            String calle = direccionTextField.getText();

            if (nombre.isBlank()) {
                mostrarAlerta("Error", "Es necesario ingresar un nombre.", Alert.AlertType.ERROR);
                return;
            }

            // 1. Crear Persona
            personaService.crearPersona(nombre);
            Persona nuevaPersona = personaService.listarPersonas()
                    .stream()
                    .reduce((first, second) -> second).orElseThrow();

            // 2. Crear Telefono si hay número ingresado
            if (!numero.isBlank()) {
                telefonoService.agregarTelefono(nuevaPersona.getId(), numero);
            }

            // 3. Crear Direccion y asociarla a la persona si se ingresó una dirección
            if (!calle.isBlank()) {
                direccionService.crearDireccion(calle);
                Direccion nuevaDireccion = direccionService.listarDirecciones()
                        .stream()
                        .reduce((first, second) -> second).orElseThrow();
                personaDireccionService.asignarDireccionAPersona(nuevaPersona.getId(), nuevaDireccion.getId());
            }

            mostrarAlerta("Éxito", "Registro agregado correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            actualizarDatos();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agregar el registro: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML protected void onAgregarTelefonoButtonClick() {
        try {
            Persona seleccionada = personaTableView.getSelectionModel().getSelectedItem();
            if (seleccionada == null) {
                mostrarAlerta("Error", "Debe seleccionar una persona.", Alert.AlertType.ERROR);
                return;
            }

            String numero = numeroDeTelefonoTextField.getText();
            if (numero.isBlank()) {
                mostrarAlerta("Error", "Debe ingresar un número de teléfono.", Alert.AlertType.ERROR);
                return;
            }

            telefonoService.agregarTelefono(seleccionada.getId(), numero);

            mostrarAlerta("Éxito", "Teléfono agregado correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            actualizarDatos();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agregar el teléfono: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML protected void onAgregarDireccionButtonClick() {
        try {
            Persona seleccionada = personaTableView.getSelectionModel().getSelectedItem();
            if (seleccionada == null) {
                mostrarAlerta("Error", "Debe seleccionar una persona.", Alert.AlertType.ERROR);
                return;
            }

            String calle = direccionTextField.getText();
            if (calle.isBlank()) {
                mostrarAlerta("Error", "Debe ingresar una dirección.", Alert.AlertType.ERROR);
                return;
            }

            // Crear la dirección
            direccionService.crearDireccion(calle);
            Direccion nuevaDireccion = direccionService.listarDirecciones()
                    .stream()
                    .reduce((first, second) -> second).orElseThrow();

            // Relacionar persona con la dirección
            personaDireccionService.asignarDireccionAPersona(seleccionada.getId(), nuevaDireccion.getId());

            mostrarAlerta("Éxito", "Dirección agregada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            actualizarDatos();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agregar la dirección: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML protected void onActualizarButtonClick() {
        try {
            Persona personaSeleccionada = personaTableView.getSelectionModel().getSelectedItem();
            Telefono numeroSeleccionado = telefonoTableView.getSelectionModel().getSelectedItem();
            Direccion direccionSeleccionada = direccionTableView.getSelectionModel().getSelectedItem();

            if (personaSeleccionada == null && numeroSeleccionado == null && direccionSeleccionada == null) {
                mostrarAlerta("Error", "Debe seleccionar una persona, número de teléfono o dirección.", Alert.AlertType.ERROR);
                return;
            }

            String nuevoNombre = nombreTextField.getText();
            String nuevoNumero = numeroDeTelefonoTextField.getText();
            String nuevaCalle = direccionTextField.getText();

            if (!nuevoNombre.isBlank() && personaSeleccionada != null) {
                personaService.actualizarPersona(personaSeleccionada.getId(), nuevoNombre);
            }

            if (!nuevoNumero.isBlank() && numeroSeleccionado != null) {
                telefonoService.actualizarTelefono(numeroSeleccionado.getId(), numeroSeleccionado.getPersonaId(), nuevoNumero);
            }

            if (!nuevaCalle.isBlank() && direccionSeleccionada != null) {
                direccionService.actualizarDireccion(direccionSeleccionada.getId(), nuevaCalle);
            }

            mostrarAlerta("Éxito", "Registro actualizado correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            actualizarDatos();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo actualizar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML protected void onEliminarButtonClick() {
        try {
            Persona personaSeleccionada = personaTableView.getSelectionModel().getSelectedItem();
            Telefono numeroSeleccionado = telefonoTableView.getSelectionModel().getSelectedItem();
            Direccion direccionSeleccionada = direccionTableView.getSelectionModel().getSelectedItem();

            if (personaSeleccionada == null && numeroSeleccionado == null && direccionSeleccionada == null) {
                mostrarAlerta("Error", "Debe seleccionar una persona, número de teléfono o dirección.", Alert.AlertType.ERROR);
                return;
            }

            if (personaSeleccionada != null) {
                // Eliminar relaciones (teléfonos y direcciones)
                telefonoService.listarTelefonosPorPersona(personaSeleccionada.getId())
                        .forEach(t -> telefonoService.eliminarTelefono(t.getId()));

                personaDireccionService.listarDireccionesDePersona(personaSeleccionada.getId())
                        .forEach(rel -> personaDireccionService.eliminarRelacion(rel.getPersonaId(), rel.getDireccionId()));

                // Eliminar Persona
                personaService.eliminarPersona(personaSeleccionada.getId());
            }

            if (numeroSeleccionado != null) {
                telefonoService.eliminarTelefono(numeroSeleccionado.getId());
            }

            if (direccionSeleccionada != null) {
                personaDireccionService.listarPersonasEnDireccion(direccionSeleccionada.getId())
                        .forEach(rel -> personaDireccionService.eliminarRelacion(rel.getPersonaId(), rel.getDireccionId()));
                direccionService.eliminarDireccion(direccionSeleccionada.getId());
            }

            mostrarAlerta("Éxito", "Registro eliminado correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            actualizarDatos();
            actualizarSelecciones();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void limpiarCampos() {
        nombreTextField.clear();
        numeroDeTelefonoTextField.clear();
        direccionTextField.clear();
        idSeleccionadoLabel.setText("Ningún ID seleccionado.");
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipoAlerta) {
        Alert alert = new Alert(tipoAlerta);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}