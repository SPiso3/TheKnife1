package it.uninsubria.controller;

import it.uninsubria.session.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller class for the registration view.
 * Handles user registration form and navigation.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class RegistrationController {
    private static final Logger LOGGER = Logger.getLogger(RegistrationController.class.getName());

    // Account information
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    // Personal information
    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    // Birth date components
    @FXML
    private ComboBox<Integer> dayComboBox;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private ComboBox<Integer> yearComboBox;

    // Location information
    @FXML
    private TextField countryField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField latitudeField;

    @FXML
    private TextField longitudeField;

    // User type selection
    @FXML
    private ToggleGroup userTypeGroup;

    @FXML
    private RadioButton clientRadioButton;

    @FXML
    private RadioButton restaurateurRadioButton;

    // Buttons and messages
    @FXML
    private Button backButton;

    @FXML
    private Button registerButton;

    @FXML
    private Label errorLabel;

    private UserSession userSession;

    // Month names for the month ComboBox
    private final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        // Clear any error messages
        errorLabel.setText("");

        // Initialize the date ComboBoxes
        initializeDateComboBoxes();

        // Set the default selected radio button
        clientRadioButton.setSelected(true);
    }

    /**
     * Initializes the day, month, and year ComboBoxes with appropriate values.
     */
    private void initializeDateComboBoxes() {
        // Set up months
        monthComboBox.setItems(FXCollections.observableArrayList(MONTHS));

        // Set up years (100 years from current year going backwards)
        int currentYear = LocalDate.now().getYear();
        ObservableList<Integer> years = IntStream.rangeClosed(currentYear - 100, currentYear)
                .boxed()
                .sorted((y1, y2) -> y2.compareTo(y1)) // Sort descending
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        yearComboBox.setItems(years);

        // Set up days (1-31 initially)
        updateDaysInMonth(31);

        // Add listeners to update days when month or year changes
        monthComboBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            updateDaysBasedOnMonthAndYear();
        });

        yearComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateDaysBasedOnMonthAndYear();
        });
    }

    /**
     * Updates the days ComboBox based on the selected month and year.
     */
    private void updateDaysBasedOnMonthAndYear() {
        Integer selectedYear = yearComboBox.getValue();
        Integer selectedMonthIndex = monthComboBox.getSelectionModel().getSelectedIndex();

        if (selectedMonthIndex != null && selectedYear != null) {
            // Month in YearMonth is 1-based
            YearMonth yearMonth = YearMonth.of(selectedYear, selectedMonthIndex + 1);
            int daysInMonth = yearMonth.lengthOfMonth();
            updateDaysInMonth(daysInMonth);
        }
    }

    /**
     * Updates the days ComboBox with the appropriate number of days.
     *
     * @param daysInMonth The number of days to include
     */
    private void updateDaysInMonth(int daysInMonth) {
        // Get currently selected day if any
        Integer currentSelection = dayComboBox.getValue();

        // Create new list of days
        ObservableList<Integer> days = IntStream.rangeClosed(1, daysInMonth)
                .boxed()
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        dayComboBox.setItems(days);

        // Restore selection if valid
        if (currentSelection != null && currentSelection <= daysInMonth) {
            dayComboBox.setValue(currentSelection);
        }
    }

    /**
     * Sets the user session reference.
     *
     * @param userSession The user session instance
     */
    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    /**
     * Handles the register button action.
     * Validates the form inputs and attempts to register a new user.
     */
    @FXML
    private void handleRegister() {
        // Clear previous error messages
        errorLabel.setText("");

        // Validate required fields
        if (!validateRequiredFields()) {
            return;
        }

        // Validate numeric fields
        if (!validateNumericFields()) {
            return;
        }

        // TODO: Create user and register with the server
        // For now, just show success and navigate back to login

        // Determine the user role
        String role = clientRadioButton.isSelected() ? "client" : "restaurateur";

        // Show success message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText("Welcome to TheKnife!");
        alert.setContentText("Your account has been created successfully. You can now log in.");
        alert.showAndWait();

        // Navigate back to login
        handleBack();
    }

    /**
     * Validates that all required fields are filled.
     *
     * @return true if all required fields are valid, false otherwise
     */
    private boolean validateRequiredFields() {
        StringBuilder errors = new StringBuilder();

        // Check account information
        if (usernameField.getText().trim().isEmpty()) {
            errors.append("Username is required.\n");
        }
        if (passwordField.getText().isEmpty()) {
            errors.append("Password is required.\n");
        }

        // Check personal information
        if (nameField.getText().trim().isEmpty()) {
            errors.append("Name is required.\n");
        }
        if (surnameField.getText().trim().isEmpty()) {
            errors.append("Surname is required.\n");
        }

        // Check location information
        if (countryField.getText().trim().isEmpty()) {
            errors.append("Country is required.\n");
        }
        if (cityField.getText().trim().isEmpty()) {
            errors.append("City is required.\n");
        }
        if (addressField.getText().trim().isEmpty()) {
            errors.append("Address is required.\n");
        }
        if (latitudeField.getText().trim().isEmpty()) {
            errors.append("Latitude is required.\n");
        }
        if (longitudeField.getText().trim().isEmpty()) {
            errors.append("Longitude is required.\n");
        }

        // Display error message if any required fields are missing
        if (errors.length() > 0) {
            errorLabel.setText(errors.toString());
            return false;
        }

        return true;
    }

    /**
     * Validates that numeric fields contain valid numbers.
     *
     * @return true if all numeric fields are valid, false otherwise
     */
    private boolean validateNumericFields() {
        StringBuilder errors = new StringBuilder();

        // Validate latitude
        try {
            double latitude = Double.parseDouble(latitudeField.getText().trim());
            if (latitude < -90 || latitude > 90) {
                errors.append("Latitude must be between -90 and 90.\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Latitude must be a valid number.\n");
        }

        // Validate longitude
        try {
            double longitude = Double.parseDouble(longitudeField.getText().trim());
            if (longitude < -180 || longitude > 180) {
                errors.append("Longitude must be between -180 and 180.\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Longitude must be a valid number.\n");
        }

        // Display error message if any numeric fields are invalid
        if (errors.length() > 0) {
            errorLabel.setText(errors.toString());
            return false;
        }

        return true;
    }

    /**
     * Handles the back button action.
     * Navigates back to the login view.
     */
    @FXML
    private void handleBack() {
        try {
            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();

            // Get the login controller and set the user session
            LoginController loginController = loader.getController();
            if (userSession != null) {
                loginController.setUserSession(userSession);
            }

            // Get the current stage
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("TheKnife - Login");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading login view", e);
            errorLabel.setText("Error navigating back to login: " + e.getMessage());
        }
    }
}