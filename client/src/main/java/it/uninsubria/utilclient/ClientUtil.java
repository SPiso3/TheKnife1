package it.uninsubria.utilclient;

import it.uninsubria.dto.CuisineType;
import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.dto.ReviewDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for client-side operations and mock data.
 * This class provides temporary mock data and utility functions for the client application.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class ClientUtil {

    /**
     * Returns a mock list of restaurants for testing purposes.
     * TODO: Replace this method with actual RMI call to RestaurantService.searchRestaurants()
     * when the server implementation is ready.
     *
     * @return A list of mock restaurant data
     */
    public static List<RestaurantDTO> getRestaurantList() {
        List<RestaurantDTO> restaurants = new ArrayList<>();

        // Mock restaurant 1
        RestaurantDTO restaurant1 = new RestaurantDTO("1", "Osteria del Borgo", "Italy", "Varese", "Via Roma 15",
                45.8206, 8.8257, 35.0, 127, 4.2, true, true, CuisineType.ITALIAN, "owner");
        restaurants.add(restaurant1);

        // Mock restaurant 2
        RestaurantDTO restaurant2 = new RestaurantDTO("2", "Sushi Zen", "Italy", "Varese", "Corso Matteotti 8",
                45.8189, 8.8245, 45.0, 89, 4.5, false, true, CuisineType.JAPANESE, "owner2");
        restaurants.add(restaurant2);

        // Mock restaurant 3
        RestaurantDTO restaurant3 = new RestaurantDTO("3", "La Brasserie", "Italy", "Varese", "Via Cairoli 22",
                45.8195, 8.8263, 28.0, 203, 3.8, true, false, CuisineType.FRENCH, "owner3");
        restaurants.add(restaurant3);

        // Mock restaurant 4
        RestaurantDTO restaurant4 = new RestaurantDTO("4", "Spice Garden", "Italy", "Varese", "Piazza San Vittore 5",
                45.8178, 8.8234, 22.0, 156, 4.1, true, true, CuisineType.INDIAN, "owner4");
        restaurants.add(restaurant4);

        // Mock restaurant 5
        RestaurantDTO restaurant5 = new RestaurantDTO("5", "El Toro Loco", "Italy", "Varese", "Via Verdi 12",
                45.8201, 8.8278, 18.0, 91, 3.9, true, false, CuisineType.MEXICAN, "owner5");
        restaurants.add(restaurant5);

        return restaurants;
    }

    /**
     * Returns a mock list of reviews for a specific restaurant.
     * TODO: Replace this method with actual RMI call to ReviewService.getReviews(restaurantId)
     * when the server implementation is ready.
     *
     * @param restaurantId The ID of the restaurant to get reviews for
     * @return A list of mock review data for the restaurant
     */
    public static List<ReviewDTO> getReviewList(String restaurantId) {
        List<ReviewDTO> reviews = new ArrayList<>();

        switch (restaurantId) {
            case "1": // Osteria del Borgo
                reviews.add(new ReviewDTO("marco_rossi", "1",
                        "Excellent traditional Italian cuisine! The pasta was perfectly cooked and the atmosphere was cozy. Highly recommended for a romantic dinner.",
                        5, "Thank you Marco! We're delighted you enjoyed your evening with us. We look forward to welcoming you back soon!"));

                reviews.add(new ReviewDTO("giulia_m", "1",
                        "Good food but service was a bit slow. The risotto was amazing though, definitely worth the wait. Will come back.",
                        4, null));

                reviews.add(new ReviewDTO("tourist_2024", "1",
                        "Authentic Italian experience! The staff was friendly and the wine selection was impressive. Perfect for our vacation dinner.",
                        5, "Grazie mille! It was our pleasure to make your vacation special. Buon viaggio!"));
                break;

            case "2": // Sushi Zen
                reviews.add(new ReviewDTO("sushi_lover", "2",
                        "Best sushi in Varese! Fresh fish, expertly prepared. The chef's special was outstanding. A bit pricey but worth every euro.",
                        5, "Thank you for appreciating our commitment to quality. We source our fish daily to ensure the best experience."));

                reviews.add(new ReviewDTO("anna_b", "2",
                        "Beautiful presentation and delicious food. The atmosphere is very zen-like and peaceful. Perfect for a quiet lunch.",
                        4, null));

                reviews.add(new ReviewDTO("foodie_critic", "2",
                        "Impressive technique and authentic flavors. The omakase menu was a journey through Japanese cuisine. Highly recommend booking in advance.",
                        5, null));
                break;

            case "3": // La Brasserie
                reviews.add(new ReviewDTO("french_fan", "3",
                        "Decent French bistro. The coq au vin was good but not exceptional. Nice wine list and friendly service.",
                        3, "Merci for your feedback! We're always working to improve our dishes. Please try our new seasonal menu next time."));

                reviews.add(new ReviewDTO("couple_date", "3",
                        "Lovely atmosphere for our anniversary dinner. The escargot was perfectly prepared and the dessert was divine!",
                        4, "Félicitations on your anniversary! We're honored to have been part of your special celebration."));
                break;

            case "4": // Spice Garden
                reviews.add(new ReviewDTO("spice_enthusiast", "4",
                        "Authentic Indian flavors! The curry had the perfect level of spice and the naan was freshly baked. Great value for money.",
                        4, "Thank you! Our chef uses traditional spice blends imported directly from India. We're glad you enjoyed the authentic taste."));

                reviews.add(new ReviewDTO("vegetarian_v", "4",
                        "Excellent vegetarian options! The dal was creamy and flavorful. Staff was very accommodating with dietary requirements.",
                        5, null));

                reviews.add(new ReviewDTO("local_resident", "4",
                        "Our go-to place for Indian food. Consistent quality and quick delivery service. The butter chicken is always perfect.",
                        4, "We appreciate your loyalty! It's customers like you that make us strive for excellence every day."));
                break;

            case "5": // El Toro Loco
                reviews.add(new ReviewDTO("mexican_food_lover", "5",
                        "Fun atmosphere and tasty food! The tacos were authentic and the margaritas were strong. Perfect for a casual night out.",
                        4, "¡Gracias amigo! We're happy you had a great time. Our bartender takes pride in those margaritas!"));

                reviews.add(new ReviewDTO("student_budget", "5",
                        "Great prices and generous portions! Perfect for students. The nachos were loaded with toppings.",
                        4, null));
                break;

            default:
                // Return empty list for unknown restaurant IDs
                break;
        }

        return reviews;
    }

    /**
     * Calculates the distance between two geographic points using the Haversine formula.
     *
     * @param lat1 Latitude of the first point
     * @param lon1 Longitude of the first point
     * @param lat2 Latitude of the second point
     * @param lon2 Longitude of the second point
     * @return Distance in kilometers
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}