package it.uninsubria.server_services;

import it.uninsubria.dto.AddressDTO;
import it.uninsubria.dto.UserDTO;
import it.uninsubria.exceptions.AddressException;
import it.uninsubria.services.UserService;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserServiceImpl implements UserService {
    private final Connection conn;
    public UserServiceImpl(Connection conn) {
        this.conn = conn;
    }
    @Override
    public UserDTO login(UserDTO credentials) throws RemoteException, SecurityException {
        return null;
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
