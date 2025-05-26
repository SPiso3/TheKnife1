package it.uninsubria.controller.ui_components;

import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.session.UserSession;
import it.uninsubria.utilclient.ClientUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

/**
 * A custom JavaFX component representing a restaurant card in search results.
 * This component displays restaurant information in a clickable card format.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class RestaurantCardComponent extends VBox {

    private final RestaurantDTO restaurant;
    private Consumer<RestaurantDTO> onCardClick;

    /**
     * Creates a new restaurant card component.
     *
     * @param restaurant The restaurant data to display
     */
    public RestaurantCardComponent(RestaurantDTO restaurant) {
        this.restaurant = restaurant;
        setupCard();
        setupContent();
        setupClickHandler();
    }

    /**
     * Sets the callback function to be called when the card is clicked.
     *
     * @param onCardClick Callback function that receives the restaurant data
     */
    public void setOnCardClick(Consumer<RestaurantDTO> onCardClick) {
        this.onCardClick = onCardClick;
    }

    /**
     * Sets up the basic card appearance and layout.
     */
    private void setupCard() {
        // Card styling
        setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;");

        setPadding(new Insets(15));
        setSpacing(8);
        setCursor(Cursor.HAND);

        // Hover effect
        setOnMouseEntered(e -> setStyle("-fx-background-color: #f8f9fa; " +
                "-fx-border-color: #1a73e8; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;"));

        setOnMouseExited(e -> setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;"));
    }

    /**
     * Sets up the content layout and information display.
     */
    private void setupContent() {
        // Restaurant name
        Label nameLabel = new Label(restaurant.name);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        // Cuisine and location
        Label cuisineLocationLabel = new Label(restaurant.cuisine.getDisplayName() +
                " • " + restaurant.getCity());
        cuisineLocationLabel.setStyle("-fx-text-fill: #666666;");

        // Rating and reviews
        HBox ratingBox = createRatingBox();

        // Price and distance
        HBox priceDistanceBox = createPriceDistanceBox();

        // Services
        HBox servicesBox = createServicesBox();

        getChildren().addAll(nameLabel, cuisineLocationLabel, ratingBox, priceDistanceBox, servicesBox);
    }

    /**
     * Creates the rating display box.
     *
     * @return HBox containing rating information
     */
    private HBox createRatingBox() {
        HBox ratingBox = new HBox(5);
        ratingBox.setAlignment(Pos.CENTER_LEFT);

        // Star rating
        String stars = "★".repeat(Math.max(0, Math.min(5, (int) Math.round(restaurant.avg_rating)))) +
                "☆".repeat(Math.max(0, 5 - (int) Math.round(restaurant.avg_rating)));
        Label starsLabel = new Label(stars);
        starsLabel.setStyle("-fx-text-fill: #ffc107;");

        // Rating value and count
        Label ratingLabel = new Label(String.format("%.1f (%d reviews)",
                restaurant.avg_rating, restaurant.rating_count));
        ratingLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");

        ratingBox.getChildren().addAll(starsLabel, ratingLabel);
        return ratingBox;
    }

    /**
     * Creates the price and distance display box.
     *
     * @return HBox containing price and distance information
     */
    private HBox createPriceDistanceBox() {
        HBox priceDistanceBox = new HBox();
        priceDistanceBox.setAlignment(Pos.CENTER_LEFT);

        // Price
        Label priceLabel = new Label("€" + String.format("%.0f", restaurant.avg_price) + " avg");
        priceLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");

        // Spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Distance
        Label distanceLabel = createDistanceLabel();

        priceDistanceBox.getChildren().addAll(priceLabel, spacer, distanceLabel);
        return priceDistanceBox;
    }

    /**
     * Creates the distance label based on user location.
     *
     * @return Label showing distance to restaurant
     */
    private Label createDistanceLabel() {
        UserSession session = UserSession.getInstance();
        Label distanceLabel = new Label();

        double[] userCoords = session.getUserCoordinates();
        if (userCoords != null) {
            double distance = ClientUtil.calculateDistance(
                    userCoords[0], userCoords[1],
                    restaurant.latitude, restaurant.longitude);
            distanceLabel.setText(String.format("%.1f km", distance));
        } else {
            distanceLabel.setText("Location unknown");
        }

        distanceLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
        return distanceLabel;
    }

    /**
     * Creates the services display box.
     *
     * @return HBox containing service availability icons
     */
    private HBox createServicesBox() {
        HBox servicesBox = new HBox(10);
        servicesBox.setAlignment(Pos.CENTER_LEFT);

        if (restaurant.delivery) {
            Label deliveryLabel = new Label("🚚 Delivery");
            deliveryLabel.setStyle("-fx-text-fill: #1976d2; -fx-font-size: 11px;");
            servicesBox.getChildren().add(deliveryLabel);
        }

        if (restaurant.online_booking) {
            Label bookingLabel = new Label("📅 Booking");
            bookingLabel.setStyle("-fx-text-fill: #1976d2; -fx-font-size: 11px;");
            servicesBox.getChildren().add(bookingLabel);
        }

        return servicesBox;
    }

    /**
     * Sets up the click handler for the card.
     */
    private void setupClickHandler() {
        setOnMouseClicked(event -> {
            if (onCardClick != null) {
                onCardClick.accept(restaurant);
            }
        });
    }

    /**
     * Gets the restaurant data associated with this card.
     *
     * @return The restaurant data
     */
    public RestaurantDTO getRestaurant() {
        return restaurant;
    }
}