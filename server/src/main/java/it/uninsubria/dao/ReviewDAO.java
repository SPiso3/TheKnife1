package it.uninsubria.dao;

import it.uninsubria.DBConnection;
import it.uninsubria.dto.ReviewDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    public static List<ReviewDTO> getRestaurantReviews(String restaurantId) throws SQLException {
        final String query = "SELECT * FROM reviews WHERE restaurant_id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, Integer.parseInt(restaurantId));
        ResultSet rs = stmt.executeQuery();
        List<ReviewDTO> reviews = parseReviewResultSet(rs);
        rs.close();
        stmt.close();
        return reviews;
    }
    private static List<ReviewDTO> parseReviewResultSet(ResultSet rs) throws SQLException {
        List<ReviewDTO> reviews = new ArrayList<>();
        while (rs.next()) {
            reviews.add(parseReviewResult(rs));
        }
        return reviews;
    }
    private static ReviewDTO parseReviewResult(ResultSet rs) throws SQLException {
        ReviewDTO review = new ReviewDTO();
        review.setUsr_id(rs.getString(1));
        review.setRest_id(rs.getString(2));
        review.setRating(rs.getInt(3));
        review.setCustomer_msg(rs.getString(4));
        review.setRest_rep(rs.getString(5));
        return review;
    }

    public static boolean updateReview(ReviewDTO review) throws SQLException {
        final String query = "INSERT INTO reviews (username, restaurant_id, rating, comment, reply) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (username, restaurant_id) DO UPDATE SET rating = ?, comment = ?, reply = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, review.getUsr_id());
        stmt.setInt(2, Integer.parseInt(review.getRest_id()));
        stmt.setInt(3, review.getRating());
        stmt.setString(4, review.getCustomer_msg());
        stmt.setString(5, review.getRest_rep());
        stmt.setInt(6, review.getRating());
        stmt.setString(7, review.getCustomer_msg());
        stmt.setString(8, review.getRest_rep());
        return stmt.executeUpdate() > 0;
    }

    public static boolean deleteReview(String userId, String restaurantId) throws SQLException {
        final String query = "DELETE FROM reviews WHERE username = ? AND restaurant_id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, userId);
        stmt.setInt(2, Integer.parseInt(restaurantId));
        return stmt.executeUpdate() > 0;
    }

    public static List<ReviewDTO> getUserReviews(String userId) throws SQLException {
        final String query = "SELECT * FROM reviews WHERE username = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, userId);
        ResultSet rs = stmt.executeQuery();
        List<ReviewDTO> reviews = parseReviewResultSet(rs);
        rs.close();
        stmt.close();
        return reviews;
    }
}
