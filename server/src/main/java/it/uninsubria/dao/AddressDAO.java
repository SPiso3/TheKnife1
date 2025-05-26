package it.uninsubria.dao;

import it.uninsubria.DBConnection;
import it.uninsubria.dto.AddressDTO;
import it.uninsubria.exceptions.AddressException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressDAO {
    public static synchronized Integer getAddressId(AddressDTO address) throws AddressException {
        final String insertAddressSQL = "INSERT INTO addresses (country, city, street) VALUES (?, ?, ?)";
        final String getAddressIdSQL = "SELECT id FROM addresses WHERE nation = ? AND city = ? AND address = ?";
        Connection conn = DBConnection.getConnection();
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
