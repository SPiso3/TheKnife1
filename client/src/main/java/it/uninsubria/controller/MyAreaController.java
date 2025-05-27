package it.uninsubria.controller;

import it.uninsubria.controller.ui_components.GenericResultsComponent;
import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.session.UserSession;
import it.uninsubria.utilclient.ClientUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for the My Area view.
 * Handles displaying user's favorites, owned restaurants, and reviewed restaurants
 * based on the user's role (client or restaurateur).
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class MyAreaController {

    private static final Logger LOGGER = Logger.getLogger(MyAreaController.class.getName());

    // Header components
    @FXML private Label titleLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label leftStatsLabel;
    @FXML private Label averageRatingLabel;
    @FXML private Label reviewedStatsLabel;

    // Navigation buttons
    @FXML private Button backToSearchButton;
    @FXML private Button logoutButton;
    @FXML private Button actionButton;

    // Content panels
    @FXML private TitledPane leftTitledPane;
    @FXML private TitledPane rightTitledPane;
    @FXML private VBox leftResultsContainer;
    @FXML private VBox rightResultsContainer;

    // Status indicator
    @FXML private Label statusLabel;

    // Components and session
    private UserSession userSession;
    private GenericResultsComponent leftResultsComponent;
    private GenericResultsComponent rightResultsComponent;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        userSession = UserSession.getInstance();

        // Initialize result components
        initializeResultComponents();

        // Setup UI based on user role
        setupUserInterface();

        // Load user data
        loadUserData();
    }

    /**
     * Initializes the result components for both panels.
     */
    private void initializeResultComponents() {
        // Left panel results component
        leftResultsComponent = new GenericResultsComponent();
        leftResultsComponent.setOnRestaurantClick(this::handleRestaurantClick);
        leftResultsContainer.getChildren().clear();
        leftResultsContainer.getChildren().add(leftResultsComponent);

        // Right panel results component
        rightResultsComponent = new GenericResultsComponent();
        rightResultsComponent.setOnRestaurantClick(this::handleRestaurantClick);
        rightResultsContainer.getChildren().clear();
        rightResultsContainer.getChildren().add(rightResultsComponent);
    }

    /**
     * Sets up the user interface based on the user's role.
     */
    private void setupUserInterface() {
        if (!userSession.isLoggedIn()) {
            handleBackToSearch();
            return;
        }

        // Update welcome message
        welcomeLabel.setText("Welcome to your area, " + userSession.getUserId() + "!");

        // Configure UI based on user role
        if (userSession.isClient()) {
            setupClientInterface();
        } else if (userSession.isOwner()) {
            setupRestaurateurInterface();
        }
    }

    /**
     * Sets up the interface for client users.
     */
    private void setupClientInterface() {
        titleLabel.setText("My Area - Client");
        leftTitledPane.setText("My Favorites");
        rightTitledPane.setText("My Reviews");
        actionButton.setText("Find More Restaurants");
    }

    /**
     * Sets up the interface for restaurateur users.
     */
    private void setupRestaurateurInterface() {
        titleLabel.setText("My Area - Restaurant Owner");
        leftTitledPane.setText("My Restaurants");
        rightTitledPane.setText("My Reviews");
        actionButton.setText("Add Restaurant");
    }

    /**
     * Loads user data based on their role.
     */
    private void loadUserData() {
        statusLabel.setText("Loading your data...");

        try {
            if (userSession.isClient()) {
                loadClientData();
            } else if (userSession.isOwner()) {
                loadRestaurateurData();
            }

            statusLabel.setText("Data loaded successfully");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading user data", e);
            statusLabel.setText("Error loading data");
        }
    }

    /**
     * Loads data for client users (favorites and reviewed restaurants).
     */
    private void loadClientData() {
        String userId = userSession.getUserId();

        // Load favorite restaurants
        // TODO: Replace with actual RMI call to RestaurantService.getFavoriteRestaurants(userId)
        List<RestaurantDTO> favoriteRestaurants = getMockFavoriteRestaurants();
        leftResultsComponent.showRestaurants(favoriteRestaurants);

        // Load reviewed restaurants
        // TODO: Replace with actual RMI call to RestaurantService.getReviewedRestaurants(userId)
        List<RestaurantDTO> reviewedRestaurants = getMockReviewedRestaurants();
        rightResultsComponent.showRestaurants(reviewedRestaurants);

        // Update statistics labels
        updateClientStatistics(favoriteRestaurants, reviewedRestaurants);

        LOGGER.info("Loaded " + favoriteRestaurants.size() + " favorites and " +
                reviewedRestaurants.size() + " reviewed restaurants for client: " + userId);
    }

    /**
     * Loads data for restaurateur users (owned and reviewed restaurants).
     */
    private void loadRestaurateurData() {
        String userId = userSession.getUserId();

        // Load owned restaurants
        // TODO: Replace with actual RMI call to RestaurantService.getOwnedRestaurants(userId)
        List<RestaurantDTO> ownedRestaurants = getMockOwnedRestaurants();
        leftResultsComponent.showRestaurants(ownedRestaurants);

        // Load reviewed restaurants (restaurateurs can also review other restaurants)
        // TODO: Replace with actual RMI call to RestaurantService.getReviewedRestaurants(userId)
        List<RestaurantDTO> reviewedRestaurants = getMockReviewedRestaurants();
        rightResultsComponent.showRestaurants(reviewedRestaurants);

        // Update statistics labels
        updateOwnerStatistics(ownedRestaurants, reviewedRestaurants);

        LOGGER.info("Loaded " + ownedRestaurants.size() + " owned restaurants and " +
                reviewedRestaurants.size() + " reviewed restaurants for owner: " + userId);
    }

    /**
     * Updates statistics labels for client users.
     *
     * @param favoriteRestaurants List of favorite restaurants
     * @param reviewedRestaurants List of reviewed restaurants
     */
    private void updateClientStatistics(List<RestaurantDTO> favoriteRestaurants,
                                        List<RestaurantDTO> reviewedRestaurants) {
        // Update favorites count
        leftStatsLabel.setText("My Favorites: " + favoriteRestaurants.size() + " restaurants");

        // Calculate average rating of favorite restaurants
        double avgRating = calculateAverageRating(favoriteRestaurants);
        if (avgRating > 0) {
            averageRatingLabel.setText(String.format("Average rating: %.1f stars", avgRating));
        } else {
            averageRatingLabel.setText("Average rating: N/A");
        }

        // Update reviewed count
        reviewedStatsLabel.setText("Reviewed restaurants: " + reviewedRestaurants.size());
    }

    /**
     * Updates statistics labels for restaurant owner users.
     *
     * @param ownedRestaurants List of owned restaurants
     * @param reviewedRestaurants List of reviewed restaurants
     */
    private void updateOwnerStatistics(List<RestaurantDTO> ownedRestaurants,
                                       List<RestaurantDTO> reviewedRestaurants) {
        // Update owned restaurants count
        leftStatsLabel.setText("My Restaurants: " + ownedRestaurants.size() + " restaurants");

        // Calculate average rating of owned restaurants
        double avgRating = calculateAverageRating(ownedRestaurants);
        if (avgRating > 0) {
            averageRatingLabel.setText(String.format("Average rating: %.1f stars", avgRating));
        } else {
            averageRatingLabel.setText("Average rating: N/A");
        }

        // Update reviewed count
        reviewedStatsLabel.setText("Reviewed restaurants: " + reviewedRestaurants.size());
    }

    /**
     * Calculates the average rating of a list of restaurants.
     *
     * @param restaurants List of restaurants to calculate average for
     * @return Average rating, or 0 if no valid ratings found
     */
    private double calculateAverageRating(List<RestaurantDTO> restaurants) {
        if (restaurants.isEmpty()) {
            return 0.0;
        }

        double totalRating = 0.0;
        int count = 0;

        for (RestaurantDTO restaurant : restaurants) {
            if (restaurant.avg_rating != null && restaurant.avg_rating > 0) {
                totalRating += restaurant.avg_rating;
                count++;
            }
        }

        return count > 0 ? totalRating / count : 0.0;
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
     * Handles the action button click.
     * For clients: navigates to search view.
     * For restaurateurs: opens restaurant registration (TODO).
     */
    @FXML
    private void handleActionButton() {
        if (userSession.isClient()) {
            handleBackToSearch();
        } else if (userSession.isOwner()) {
            handleAddRestaurant();
        }
    }

    /**
     * Handles adding a new restaurant (for restaurateurs).
     * Navigates to the restaurant registration view.
     */
    private void handleAddRestaurant() {
        try {
            // Load the add restaurant view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-restaurant-view.fxml"));
            Parent root = loader.load();

            // Get the add restaurant controller (it will initialize itself with the current session)
            AddRestaurantController addRestaurantController = loader.getController();

            // Get the current stage and replace the scene
            Stage stage = (Stage) actionButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("TheKnife - Add Restaurant");

            statusLabel.setText("Navigated to restaurant registration");
            LOGGER.info("Restaurant owner navigated to restaurant registration: " + userSession.getUserId());

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading add restaurant view", e);
            statusLabel.setText("Error loading restaurant registration: " + e.getMessage());
        }
    }

    /**
     * Handles returning to the search view.
     */
    @FXML
    private void handleBackToSearch() {
        try {
            // Load the search view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("search-view.fxml"));
            Parent root = loader.load();

            // Get the search controller and set the user session
            SearchController searchController = loader.getController();
            searchController.setUserSession();

            // Get the current stage and replace the scene
            Stage stage = (Stage) backToSearchButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("TheKnife - Restaurant Search");

            LOGGER.info("Returned to search view");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading search view", e);
            statusLabel.setText("Error returning to search");
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

            // Get the current stage
            Stage stage = (Stage) logoutButton.getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("TheKnife - Login");
            stage.show();

            LOGGER.info("User logged out successfully");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading login view", e);
            statusLabel.setText("Error returning to login: " + e.getMessage());
        }
    }

    // Mock data methods - TODO: Replace with actual RMI calls

    /**
     * Returns mock favorite restaurants for testing.
     * TODO: Replace with actual RMI call to RestaurantService.getFavoriteRestaurants()
     *
     * @return List of mock favorite restaurants
     */
    private List<RestaurantDTO> getMockFavoriteRestaurants() {
        List<RestaurantDTO> restaurants = ClientUtil.getRestaurantList();
        // Return first 2 restaurants as favorites
        return restaurants.subList(0, Math.min(2, restaurants.size()));
    }

    /**
     * Returns mock reviewed restaurants for testing.
     * TODO: Replace with actual RMI call to RestaurantService.getReviewedRestaurants()
     *
     * @return List of mock reviewed restaurants
     */
    private List<RestaurantDTO> getMockReviewedRestaurants() {
        List<RestaurantDTO> restaurants = ClientUtil.getRestaurantList();
        // Return restaurants 2-4 as reviewed
        int start = Math.min(2, restaurants.size());
        int end = Math.min(4, restaurants.size());
        return restaurants.subList(start, end);
    }

    /**
     * Returns mock owned restaurants for testing.
     * TODO: Replace with actual RMI call to RestaurantService.getOwnedRestaurants()
     *
     * @return List of mock owned restaurants
     */
    private List<RestaurantDTO> getMockOwnedRestaurants() {
        List<RestaurantDTO> restaurants = ClientUtil.getRestaurantList();
        // Return first 3 restaurants as owned
        return restaurants.subList(0, Math.min(3, restaurants.size()));
    }

    /**
     * Refreshes all user data.
     * Can be called after making changes to favorites, reviews, or restaurants.
     */
    public void refreshData() {
        loadUserData();
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