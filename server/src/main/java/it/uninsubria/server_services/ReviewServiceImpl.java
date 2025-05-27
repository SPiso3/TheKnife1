package it.uninsubria.server_services;

import it.uninsubria.dao.ReviewDAO;
import it.uninsubria.dto.ReviewDTO;
import it.uninsubria.services.ReviewService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

public class ReviewServiceImpl extends UnicastRemoteObject implements ReviewService {

    public ReviewServiceImpl() throws RemoteException {}

    @Override
    public synchronized List<ReviewDTO> getReviews(String restaurantId) throws RemoteException {
        try {
            return ReviewDAO.getRestaurantReviews(restaurantId);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving reviews for restaurant ID: " + restaurantId, e);
        }
    }

    @Override
    public synchronized boolean createOrUpdateReview(ReviewDTO review) throws RemoteException, SecurityException, IllegalArgumentException {
        try {
            return ReviewDAO.updateReview(review);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating or updating review for user ID: " + review.usr_id + " and restaurant ID: " + review.rest_id, e);
        }
    }

    @Override
    public synchronized boolean deleteReview(String userId, String restaurantId) throws RemoteException, SecurityException {
        try {
            ReviewDAO.deleteReview(userId, restaurantId);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting review for user ID: " + userId + " and restaurant ID: " + restaurantId, e);
        }
    }

    @Override
    public synchronized List<ReviewDTO> getUserReviews(String userId) throws RemoteException {
        try {
            return ReviewDAO.getUserReviews(userId);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving reviews for user ID: " + userId, e);
        }
    }
}
