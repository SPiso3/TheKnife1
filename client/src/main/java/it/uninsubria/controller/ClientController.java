package it.uninsubria.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ClientController {
    @FXML
    private Label loginText;

    @FXML
    protected void onLoginButtonClick() {
        // TODO: login stuff
        loginText.setText("Welcome to JavaFX Application!");
    }
}