package it.uninsubria.dao;

import it.uninsubria.DBConnection;
import it.uninsubria.dto.AddressDTO;
import it.uninsubria.dto.CuisineType;
import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.dto.SearchCriteriaDTO;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Data Access Object for restaurant-related database operations.
 * Handles searching, retrieving, and managing restaurant data in the database.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class RestaurantDAO {

    /* restaurants (restaurant_id, r_owner, r_name, avg_price, delivery, booking, r_type, latitude, longitude, address_id) */
    private static final String BASE_QUERY = "SELECT *, " +
            // Calculate distance using Haversine formula
            "(6371 * acos(" +
            "cos(radians(?)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(?)) + " +
            "sin(radians(?)) * sin(radians(latitude))" +
            ")) AS distance " +
            "FROM restaurants ";

    /**
     * Searches for restaurants based on the provided criteria.
     * Returns the 10 closest restaurants that match the search criteria.
     *
     * @param criteria SearchCriteriaDTO containing search parameters such as coordinates,
     *                cuisine type, price range, delivery and booking options, and minimum rating.
     * @return List of up to 10 RestaurantDTO objects ordered by distance from the search coordinates.
     */
    public static List<RestaurantDTO> searchRestaurants(SearchCriteriaDTO criteria) {
        if (!criteria.hasValidCoordinates()) {
            throw new IllegalArgumentException("Search criteria must have valid coordinates");
        }

        List<RestaurantDTO> result = new ArrayList<>();
        String query = buildDistanceBasedQuery(criteria);

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            int paramIndex = setQueryParameters(stmt, criteria);

            try (ResultSet res = stmt.executeQuery()) {
                result = parseSQLRestaurantResults(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Builds a distance-based SQL query with filtering criteria.
     * The query calculates distance using the Haversine formula and orders results by proximity.
     *
     * @param criteria The search criteria containing filters and coordinates
     * @return Complete SQL query string with distance calculation and filtering
     */
    private static String buildDistanceBasedQuery(SearchCriteriaDTO criteria) {
        StringBuilder query = new StringBuilder(BASE_QUERY);
        boolean hasWhere = false;

        // Add filtering conditions (excluding coordinates since we use distance)
        if (criteria.cuisineType != null) {
            query.append("WHERE r_type::text = ? ");
            hasWhere = true;
        }

        if (criteria.minPrice != null) {
            query.append(hasWhere ? "AND " : "WHERE ");
            query.append("avg_price >= ? ");
            hasWhere = true;
        }

        if (criteria.maxPrice != null) {
            query.append(hasWhere ? "AND " : "WHERE ");
            query.append("avg_price <= ? ");
            hasWhere = true;
        }

        if (criteria.deliveryAvailable != null) {
            query.append(hasWhere ? "AND " : "WHERE ");
            query.append("delivery = ? ");
            hasWhere = true;
        }

        if (criteria.onlineBookingAvailable != null) {
            query.append(hasWhere ? "AND " : "WHERE ");
            query.append("booking = ? ");
            hasWhere = true;
        }

        if (criteria.minRating != null) {
            query.append(hasWhere ? "AND " : "WHERE ");
            query.append("(SELECT AVG(rating) FROM reviews rev WHERE rev.restaurant_id = restaurants.restaurant_id) >= ? ");
        }

        // Order by distance and limit to 10 closest restaurants
        query.append("ORDER BY distance ASC LIMIT 10");

        return query.toString();
    }

    /**
     * Sets the parameters for the prepared statement based on search criteria.
     *
     * @param stmt The prepared statement to set parameters for
     * @param criteria The search criteria containing the parameter values
     * @return The next parameter index after setting all parameters
     * @throws SQLException If there's an error setting parameters
     */
    private static int setQueryParameters(PreparedStatement stmt, SearchCriteriaDTO criteria) throws SQLException {
        int paramIndex = 1;

        // First 3 parameters are always the coordinates for distance calculation
        stmt.setDouble(paramIndex++, criteria.latitude);  // For cos(radians(?))
        stmt.setDouble(paramIndex++, criteria.longitude); // For radians(longitude) - radians(?)
        stmt.setDouble(paramIndex++, criteria.latitude);  // For sin(radians(?))

        // Set filtering parameters
        if (criteria.cuisineType != null) {
            stmt.setString(paramIndex++, criteria.cuisineType.getDisplayName());
        }

        if (criteria.minPrice != null) {
            stmt.setDouble(paramIndex++, criteria.minPrice);
        }

        if (criteria.maxPrice != null) {
            stmt.setDouble(paramIndex++, criteria.maxPrice);
        }

        if (criteria.deliveryAvailable != null) {
            stmt.setBoolean(paramIndex++, criteria.deliveryAvailable);
        }

        if (criteria.onlineBookingAvailable != null) {
            stmt.setBoolean(paramIndex++, criteria.onlineBookingAvailable);
        }

        if (criteria.minRating != null) {
            stmt.setDouble(paramIndex++, criteria.minRating);
        }

        return paramIndex;
    }

    /**
     * Retrieves the count of ratings for a given restaurant.
     *
     * @param restaurantId ID of the restaurant for which to count ratings.
     * @return The number of ratings for the restaurant, or 0 if none exist.
     */
    private static Integer getRatingCount(String restaurantId) {
        int count = 0;
        final String query = "SELECT COUNT(*) AS rating_count FROM reviews WHERE restaurant_id = ?";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(restaurantId));
            try (ResultSet res = stmt.executeQuery()) {
                if (res.next()) {
                    count = res.getInt("rating_count");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Retrieves the average rating for a given restaurant.
     *
     * @param restaurantId ID of the restaurant for which to calculate the average rating.
     * @return The average rating, or null if no ratings exist.
     */
    private static Double getAvgRating(String restaurantId) {
        Double avgRating = null;
        final String query = "SELECT AVG(rating) AS avg_rating FROM reviews WHERE restaurant_id = ?";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(restaurantId));
            try (ResultSet res = stmt.executeQuery()) {
                if (res.next()) {
                    avgRating = res.getDouble("avg_rating");
                    // Handle case where AVG returns 0 for no results
                    if (res.wasNull()) {
                        avgRating = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return avgRating;
    }

    /**
     * Retrieves favorite restaurants for a specific user.
     *
     * @param userId The ID of the user whose favorite restaurants to retrieve
     * @return List of RestaurantDTO objects representing the user's favorite restaurants
     */
    public static List<RestaurantDTO> getFavoriteRestaurants(String userId) {
        final String query = "SELECT *, 0 AS distance FROM restaurants WHERE restaurant_id IN " +
                "(SELECT restaurant_id FROM favorites WHERE username = ?)";
        List<RestaurantDTO> result = new ArrayList<>();

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            try (ResultSet res = stmt.executeQuery()) {
                result = parseSQLRestaurantResults(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Retrieves restaurants owned by a specific user (restaurateur).
     *
     * @param userId The ID of the user whose owned restaurants to retrieve
     * @return List of RestaurantDTO objects representing restaurants owned by the user
     */
    public static List<RestaurantDTO> getOwnedRestaurants(String userId) {
        final String query = "SELECT *, 0 AS distance FROM restaurants WHERE r_owner = ?";
        List<RestaurantDTO> result = new ArrayList<>();

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            try (ResultSet res = stmt.executeQuery()) {
                result = parseSQLRestaurantResults(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Retrieves restaurants that have been reviewed by a specific user.
     *
     * @param userId The ID of the user whose reviewed restaurants to retrieve
     * @return List of RestaurantDTO objects representing restaurants reviewed by the user
     */
    public static List<RestaurantDTO> getReviewedRestaurants(String userId) {
        final String query = "SELECT *, 0 AS distance FROM restaurants WHERE restaurant_id IN " +
                "(SELECT restaurant_id FROM reviews WHERE username = ?)";
        List<RestaurantDTO> result = new ArrayList<>();

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            try (ResultSet res = stmt.executeQuery()) {
                result = parseSQLRestaurantResults(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Adds a restaurant to a user's favorites list.
     *
     * @param userId The ID of the user
     * @param restaurantId The ID of the restaurant to add to favorites
     * @throws SQLException If there's an error executing the database operation
     */
    public static void insertFavoriteRestaurant(String userId, String restaurantId) throws SQLException {
        final String query = "INSERT INTO favorites (username, restaurant_id) VALUES (?, ?)";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setInt(2, Integer.parseInt(restaurantId));
            stmt.executeUpdate();
        }
    }

    /**
     * Removes a restaurant from a user's favorites list.
     *
     * @param userId The ID of the user
     * @param restaurantId The ID of the restaurant to remove from favorites
     * @throws SQLException If there's an error executing the database operation
     */
    public static void deleteFavoriteRestaurant(String userId, String restaurantId) throws SQLException {
        final String query = "DELETE FROM favorites WHERE username = ? AND restaurant_id = ?";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setInt(2, Integer.parseInt(restaurantId));
            stmt.executeUpdate();
        }
    }

    /**
     * Inserts a new restaurant into the database.
     *
     * @param restaurant The restaurant data to insert
     * @return The restaurant DTO with the assigned ID
     * @throws SQLException If there's an error executing the database operation
     */
    public static RestaurantDTO insertRestaurant(RestaurantDTO restaurant) throws SQLException {
        final String query = "INSERT INTO restaurants (restaurant_id, r_owner, r_name, avg_price, delivery, booking, r_type, latitude, longitude, address_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();

        // Get address ID
        int addressId = AddressDAO.getAddressId(restaurant.address);

        // Get next restaurant ID
        int id;
        try (PreparedStatement stmt = conn.prepareStatement("SELECT COALESCE(MAX(restaurant_id), 0) + 1 FROM restaurants")) {
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                id = rs.getInt(1);
            }
        }

        // Insert restaurant
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.setString(2, restaurant.owner_usrId);
            stmt.setString(3, restaurant.name);
            stmt.setDouble(4, restaurant.avg_price);
            stmt.setBoolean(5, restaurant.delivery);
            stmt.setBoolean(6, restaurant.online_booking);
            stmt.setObject(7, restaurant.cuisine.getDisplayName(), Types.OTHER);
            stmt.setDouble(8, restaurant.latitude);
            stmt.setDouble(9, restaurant.longitude);
            stmt.setInt(10, addressId);
            stmt.executeUpdate();
        }

        restaurant.id = String.valueOf(id);
        return restaurant;
    }

    /**
     * Parses a single restaurant result from a SQL ResultSet.
     *
     * @param res The ResultSet containing restaurant data
     * @return A RestaurantDTO object with the parsed data
     * @throws Exception If there's an error parsing the result
     */
    private static RestaurantDTO parseSQLRestaurantResult(ResultSet res) throws Exception {
        String restaurantId = res.getString("restaurant_id");
        String ownerId = res.getString("r_owner");
        String name = res.getString("r_name");
        Double avgPrice = res.getDouble("avg_price");
        Boolean deliveryAvailable = res.getBoolean("delivery");
        Boolean onlineBookingAvailable = res.getBoolean("booking");
        String r_type = res.getString("r_type");
        CuisineType cuisineType = CuisineType.fromDisplayName(r_type);
        Double latitude = res.getDouble("latitude");
        Double longitude = res.getDouble("longitude");
        AddressDTO addressDTO = AddressDAO.getAddress(res.getInt("address_id"));
        Integer rating_count = getRatingCount(restaurantId);
        Double avg_rating = getAvgRating(restaurantId);

        if (addressDTO == null) {
            throw new Exception("Address not found for restaurant " + restaurantId);
        }

        return new RestaurantDTO(restaurantId, name, addressDTO.getCountry(), addressDTO.getCity(), addressDTO.getStreet(),
                latitude, longitude, avgPrice,
                rating_count, avg_rating,
                deliveryAvailable, onlineBookingAvailable, cuisineType,
                ownerId);
    }

    /**
     * Parses multiple restaurant results from a SQL ResultSet.
     *
     * @param res The ResultSet containing multiple restaurant records
     * @return A List of RestaurantDTO objects with the parsed data
     * @throws Exception If there's an error parsing the results
     */
    private static List<RestaurantDTO> parseSQLRestaurantResults(ResultSet res) throws Exception {
        List<RestaurantDTO> result = new ArrayList<>();
        while (res.next()) {
            try {
                RestaurantDTO restaurant = parseSQLRestaurantResult(res);
                result.add(restaurant);
            } catch (Exception e) {
                // Log the error but continue processing other restaurants
                System.err.println("Error parsing restaurant: " + e.getMessage());
            }
        }
        return result;
    }
}