package it.uninsubria;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.Console;

/**
 * DBConnector class helps to connect to the database
 */
public class DBConnector {
    /** Database URL */
    private static final String url = "jdbc:postgresql://localhost:5432/theknife_db";
    /** Database properties */
    private final Properties properties = new Properties();
    /**
     * Constructor for DBConnector
     */
    public DBConnector() {
        this.properties.setProperty("driver", Driver.class.getName());
        this.properties.setProperty("user", askUsername());
        this.properties.setProperty("password", askPassword());
    }
    /**
     * Constructor for DBConnector
     * @param username username for the database
     */
    public DBConnector(String username) {
        this.properties.setProperty("driver", Driver.class.getName());
        username = username == null ? this.askUsername() : username;
        this.properties.setProperty("user", username);
        this.properties.setProperty("password", askPassword());
    }
    /**
     * Constructor for DBConnector
     * @param username username for the database
     * @param password password for the database
     */
    public DBConnector(String username, String password) {
        this.properties.setProperty("driver", Driver.class.getName());
        username = username == null ? this.askUsername() : username;
        this.properties.setProperty("user", username);
        password = password == null ? this.askPassword() : password;
        this.properties.setProperty("password", password);
    }
    /**
     * Ask the user for the username
     * @return username
     */
    private String askUsername() {
        Console c = System.console();
        return c.readLine("Username: ");
    }
    /**
     * Ask the user for the password
     * @return password
     */
    private String askPassword() {
        Console c = System.console();
        return (new String(c.readPassword("Password: ")));
    }
    /**
     * Get the connection to the database
     * @return Connection object
     */
    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, this.properties);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return connection;
    }
}
