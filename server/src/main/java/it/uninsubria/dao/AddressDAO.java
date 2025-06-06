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
        final String insertAddressSQL = """
                INSERT INTO addresses (address_id,
                country, city, street) VALUES ((
                SELECT MAX(a.address_id)+1
                FROM addresses a
                ),?, ?, ?);""";
        final String getAddressIdSQL = "SELECT address_id FROM addresses WHERE country = ? AND city = ? AND street = ?;";
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
            getAddressIdStmt.setString(1, address.getCountry());
            getAddressIdStmt.setString(2, address.getCity());
            getAddressIdStmt.setString(3, address.getStreet());
            ResultSet rs = getAddressIdStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("address_id");
            } else {
                throw new AddressException("Error getting address ID");
            }
        } catch (SQLException e) {
            throw new AddressException("Error querying address ID");
        }
    }

    public static AddressDTO getAddress(int addressId) {
        final String getAddressSQL = "SELECT country, city, street FROM addresses WHERE address_id = ?;";
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(getAddressSQL);
            stmt.setInt(1, addressId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String country = rs.getString("country");
                String city = rs.getString("city");
                String street = rs.getString("street");
                return new AddressDTO(country, city, street);
            } else {
                return null; // Address not found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Error occurred
        }
    }
}
