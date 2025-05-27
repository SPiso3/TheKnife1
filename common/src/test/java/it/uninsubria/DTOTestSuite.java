package it.uninsubria;

import it.uninsubria.dto.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.sql.Date;

/**
 * JUnit 3.8.1 test suite for all DTO classes in TheKnife system.
 * Tests the functionality, validation, and edge cases of DTOs.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class DTOTestSuite extends TestCase {

    /**
     * Create the test case
     */
    public DTOTestSuite(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DTOTestSuite.class);
    }

    // ======================== UserDTO Tests ========================

    public void testUserDTOCompleteConstructor() {
        String userId = "johndoe";
        String password = "securePass123";
        Date birthDate = Date.valueOf("1990-05-15");

        UserDTO testUser = new UserDTO(userId, password, "John", "Doe",
                "Italy", "Varese", "123 Main St",
                45.8183, 8.8239, birthDate, "client");

        assertEquals("User ID should match", userId, testUser.getUsername());
        assertEquals("Password should match", password, testUser.getPassword());
        assertEquals("Name should match", "John", testUser.getName());
        assertEquals("Surname should match", "Doe", testUser.getSurname());
        assertEquals("Nation should match", "Italy", testUser.getCountry());
        assertEquals("City should match", "Varese", testUser.getCity());
        assertEquals("Address should match", "123 Main St", testUser.getStreet());
        assertEquals("Latitude should match", 45.8183, testUser.getLatitude(), 0.0001);
        assertEquals("Longitude should match", 8.8239, testUser.getLongitude(), 0.0001);
        assertEquals("Birth date should match", birthDate, testUser.getBirthday());
        assertEquals("Role should match", "client", testUser.getRole());
    }

    public void testUserDTOAuthenticationConstructor() {
        String userId = "johndoe";
        String password = "securePass123";
        UserDTO authUser = new UserDTO(userId, password);

        assertEquals("User ID should match", userId, authUser.getUsername());
        assertEquals("Password should match", password, authUser.getPassword());
        assertNull("Name should be null", authUser.getName());
        assertNull("Surname should be null", authUser.getSurname());
        assertNull("Role should be null", authUser.getRole());
    }

    public void testUserDTOCoordinatesConstructor() {
        double testLat = 45.4642;
        double testLng = 9.1900;
        UserDTO guestUser = new UserDTO(testLat, testLng);

        assertEquals("Latitude should match", testLat, guestUser.getLatitude() , 0.0001);
        assertEquals("Longitude should match", testLng, guestUser.getLongitude(), 0.0001);
        assertNull("User ID should be null", guestUser.getUsername());
        assertNull("Name should be null", guestUser.getName());
    }

    public void testUserDTODefaultConstructor() {
        UserDTO emptyUser = new UserDTO();

        assertNull("User ID should be null", emptyUser.getUsername());
        assertNull("Name should be null", emptyUser.getName());
        assertNull("Latitude should be null", emptyUser.getLatitude());
        assertNull("Longitude should be null", emptyUser.getLongitude());
    }

    public void testUserDTOToString() {
        UserDTO testUser = new UserDTO("johndoe", "securePass123", "John", "Doe",
                "Italy", "Varese", "123 Main St",
                45.8183, 8.8239, Date.valueOf("1990-05-15"), "client");
        String userString = testUser.toString();

        assertTrue("ToString should contain UserDTO", userString.contains("UserDTO"));
        assertTrue("ToString should contain user ID", userString.contains("johndoe"));
        assertTrue("ToString should contain name", userString.contains("John"));
        // Birthdate is commented out in toString, so it shouldn't appear
    }

    // ======================== ReviewDTO Tests ========================

    public void testReviewDTOWithoutReply() {
        String userId = "user123";
        String restaurantId = "restaurant456";
        String message = "Great food and service!";
        Integer rating = 5;

        ReviewDTO review = new ReviewDTO(userId, restaurantId, message, rating);

        assertEquals("User ID should match", userId, review.usr_id);
        assertEquals("Restaurant ID should match", restaurantId, review.rest_id);
        assertEquals("Message should match", message, review.customer_msg);
        assertEquals("Rating should match", rating, review.rating);
        assertNull("Reply should be null", review.rest_rep);
    }

    public void testReviewDTOWithReply() {
        String userId = "user123";
        String restaurantId = "restaurant456";
        String message = "Great food and service!";
        Integer rating = 5;
        String reply = "Thank you for your kind words!";

        ReviewDTO review = new ReviewDTO(userId, restaurantId, message, rating, reply);

        assertEquals("User ID should match", userId, review.usr_id);
        assertEquals("Restaurant ID should match", restaurantId, review.rest_id);
        assertEquals("Message should match", message, review.customer_msg);
        assertEquals("Rating should match", rating, review.rating);
        assertEquals("Reply should match", reply, review.rest_rep);
    }

    public void testReviewDTOToStringWithNullReply() {
        ReviewDTO review = new ReviewDTO("user123", "restaurant456", "Great food!", 5);
        String reviewString = review.toString();

        assertTrue("ToString should contain ReviewDTO", reviewString.contains("ReviewDTO"));
        assertTrue("ToString should contain user ID", reviewString.contains("user123"));
        assertTrue("ToString should contain rating", reviewString.contains("5"));
        assertTrue("ToString should show no reply message", reviewString.contains("No reply yet"));
    }

    public void testReviewDTOToStringWithReply() {
        String reply = "Thank you!";
        ReviewDTO review = new ReviewDTO("user123", "restaurant456", "Great food!", 5, reply);
        String reviewString = review.toString();

        assertTrue("ToString should contain reply", reviewString.contains(reply));
    }

    // ======================== CuisineType Tests ========================

    public void testCuisineTypeGetDisplayName() {
        assertEquals("Italian display name should match", "Italian", CuisineType.ITALIAN.getDisplayName());
        assertEquals("Pizza display name should match", "Pizza", CuisineType.PIZZA.getDisplayName());
        assertEquals("Japanese display name should match", "Japanese", CuisineType.JAPANESE.getDisplayName());
        assertEquals("Mediterranean display name should match", "Mediterranean Cuisine", CuisineType.MEDITERRANEAN_CUISINE.getDisplayName());
    }

    public void testCuisineTypeFromDisplayName() {
        assertEquals("Should find Pizza cuisine", CuisineType.PIZZA, CuisineType.fromDisplayName("Pizza"));
        assertEquals("Should find Italian cuisine", CuisineType.ITALIAN, CuisineType.fromDisplayName("Italian"));
        assertEquals("Should find Japanese cuisine", CuisineType.JAPANESE, CuisineType.fromDisplayName("Japanese"));
    }

    public void testCuisineTypeFromDisplayNameCaseInsensitive() {
        assertEquals("Should find pizza (lowercase)", CuisineType.PIZZA, CuisineType.fromDisplayName("pizza"));
        assertEquals("Should find ITALIAN (uppercase)", CuisineType.ITALIAN, CuisineType.fromDisplayName("ITALIAN"));
        assertEquals("Should find Japanese (mixed case)", CuisineType.JAPANESE, CuisineType.fromDisplayName("Japanese"));
    }

    public void testCuisineTypeFromDisplayNameInvalid() {
        assertNull("Should return null for non-existent cuisine", CuisineType.fromDisplayName("NonExistent"));
        assertNull("Should return null for empty string", CuisineType.fromDisplayName(""));
        assertNull("Should return null for null input", CuisineType.fromDisplayName(null));
    }

    public void testCuisineTypeToString() {
        assertEquals("Italian toString should match display name", "Italian", CuisineType.ITALIAN.toString());
        assertEquals("Pizza toString should match display name", "Pizza", CuisineType.PIZZA.toString());
    }

    public void testAllCuisineTypesHaveValidDisplayNames() {
        CuisineType[] allCuisines = CuisineType.values();
        for (CuisineType cuisine : allCuisines) {
            assertNotNull("Cuisine " + cuisine.name() + " should have display name", cuisine.getDisplayName());
            assertFalse("Cuisine " + cuisine.name() + " display name should not be empty",
                    cuisine.getDisplayName().isEmpty());
            assertEquals("Should be able to find cuisine by display name",
                    cuisine, CuisineType.fromDisplayName(cuisine.getDisplayName()));
        }
    }

    // ======================== RestaurantDTO Tests ========================

    public void testRestaurantDTOCompleteConstructor() {
        String id = "rest1";
        String name = "Pizzeria Napoli";

        RestaurantDTO restaurant = new RestaurantDTO(
                id, name, "Italy", "Varese", "22 Via Milano",
                45.8156, 8.8267, 25.00, 7, 4.7,
                true, true, CuisineType.PIZZA, "gustavo"
        );
        Integer expectedRatingCount = 7;

        assertEquals("ID should match", id, restaurant.id);
        assertEquals("Name should match", name, restaurant.name);
        assertEquals("Nation should match", "Italy", restaurant.getCountry());
        assertEquals("City should match", "Varese", restaurant.getCity());
        assertEquals("Address should match", "22 Via Milano", restaurant.getStreet());
        assertEquals("Latitude should match", 45.8156, restaurant.latitude, 0.0001);
        assertEquals("Longitude should match", 8.8267, restaurant.longitude, 0.0001);
        assertEquals("Average price should match", 25.00, restaurant.avg_price, 0.01);
        assertEquals("Rating count should match", expectedRatingCount, restaurant.rating_count);
        assertEquals("Average rating should match", 4.7, restaurant.avg_rating, 0.01);
        assertTrue("Delivery should be true", restaurant.delivery);
        assertTrue("Online booking should be true", restaurant.online_booking);
        assertEquals("Cuisine should match", CuisineType.PIZZA, restaurant.cuisine);
        assertEquals("Owner ID should match", "gustavo", restaurant.owner_usrId);
    }

    public void testRestaurantDTOConstructorWithoutId() {
        String name = "Pizzeria Napoli";
        Integer expectedRatingCount = 0;

        RestaurantDTO newRestaurant = new RestaurantDTO(
                name, "Italy", "Varese", "22 Via Milano",
                45.8156, 8.8267, 25.00,
                true, true, CuisineType.PIZZA, "gustavo"
        );

        assertNull("ID should be null", newRestaurant.id);
        assertEquals("Name should match", name, newRestaurant.name);
        assertEquals("Rating count should be zero", expectedRatingCount, newRestaurant.rating_count);
        assertNull("Average rating should be null", newRestaurant.avg_rating);
        assertEquals("Cuisine should match", CuisineType.PIZZA, newRestaurant.cuisine);
    }

    public void testRestaurantDTOToString() {
        RestaurantDTO restaurant = new RestaurantDTO(
                "rest1", "Pizzeria Napoli", "Italy", "Varese", "22 Via Milano",
                45.8156, 8.8267, 25.00, 7, 4.7,
                true, true, CuisineType.PIZZA, "gustavo"
        );
        String restaurantString = restaurant.toString();

        assertTrue("ToString should contain RestaurantDTO", restaurantString.contains("RestaurantDTO"));
        assertTrue("ToString should contain ID", restaurantString.contains("rest1"));
        assertTrue("ToString should contain name", restaurantString.contains("Pizzeria Napoli"));
        assertTrue("ToString should contain nation", restaurantString.contains("Italy"));
        assertTrue("ToString should contain cuisine", restaurantString.contains("Pizza"));
    }

    // ======================== SearchCriteriaDTO Tests ========================

    public void testSearchCriteriaDTOAdvancedSearch() {
        Double lat = 45.4642;
        Double lng = 9.1900;

        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
                .coordinates(lat, lng)
                .cuisineType(CuisineType.ITALIAN)
                .priceRange(20.0, 50.0)
                .deliveryAvailable(true)
                .minRating(4.0)
                .build();

        assertEquals("Latitude should match", lat, criteria.latitude);
        assertEquals("Longitude should match", lng, criteria.longitude);
        assertEquals("Cuisine type should match", CuisineType.ITALIAN, criteria.cuisineType);
        assertEquals("Min price should match", 20.0, criteria.minPrice, 0.01);
        assertEquals("Max price should match", 50.0, criteria.maxPrice, 0.01);
        assertTrue("Delivery should be true", criteria.deliveryAvailable);
        assertEquals("Min rating should match", 4.0, criteria.minRating, 0.01);
        assertTrue("Should have valid coordinates", criteria.hasValidCoordinates());
    }

    public void testSearchCriteriaDTOMinPriceOnly() {
        Double lat = 45.4642;
        Double lng = 9.1900;

        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
                .coordinates(lat, lng)
                .priceRange(30.0, null)
                .build();

        assertEquals("Min price should match", 30.0, criteria.minPrice, 0.01);
        assertNull("Max price should be null", criteria.maxPrice);
        assertTrue("Should have valid coordinates", criteria.hasValidCoordinates());
    }

    public void testSearchCriteriaDTOMaxPriceOnly() {
        Double lat = 45.4642;
        Double lng = 9.1900;

        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
                .coordinates(lat, lng)
                .priceRange(null, 30.0)
                .build();

        assertNull("Min price should be null", criteria.minPrice);
        assertEquals("Max price should match", 30.0, criteria.maxPrice, 0.01);
        assertTrue("Should have valid coordinates", criteria.hasValidCoordinates());
    }

    public void testSearchCriteriaDTOCuisineAndDelivery() {
        Double lat = 45.4642;
        Double lng = 9.1900;

        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
                .coordinates(lat, lng)
                .cuisineType(CuisineType.JAPANESE)
                .deliveryAvailable(true)
                .build();

        assertEquals("Cuisine should match", CuisineType.JAPANESE, criteria.cuisineType);
        assertTrue("Delivery should be true", criteria.deliveryAvailable);
        assertNull("Online booking should be null", criteria.onlineBookingAvailable);
        assertTrue("Should have valid coordinates", criteria.hasValidCoordinates());
    }

    public void testSearchCriteriaDTOInvalidPriceRange() {
        try {
            SearchCriteriaDTO.builder()
                    .coordinates(45.4642, 9.1900)
                    .priceRange(50.0, 20.0) // Invalid: min > max
                    .build();
            fail("Should throw IllegalArgumentException when min price > max price");
        } catch (IllegalArgumentException e) {
            // Expected exception
            assertTrue("Exception message should mention price",
                    e.getMessage().contains("price"));
        }
    }

    public void testSearchCriteriaDTOMissingCoordinates() {
        try {
            SearchCriteriaDTO.builder()
                    .cuisineType(CuisineType.ITALIAN)
                    .build(); // Missing coordinates
            fail("Should throw IllegalStateException when coordinates are missing");
        } catch (IllegalStateException e) {
            // Expected exception
            assertTrue("Exception message should mention coordinates",
                    e.getMessage().contains("Coordinates"));
        }
    }

    public void testSearchCriteriaDTOValidMinRating() {
        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
                .coordinates(45.4642, 9.1900)
                .minRating(3.5)
                .build();

        assertEquals("Min rating should match", 3.5, criteria.minRating, 0.01);
    }

    public void testSearchCriteriaDTOInvalidMinRating() {
        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
                .coordinates(45.4642, 9.1900)
                .minRating(6.0) // Invalid: > 5.0
                .build();

        assertNull("Min rating should be null for invalid values", criteria.minRating);
    }

    public void testSearchCriteriaDTOToString() {
        Double lat = 45.4642;
        Double lng = 9.1900;

        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
                .coordinates(lat, lng)
                .cuisineType(CuisineType.ITALIAN)
                .priceRange(20.0, 50.0)
                .deliveryAvailable(true)
                .build();

        String criteriaString = criteria.toString();

        assertTrue("ToString should contain SearchCriteriaDTO", criteriaString.contains("SearchCriteriaDTO"));
        assertTrue("ToString should contain latitude", criteriaString.contains(lat.toString()));
        assertTrue("ToString should contain longitude", criteriaString.contains(lng.toString()));
        assertTrue("ToString should contain cuisine", criteriaString.contains("ITALIAN"));
        assertTrue("ToString should contain min price", criteriaString.contains("20.0"));
        assertTrue("ToString should contain max price", criteriaString.contains("50.0"));
        assertTrue("ToString should contain delivery", criteriaString.contains("deliveryAvailable=true"));
    }

    public void testSearchCriteriaDTOToStringWithNulls() {
        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
                .coordinates(45.4642, 9.1900)
                .priceRange(null, 30.0)
                .build();

        String criteriaString = criteria.toString();

        assertTrue("ToString should handle null min price", criteriaString.contains("any to 30.0"));
        assertFalse("ToString should not contain cuisineType when null", criteriaString.contains("cuisineType="));
    }

    // ======================== Integration Tests ========================

    public void testRestaurantSearchIntegration() {
        // Create a restaurant
        RestaurantDTO restaurant = new RestaurantDTO(
                "rest1", "Test Restaurant", "Italy", "Milan", "Test Address",
                45.4642, 9.1900, 30.0, 5, 4.2,
                true, false, CuisineType.ITALIAN, "owner1"
        );

        // Create search criteria that should match this restaurant
        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
                .coordinates(45.4642, 9.1900)
                .cuisineType(CuisineType.ITALIAN)
                .priceRange(25.0, 35.0)
                .deliveryAvailable(true)
                .minRating(4.0)
                .build();

        // Verify compatibility
        assertEquals("Cuisine should match", restaurant.cuisine, criteria.cuisineType);
        assertTrue("Price should be within range", restaurant.avg_price >= criteria.minPrice);
        assertTrue("Price should be within range", restaurant.avg_price <= criteria.maxPrice);
        assertEquals("Delivery availability should match", restaurant.delivery, criteria.deliveryAvailable);
        assertTrue("Rating should meet minimum", restaurant.avg_rating >= criteria.minRating);
    }

    public void testAllDTOsAreSerializable() {
        assertTrue("UserDTO should be serializable",
                java.io.Serializable.class.isAssignableFrom(UserDTO.class));
        assertTrue("RestaurantDTO should be serializable",
                java.io.Serializable.class.isAssignableFrom(RestaurantDTO.class));
        assertTrue("ReviewDTO should be serializable",
                java.io.Serializable.class.isAssignableFrom(ReviewDTO.class));
        assertTrue("SearchCriteriaDTO should be serializable",
                java.io.Serializable.class.isAssignableFrom(SearchCriteriaDTO.class));
        assertTrue("CuisineType should be serializable",
                java.io.Serializable.class.isAssignableFrom(CuisineType.class));
    }

    // ======================== Test Main Method ========================

    /**
     * Main method to run all tests - mimics the original TestDTO main method output
     */
    public static void main(String[] args) {
        // Run the test suite
        junit.textui.TestRunner.run(suite());

        // Also run the original style output for compatibility
        System.out.println("\n=== Original Test Style Output ===");

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