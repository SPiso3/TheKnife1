package it.uninsubria.server_services;

import it.uninsubria.DBConnection;
import it.uninsubria.dto.AddressDTO;
import it.uninsubria.dto.UserDTO;
import it.uninsubria.dao.UserDAO;
import it.uninsubria.exceptions.AddressException;
import it.uninsubria.services.UserService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    private final Connection conn;
    // non c'è bisogno di passare la connection, per le mie cose l'ho gestita tutta dai DAO.
    public UserServiceImpl() throws RemoteException {
        this.conn = DBConnection.getConnection();
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param credentials UserDTO containing username and password
     * @return UserDTO with user information if authentication successful
     * @throws RemoteException If a remote communication error occurs
     * @throws SecurityException If credentials are invalid (user not found or wrong password)
     */
    @Override
    public synchronized UserDTO login(UserDTO credentials) throws RemoteException, SecurityException {
        String usr = credentials.getUsername();
        String psw = credentials.getPassword();
        // Get user from database
        UserDTO userRecord = UserDAO.getUserByID(usr);
        // Check if user exists
        if (userRecord == null) {
            System.err.println("Login attempt failed: User not found - " + usr);
            throw new SecurityException("Invalid credentials");
        }
        // Check password
        if (!userRecord.getPassword().equals(psw)) {
            System.err.println("Login attempt failed: Wrong password for user - " + usr);
            throw new SecurityException("Invalid credentials");
        }
        // Authentication successful
        System.out.println("User successfully logged in: " + usr);
        return userRecord;
    }

    @Override
    public synchronized void register(UserDTO userData) throws RemoteException, IllegalArgumentException, SecurityException {

        final String insertUserSQL = "INSERT INTO users (username, h_password, name, surname, birth_date, role, address_id) VALUES (?, ?, ?, ?, ?, ?)";


    }

    private Integer getAddressId(AddressDTO address) throws AddressException {
        final String insertAddressSQL = "INSERT INTO addresses (country, city, street) VALUES (?, ?, ?)";
        final String getAddressIdSQL = "SELECT id FROM addresses WHERE nation = ? AND city = ? AND address = ?";
        try {
            PreparedStatement insertAddressStmt = conn.prepareStatement(insertAddressSQL);
            insertAddressStmt.setString(1, address.getCountry());
            insertAddressStmt.setString(2, address.getCity());
            insertAddressStmt.setString(3, address.getStreet());
            insertAddressStmt.executeUpdate();
            conn.commit();
        } catch (SQLException ignored) {}
        try {
            PreparedStatement getAddressIdStmt = conn.prepareStatement(getAddressIdSQL);
            getAddressIdStmt.setString(1, address.nation);
            getAddressIdStmt.setString(2, address.city);
            getAddressIdStmt.setString(3, address.address);
            ResultSet rs = getAddressIdStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("address_id");
            } else {
                throw new AddressException("Error getting address ID");
            }
        } catch (SQLException e) {
            throw new AddressException("Error getting address ID");
        }
    }
}
