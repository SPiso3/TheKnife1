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

    public void testSearchRestaurants() {
        // Test with valid coordinates
        try {
            SearchCriteriaDTO criteria = new SearchCriteriaDTO();
            criteria.latitude = null;
            criteria.longitude = null;
            criteria.cuisineType = null;
            criteria.minPrice = null;
            criteria.maxPrice = null;
            criteria.deliveryAvailable = true;
            criteria.onlineBookingAvailable = true;
            criteria.minRating = null;
            RestaurantServiceImpl service = new RestaurantServiceImpl();
            List<RestaurantDTO> results = service.searchRestaurants(criteria);
            System.out.println("Total restaurants found: " + results.size());
            assertFalse(results.isEmpty());
        } catch (RemoteException e) {
            fail("RemoteException should not be thrown for valid criteria.");
        }

    }

    public void testGetFavoriteRestaurants() {
        try {
            RestaurantServiceImpl service = new RestaurantServiceImpl();
            List<RestaurantDTO> favorites = service.getFavoriteRestaurants("Aiden56");
            System.out.println("Total favorite restaurants found: " + favorites.size());
            assertFalse(favorites.isEmpty());
        } catch (RemoteException e) {
            fail("RemoteException should not be thrown when fetching favorite restaurants.");
        }
    }

    public void testGetOwnedRestaurants() {
        try {
            RestaurantServiceImpl service = new RestaurantServiceImpl();
            List<RestaurantDTO> ownedRestaurants = service.getOwnedRestaurants("Jo_Schaefer6");
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
            List<RestaurantDTO> reviewedRestaurants = service.getReviewedRestaurants("Alexanne30");
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