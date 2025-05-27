package it.uninsubria.server_services;

import it.uninsubria.dao.RestaurantDAO;
import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.dto.SearchCriteriaDTO;
import it.uninsubria.services.RestaurantService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class RestaurantServiceImpl extends UnicastRemoteObject implements RestaurantService {

    public RestaurantServiceImpl() throws RemoteException {}

    @Override
    public synchronized List<RestaurantDTO> searchRestaurants(SearchCriteriaDTO criteria) throws RemoteException {
        return RestaurantDAO.searchRestaurants(criteria);
    }

    @Override
    public synchronized List<RestaurantDTO> getFavoriteRestaurants(String userId) throws RemoteException {
        return RestaurantDAO.getFavoriteRestaurants(userId);
    }

    @Override
    public synchronized List<RestaurantDTO> getOwnedRestaurants(String userId) throws RemoteException {
        return RestaurantDAO.getOwnedRestaurants(userId);
    }

    @Override
    public synchronized List<RestaurantDTO> getReviewedRestaurants(String userId) throws RemoteException {
        return RestaurantDAO.getReviewedRestaurants(userId);
    }

    @Override
    public synchronized boolean addFavoriteRestaurant(String userId, String restaurantId) throws RemoteException {
        try {
            RestaurantDAO.insertFavoriteRestaurant(userId, restaurantId);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public synchronized boolean removeFavoriteRestaurant(String userId, String restaurantId) throws RemoteException {
        try {
            RestaurantDAO.deleteFavoriteRestaurant(userId, restaurantId);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public synchronized RestaurantDTO createRestaurant(RestaurantDTO restaurant, String ownerId) throws RemoteException, SecurityException {
        try {
            restaurant.owner_usrId = ownerId;
            return RestaurantDAO.insertRestaurant(restaurant);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SecurityException("Failed to create restaurant: " + e.getMessage());
        }
    }
}
