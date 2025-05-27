package it.uninsubria.server_services;

import it.uninsubria.DBConnection;
import it.uninsubria.dto.ReviewDTO;
import junit.framework.TestCase;

import java.util.List;

public class ReviewServiceImplTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
        String[] args = {"theknife", "password"};
        DBConnection.login(args);
    }

    public void testGetReviews() {
        // Test case for getting reviews of a restaurant
        String restaurantId = "17130"; // Example restaurant ID
        try {
            ReviewServiceImpl service = new ReviewServiceImpl();
            List<ReviewDTO> reviews = service.getReviews(restaurantId);
            assertNotNull(reviews);
            for (ReviewDTO review : reviews) {
                System.out.println(review.toString());
            }
        } catch (Exception e) {
            fail("Exception thrown while getting reviews: " + e.getMessage());
        }
    }

    public void testCreateOrUpdateReview() {
        // Test case for creating or updating a review
        ReviewDTO review = new ReviewDTO("Aiden56", "1", "Great food!", 5);
        try {
            ReviewServiceImpl service = new ReviewServiceImpl();
            boolean result = service.createOrUpdateReview(review);
            assertTrue(result);
        } catch (Exception e) {
            fail("Exception thrown while creating/updating review: " + e.getMessage());
        }
    }

    public void testDeleteReview() {
        // Test case for deleting a review
        String userId = "Aiden56"; // Example user ID
        String restaurantId = "1"; // Example restaurant ID
        try {
            ReviewServiceImpl service = new ReviewServiceImpl();
            boolean result = service.deleteReview(userId, restaurantId);
            assertTrue(result);
        } catch (Exception e) {
            fail("Exception thrown while deleting review: " + e.getMessage());
        }
    }

    public void testGetUserReviews() {
        // Test case for getting reviews by a user
        String userId = "Alexanne30"; // Example user ID
        try {
            ReviewServiceImpl service = new ReviewServiceImpl();
            List<ReviewDTO> userReviews = service.getUserReviews(userId);
            assertNotNull(userReviews);
            for (ReviewDTO review : userReviews) {
                System.out.println(review.toString());
            }
        } catch (Exception e) {
            fail("Exception thrown while getting user reviews: " + e.getMessage());
        }
    }
}