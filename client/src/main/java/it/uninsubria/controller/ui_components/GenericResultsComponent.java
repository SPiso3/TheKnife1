package it.uninsubria.controller.ui_components;

import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.dto.ReviewDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;
import java.util.function.Consumer;

/**
 * A generic JavaFX component for displaying search results.
 * This component can display either restaurant cards or review cards in a scrollable list.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class GenericResultsComponent extends VBox {

    private VBox resultsContainer;
    private ScrollPane scrollPane;
    private Label statusLabel;
    private Consumer<RestaurantDTO> onRestaurantClick;
    private Consumer<ReviewDTO> onReviewClick;

    /**
     * Creates a new generic results component.
     */
    public GenericResultsComponent() {
        setupComponent();
    }

    /**
     * Sets the callback function to be called when a restaurant card is clicked.
     *
     * @param onRestaurantClick Callback function that receives the clicked restaurant data
     */
    public void setOnRestaurantClick(Consumer<RestaurantDTO> onRestaurantClick) {
        this.onRestaurantClick = onRestaurantClick;
    }

    /**
     * Sets the callback function to be called when a review card is clicked.
     *
     * @param onReviewClick Callback function that receives the clicked review data
     */
    public void setOnReviewClick(Consumer<ReviewDTO> onReviewClick) {
        this.onReviewClick = onReviewClick;
    }

    /**
     * Sets up the basic component structure and layout.
     */
    private void setupComponent() {
        setSpacing(10);
        setPadding(new Insets(15));

        // Status label for showing result count or messages
        statusLabel = new Label("Ready to search");
        statusLabel.setFont(Font.font("System", 14));
        statusLabel.setStyle("-fx-text-fill: #666666;");

        // Container for cards (restaurants or reviews)
        resultsContainer = new VBox(10);
        resultsContainer.setPadding(new Insets(10));

        // Scroll pane for the results
        scrollPane = new ScrollPane(resultsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setPrefViewportHeight(400);

        getChildren().addAll(statusLabel, scrollPane);
    }

    /**
     * Updates the component with a new list of restaurants.
     *
     * @param restaurants The list of restaurants to display
     */
    public void showRestaurants(List<RestaurantDTO> restaurants) {
        resultsContainer.getChildren().clear();

        if (restaurants == null || restaurants.isEmpty()) {
            showNoResults("No restaurants found", "Try adjusting your filters or search in a different area");
            return;
        }

        // Update status label
        statusLabel.setText(String.format("Found %d restaurant%s",
                restaurants.size(), restaurants.size() == 1 ? "" : "s"));

        // Create and add restaurant cards
        for (RestaurantDTO restaurant : restaurants) {
            RestaurantCardComponent card = new RestaurantCardComponent(restaurant);
            card.setOnCardClick(onRestaurantClick);
            resultsContainer.getChildren().add(card);
        }
    }

    /**
     * Updates the component with a new list of reviews.
     *
     * @param reviews The list of reviews to display
     */

    public void showReviews(List<ReviewDTO> reviews) {
        resultsContainer.getChildren().clear();

        if (reviews == null || reviews.isEmpty()) {
            showNoResults("No reviews found", "Be the first to write a review for this restaurant");
            return;
        }

        // Update status label
        statusLabel.setText(String.format("Found %d review%s",
                reviews.size(), reviews.size() == 1 ? "" : "s"));

        // Create and add review cards
        for (ReviewDTO review : reviews) {
            ReviewCardComponent card = new ReviewCardComponent(review);
            card.setOnCardClick(onReviewClick);
            resultsContainer.getChildren().add(card);
        }
    }

    /**
     * Shows a "no results found" message with custom text.
     *
     * @param mainMessage The main message to display
     * @param suggestionMessage The suggestion message to display
     */
    private void showNoResults(String mainMessage, String suggestionMessage) {
        statusLabel.setText("No results");

        VBox noResultsBox = new VBox(10);
        noResultsBox.setAlignment(Pos.CENTER);
        noResultsBox.setPadding(new Insets(40));

        Label noResultsLabel = new Label(mainMessage);
        noResultsLabel.setFont(Font.font("System", 16));
        noResultsLabel.setStyle("-fx-text-fill: #666666;");

        Label suggestionLabel = new Label(suggestionMessage);
        suggestionLabel.setFont(Font.font("System", 12));
        suggestionLabel.setStyle("-fx-text-fill: #999999;");

        noResultsBox.getChildren().addAll(noResultsLabel, suggestionLabel);
        resultsContainer.getChildren().add(noResultsBox);
    }

    /**
     * Shows a loading state while search is in progress.
     *
     * @param loadingMessage The message to display during loading
     */
    public void showLoading(String loadingMessage) {
        resultsContainer.getChildren().clear();
        statusLabel.setText("Loading...");

        VBox loadingBox = new VBox(10);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(40));

        Label loadingLabel = new Label("🔍 " + loadingMessage);
        loadingLabel.setFont(Font.font("System", 16));
        loadingLabel.setStyle("-fx-text-fill: #1a73e8;");

        loadingBox.getChildren().add(loadingLabel);
        resultsContainer.getChildren().add(loadingBox);
    }

    /**
     * Shows a loading state for restaurant search.
     */
    public void showLoadingRestaurants() {
        showLoading("Searching for restaurants...");
    }

    /**
     * Shows a loading state for review loading.
     */
    public void showLoadingReviews() {
        showLoading("Loading reviews...");
    }

    /**
     * Clears all results and resets to initial state.
     */
    public void clearResults() {
        resultsContainer.getChildren().clear();
        statusLabel.setText("Ready");
    }

    /**
     * Sets a custom status message.
     *
     * @param message The status message to display
     */
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }
}