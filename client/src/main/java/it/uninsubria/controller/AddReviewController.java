package it.uninsubria.controller;

import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.dto.ReviewDTO;
import it.uninsubria.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.util.logging.Logger;

/**
 * Controller class for the add review modal window.
 * Handles both creating new reviews and replying to existing reviews.
 * This controller manages the star rating system and review text input.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class AddReviewController {

    private static final Logger LOGGER = Logger.getLogger(AddReviewController.class.getName());
    private static final int MAX_REVIEW_LENGTH = 1000; // Maximum review length

    // Header components
    @FXML private Label titleLabel;
    @FXML private Label restaurantNameLabel;

    // Rating components
    @FXML private ToggleButton star1Button;
    @FXML private ToggleButton star2Button;
    @FXML private ToggleButton star3Button;
    @FXML private ToggleButton star4Button;
    @FXML private ToggleButton star5Button;
    @FXML private Label ratingLabel;

    // Review text components
    @FXML private TextArea reviewTextArea;
    @FXML private Label characterCountLabel;

    // Error display
    @FXML private Label errorLabel;
    // Action buttons
    @FXML private Button cancelButton;
    @FXML private Button submitButton;

    // Data and state
    private ToggleButton[] starButtons;
    private int currentRatingSelection = 1;
    private RestaurantDTO restaurant;
    private ReviewDTO existingReview; // For reply mode
    private boolean isReplyMode = false;
    private UserSession userSession;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        userSession = UserSession.getInstance();

        // Initialize star buttons array
        starButtons = new ToggleButton[]{star1Button, star2Button, star3Button, star4Button, star5Button};

        // Setup star rating functionality
        initializeStarRating();

        // Setup text area functionality
        initializeTextArea();

        // Apply star button styling
        applyStarStyling();
    }

    /**
     * Sets up the controller for writing a new review for a restaurant.
     *
     * @param restaurant The restaurant to review
     */
    public void setForReview(RestaurantDTO restaurant) {
        this.restaurant = restaurant;
        this.isReplyMode = false;

        // Update UI for review mode
        titleLabel.setText("Write a Review");
        restaurantNameLabel.setText(restaurant.name);
        submitButton.setText("Submit Review");
        reviewTextArea.setPromptText("Share your experience at this restaurant...");

        // Reset to default state
        updateStarSelection(1);
        reviewTextArea.clear();
        updateCharacterCount();
    }

    /**
     * Sets up the controller for replying to an existing review.
     *
     * @param review The review to reply to
     * @param restaurant The restaurant associated with the review
     */
    public void setForReply(ReviewDTO review, RestaurantDTO restaurant) {
        this.existingReview = review;
        this.restaurant = restaurant;
        this.isReplyMode = true;

        // Update UI for reply mode
        titleLabel.setText("Reply to Review");
        restaurantNameLabel.setText(restaurant.name + " - Reply to " + review.usr_id);
        submitButton.setText("Submit Reply");
        reviewTextArea.setPromptText("Write your response to this review...");

        // Disable star rating for replies
        disableStarRating();

        // Pre-fill with existing reply if it exists
        if (review.rest_rep != null && !review.rest_rep.trim().isEmpty()) {
            reviewTextArea.setText(review.rest_rep);
        } else {
            reviewTextArea.clear();
        }
        updateCharacterCount();
    }

    /**
     * Initializes the star rating functionality.
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

        // Clear all selections first
        for (ToggleButton star : starButtons) {
            star.setSelected(false);
        }

        // Set selected state for stars up to the rating value
        for (int i = 0; i < rating; i++) {
            starButtons[i].setSelected(true);
        }

        // Update rating label
        ratingLabel.setText(rating + " stars selected");

        // Clear any rating-related errors
        hideError();
    }

    /**
     * Disables the star rating section for reply mode.
     */
    private void disableStarRating() {
        for (ToggleButton star : starButtons) {
            star.setDisable(true);
        }
        ratingLabel.setText("Rating not applicable for replies");
    }

    /**
     * Applies custom styling to star buttons.
     */
    private void applyStarStyling() {
        String selectedStyle = "-fx-background-color: #ffc107; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-border-color: #e6a800; -fx-border-width: 1;";
        String unselectedStyle = "-fx-background-color: #f5f5f5; -fx-text-fill: #cccccc; -fx-border-color: #e0e0e0; -fx-border-width: 1;";

        for (ToggleButton star : starButtons) {
            // Ensure star symbol is always visible
            star.setText("★");

            // Set initial style
            star.setStyle(unselectedStyle);

            // Add style change listeners
            star.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    star.setStyle(selectedStyle);
                } else {
                    star.setStyle(unselectedStyle);
                }
            });
        }

        // Apply initial styling based on selection
        updateStarSelection(currentRatingSelection);
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

        // Initialize character count
        updateCharacterCount();
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

        // For review mode, rating is required
        if (!isReplyMode && (currentRatingSelection < 1 || currentRatingSelection > 5)) {
            showError("Please select a rating before submitting.");
            return false;
        }

        return true;
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
     * Handles the submit button action.
     * Creates the review or reply and submits it to the system.
     */
    @FXML
    private void handleSubmit() {
        if (!validateForm()) {
            return;
        }

        String reviewText = reviewTextArea.getText().trim();
        String userId = userSession.getUserId();

        if (isReplyMode) {
            // Handle restaurant reply submission
            handleReplySubmission(reviewText);
        } else {
            // Handle new review submission
            handleReviewSubmission(userId, reviewText);
        }

        // Close the modal window
        closeWindow();
    }

    /**
     * Handles submitting a new review.
     *
     * @param userId The ID of the user submitting the review
     * @param reviewText The review text content
     */
    private void handleReviewSubmission(String userId, String reviewText) {
        // Create new review DTO
        ReviewDTO newReview = new ReviewDTO(userId, restaurant.id, reviewText, currentRatingSelection);

        // TODO: Replace with actual RMI call to ReviewService.createOrUpdateReview()
        System.out.println("=== NEW REVIEW SUBMISSION ===");
        System.out.println("User ID: " + userId);
        System.out.println("Restaurant ID: " + restaurant.id);
        System.out.println("Restaurant Name: " + restaurant.name);
        System.out.println("Rating: " + currentRatingSelection + " stars");
        System.out.println("Review Text: " + reviewText);
        System.out.println("Review DTO: " + newReview.toString());
        System.out.println("=============================");

        LOGGER.info("Review submitted successfully for restaurant: " + restaurant.name);
    }

    /**
     * Handles submitting a restaurant reply to a review.
     *
     * @param replyText The reply text content
     */
    private void handleReplySubmission(String replyText) {
        // Create updated review DTO with reply
        ReviewDTO updatedReview = new ReviewDTO(
                existingReview.usr_id,
                existingReview.rest_id,
                existingReview.customer_msg,
                existingReview.rating,
                replyText
        );

        // TODO: Replace with actual RMI call to ReviewService.createOrUpdateReply()
        System.out.println("=== RESTAURANT REPLY SUBMISSION ===");
        System.out.println("Restaurant Owner ID: " + userSession.getUserId());
        System.out.println("Original Review User: " + existingReview.usr_id);
        System.out.println("Restaurant ID: " + restaurant.id);
        System.out.println("Restaurant Name: " + restaurant.name);
        System.out.println("Original Review: " + existingReview.customer_msg);
        System.out.println("Original Rating: " + existingReview.rating + " stars");
        System.out.println("Reply Text: " + replyText);
        System.out.println("Updated Review DTO: " + updatedReview.toString());
        System.out.println("==================================");

        LOGGER.info("Reply submitted successfully for review by: " + existingReview.usr_id);
    }

    /**
     * Handles the cancel button action.
     * Closes the modal window without saving changes.
     */
    @FXML
    private void handleCancel() {
        LOGGER.info("Review/reply cancelled by user");
        closeWindow();
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
     * Checks if the controller is in reply mode.
     *
     * @return true if in reply mode, false if in review mode
     */
    public boolean isReplyMode() {
        return isReplyMode;
    }
}