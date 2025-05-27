package it.uninsubria.controller;

import it.uninsubria.controller.ui_components.GenericResultsComponent;
import it.uninsubria.dto.CuisineType;
import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.dto.ReviewDTO;
import it.uninsubria.dto.SearchCriteriaDTO;
import it.uninsubria.controller.LoginController;
import it.uninsubria.services.RestaurantService;
import it.uninsubria.services.UserService;
import it.uninsubria.session.UserSession;
import it.uninsubria.utilclient.ClientUtil;
import javafx.beans.value.ChangeListener;
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
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for the restaurant search view.
 * Handles search criteria selection and navigation.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class SearchController {
    private static final Logger LOGGER = Logger.getLogger(SearchController.class.getName());
    // Location display
    @FXML
    private Label locationLabel;
    // Search criteria controls
    @FXML private ComboBox<CuisineType> cuisineTypeComboBox;
    @FXML private Slider minPriceSlider;
    @FXML private Slider maxPriceSlider;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private CheckBox deliveryCheckBox;
    @FXML private CheckBox onlineBookingCheckBox;
    // Star rating toggles
    @FXML private ToggleButton star1Button;
    @FXML private ToggleButton star2Button;
    @FXML private ToggleButton star3Button;
    @FXML private ToggleButton star4Button;
    @FXML private ToggleButton star5Button;
    @FXML private Label ratingLabel;
    // Navigation and action buttons
    @FXML private Button userAreaButton;
    @FXML private Button logoutButton;
    @FXML private Button searchButton;
    // Status indicator
    @FXML private Label statusLabel;
    // Result Pane
    @FXML private TitledPane rightTitledPane;
    private GenericResultsComponent resultsComponent;

    private UserSession userSession;
    private RestaurantService restaurantService;

    private ToggleButton[] starButtons;
    private int currentRatingSelection = 1; // Default is 1 star
    private final DecimalFormat priceFormat = new DecimalFormat("#.##");

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        setUserSession();

        initServices();
        // Initialize UI
        starButtons = new ToggleButton[]{star1Button, star2Button, star3Button, star4Button, star5Button};
        initializeCuisineTypes();
        initializePriceControls();
        initializeStarRating();
        initializeResultsComponent();
        // Set default status
        updateStatus("Ready to search");
    }

    private void initServices() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            restaurantService= (RestaurantService) registry.lookup("RestaurantService");
        } catch (NotBoundException | RemoteException e) {
            System.err.println("Error connecting to UserService: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void setUserSession() {
        this.userSession = UserSession.getInstance();
        updateLocationFromSession();
    }


    /**
     * Updates the location label with coordinates from the user session.
     */
    private void updateLocationFromSession() {
        if (userSession != null) {
            double latitude = 0.0;
            double longitude = 0.0;
            double[] coordinates = userSession.getUserCoordinates();
            if (coordinates != null && coordinates.length == 2) {
                latitude = coordinates[0];
                longitude = coordinates[1];
            }
            // Update location label
            locationLabel.setText(String.format("Latitude: %.6f, Longitude: %.6f", latitude, longitude));
        } else {
            locationLabel.setText("No location data available");
        }
    }

    /**
     * Updates the status label with a message.
     *
     * @param message The status message
     */
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Handles the search button action.
     * Collects search criteria and navigates to results view.
     */
    @FXML
    private void handleSearch() {
        updateStatus("Searching...");

        // use service to get results
        SearchCriteriaDTO searchCriteria = buildSearchCriteria();
        List<RestaurantDTO> restaurants = null;
        try {
            restaurants = restaurantService.searchRestaurants(searchCriteria);
        } catch (RemoteException e) {
            statusLabel.setText("Error searching restaurants");
            throw new RuntimeException(e);
        }

        // Show results component and update with restaurants
        showResultsPanel();

        resultsComponent.showRestaurants(restaurants);

        // Update status
        statusLabel.setText("Found " + restaurants.size() + " restaurants");
    }

    /**
     * Replaces the right panel content with the results component.
     */
    private void showResultsPanel() {
        if (rightTitledPane != null) {
            rightTitledPane.setText("Search Results");
            rightTitledPane.setContent(resultsComponent);
        }
    }

    /**
     * Builds a SearchCriteriaDTO object from the current UI selection.
     *
     * @return A SearchCriteriaDTO with the selected search criteria
     */
    private SearchCriteriaDTO buildSearchCriteria() {
        double minPrice = minPriceSlider.getValue();
        double maxPrice = maxPriceSlider.getValue();

        double latitude = 0.0;
        double longitude = 0.0;
        if (userSession != null) {
            double[] coord = userSession.getUserCoordinates();
            latitude = coord != null ? coord[0] : 0.0;
            longitude = coord != null ? coord[1] : 0.0;

        }

        // Build search criteria using builder pattern
        SearchCriteriaDTO.Builder builder = SearchCriteriaDTO.builder()
                .coordinates(latitude, longitude)
                .priceRange(minPrice > 0 ? minPrice : null, maxPrice < 300 ? maxPrice : null)
                .minRating((double) currentRatingSelection);

        // Add optional criteria
        if (cuisineTypeComboBox.getValue() != null) {
            builder.cuisineType(cuisineTypeComboBox.getValue());
        }

        if (deliveryCheckBox.isSelected()) {
            builder.deliveryAvailable(true);
        }

        if (onlineBookingCheckBox.isSelected()) {
            builder.onlineBookingAvailable(true);
        }

        return builder.build();
    }


    /**
     * Handles clicking on a restaurant card.
     * Opens the restaurant detail window with the selected restaurant information.
     *
     * @param restaurant The clicked restaurant
     */
    private void handleRestaurantClick(RestaurantDTO restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("restaurant-info-view.fxml"));
            Parent root = loader.load();

            // Get the controller and set the restaurant data
            RestaurantInfoController controller = loader.getController();
            controller.setRestaurant(restaurant);

            // Create new stage for restaurant info
            Stage restaurantStage = new Stage();
            restaurantStage.setTitle("Restaurant Info - " + restaurant.name);
            restaurantStage.setScene(new Scene(root));
            // Set window properties
            restaurantStage.setResizable(true);
            restaurantStage.setMinWidth(600);
            restaurantStage.setMinHeight(500);

            // Show the window (non-modal)
            restaurantStage.show();

            statusLabel.setText("Opened details for: " + restaurant.name);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading restaurant info view", e);
            statusLabel.setText("Error opening restaurant details");
        }
    }

    /**
     * Handles the user area button action.
     * Navigates to the user's personal area.
     */
    @FXML
    private void handleUserArea() {
        try {
            // Load the my area view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("my-area-view.fxml"));
            Parent root = loader.load();

            // Get the my area controller (it will initialize itself with the current session)
            MyAreaController myAreaController = loader.getController();

            // Get the current stage and replace the scene
            Stage stage = (Stage) userAreaButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("TheKnife - My Area");

            updateStatus("Navigated to My Area");
            LOGGER.info("User navigated to My Area");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading my area view", e);
            updateStatus("Error loading My Area: " + e.getMessage());
        }
    }

    /**
     * Handles the logout button action.
     * Clears the user session and returns to the login view.
     */
    @FXML
    private void handleLogout() {
        try {
            // Clear the user session
            if (userSession != null) {
                userSession.logout();
            }
            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();
            // Get the login controller and set the user session
            LoginController loginController = loader.getController();
            // Get the current stage
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("TheKnife - Login");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading login view", e);
            updateStatus("Error returning to login: " + e.getMessage());
        }
    }



    // UI STUFF, maybe refactor later, I know it's ugly

    /**
     * Initializes the results component and sets up the UI.
     * Add this to your existing initialize() method.
     */
    private void initializeResultsComponent() {
        // Initialize the results component
        resultsComponent = new GenericResultsComponent();

        // Set up the click handler for restaurant cards
        resultsComponent.setOnRestaurantClick(this::handleRestaurantClick);

    }


    /**
     * Initializes the cuisine type combo box with all available cuisine types.
     */
    private void initializeCuisineTypes() {
        // Create an ObservableList with all CuisineType enum values
        ObservableList<CuisineType> cuisineTypes = FXCollections.observableArrayList(CuisineType.values());
        cuisineTypeComboBox.setItems(cuisineTypes);
        // Set a custom string converter to display the cuisine type names
        cuisineTypeComboBox.setConverter(new StringConverter<CuisineType>() {
            @Override
            public String toString(CuisineType cuisineType) {
                return cuisineType == null ? "Any cuisine type" : cuisineType.getDisplayName();
            }

            @Override
            public CuisineType fromString(String string) {
                return Arrays.stream(CuisineType.values())
                        .filter(c -> c.getDisplayName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
        // Add an "Any" option at the beginning
        cuisineTypeComboBox.getItems().add(0, null);
        cuisineTypeComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Initializes the price range sliders and text fields with synchronization.
     */
    private void initializePriceControls() {
        // Sync slider value with text field for minimum price
        minPriceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double value = newVal.doubleValue();
            // Ensure min is not greater than max
            if (value > maxPriceSlider.getValue()) {
                minPriceSlider.setValue(maxPriceSlider.getValue());
                value = maxPriceSlider.getValue();
            }
            minPriceField.setText(priceFormat.format(value));
        });

        // Sync slider value with text field for maximum price
        maxPriceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double value = newVal.doubleValue();
            // Ensure max is not less than min
            if (value < minPriceSlider.getValue()) {
                maxPriceSlider.setValue(minPriceSlider.getValue());
                value = minPriceSlider.getValue();
            }
            maxPriceField.setText(priceFormat.format(value));
        });

        // Sync text field with slider for minimum price
        minPriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                minPriceField.setText(oldValue);
                return;
            }
            try {
                double value = newValue.isEmpty() ? 0 : Double.parseDouble(newValue);
                if (value >= 0 && value <= 300) {
                    minPriceSlider.setValue(value);
                }
            } catch (NumberFormatException e) {
                minPriceField.setText(oldValue);
            }
        });

        // Sync text field with slider for maximum price
        maxPriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                maxPriceField.setText(oldValue);
                return;
            }
            try {
                double value = newValue.isEmpty() ? 0 : Double.parseDouble(newValue);
                if (value >= 0 && value <= 300) {
                    maxPriceSlider.setValue(value);
                }
            } catch (NumberFormatException e) {
                maxPriceField.setText(oldValue);
            }
        });
    }

    /**
     * Initializes the star rating toggle buttons with custom behavior.
     */
    private void initializeStarRating() {
        // Initialize with 1 star selected
        updateStarSelection(1);
        // Add click handlers for each star button
        for (int i = 0; i < starButtons.length; i++) {
            final int starValue = i + 1;
            starButtons[i].setOnAction(event -> updateStarSelection(starValue));
        }
    }

    /**
     * Updates the star rating selection to show the specified number of stars.
     *
     * @param rating The rating value (1-5)
     */
    private void updateStarSelection(int rating) {
        if (rating < 1 || rating > 5) {
            return;
        }

        // Update current selection
        currentRatingSelection = rating;

        // Update star button states (selected up to the rating value)
        for (int i = 0; i < starButtons.length; i++) {
            starButtons[i].setSelected(i < rating);
        }

        // Update rating label
        ratingLabel.setText((rating-1) + ".0+ stars");
    }
}