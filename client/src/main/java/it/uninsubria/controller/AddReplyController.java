package it.uninsubria.controller;

import it.uninsubria.dto.ReviewDTO;
import it.uninsubria.services.ReviewService;
import it.uninsubria.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for the add reply modal window.
 * Handles restaurant owner replies to customer reviews.
 * This controller is specifically designed for reply functionality only.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class AddReplyController {

    private static final Logger LOGGER = Logger.getLogger(AddReplyController.class.getName());
    private static final int MAX_REPLY_LENGTH = 800; // Maximum reply length
    // Header components
    @FXML private Label restaurantNameLabel;
    // Original review display components
    @FXML private Label customerNameLabel;
    @FXML private Label ratingStarsLabel;
    @FXML private Label ratingLabel;
    @FXML private Label customerReviewLabel;
    // Error display
    @FXML private Label errorLabel;
    // Reply text components
    @FXML private TextArea replyTextArea;
    @FXML private Label characterCountLabel;
    // Action buttons
    @FXML private Button cancelButton;
    @FXML private Button submitButton;
    // Data and state
    private ReviewDTO originalReview;
    private UserSession userSession;
    private ReviewService reviewService;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        userSession = UserSession.getInstance();

        initServices();
        // Setup text area functionality
        initializeTextArea();
    }

    private void initServices() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            reviewService = (ReviewService) registry.lookup("ReviewService");
        } catch (NotBoundException | RemoteException e) {
            System.err.println("Error connecting to ReviewService: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets up the controller for replying to a specific review.
     *
     * @param review The review to reply to
     */
    public void setReview(ReviewDTO review) {
        this.originalReview = review;

        // Update UI with review information
        updateReviewDisplay();

        // Pre-fill with existing reply if it exists
        if (review.rest_rep != null && !review.rest_rep.trim().isEmpty()) {
            replyTextArea.setText(review.rest_rep);
            submitButton.setText("Update Reply");
        } else {
            replyTextArea.clear();
            submitButton.setText("Submit Reply");
        }

        updateCharacterCount();
        hideError();
    }

    /**
     * Updates the display with the original review information.
     */
    private void updateReviewDisplay() {
        if (originalReview == null) return;

        // Set customer information
        customerNameLabel.setText(originalReview.usr_id);

        // Set rating display
        updateRatingDisplay();

        // Set review text
        customerReviewLabel.setText(originalReview.customer_msg);

        // TODO: Get restaurant name from restaurant ID
        // For now, show restaurant ID
        restaurantNameLabel.setText("Restaurant ID: " + originalReview.rest_id);
    }

    /**
     * Updates the rating display with stars and numbers.
     */
    private void updateRatingDisplay() {
        if (originalReview.rating != null) {
            // Create star display
            int rating = originalReview.rating;
            String stars = "★".repeat(Math.max(0, Math.min(5, rating))) +
                    "☆".repeat(Math.max(0, 5 - rating));
            ratingStarsLabel.setText(stars);

            // Rating text
            ratingLabel.setText("(" + rating + "/5)");
        } else {
            ratingStarsLabel.setText("☆☆☆☆☆");
            ratingLabel.setText("(No rating)");
        }
    }

    /**
     * Initializes the text area functionality including character counting.
     */
    private void initializeTextArea() {
        // Add character count listener
        replyTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateCharacterCount();

            // Clear text-related errors when user starts typing
            if (!newValue.trim().isEmpty()) {
                hideError();
            }

            // Enforce maximum length
            if (newValue.length() > MAX_REPLY_LENGTH) {
                replyTextArea.setText(oldValue);
            }
        });

        // Initialize character count
        updateCharacterCount();
    }

    /**
     * Updates the character count display.
     */
    private void updateCharacterCount() {
        int length = replyTextArea.getText().length();
        characterCountLabel.setText(length + " characters");

        // Change color based on length
        if (length > MAX_REPLY_LENGTH * 0.9) {
            characterCountLabel.setStyle("-fx-text-fill: #d32f2f;"); // Red when near limit
        } else if (length > MAX_REPLY_LENGTH * 0.7) {
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
        // Check if reply text is provided
        String replyText = replyTextArea.getText().trim();
        if (replyText.isEmpty()) {
            showError("Please write a reply before submitting.");
            return false;
        }

        // Check text length
        if (replyText.length() > MAX_REPLY_LENGTH) {
            showError("Reply text exceeds maximum length of " + MAX_REPLY_LENGTH + " characters.");
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
     * Creates the reply and submits it to the system.
     */
    @FXML
    private void handleSubmit() {
        if (!validateForm()) {
            return;
        }

        String replyText = replyTextArea.getText().trim();
        try {
            handleReplySubmission(replyText);

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reply Added");
            alert.setHeaderText("Successfully submitted Reply");
            alert.setContentText("Thank you for sharing your experience");
            alert.showAndWait();

            // Navigate back to My Area
            handleCancel();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error registering restaurant", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add Reply Failed");
            alert.setHeaderText("The Reply could not be submitted");
            alert.setContentText("We are sorry for the inconvenience. Please try again later.");
            alert.showAndWait();

        }

        // Close the modal window
        closeWindow();
    }

    /**
     * Handles submitting a restaurant reply to a review.
     *
     * @param replyText The reply text content
     */
    private void handleReplySubmission(String replyText) {
        originalReview.rest_rep = replyText;
        try {
            reviewService.createOrUpdateReview(originalReview);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Reply submitted successfully for review by: " + originalReview.usr_id);
    }

    /**
     * Handles the cancel button action.
     * Closes the modal window without saving changes.
     */
    @FXML
    private void handleCancel() {
        LOGGER.info("Reply cancelled by user");
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
     * Gets the current reply text.
     *
     * @return The text content of the reply
     */
    public String getReplyText() {
        return replyTextArea.getText().trim();
    }

    /**
     * Gets the original review being replied to.
     *
     * @return The original review DTO
     */
    public ReviewDTO getOriginalReview() {
        return originalReview;
    }
}