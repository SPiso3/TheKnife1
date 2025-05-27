package it.uninsubria.server_services;

import it.uninsubria.DBConnection;
import it.uninsubria.dto.AddressDTO;
import it.uninsubria.dto.CuisineType;
import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.dto.SearchCriteriaDTO;
import junit.framework.TestCase;

import java.rmi.RemoteException;
import java.util.List;

public class RestaurantServiceImplTest extends TestCase {
    @Override
        protected void setUp() throws Exception {
            super.setUp();
            String[] args = {"theknife", "password"};
            DBConnection.login(args);
        }

    /**
     * Simple test for restaurant search functionality.
     *
     * @author Sergio Enrico Pisoni, 755563, VA
     */
    public void testSearchRestaurants() {
        System.out.println("=== DEBUGGING CUISINE SEARCH ===");

        // Test 1: Search without cuisine filter (baseline)
        testWithoutCuisine();

        // Test 2: Search with Italian cuisine
        testWithCuisine();
    }

    public void testWithoutCuisine() {
        System.out.println("\n1. Testing WITHOUT cuisine filter:");
        try {
            SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
                    .coordinates(45.8183, 8.8239)  // Varese coordinates
                    .build();

            RestaurantServiceImpl service = new RestaurantServiceImpl();
            List<RestaurantDTO> results = service.searchRestaurants(criteria);

            System.out.println("Found " + results.size() + " restaurants total:");

            for (RestaurantDTO restaurant : results) {
                System.out.println("  - " + restaurant.name + " | Cuisine: " + restaurant.cuisine + " | DB Value: '" + restaurant.cuisine.getDisplayName() + "'");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void testWithCuisine() {
        System.out.println("\n2. Testing WITH Italian cuisine filter:");
        try {
            SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
                    .coordinates(45.8183, 8.8239)  // Varese coordinates
                    .cuisineType(CuisineType.ITALIAN)
                    .build();

            RestaurantServiceImpl service = new RestaurantServiceImpl();
            List<RestaurantDTO> results = service.searchRestaurants(criteria);

            System.out.println("Found " + results.size() + " ITALIAN restaurants:");
            System.out.println("Searching for cuisine: '" + CuisineType.ITALIAN.getDisplayName() + "'");

            for (RestaurantDTO restaurant : results) {
                System.out.println("  - " + restaurant.name + " | Cuisine: " + restaurant.cuisine + " | DB Value: '" + restaurant.cuisine.getDisplayName() + "'");
            }

            // Let's also print the actual SQL query being generated
            System.out.println("\n🔍 DEBUG INFO:");
            System.out.println("Expected cuisine string: '" + CuisineType.ITALIAN.getDisplayName() + "'");
            System.out.println("Enum value: " + CuisineType.ITALIAN);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void testGetFavoriteRestaurants() {
        try {
            RestaurantServiceImpl service = new RestaurantServiceImpl();
            List<RestaurantDTO> favorites = service.getFavoriteRestaurants("user");
            System.out.println("Total favorite restaurants found: " + favorites.size());
            for (RestaurantDTO restaurant : favorites) {
                System.out.println(restaurant);
            }
            assertFalse(favorites.isEmpty());
        } catch (RemoteException e) {
            fail("RemoteException should not be thrown when fetching favorite restaurants.");
        }
    }

    public void testGetOwnedRestaurants() {
        try {
            RestaurantServiceImpl service = new RestaurantServiceImpl();
            List<RestaurantDTO> ownedRestaurants = service.getOwnedRestaurants("owner");
            System.out.println("Total owned restaurants found: " + ownedRestaurants.size());
            assertFalse(ownedRestaurants.isEmpty());
            for (RestaurantDTO restaurant : ownedRestaurants) {
                System.out.println("Owned Restaurant: " + restaurant.name);
                System.out.println(restaurant.toString());
            }
        } catch (RemoteException e) {
            fail("RemoteException should not be thrown when fetching owned restaurants.");
        }
    }

    public void testGetReviewedRestaurants() {
        try {
            RestaurantServiceImpl service = new RestaurantServiceImpl();
            List<RestaurantDTO> reviewedRestaurants = service.getReviewedRestaurants("user");
            System.out.println("Total reviewed restaurants found: " + reviewedRestaurants.size());
            assertFalse(reviewedRestaurants.isEmpty());
            for (RestaurantDTO restaurant : reviewedRestaurants) {
                System.out.println("Reviewed Restaurant: " + restaurant.name);
                System.out.println(restaurant.toString());
            }
        } catch (RemoteException e) {
            fail("RemoteException should not be thrown when fetching reviewed restaurants.");
        }
    }

    public void testAddFavoriteRestaurant() {
        try {
            RestaurantServiceImpl service = new RestaurantServiceImpl();
            boolean result = service.addFavoriteRestaurant("Aiden56", "15");
            assertTrue(result);
            System.out.println("Favorite restaurant added successfully.");
        } catch (RemoteException e) {
            fail("RemoteException should not be thrown when adding a favorite restaurant.");
        }
    }

    public void testRemoveFavoriteRestaurant() {
        try {
            RestaurantServiceImpl service = new RestaurantServiceImpl();
            boolean result = service.removeFavoriteRestaurant("Aiden56", "15");
            assertTrue(result);
            System.out.println("Favorite restaurant removed successfully.");
        } catch (RemoteException e) {
            fail("RemoteException should not be thrown when removing a favorite restaurant.");
        }
    }

    public void testCreateRestaurant() {
        try {
            RestaurantServiceImpl service = new RestaurantServiceImpl();
            RestaurantDTO newRestaurant = new RestaurantDTO();
            newRestaurant.name = "Test Restaurant";
            newRestaurant.address = new AddressDTO("Italy", "Milan", "Via Test 123");
            newRestaurant.cuisine = CuisineType.ITALIAN;
            newRestaurant.delivery = true;
            newRestaurant.online_booking = true;
            newRestaurant.avg_price = 4.5;
            newRestaurant.latitude = 45.4642;
            newRestaurant.longitude = 9.1900;
            newRestaurant.owner_usrId = "Jo_Schaefer6";

            RestaurantDTO createdRestaurant = service.createRestaurant(newRestaurant, "Jo_Schaefer6");
            assertNotNull(createdRestaurant);
            System.out.println("Created restaurant: " + createdRestaurant.id);
        } catch (RemoteException | SecurityException e) {
            fail("RemoteException or SecurityException should not be thrown when creating a restaurant.");
        }
    }
}