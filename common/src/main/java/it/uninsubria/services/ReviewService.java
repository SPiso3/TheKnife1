package it.uninsubria.services;

import it.uninsubria.dto.ReviewDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for review-related operations in TheKnife system.
 * This service handles creating, updating, and retrieving reviews and restaurant replies.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public interface ReviewService extends Remote {

    /**
     * Retrieves all reviews for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return A list of reviews for the restaurant
     * @throws RemoteException If a remote communication error occurs
     */
    List<ReviewDTO> getReviews(String restaurantId) throws RemoteException;

    /**
     * Creates a new review or updates an existing one.
     * A user can only have one review per restaurant, so this method will update
     * an existing review if the user has already reviewed this restaurant.
     *
     * @param review The review to create or update
     * @return true if the operation was successful, false otherwise
     * @throws RemoteException If a remote communication error occurs
     * @throws SecurityException If the user does not have permission to review this restaurant
     * @throws IllegalArgumentException If the review data is invalid
     */
    boolean createOrUpdateReview(ReviewDTO review) throws RemoteException, SecurityException, IllegalArgumentException;

    /**
     * Deletes a review.
     * A user can only delete their own reviews.
     *
     * @param userId The ID of the user attempting to delete the review
     * @param restaurantId The ID of the restaurant that was reviewed
     * @return true if the review was successfully deleted, false otherwise
     * @throws RemoteException If a remote communication error occurs
     * @throws SecurityException If the user does not have permission to delete this review
     */
    boolean deleteReview(String userId, String restaurantId) throws RemoteException, SecurityException;

    /**
     * Retrieves all reviews created by a specific user.
     *
     * @param userId The ID of the user whose reviews to retrieve
     * @return A list of reviews created by the user
     * @throws RemoteException If a remote communication error occurs
     */
    //not necessary
    List<ReviewDTO> getUserReviews(String userId) throws RemoteException;
}