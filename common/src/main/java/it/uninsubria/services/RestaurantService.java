package it.uninsubria.services;

import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.dto.SearchCriteriaDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for restaurant-related operations in TheKnife system.
 * This service handles all operations related to restaurants including searching,
 * retrieving favorites, owned restaurants, and reviewed restaurants.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public interface RestaurantService extends Remote {

    /**
     * Searches for restaurants based on the provided criteria.
     *
     * @param criteria The search criteria containing filters for the search
     * @return A list of restaurants matching the criteria
     * @throws RemoteException If a remote communication error occurs
     */
    List<RestaurantDTO> searchRestaurants(SearchCriteriaDTO criteria) throws RemoteException;

    /**
     * Retrieves the favorite restaurants for a specific user.
     *
     * @param userId The ID of the user whose favorites are being retrieved
     * @return A list of the user's favorite restaurants
     * @throws RemoteException If a remote communication error occurs
     */
    List<RestaurantDTO> getFavoriteRestaurants(String userId) throws RemoteException;

    /**
     * Retrieves the restaurants owned by a specific user (restaurateur).
     *
     * @param userId The ID of the restaurateur
     * @return A list of restaurants owned by the user
     * @throws RemoteException If a remote communication error occurs
     */
    List<RestaurantDTO> getOwnedRestaurants(String userId) throws RemoteException;

    /**
     * Retrieves the restaurants that have been reviewed by a specific user.
     *
     * @param userId The ID of the user whose reviewed restaurants are being retrieved
     * @return A list of restaurants reviewed by the user
     * @throws RemoteException If a remote communication error occurs
     */
    List<RestaurantDTO> getReviewedRestaurants(String userId) throws RemoteException;

    /**
     * Adds a restaurant to a user's favorites list.
     *
     * @param userId The ID of the user
     * @param restaurantId The ID of the restaurant to add to favorites
     * @return true if the restaurant was successfully added to favorites, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean addFavoriteRestaurant(String userId, String restaurantId) throws RemoteException;

    /**
     * Removes a restaurant from a user's favorites list.
     *
     * @param userId The ID of the user
     * @param restaurantId The ID of the restaurant to remove from favorites
     * @return true if the restaurant was successfully removed from favorites, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean removeFavoriteRestaurant(String userId, String restaurantId) throws RemoteException;

    /**
     * Creates a new restaurant in the system.
     * Only users with the restaurateur role can create restaurants.
     *
     * @param restaurant The restaurant data to create
     * @param ownerId The ID of the user creating the restaurant
     * @return The created restaurant with its assigned ID
     * @throws RemoteException If a remote communication error occurs
     * @throws SecurityException If the user does not have permissions to create a restaurant
     */
    RestaurantDTO createRestaurant(RestaurantDTO restaurant, String ownerId) throws RemoteException, SecurityException;
}