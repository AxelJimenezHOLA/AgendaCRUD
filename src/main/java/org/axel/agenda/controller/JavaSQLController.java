package org.axel.agenda.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class JavaSQLController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}