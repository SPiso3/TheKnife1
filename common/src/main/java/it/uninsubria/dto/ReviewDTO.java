package it.uninsubria.dto;

import java.io.Serializable;

/**
 * Data Transfer Object for Review information.
 * This class is responsible for encapsulating review data to transfer between client and server.
 * Uses public fields for direct access without getters and setters.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class ReviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public String usr_id;
    public String rest_id;
    public String customer_msg = null;
    public Integer rating;
    public String rest_rep = null;

    /**
     * Complete constructor with all review information.
     * The restaurant reply field can be null if no reply has been made.
     *
     * @param usr_id ID of the user who created the review
     * @param rest_id ID of the restaurant being reviewed
     * @param customer_msg Review message from the customer
     * @param rating Star rating (1-5)
     * @param rest_rep Optional reply from restaurant owner (can be null)
     */
    public ReviewDTO(String usr_id, String rest_id, String customer_msg, Integer rating, String rest_rep) {
        this.usr_id = usr_id;
        this.rest_id = rest_id;
        this.customer_msg = customer_msg;
        this.rating = rating;
        this.rest_rep = rest_rep;
    }

    /**
     * Constructor for a new review without a restaurant reply.
     *
     * @param usr_id ID of the user who created the review
     * @param rest_id ID of the restaurant being reviewed
     * @param customer_msg Review message from the customer
     * @param rating Star rating (1-5)
     */
    public ReviewDTO(String usr_id, String rest_id, String customer_msg, Integer rating) {
        this.usr_id = usr_id;
        this.rest_id = rest_id;
        this.customer_msg = customer_msg;
        this.rating = rating;
        this.rest_rep = null;
    }

    public ReviewDTO() {}

    @Override
    public String toString() {
        return "ReviewDTO{" +
                "usr_id='" + usr_id + '\'' +
                ", rest_id='" + rest_id + '\'' +
                ", customer_msg='" + customer_msg + '\'' +
                ", rating=" + rating +
                ", rest_rep='" + (rest_rep != null ? rest_rep : "No reply yet") + '\'' +
                '}';
    }

    public void setUsr_id(String usr_id) {
        this.usr_id = usr_id;
    }
    public void setRest_id(String rest_id) {
        this.rest_id = rest_id;
    }
    public void setCustomer_msg(String customer_msg) {
        this.customer_msg = customer_msg;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public void setRest_rep(String rest_rep) {
        this.rest_rep = rest_rep;
    }

    public String getUsr_id() {
        return usr_id;
    }

    public String getRest_id() {
        return rest_id;
    }

    public int getRating() {
        return rating;
    }

    public String getCustomer_msg() {
        return customer_msg;
    }

    public String getRest_rep() {
        return rest_rep;
    }
}
