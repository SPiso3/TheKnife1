package it.uninsubria.server_services;

import it.uninsubria.DBConnection;
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
    }

    public void testGetOwnedRestaurants() {
    }

    public void testGetReviewedRestaurants() {
    }

    public void testAddFavoriteRestaurant() {
    }

    public void testRemoveFavoriteRestaurant() {
    }

    public void testCreateRestaurant() {
    }
}