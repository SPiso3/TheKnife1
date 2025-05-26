package it.uninsubria.services;

import it.uninsubria.dto.UserDTO;
import it.uninsubria.exceptions.UserException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for user-related operations in TheKnife system.
 * This service handles user authentication, registration, and profile management.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public interface UserService extends Remote {

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param credentials UserDTO containing only username and password
     * @return Complete UserDTO with all user information if authentication is successful
     * @throws RemoteException If a remote communication error occurs
     * @throws SecurityException If authentication fails due to invalid credentials
     */
    UserDTO login(UserDTO credentials) throws RemoteException, SecurityException;

    /**
     * Registers a new user in the system.
     *
     * @param userData UserDTO containing all required registration information
     * @throws RemoteException If a remote communication error occurs
     * @throws IllegalArgumentException If the provided user data is incomplete or invalid
     * @throws SecurityException If the username is already taken
     */
    void register(UserDTO userData) throws RemoteException, UserException;
}