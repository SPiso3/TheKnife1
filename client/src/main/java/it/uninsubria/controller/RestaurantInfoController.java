package it.uninsubria.controller;

import it.uninsubria.controller.ui_components.GenericResultsComponent;
import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.dto.ReviewDTO;
import it.uninsubria.session.UserSession;
import it.uninsubria.utilclient.ClientUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for the restaurant information view.
 * Handles displaying detailed restaurant information and customer reviews.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class RestaurantInfoController {

    private static final Logger LOGGER = Logger.getLogger(RestaurantInfoController.class.getName());

    // Restaurant info components
    @FXML private Label restaurantNameLabel;
    @FXML private Label cuisineLocationLabel;
    @FXML private Label addressLabel;
    @FXML private Label coordinatesLabel;
    @FXML private Label distanceLabel;
    @FXML private Label ratingStarsLabel;
    @FXML private Label ratingLabel;
    @FXML private Label priceLabel;
    @FXML private Label deliveryLabel;
    @FXML private Label bookingLabel;

    // Action buttons
    @FXML private Button addToFavoritesButton;
    @FXML private Button addReviewButton;

    // Reviews section
    @FXML private Label reviewsSectionLabel;
    @FXML private VBox reviewsContainer;

    // Data and components
    private RestaurantDTO restaurant;
    private UserSession userSession;
    private GenericResultsComponent reviewsComponent;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        userSession = UserSession.getInstance();
        setupReviewsComponent();

        // Initially hide buttons until restaurant data is set
        updateButtonVisibility();
    }

    /**
     * Sets the restaurant data and updates the display.
     *
     * @param restaurant The restaurant to display information for
     */
    public void setRestaurant(RestaurantDTO restaurant) {
        this.restaurant = restaurant;
        updateRestaurantInfo();
        loadReviews();
        updateButtonVisibility();
    }

    /**
     * Sets up the reviews component and adds it to the container.
     */
    private void setupReviewsComponent() {
        reviewsComponent = new GenericResultsComponent();
        reviewsComponent.setOnReviewClick(this::handleReviewClick);

        // Add the reviews component to the container
        reviewsContainer.getChildren().clear();
        reviewsContainer.getChildren().add(reviewsComponent);
    }

    /**
     * Updates the restaurant information display.
     */
    private void updateRestaurantInfo() {
        if (restaurant == null) return;

        // Basic info
        restaurantNameLabel.setText(restaurant.name);
        cuisineLocationLabel.setText(restaurant.cuisine.getDisplayName() + " • " + restaurant.getCity());

        // Address and location
        addressLabel.setText(restaurant.getStreet() + ", " + restaurant.getCity() + ", " + restaurant.getCountry());
        coordinatesLabel.setText(String.format("%.4f, %.4f", restaurant.latitude, restaurant.longitude));

        // Distance calculation
        updateDistanceDisplay();

        // Rating
        updateRatingDisplay();

        // Price
        priceLabel.setText(String.format("€%.0f average price", restaurant.avg_price));

        // Services
        updateServicesDisplay();
    }

    /**
     * Updates the distance display based on user location.
     */
    private void updateDistanceDisplay() {
        if (userSession.isLoggedIn()) {
            double[] userCoords = userSession.getUserCoordinates();
            if (userCoords != null) {
                double distance = ClientUtil.calculateDistance(
                        userCoords[0], userCoords[1],
                        restaurant.latitude, restaurant.longitude);
                distanceLabel.setText(String.format("%.1f km from your location", distance));
            } else {
                distanceLabel.setText("Distance: Location not available");
            }
        } else {
            distanceLabel.setText("Distance: Login to see distance");
        }
    }

    /**
     * Updates the rating display with stars and numbers.
     */
    private void updateRatingDisplay() {
        if (restaurant.avg_rating != null) {
            // Create star display
            int fullStars = (int) Math.round(restaurant.avg_rating);
            String stars = "★".repeat(Math.max(0, Math.min(5, fullStars))) +
                    "☆".repeat(Math.max(0, 5 - fullStars));
            ratingStarsLabel.setText(stars);

            // Rating text
            ratingLabel.setText(String.format("%.1f (%d reviews)",
                    restaurant.avg_rating, restaurant.rating_count));
        } else {
            ratingStarsLabel.setText("☆☆☆☆☆");
            ratingLabel.setText("No reviews yet");
        }
    }

    /**
     * Updates the services display (delivery and booking).
     */
    private void updateServicesDisplay() {
        deliveryLabel.setVisible(restaurant.delivery);
        deliveryLabel.setManaged(restaurant.delivery);

        bookingLabel.setVisible(restaurant.online_booking);
        bookingLabel.setManaged(restaurant.online_booking);
    }

    /**
     * Loads and displays reviews for the current restaurant.
     */
    private void loadReviews() {
        if (restaurant == null) return;

        try {
            // Show loading state
            reviewsComponent.showLoadingReviews();

            // Load reviews
            // TODO: Replace with actual RMI call when server is ready
            List<ReviewDTO> reviews = ClientUtil.getReviewList(restaurant.id);

            // Update reviews display
            reviewsComponent.showReviews(reviews);

            // Update section title
            reviewsSectionLabel.setText(String.format("Customer Reviews (%d)", reviews.size()));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading reviews for restaurant " + restaurant.id, e);
            reviewsComponent.setStatusMessage("Error loading reviews");
        }
    }

    /**
     * Updates button visibility based on user session and permissions.
     */
    private void updateButtonVisibility() {
        if (restaurant == null) {
            addToFavoritesButton.setVisible(false);
            addReviewButton.setVisible(false);
            return;
        }

        boolean isLoggedIn = userSession.isLoggedIn();
        boolean isClient = userSession.isClient();
        boolean isOwner = userSession.isOwner();
        boolean isRestaurantOwner = isOwner && restaurant.owner_usrId.equals(userSession.getUserId());

        // Add to Favorites: Only visible to clients
        addToFavoritesButton.setVisible(isLoggedIn && isClient);

        // Add Review: Visible to logged-in clients and owners who don't own this restaurant
        addReviewButton.setVisible(isLoggedIn && (isClient || (isOwner && !isRestaurantOwner)));

        // Update favorites button text
        // TODO: Check if restaurant is in favorites and update text accordingly
        // For now, always show "Add to Favorites"
        if (addToFavoritesButton.isVisible()) {
            addToFavoritesButton.setText("Add to Favorites");
        }
    }

    /**
     * Handles the "Add to Favorites" button action.
     */
    @FXML
    private void handleAddToFavorites() {
        if (!userSession.isLoggedIn() || !userSession.isClient()) {
            return;
        }

        // TODO: Implement RMI call to add/remove from favorites
        // For now, just toggle button text
        if (addToFavoritesButton.getText().equals("Add to Favorites")) {
            addToFavoritesButton.setText("Remove from Favorites");
            LOGGER.info("Added restaurant " + restaurant.name + " to favorites");
        } else {
            addToFavoritesButton.setText("Add to Favorites");
            LOGGER.info("Removed restaurant " + restaurant.name + " from favorites");
        }
    }

    /**
     * Handles the "Add Review" button action.
     * Opens the add review window for writing a new review.
     */
    @FXML
    private void handleAddReview() {
        if (!userSession.isLoggedIn()) {
            return;
        }

        try {
            // Load the add review FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-review-view.fxml"));
            Parent root = loader.load();

            // Get the controller and set it for review mode
            AddReviewController controller = loader.getController();
            controller.setForReview(restaurant);

            // Create modal stage for add review
            Stage reviewStage = new Stage();
            reviewStage.setTitle("Write Review - " + restaurant.name);
            reviewStage.setScene(new Scene(root));

            // Set window properties
            reviewStage.setResizable(false);
            reviewStage.initModality(Modality.WINDOW_MODAL);
            reviewStage.initOwner(addReviewButton.getScene().getWindow());

            // Show the window (modal) and wait for it to close
            reviewStage.showAndWait();

            // TODO: When RMI is implemented, the review will be submitted to server
            // After the window closes, refresh reviews to show the new review
            loadReviews();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading add review view", e);
        }
    }

    /**
     * Handles clicking on a review card.
     * For restaurant owners, opens reply window. For others, just logs the click.
     *
     * @param review The clicked review
     */
    private void handleReviewClick(ReviewDTO review) {
        if (!userSession.isLoggedIn()) {
            return;
        }

        // Check if current user is the restaurant owner
        boolean isRestaurantOwner = userSession.isOwner() &&
                restaurant.owner_usrId.equals(userSession.getUserId());

        if (isRestaurantOwner) {
            openReplyWindow(review);
        } else {
            // For non-owners, just log the click (future: could open review detail view)
            LOGGER.info("Review clicked: " + review.usr_id + " - " + review.rating + " stars");
            System.out.println("Review clicked: " + review.usr_id + " - " + review.rating + " stars");
        }
    }

    /**
     * Opens the reply window for a restaurant owner to reply to a review as a modal window.
     *
     * @param review The review to reply to
     */
    private void openReplyWindow(ReviewDTO review) {
        try {
            // Load the add reply FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-reply-view.fxml"));
            Parent root = loader.load();

            // Get the controller and set the review
            AddReplyController controller = loader.getController();
            controller.setReview(review);

            // Create modal stage for reply
            Stage replyStage = new Stage();
            replyStage.setTitle("Reply to Review - " + restaurant.name);
            replyStage.setScene(new Scene(root));

            // Set window properties for proper modal behavior
            replyStage.setResizable(false);
            replyStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            replyStage.initOwner(addReviewButton.getScene().getWindow());

            // Center the modal window relative to parent
            Stage parentStage = (Stage) addReviewButton.getScene().getWindow();
            replyStage.setX(parentStage.getX() + (parentStage.getWidth() - 650) / 2);
            replyStage.setY(parentStage.getY() + (parentStage.getHeight() - 550) / 2);

            // Show the window (modal) and wait for it to close
            replyStage.showAndWait();

            // After the window closes, refresh reviews to show the updated reply
            loadReviews();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading reply view", e);
        }
    }

    /**
     * Gets the current restaurant data.
     *
     * @return The current restaurant, or null if none set
     */
    public RestaurantDTO getRestaurant() {
        return restaurant;
    }
}