package it.uninsubria.dto;

import java.io.Serializable;

/**
 * Data Transfer Object for Address information.
 * This class is responsible for encapsulating address data to transfer between client and server.
 * Uses public fields for direct access with minimal constructors for different use cases.
 *
 * @author Lorenzo Radice, 753252, CO
 */
public class AddressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public String nation;
    public String city;
    public String address;

    /**
     * Default constructor.
     * Initializes a completely empty AddressDTO.
     */
    public AddressDTO() {
    }

    /**
     * Constructor with all address information.
     * Useful for full data transfer operations.
     *
     * @param nation User's nation of residence
     * @param city User's city of residence
     * @param address User's street address
     */
    public AddressDTO(String nation, String city, String address) {
        this.nation = nation;
        this.city = city;
        this.address = address;
    }

    @Override
    public String toString() {
        return "nation='" + nation + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address;
    }

    /**
     * Returns the full address as a formatted string.
     * @return Formatted string containing the full address.
     */
    public String getFullAddress() {
        return nation + ", " + city + ", " + address;
    }

    /**
     * Returns the nation of the address.
     * @return nation
     */
    public String getCountry() {
        return nation;
    }

    /**
     * Returns the city of the address.
     * @return city
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Returns the street of the address.
     * @return street
     */
    public String getStreet() {
        return this.address;
    }
}

