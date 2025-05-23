package it.uninsubria.utilcommon;

import it.uninsubria.dto.*;

/**
 * tests
 *
 */
public class TestDTO
{
    public static void main( String[] args )
    {
        // user
        UserDTO user = new UserDTO("johndoe", "securePass123", "John", "Doe", "Italy", "Varese","123 Main St", 45.8183, 8.8239, java.sql.Date.valueOf("1990-05-15"), "client");
        System.out.println(user);
        System.out.println("UserDTO test passed\n");

        // review
        ReviewDTO newReview = new ReviewDTO("user123", "restaurant456", "Great food and service!", 5);
        ReviewDTO reviewWithReply = new ReviewDTO("user123", "restaurant456", "Great food and service!", 5, "Thank you for your kind words!");
        System.out.println(newReview);
        System.out.println(reviewWithReply);
        System.out.println("ReviewDTO test passed\n");

        // cuisine enum
        System.out.println("Testing CuisineType:");
        System.out.println("Display name for ITALIAN: " + CuisineType.ITALIAN.getDisplayName());
        System.out.println("Finding cuisine type by name 'Pizza': " + CuisineType.fromDisplayName("Pizza"));
        System.out.println("CuisineType test passed\n");

        // restaurant
        RestaurantDTO existingRestaurant = new RestaurantDTO(
                "rest1",
                "Pizzeria Napoli",
                "Italy",
                "Varese",
                "22 Via Milano",
                45.8156,
                8.8267,
                25.00,
                7,
                4.7,
                true,
                true,
                CuisineType.PIZZA,
                "gustavo"
        );
        System.out.println(existingRestaurant);
        System.out.println("RestaurantDTO test passed\n");

        // search criteria testing
        SearchCriteriaDTO advancedSearch = SearchCriteriaDTO.builder()
                .coordinates(45.4642, 9.1900)
                .cuisineType(CuisineType.ITALIAN)
                .priceRange(20.0, 50.0)
                .deliveryAvailable(true)
                .minRating(4.0)
                .build();
        System.out.println("Advanced search criteria: " + advancedSearch.toString());
        SearchCriteriaDTO minPriceSearch = SearchCriteriaDTO.builder()
                .coordinates(45.4642, 9.1900)
                .priceRange(30.0, null)
                .build();
        System.out.println("Min price only search criteria: " + minPriceSearch.toString());
        SearchCriteriaDTO maxPriceSearch = SearchCriteriaDTO.builder()
                .coordinates(45.4642, 9.1900)
                .priceRange(null, 30.0)
                .build();
        System.out.println("Max price only search criteria: " + maxPriceSearch.toString());
        SearchCriteriaDTO cuisineAndDeliverySearch = SearchCriteriaDTO.builder()
                .coordinates(45.4642, 9.1900)
                .cuisineType(CuisineType.JAPANESE) .deliveryAvailable(true)
                .build();
        System.out.println("Cuisine and delivery search criteria: " + cuisineAndDeliverySearch.toString());
        try {
            SearchCriteriaDTO invalidPriceSearch = SearchCriteriaDTO.builder()
                    .coordinates(45.4642, 9.1900)
                    .priceRange(50.0, 20.0) // Invalid: min > max
                    .build();
            System.out.println("This should not be printed as an exception should be thrown");
        } catch (IllegalArgumentException e) {
            System.out.println("Price validation correctly caught the invalid price range: " + e.getMessage());
        }
        System.out.println("SearchCriteriaDTO test passed");
    }
}
