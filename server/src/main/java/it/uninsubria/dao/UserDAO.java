package it.uninsubria.dao;

import it.uninsubria.DBConnection;
import it.uninsubria.dto.UserDTO;
import it.uninsubria.dto.UserRoleDTO;

import java.sql.*;

public class UserDAO {
    //queries

    private static final String QUERY_GET_USER_BY_ID = """
            SELECT username, h_password, name, surname, birth_date, role, latitude, longitude, address_id
            FROM Users
            WHERE username = ?
            """;


    public static UserDTO getUserByID(String usr) {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(QUERY_GET_USER_BY_ID)) {
            // Set the parameter (username)
            stmt.setString(1, usr);

            // Execute the query
            try (ResultSet rs = stmt.executeQuery()) {
                // Check if user was found
                if (rs.next()) {
                    // Extract data from ResultSet
                    String username = rs.getString("username");
                    String hashedPassword = rs.getString("h_password");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    System.out.println(surname);
                    Date birthDate = rs.getDate("birth_date");  // Can be null
                    String roleString = rs.getString("role");
                    UserRoleDTO role = UserRoleDTO.fromDisplayName(roleString);  // ENUM converted to String
                    Double latitude = rs.getDouble("latitude");
                    Double longitude = rs.getDouble("longitude");
                    // Note: address_id is not mapped to UserDTO as it's not in the DTO structure
                    UserDTO res = new UserDTO(username, hashedPassword, name, surname, latitude, longitude, birthDate, role);
                    //System.out.println(res);
                    // Create and return UserDTO with all available information
                    return res;
                } else {
                    // User not found
                    System.out.println("not entering");
                    return null;
                }
            }

        } catch (SQLException e) {
            // Log the error and handle appropriately
            System.err.println("Database error while retrieving user: " + e.getMessage());
            // You might want to throw a custom exception here instead
            throw new RuntimeException("Failed to retrieve user from database", e);
        }
    }
}
