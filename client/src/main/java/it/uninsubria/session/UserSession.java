package it.uninsubria.session;

import it.uninsubria.dto.UserDTO;

/**
 * Maintains the current user's session information on the client side.
 * This class follows the Singleton pattern to ensure only one session exists
 * throughout the application's lifecycle.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class UserSession {
    private static UserSession instance;
    private UserDTO currentUser;
    private boolean isLoggedIn;

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private UserSession() {
        this.isLoggedIn = false;
        this.currentUser = null;
    }

    /**
     * Gets the single instance of UserSession.
     *
     * @return The UserSession instance
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Starts a user session with the provided user data.
     *
     * @param user The user data to associate with this session
     */
    public void login(UserDTO user) {
        this.currentUser = user;
        this.isLoggedIn = true;
    }

    /**
     * Ends the current user session.
     */
    public void logout() {
        this.currentUser = null;
        this.isLoggedIn = false;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    /**
     * Gets the current logged-in user's data.
     *
     * @return The UserDTO of the current user, or null if no user is logged in
     */
    public UserDTO getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if the current user is a restaurateur.
     *
     * @return true if the current user is a restaurateur, false otherwise
     */
    public boolean isRestaurateur() {
        return isLoggedIn && currentUser != null &&
                "owner".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Checks if the current user is a client.
     *
     * @return true if the current user is a client, false otherwise
     */
    public boolean isClient() {
        return isLoggedIn && currentUser != null &&
                "client".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Gets the user ID of the current user.
     *
     * @return The user ID of the current user, or null if no user is logged in
     */
    public String getUserId() {
        return isLoggedIn && currentUser != null ? currentUser.usr_id : null;
    }

    /**
     * Gets the name of the current user.
     *
     * @return The name of the current user, or null if no user is logged in
     */
    public String getUserName() {
        return isLoggedIn && currentUser != null ? currentUser.name : null;
    }

    /**
     * Gets the location coordinates of the current user.
     *
     * @return An array containing latitude and longitude, or null if no user is logged in
     */
    public double[] getUserCoordinates() {
        if (currentUser != null &&
                currentUser.latitude != null && currentUser.longitude != null) {
            System.out.println("returned:"+ currentUser.latitude + currentUser.longitude);
            return new double[] {currentUser.latitude, currentUser.longitude};
        }
        return null;
    }

    public void setGuestMode(double latitude, double longitude) {
        currentUser = new UserDTO(latitude, longitude);
        System.out.println("setted" + latitude + longitude); //debug
    }
}