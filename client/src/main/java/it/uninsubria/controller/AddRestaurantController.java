package it.uninsubria.controller;

import it.uninsubria.dto.CuisineType;
import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.session.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for the add restaurant view.
 * Handles restaurant registration form and navigation for restaurant owners.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class AddRestaurantController {

    private static final Logger LOGGER = Logger.getLogger(AddRestaurantController.class.getName());

    // Restaurant information fields
    @FXML private TextField nameField;
    @FXML private ComboBox<CuisineType> cuisineTypeComboBox;
    @FXML private TextField avgPriceField;

    // Location information fields
    @FXML private TextField nationField;
    @FXML private TextField cityField;
    @FXML private TextField addressField;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;

    // Services checkboxes
    @FXML private CheckBox deliveryCheckBox;
    @FXML private CheckBox onlineBookingCheckBox;

    // Buttons and messages
    @FXML private Button cancelButton;
    @FXML private Button submitButton;
    @FXML private Label errorLabel;

    private UserSession userSession;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        userSession = UserSession.getInstance();

        // Initialize UI
        errorLabel.setText("");
        initializeCuisineTypes();

        // Check if user is authorized (should be restaurant owner)
        if (!userSession.isLoggedIn() || !userSession.isOwner()) {
            handleCancel();
            return;
        }
    }

    /**
     * Handles the submit button action.
     * Validates the form inputs and attempts to register a new restaurant.
     */
    @FXML
    private void handleSubmit() {
        // Clear previous error messages
        errorLabel.setText("");

        // Validate required fields
        if (!validateRequiredFields()) {
            return;
        }

        if (!validateNumericFields()) {
            return;
        }

        // Build restaurant DTO
        RestaurantDTO restaurantDTO = buildRestaurantDTO();

        try {
            // TODO: Replace with actual RMI call to RestaurantService.createRestaurant()
            handleRestaurantSubmission(restaurantDTO);

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Restaurant Registration Successful");
            alert.setHeaderText("Welcome to TheKnife!");
            alert.setContentText("Your restaurant has been registered successfully.");
            alert.showAndWait();

            // Navigate back to My Area
            handleCancel();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error registering restaurant", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration Failed");
            alert.setHeaderText("Error during registration");
            alert.setContentText("We are sorry for the inconvenience. Please try again later.");
            alert.showAndWait();
        }
    }

    /**
     * Handles the cancel button action.
     * Navigates back to the My Area view without saving changes.
     */
    @FXML
    private void handleCancel() {
        try {
            // Load the My Area view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("my-area-view.fxml"));
            Parent root = loader.load();

            // Get the My Area controller (it will initialize itself with the current session)
            MyAreaController myAreaController = loader.getController();

            // Get the current stage
            Stage stage = (Stage) cancelButton.getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("TheKnife - My Area");
            stage.show();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading My Area view", e);
            errorLabel.setText("Error navigating back to My Area: " + e.getMessage());
        }
    }

    /**
     * Builds a RestaurantDTO object from the current form inputs.
     *
     * @return A RestaurantDTO with the form data
     */
    private RestaurantDTO buildRestaurantDTO() {
        String name = nameField.getText().trim();
        String nation = nationField.getText().trim();
        String city = cityField.getText().trim();
        String address = addressField.getText().trim();
        Double latitude = Double.parseDouble(latitudeField.getText().trim());
        Double longitude = Double.parseDouble(longitudeField.getText().trim());
        Double avgPrice = Double.parseDouble(avgPriceField.getText().trim());
        Boolean delivery = deliveryCheckBox.isSelected();
        Boolean onlineBooking = onlineBookingCheckBox.isSelected();
        CuisineType cuisine = cuisineTypeComboBox.getValue();
        String ownerUsrId = userSession.getUserId();

        return new RestaurantDTO(name, nation, city, address, latitude, longitude,
                avgPrice, delivery, onlineBooking, cuisine, ownerUsrId);
    }

    /**
     * Handles submitting the restaurant data to the server.
     * TODO: Replace with actual RMI call to RestaurantService.createRestaurant()
     *
     * @param restaurant The restaurant data to submit
     */
    private void handleRestaurantSubmission(RestaurantDTO restaurant) {
        System.out.println("=== RESTAURANT REGISTRATION SUBMISSION ===");
        System.out.println("Owner ID: " + userSession.getUserId());
        System.out.println("Restaurant Name: " + restaurant.name);
        System.out.println("Cuisine: " + restaurant.cuisine.getDisplayName());
        System.out.println("Coordinates: " + restaurant.latitude + ", " + restaurant.longitude);
        System.out.println("Average Price: €" + restaurant.avg_price);
        System.out.println("Delivery: " + restaurant.delivery);
        System.out.println("Online Booking: " + restaurant.online_booking);
        System.out.println("Restaurant DTO: " + restaurant.toString());
        System.out.println("==========================================");

        LOGGER.info("Restaurant registered successfully: " + restaurant.name + " by owner: " + userSession.getUserId());
    }

    /**
     * Validates that all required fields are filled.
     *
     * @return true if all required fields are valid, false otherwise
     */
    private boolean validateRequiredFields() {
        StringBuilder errors = new StringBuilder();

        // Check restaurant information
        if (nameField.getText().trim().isEmpty()) {
            errors.append("Restaurant name is required.\n");
        }

        if (cuisineTypeComboBox.getValue() == null) {
            errors.append("Cuisine type is required.\n");
        }

        if (avgPriceField.getText().trim().isEmpty()) {
            errors.append("Average price is required.\n");
        }

        // Check location information
        if (nationField.getText().trim().isEmpty()) {
            errors.append("Country is required.\n");
        }

        if (cityField.getText().trim().isEmpty()) {
            errors.append("City is required.\n");
        }

        if (addressField.getText().trim().isEmpty()) {
            errors.append("Street address is required.\n");
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

        // Validate average price
        try {
            double avgPrice = Double.parseDouble(avgPriceField.getText().trim());
            if (avgPrice <= 0) {
                errors.append("Average price must be greater than 0.\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Average price must be a valid number.\n");
        }

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
     * Initializes the cuisine type combo box with all available cuisine types.
     * Uses the same pattern as the search view.
     */
    private void initializeCuisineTypes() {
        // Create an ObservableList with all CuisineType enum values
        ObservableList<CuisineType> cuisineTypes = FXCollections.observableArrayList(CuisineType.values());
        cuisineTypeComboBox.setItems(cuisineTypes);

        // Set a custom string converter to display the cuisine type names
        cuisineTypeComboBox.setConverter(new StringConverter<CuisineType>() {
            @Override
            public String toString(CuisineType cuisineType) {
                return cuisineType == null ? "Select cuisine type" : cuisineType.getDisplayName();
            }

            @Override
            public CuisineType fromString(String string) {
                return Arrays.stream(CuisineType.values())
                        .filter(c -> c.getDisplayName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        // Set prompt text
        cuisineTypeComboBox.setPromptText("Select cuisine type");
    }

    /**
     * Gets the current user session.
     *
     * @return The current UserSession instance
     */
    public UserSession getUserSession() {
        return userSession;
    }
}