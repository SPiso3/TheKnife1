package it.uninsubria.dto;

import java.io.Serializable;
import java.sql.Date;

/**
 * Data Transfer Object for User information.
 * This class is responsible for encapsulating user data to transfer between client and server.
 * Uses public fields for direct access with minimal constructors for different use cases.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String usr_id;
    private String password_pt;
    private String name;
    private String surname;
    private AddressDTO address;
    private Double latitude;
    private Double longitude;
    private Date bd;
    private UserRoleDTO role;

    /**
     * Default constructor.
     * Initializes a completely empty UserDTO.
     */
    public UserDTO() {
    }

    /**
     * Constructor with minimal authentication information.
     * Useful for login operations.
     *
     * @param usr_id User's unique identifier
     * @param password_pt User's password
     */
    public UserDTO(String usr_id, String password_pt) {
        this.usr_id = usr_id;
        this.password_pt = password_pt;
    }

    /**
     * Constructor with all user information including nation and city.
     * Useful for full data transfer operations.
     *
     * @param usr_id User's unique identifier
     * @param password_pt User's password
     * @param name User's first name
     * @param surname User's last name
     * @param nation User's nation of residence
     * @param city User's city of residence
     * @param address User's street address
     * @param latitude User's latitude location
     * @param longitude User's longitude location
     * @param bd User's birthdate (can be null)
     * @param role User's role (client/owner)
     *
     * @author Sergio Enrico Pisoni, 755563, VA
     */
    public UserDTO(String usr_id, String password_pt, String name, String surname,
                   String nation, String city, String address, Double latitude, Double longitude,
                   Date bd, String role) {
        this.usr_id = usr_id;
        this.password_pt = password_pt;
        this.name = name;
        this.surname = surname;
        this.address = new AddressDTO(nation, city, address);
        this.latitude = latitude;
        this.longitude = longitude;
        this.bd = bd;
        this.role = UserRoleDTO.valueOf(role.toUpperCase());
    }
    /**
     * Addressless constructor, currently used from server to store user. Might remove if useless.
     *
     * @param usr_id User's unique identifier
     * @param password_pt User's password
     * @param name User's first name
     * @param surname User's last name
     * @param latitude User's latitude location
     * @param longitude User's longitude location
     * @param bd User's birthdate (can be null)
     * @param role User's role (client/owner)
     *
     * @author Sergio Enrico Pisoni, 755563, VA
     */
    public UserDTO(String usr_id, String password_pt, String name, String surname,
                   Double latitude, Double longitude,
                   Date bd, UserRoleDTO role) {
        this.usr_id = usr_id;
        this.password_pt = password_pt;
        this.name = name;
        this.surname = surname;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bd = bd;
        this.role = role;
    }
    /**
     * Constructor with only latitude and longitude used for guests handling in the client.
     *
     * @param latitude User's latitude location
     * @param longitude User's longitude location
     *
     * @author Sergio Enrico Pisoni, 755563, VA
     */
    public UserDTO(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "usr_id='" + usr_id + '\'' +
                ", password_pt='" + password_pt + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                // address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                // ", bd=" + bd +
                ", role='" + role + '\'' +
                '}';
    }

    /**
     * Get username.
     * @return username
     */
    public String getUsername() {
        return usr_id;
    }
    /**
     * Get password.
     * @return username
     */
    public String getPassword() { return password_pt;}
    /**
     * Get name.
     * @return name
     */
    public String getName() {
        return name;
    }
    /**
     * Get surname.
     * @return surname
     */
    public String getSurname() {
        return surname;
    }
    /**
     * Get address.
     * @return address
     */
    public AddressDTO getAddress() {
        return address;
    }
    /**
     * Get latitude.
     * @return latitude
     */
    public Double getLatitude() {
        return latitude;
    }
    /**
     * Get longitude.
     * @return longitude
     */
    public Double getLongitude() {
        return longitude;
    }
    /**
     * Get birthdate.
     * @return birthdate
     */
    public Date getBirthday() {
        return bd;
    }
    /**
     * Get role.
     * @return role
     */
    public String getRole() {
        return role.getDisplayName().toLowerCase();
    }

    /**
     * Get country.
     * @return address country
     */
    public String getCountry() {
        return this.address.getCountry();
    }
    /**
     * Get city.
     * @return address city
     */
    public String getCity() {
        return this.address.getCity();
    }
    /**
     * Get street.
     * @return address street
     */
    public String getStreet() {
        return this.address.getStreet();
    }
}
