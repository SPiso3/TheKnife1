package it.uninsubria.server_services;

import it.uninsubria.dao.RestaurantDAO;
import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.dto.SearchCriteriaDTO;
import it.uninsubria.services.RestaurantService;

import java.rmi.RemoteException;
import java.util.List;

public class RestaurantServiceImpl implements RestaurantService {
    @Override
    public List<RestaurantDTO> searchRestaurants(SearchCriteriaDTO criteria) throws RemoteException {
        return RestaurantDAO.searchRestaurants(criteria);
    }

    @Override
    public List<RestaurantDTO> getFavoriteRestaurants(String userId) throws RemoteException {
        return RestaurantDAO.getFavoriteRestaurants(userId);
    }

    @Override
    public List<RestaurantDTO> getOwnedRestaurants(String userId) throws RemoteException {
        return RestaurantDAO.getOwnedRestaurants(userId);
    }

    @Override
    public List<RestaurantDTO> getReviewedRestaurants(String userId) throws RemoteException {
        return List.of();
    }

    @Override
    public boolean addFavoriteRestaurant(String userId, String restaurantId) throws RemoteException {
        return false;
    }

    @Override
    public boolean removeFavoriteRestaurant(String userId, String restaurantId) throws RemoteException {
        return false;
    }

    @Override
    public RestaurantDTO createRestaurant(RestaurantDTO restaurant, String ownerId) throws RemoteException, SecurityException {
        return null;
    }
}
