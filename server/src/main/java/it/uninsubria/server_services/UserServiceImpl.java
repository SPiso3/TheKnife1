package it.uninsubria.server_services;

import it.uninsubria.dao.AddressDAO;
import it.uninsubria.dto.UserDTO;
import it.uninsubria.dao.UserDAO;
import it.uninsubria.exceptions.UserException;
import it.uninsubria.services.UserService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    public UserServiceImpl() throws RemoteException {}

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
    public synchronized void register(UserDTO userData) throws RemoteException, UserException {
        // check if user already exists
        if (UserDAO.getUserByID(userData.getUsername()) != null) { // magari mettere un altro check più veloce
            throw new UserException("User already exists"); // to handle better, maybe custom exception
        }
        // add address to database and get address id
        Integer addressId = AddressDAO.getAddressId(userData.getAddress());
        // add user to database
        try {
            UserDAO.addUser(userData, addressId);
        } catch (SQLException e) {
            throw new RemoteException("Error adding user to database");
        }
    }
}
