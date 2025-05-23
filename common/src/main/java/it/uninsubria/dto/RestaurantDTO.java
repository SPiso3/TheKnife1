package it.uninsubria.dto;

import java.io.Serializable;

/**
 * Data Transfer Object for Restaurant information.
 * This class is responsible for encapsulating restaurant data to transfer between client and server.
 * Uses public fields for direct access without getters and setters.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class RestaurantDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public String id;
    public String name;
    public AddressDTO address;
    public Double latitude;
    public Double longitude;
    public Double avg_price;
    public Integer rating_count;  // Added the number of ratings
    public Double avg_rating;
    public Boolean delivery;
    public Boolean online_booking;
    public CuisineType cuisine;
    public String owner_usrId;

    /**
     * Complete constructor with all restaurant information including ID.
     * Useful for retrieving existing restaurant data.
     *
     * @param id Unique identifier for the restaurant
     * @param name Name of the restaurant
     * @param nation Nation where the restaurant is located
     * @param city City where the restaurant is located
     * @param address Street address of the restaurant
     * @param latitude Geographic latitude
     * @param longitude Geographic longitude
     * @param avg_price Average price in euros
     * @param rating_count Number of ratings received
     * @param avg_rating Average rating from reviews (1-5)
     * @param delivery Whether delivery service is available
     * @param online_booking Whether online booking is available
     * @param cuisine Type of cuisine from the enumeration
     * @param owner_usrId User ID of the restaurant owner
     */
    public RestaurantDTO(String id, String name, String nation, String city, String address,
                         Double latitude, Double longitude, Double avg_price,
                         Integer rating_count, Double avg_rating,
                         Boolean delivery, Boolean online_booking, CuisineType cuisine,
                         String owner_usrId) {
        this.id = id;
        this.name = name;
        this.address = new AddressDTO(nation, city, address);
        this.latitude = latitude;
        this.longitude = longitude;
        this.avg_price = avg_price;
        this.rating_count = rating_count;
        this.avg_rating = avg_rating;
        this.delivery = delivery;
        this.online_booking = online_booking;
        this.cuisine = cuisine;
        this.owner_usrId = owner_usrId;
    }

    /**
     * Constructor without ID for creating a new restaurant.
     * The ID will be assigned by the database.
     * Average rating is set to null as a new restaurant has no reviews yet.
     *
     * @param name Name of the restaurant
     * @param nation Nation where the restaurant is located
     * @param city City where the restaurant is located
     * @param address Street address of the restaurant
     * @param latitude Geographic latitude
     * @param longitude Geographic longitude
     * @param avg_price Average price in euros
     * @param delivery Whether delivery service is available
     * @param online_booking Whether online booking is available
     * @param cuisine Type of cuisine from the enumeration
     * @param owner_usrId User ID of the restaurant owner
     */
    public RestaurantDTO(String name, String nation, String city, String address,
                         Double latitude, Double longitude, Double avg_price,
                         Boolean delivery, Boolean online_booking, CuisineType cuisine,
                         String owner_usrId) {
        this.id = null;
        this.name = name;
        this.address = new AddressDTO(nation, city, address);
        this.latitude = latitude;
        this.longitude = longitude;
        this.avg_price = avg_price;
        this.rating_count = 0;     // Initialize with zero ratings
        this.avg_rating = null;    // no reviews
        this.delivery = delivery;
        this.online_booking = online_booking;
        this.cuisine = cuisine;
        this.owner_usrId = owner_usrId;
    }

    @Override
    public String toString() {
        return "RestaurantDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\''
                + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", avg_price=" + avg_price +
                ", rating_count=" + rating_count +
                ", avg_rating=" + avg_rating +
                ", delivery=" + delivery +
                ", online_booking=" + online_booking +
                ", cuisine=" + cuisine +
                ", owner_usrId='" + owner_usrId + '\'' +
                '}';
    }
    /**
     * Returns the country of the restaurant.
     * @return address country
     */
    public String getCountry() {
        return address.getCountry();
    }

    /**
     * Returns the city of the restaurant.
     * @return address city
     */
    public String getCity() {
        return address.getCity();
    }
    /**
     * Returns the street of the restaurant.
     * @return address street
     */
    public String getStreet() {
        return address.getStreet();
    }
}