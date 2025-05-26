package it.uninsubria.controller;

import it.uninsubria.dto.UserDTO;
import it.uninsubria.services.UserService;
import it.uninsubria.session.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for the login view.
 * Handles user authentication, navigation to registration page,
 * and guest access functionality.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private Button guestButton;
    @FXML private Label errorLabel;

    private UserSession userSession;
    private UserService userService;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        setUserSession();
        initServices();
        // Clear any error messages
        errorLabel.setText("");
    }

    private void initServices() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            userService = (UserService) registry.lookup("UserService");
        } catch (NotBoundException | RemoteException e) {
            System.err.println("Error connecting to UserService: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the user session reference.
     */
    public void setUserSession() {
        this.userSession = UserSession.getInstance();
    }

    /**
     * Handles the login button action.
     * Validates user credentials and attempts to log in.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate inputs
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password are required");
            return;
        }

        UserDTO credentials = new UserDTO(username, password);
        UserDTO userData = null;
        try {
            userData = userService.login(credentials);
        } catch (SecurityException e) {
            errorLabel.setText(e.getMessage());
        } catch (RemoteException e) {
            errorLabel.setText("Server connection error: " + e.getMessage());
        }

        if (userData != null) {
            userSession.login(userData);
            navigateToSearchView();
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }

    /**
     * Handles the register button action.
     * Navigates to the registration view.
     */
    @FXML
    private void handleRegister() {
        try {
            // Load the registration view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("registration-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) registerButton.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("TheKnife - Registration");

            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading registration view", e);
            errorLabel.setText("Error loading registration page: " + e.getMessage());
        }
    }

    /**
     * Handles the guest access button action.
     * Validates coordinates and continues as guest.
     */
    @FXML
    private void handleGuestAccess() {
        String latitudeText = latitudeField.getText().trim();
        String longitudeText = longitudeField.getText().trim();

        // Validate inputs
        if (latitudeText.isEmpty() || longitudeText.isEmpty()) {
            errorLabel.setText("Both latitude and longitude are required");
            return;
        }

        // Parse coordinates
        try {
            double latitude = Double.parseDouble(latitudeText);
            double longitude = Double.parseDouble(longitudeText);
            // Validate coordinate ranges
            if (latitude < -90 || latitude > 90) {
                errorLabel.setText("Latitude must be between -90 and 90");
                return;
            }
            if (longitude < -180 || longitude > 180) {
                errorLabel.setText("Longitude must be between -180 and 180");
                return;
            }

            // Set guest mode in the user session
            if (userSession != null) {
                userSession.setGuestMode(latitude, longitude);
            }

            // Navigate to the search view as guest
            navigateToSearchView();
        } catch (NumberFormatException e) {
            errorLabel.setText("Coordinates must be valid numbers");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during guest access", e);
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Navigates to the search view
     */
    private void navigateToSearchView() {
        try {
            // Load the search view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("search-view.fxml"));
            Parent root = loader.load();
            // Get the search controller and set the user session
            SearchController searchController = loader.getController();
            // Get the current stage
            Stage stage = (Stage) guestButton.getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));

            // Set the title based on the access mode
            if (!userSession.isLoggedIn()) {
                stage.setTitle("TheKnife - Guest Search");
            } else if (userSession.getCurrentUser().getRole().equals("OWNER")) {
                stage.setTitle("TheKnife - Restaurateur");
            } else {
                stage.setTitle("TheKnife - Client");
            }

            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading search view", e);
            errorLabel.setText("Error loading search page: " + e.getMessage());
        }
    }
}