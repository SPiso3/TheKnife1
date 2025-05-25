package it.uninsubria;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    /** Database connection */
    private static Connection connection = null;
    /** Maximum number of attempts to connect to the database */
    private static final short maxAttempts = 3;
    /** Number of remaining attempts to connect to the database */
    private static short remainingAttempts = maxAttempts;
    /**
     * Method to log in to the database given command line arguments which
     * might contain username and password
     * @param args command line arguments
     */
    public static synchronized void login(String[] args) {
        DBConnector dbConnector;
        String username = null;
        if (args.length >= 1) {
            username = args[0];
            if (args.length >= 2) {
                dbConnector = new DBConnector(username, args[1]);
            } else {
                dbConnector = new DBConnector(username);
            }
        } else {
            dbConnector = new DBConnector();
        }
        attempt(dbConnector);
        while ( connection == null ) {
            dbConnector = new DBConnector(username);
            attempt(dbConnector);
        }
        System.out.println("Database connection established");
    }
    /**
     * Gets the singleton database connection instance.
     *
     * @return The database connection, or null if not yet established
     * @throws IllegalStateException if connection has not been established via login()
     */
    public static synchronized Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Database connection not established. Call login() first.");
        }
        return connection;
    }
    /**
     * Method to attempt to connect to the database
     * @param connector DBConnector object
     */
    private static void attempt(DBConnector connector) {
        connection = connector.getConnection();
        remainingAttempts--;
        checkAttempts();
    }
    /**
     * Method to check if the maximum number of attempts to connect to the database
     * has been reached. If so, the program will exit with an error message.
     */
    public static void checkAttempts() {
        if (connection == null && remainingAttempts <= 0) {
            System.err.println("Maximum number of attempts reached");
            System.exit(1);
        }
    }
    /**
     * Method to close the connection to the database
     * @return true if the connection was closed successfully, false otherwise
     */
    public static synchronized boolean closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
        System.out.println("Database connection closed");
        return true;
    }
}
