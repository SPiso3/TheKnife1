package it.uninsubria.controller;

import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.dto.ReviewDTO;
import it.uninsubria.services.ReviewService;
import it.uninsubria.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for the add review modal window.
 * Handles creating new reviews for restaurants with star rating and text input,
 * as well as editing and deleting existing reviews.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class AddReviewController {

    private static final Logger LOGGER = Logger.getLogger(AddReviewController.class.getName());
    private static final int MAX_REVIEW_LENGTH = 1000; // Maximum review length

    // FXML components
    @FXML private Label titleLabel;
    @FXML private Label restaurantNameLabel;
    @FXML private ToggleButton star1Button;
    @FXML private ToggleButton star2Button;
    @FXML private ToggleButton star3Button;
    @FXML private ToggleButton star4Button;
    @FXML private ToggleButton star5Button;
    @FXML private Label ratingLabel;
    @FXML private TextArea reviewTextArea;
    @FXML private Label characterCountLabel;
    @FXML private Label errorLabel;
    @FXML private Button cancelButton;
    @FXML private Button submitButton;
    @FXML private Button deleteButton;

    // Data and state
    private ToggleButton[] starButtons;
    private int currentRatingSelection = 1;
    private RestaurantDTO restaurant;
    private UserSession userSession;
    private ReviewService reviewService;
    private boolean isEditingMode = false;
    private ReviewDTO existingReview = null;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        userSession = UserSession.getInstance();
        initServices();

        // Initialize star buttons array
        starButtons = new ToggleButton[]{star1Button, star2Button, star3Button, star4Button, star5Button};

        // Setup components
        initializeStarRating();
        initializeTextArea();
        applyStarStyling();

        // Set static UI elements
        titleLabel.setText("Write a Review");
        submitButton.setText("Submit Review");
        reviewTextArea.setPromptText("Share your experience at this restaurant...");

        // Initialize with default state
        updateStarSelection(1);
        updateCharacterCount();

        // Delete button is initially hidden (will be shown only in edit mode)
        deleteButton.setVisible(false);
        // Don't set managed=false here, let JavaFX handle it
    }

    /**
     * Initializes the RMI services.
     */
    private void initServices() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            reviewService = (ReviewService) registry.lookup("ReviewService");
        } catch (NotBoundException | RemoteException e) {
            LOGGER.log(Level.SEVERE, "Error connecting to ReviewService: " + e.getMessage(), e);
            showError("Unable to connect to review service. Please try again later.");
        }
    }

    /**
     * Sets the restaurant for this review.
     * This method should be called after the controller is loaded for creating a new review.
     *
     * @param restaurant The restaurant to review
     */
    public void setRestaurant(RestaurantDTO restaurant) {
        this.restaurant = restaurant;
        this.isEditingMode = false;
        this.existingReview = null;

        restaurantNameLabel.setText(restaurant.name);

        // Hide delete button for new reviews
        deleteButton.setVisible(false);

        LOGGER.info("Set restaurant for new review: " + restaurant.name + ", delete button hidden");
    }

    /**
     * Sets an existing review for editing.
     * This method populates the form fields with the existing review data.
     *
     * @param review The existing review to edit
     */
    public void setReview(ReviewDTO review) {
        if (review != null) {
            this.existingReview = review;
            this.isEditingMode = true;

            // Set the restaurant info
            this.restaurant = new RestaurantDTO();
            this.restaurant.id = review.rest_id;

            // Update title for editing mode
            titleLabel.setText("Edit Review");
            submitButton.setText("Update Review");

            // Set the rating
            updateStarSelection(review.rating);

            // Set the review text
            if (review.customer_msg != null) {
                reviewTextArea.setText(review.customer_msg);
            }

            // Update character count
            updateCharacterCount();

            // Show delete button for existing reviews
            deleteButton.setVisible(true);

            LOGGER.info("Set review for editing, delete button should be visible: " + deleteButton.isVisible());
        }
    }

    /**
     * Initializes the star rating functionality.
     */
    private void initializeStarRating() {
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

        currentRatingSelection = rating;

        // Update star button states
        for (int i = 0; i < starButtons.length; i++) {
            starButtons[i].setSelected(i < rating);
        }

        // Update rating label
        String starText = rating == 1 ? "star" : "stars";
        ratingLabel.setText(rating + " " + starText + " selected");

        // Clear any rating-related errors
        hideError();
    }

    /**
     * Applies custom styling to star buttons.
     */
    private void applyStarStyling() {
        String selectedStyle = "-fx-background-color: #ffc107; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-border-color: #e6a800; -fx-border-width: 1;";
        String unselectedStyle = "-fx-background-color: #f5f5f5; -fx-text-fill: #cccccc; -fx-border-color: #e0e0e0; -fx-border-width: 1;";

        for (ToggleButton star : starButtons) {
            star.setText("★");

            // Add style change listeners
            star.selectedProperty().addListener((observable, oldValue, newValue) -> {
                star.setStyle(newValue ? selectedStyle : unselectedStyle);
            });
        }
    }

    /**
     * Initializes the text area functionality including character counting.
     */
    private void initializeTextArea() {
        // Add character count listener
        reviewTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateCharacterCount();

            // Clear text-related errors when user starts typing
            if (!newValue.trim().isEmpty()) {
                hideError();
            }

            // Enforce maximum length
            if (newValue.length() > MAX_REVIEW_LENGTH) {
                reviewTextArea.setText(oldValue);
            }
        });
    }

    /**
     * Updates the character count display.
     */
    private void updateCharacterCount() {
        int length = reviewTextArea.getText().length();
        characterCountLabel.setText(length + " characters");

        // Change color based on length
        if (length > MAX_REVIEW_LENGTH * 0.9) {
            characterCountLabel.setStyle("-fx-text-fill: #d32f2f;"); // Red when near limit
        } else if (length > MAX_REVIEW_LENGTH * 0.7) {
            characterCountLabel.setStyle("-fx-text-fill: #f57c00;"); // Orange when getting close
        } else {
            characterCountLabel.setStyle("-fx-text-fill: #666666;"); // Normal gray
        }
    }

    /**
     * Validates the form input before submission.
     *
     * @return true if the form is valid, false otherwise
     */
    private boolean validateForm() {
        // Check if restaurant is set
        if (restaurant == null) {
            showError("No restaurant selected. Please try again.");
            return false;
        }

        // Check if user is logged in
        if (!userSession.isLoggedIn()) {
            showError("You must be logged in to write a review.");
            return false;
        }

        // Check if review text is provided
        String reviewText = reviewTextArea.getText().trim();
        if (reviewText.isEmpty()) {
            showError("Please write a review before submitting.");
            return false;
        }

        // Check text length
        if (reviewText.length() > MAX_REVIEW_LENGTH) {
            showError("Review text exceeds maximum length of " + MAX_REVIEW_LENGTH + " characters.");
            return false;
        }

        // Check rating selection
        if (currentRatingSelection < 1 || currentRatingSelection > 5) {
            showError("Please select a rating before submitting.");
            return false;
        }

        return true;
    }

    /**
     * Handles the submit button action.
     * Validates and submits the review.
     */
    @FXML
    private void handleSubmit() {
        if (!validateForm()) {
            return;
        }

        try {
            // Create review DTO
            ReviewDTO newReview = new ReviewDTO(
                    userSession.getUserId(),
                    restaurant.id,
                    reviewTextArea.getText().trim(),
                    currentRatingSelection
            );

            // Submit review via service
            boolean success = reviewService.createOrUpdateReview(newReview);
            if (success) {
                String operationType = isEditingMode ? "updated" : "added";
                String titleText = isEditingMode ? "Review Updated" : "Review Added";
                String headerText = isEditingMode ? "Successfully updated review" : "Successfully submitted review";

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(titleText);
                alert.setHeaderText(headerText);
                alert.setContentText("Thank you for sharing your experience");
                alert.showAndWait();

                LOGGER.info("Review " + operationType + " successfully for restaurant " + restaurant.id);
                closeWindow();
            } else {
                String operationType = isEditingMode ? "Update" : "Add";
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(operationType + " Review Failed");
                alert.setHeaderText("The review could not be " + (isEditingMode ? "updated" : "submitted"));
                alert.setContentText("We are sorry for the inconvenience. Please try again later.");
                alert.showAndWait();
                showError("Failed to " + (isEditingMode ? "update" : "submit") + " review. Please try again.");
            }

        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "Error submitting review", e);
            showError("Network error occurred. Please check your connection and try again.");
        }
    }

    /**
     * Handles the delete review button action.
     * Deletes the current review after validation.
     */
    @FXML
    private void handleDeleteReview() {
        // Validate that we're in editing mode and have a review to delete
        if (!isEditingMode || existingReview == null) {
            showError("No review to delete.");
            return;
        }

        // Check if user is logged in
        if (!userSession.isLoggedIn()) {
            showError("You must be logged in to delete a review.");
            return;
        }

        try {
            // Call the delete service
            boolean success = reviewService.deleteReview(userSession.getUserId(), restaurant.id);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Review Deleted");
                alert.setHeaderText("Review successfully deleted");
                alert.setContentText("Your review has been removed from the restaurant");
                alert.showAndWait();

                LOGGER.info("Review deleted successfully for restaurant " + restaurant.id + " by user " + userSession.getUserId());
                closeWindow();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Delete Review Failed");
                alert.setHeaderText("The review could not be deleted");
                alert.setContentText("We are sorry for the inconvenience. Please try again later.");
                alert.showAndWait();
                showError("Failed to delete review. Please try again.");
            }

        } catch (SecurityException e) {
            LOGGER.log(Level.WARNING, "Security error deleting review: " + e.getMessage(), e);
            showError("You don't have permission to delete this review.");
        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "Error deleting review", e);
            showError("Network error occurred. Please check your connection and try again.");
        }
    }

    /**
     * Handles the cancel button action.
     * Closes the modal window without saving changes.
     */
    @FXML
    private void handleCancel() {
        LOGGER.info("Review operation cancelled by user");
        closeWindow();
    }

    /**
     * Shows an error message to the user.
     *
     * @param message The error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * Hides the error message.
     */
    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setText("");
    }

    /**
     * Closes the modal window.
     */
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Gets the current rating selection.
     *
     * @return The currently selected rating (1-5)
     */
    public int getCurrentRating() {
        return currentRatingSelection;
    }

    /**
     * Gets the current review text.
     *
     * @return The text content of the review
     */
    public String getReviewText() {
        return reviewTextArea.getText().trim();
    }

    /**
     * Checks if the controller is in editing mode.
     *
     * @return true if editing an existing review, false if creating new
     */
    public boolean isEditingMode() {
        return isEditingMode;
    }

    /**
     * Gets the existing review being edited.
     *
     * @return The existing review DTO, or null if creating a new review
     */
    public ReviewDTO getExistingReview() {
        return existingReview;
    }
}