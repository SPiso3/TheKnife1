package it.uninsubria.dao;

import it.uninsubria.DBConnection;
import it.uninsubria.dto.AddressDTO;
import it.uninsubria.dto.CuisineType;
import it.uninsubria.dto.RestaurantDTO;
import it.uninsubria.dto.SearchCriteriaDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class RestaurantDAO {
    /* restaurants (restaurant_id, r_owner, r_name, avg_price, delivery, booking, r_type, latitude, longitude, address_id) */
    final static String std_Query = "SELECT * FROM restaurants ";

    /**
     * Searches for restaurants based on the provided criteria.
     * This method constructs a SQL query based on the criteria and retrieves matching restaurants from the database.
     * @param criteria SearchCriteriaDTO containing search parameters such as coordinates, cuisine type, price range, delivery and booking options, and minimum rating.
     * @return List of RestaurantDTO objects that match the search criteria.
     */
    public static List<RestaurantDTO> searchRestaurants(SearchCriteriaDTO criteria) {
        List<RestaurantDTO> result = new java.util.ArrayList<RestaurantDTO>(List.of());
        final String query = makeCriteriaQuery(criteria);
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            ResultSet res = stmt.executeQuery();
            result = parseSQLRestaurantResults(res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Retrieves the count of ratings for a given restaurant.
     * @param restaurantId ID of the restaurant for which to count ratings.
     * @return The number of ratings for the restaurant, or 0 if none exist.
     */
    private static Integer getRatingCount(String restaurantId) {
        int count = 0;
        final String query = "SELECT COUNT(*) AS rating_count FROM reviews WHERE restaurant_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(restaurantId));
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                count = res.getInt("rating_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Retrieves the average rating for a given restaurant.
     * @param restaurantId ID of the restaurant for which to calculate the average rating.
     * @return The average rating, or null if no ratings exist.
     */
    private static Double getAvgRating(String restaurantId) {
        Double avgRating = null;
        final String query = "SELECT AVG(rating) AS avg_rating FROM reviews WHERE restaurant_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(restaurantId));
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                avgRating = res.getDouble("avg_rating");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return avgRating;
    }

    /**
     * Constructs a SQL query based on the provided search criteria.
     * This method builds a dynamic WHERE clause based on the criteria fields that are set.
     * @param criteria SearchCriteriaDTO containing search parameters.
     * @return A SQL query string with the appropriate WHERE conditions.
     */
    private static String makeCriteriaQuery(SearchCriteriaDTO criteria) {
        StringBuilder query = new StringBuilder(std_Query);
        boolean hasWhere = false;

        if (criteria.hasValidCoordinates()) {
            query.append("WHERE latitude = ").append(criteria.latitude)
                 .append(" AND longitude = ").append(criteria.longitude);
            hasWhere = true;
        }

        if (criteria.cuisineType != null) {
            if (!hasWhere) {
                query.append("WHERE ");
                hasWhere = true;
            } else {
                query.append(" AND ");
            }
            query.append("r_type = '").append(criteria.cuisineType).append("'");
        }

        if (criteria.minPrice != null) {
            if (!hasWhere) {
                query.append("WHERE ");
                hasWhere = true;
            } else {
                query.append(" AND ");
            }
            query.append("avg_price >= ").append(criteria.minPrice);
        }

        if (criteria.maxPrice != null) {
            if (!hasWhere) {
                query.append("WHERE ");
                hasWhere = true;
            } else {
                query.append(" AND ");
            }
            query.append("avg_price <= ").append(criteria.maxPrice);
        }

        if (criteria.deliveryAvailable != null) {
            if (!hasWhere) {
                query.append("WHERE ");
                hasWhere = true;
            } else {
                query.append(" AND ");
            }
            query.append("delivery = ").append(criteria.deliveryAvailable ? "TRUE" : "FALSE");
        }

        if (criteria.onlineBookingAvailable != null) {
            if (!hasWhere) {
                query.append("WHERE ");
                hasWhere = true;
            } else {
                query.append(" AND ");
            }
            query.append("booking = ").append(criteria.onlineBookingAvailable ? "TRUE" : "FALSE");
        }

        if (criteria.minRating != null) {
            if (!hasWhere) {
                query.append("WHERE ");
            } else {
                query.append(" AND ");
            }
            query.append("(SELECT AVG(rating) FROM reviews rev WHERE rev.restaurant_id = restaurants.restaurant_id) >= ")
                 .append(criteria.minRating);
        }
        query.append(";");
        return query.toString();
    }


    public static List<RestaurantDTO> getFavoriteRestaurants(String userId) {
        final String query = "SELECT * FROM restaurants WHERE restaurant_id IN " +
                "(SELECT restaurant_id FROM favorites WHERE username = ?);";
        List<RestaurantDTO> result = new java.util.ArrayList<RestaurantDTO>(List.of());
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet res = stmt.executeQuery();
            result = parseSQLRestaurantResults(res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

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

        assert addressDTO != null;
        return new RestaurantDTO(restaurantId, name, addressDTO.getCountry(), addressDTO.getCity(), addressDTO.getStreet(),
                latitude, longitude, avgPrice,
                rating_count, avg_rating,
                deliveryAvailable, onlineBookingAvailable, cuisineType,
                ownerId);
    }

    private static List<RestaurantDTO> parseSQLRestaurantResults(ResultSet res) throws Exception {
        List<RestaurantDTO> result = new java.util.ArrayList<RestaurantDTO>(List.of());
        while (res.next()) {
            try {
                RestaurantDTO restaurant = parseSQLRestaurantResult(res);
                result.add(restaurant);
            } catch (Exception ignored) {}
        }
        return result;
    }

    public static List<RestaurantDTO> getOwnedRestaurants(String userId) {
        final String query = "SELECT * FROM restaurants WHERE r_owner = ?;";
        List<RestaurantDTO> result = new java.util.ArrayList<RestaurantDTO>(List.of());
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet res = stmt.executeQuery();
            result = parseSQLRestaurantResults(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
