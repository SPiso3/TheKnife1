package it.uninsubria.dao;

import it.uninsubria.DBConnection;
import it.uninsubria.dto.UserDTO;
import it.uninsubria.dto.UserRoleDTO;

import java.sql.*;

public class UserDAO {
    //queries

    private static final String QUERY_GET_USER_BY_USERID = """
            SELECT username, h_password, name, surname, birth_date, role, latitude, longitude, address_id
            FROM Users
            WHERE username = ?
            """;

    private static final String QUERY_ADD_USER = "INSERT INTO users (username, h_password, name, surname, birth_date, role, latitude, longitude, address_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";


    /**
     * Retrieves a user by their username.
     * @param usr username of the user to retrieve
     * @return UserDTO containing user information, or null if not found
     */
    public static UserDTO getUserByID(String usr) {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(QUERY_GET_USER_BY_USERID)) {
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
                    //System.out.println(res);
                    // Create and return UserDTO with all available information
                    return new UserDTO(username, hashedPassword, name, surname, latitude, longitude, birthDate, role);
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


    /**
     * Adds a new user to the database.
     * @param userData UserDTO containing user information
     * @param addressId ID of the address associated with the user
     * @throws SQLException if there is an error during the database operation
     */
    public static synchronized void addUser(UserDTO userData, Integer addressId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(QUERY_ADD_USER);
        stmt.setString(1, userData.getUsername());
        stmt.setString(2, userData.getPassword()); // Assuming password is already hashed
        stmt.setString(3, userData.getName());
        stmt.setString(4, userData.getSurname());
        stmt.setDate(5, userData.getBirthday());
        stmt.setObject(6, userData.getRole(), Types.OTHER); // Convert ENUM to String
        stmt.setDouble(7, userData.getLatitude());
        stmt.setDouble(8, userData.getLongitude());
        stmt.setInt(9, addressId);
        stmt.executeUpdate();
    }
}
