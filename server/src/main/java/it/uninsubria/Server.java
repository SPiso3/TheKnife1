package it.uninsubria;


import it.uninsubria.dto.UserDTO;
import it.uninsubria.server_services.UserServiceImpl;

/**
 * Server class for TheKnife application
 * @author Lorenzo Radice
 * @version 1.0.0
 */
public class Server
{
    /** Version of the application */
    private static final String version = "1.0.0";
    /** Application title */
    private static final String title = "TheKnife Server";
    /**
     * Main method for the server
     * @param args command line arguments
     */
    public static void main( String[] args )
    {
        if (hasOption(args)) {
            return;
        }
        System.out.println(title);
        DBConnection.login(args);
        // for testing purpose
        testStuff();
        if (DBConnection.closeConnection())
            System.out.println("Connection closed");
        else
            System.out.println("Connection not closed");
    }


    /**
     * Check if the user has provided any options and execute the corresponding action
     * @param args command line arguments
     * @return true if the user has provided any options, false otherwise
     */
    private static boolean hasOption(String[] args) {
        for (String arg : args) {
            if (isHelp(arg)) {
                showHelp();
                return true;
            }
            if (isVersion(arg)) {
                showVersion();
                return true;
            }
        }
        return false;
    }
    /**
     * Show the help message
     */
    private static void showHelp() {
        final String usage =
                "Usage: java -jar TheKnifeServer.jar [option|username] [password]\n" +
                "Options:\n" +
                "\t-h, --help\t\tShow this help message\n" +
                "\t-v, --version\t\tShow version information";
        System.out.println(usage);
    }
    /**
     * Show the version information
     */
    private static void showVersion() {
        final String versionInfo = title + version;
        System.out.println(versionInfo);
    }
    /**
     * Check if the argument is a help option
     * @param arg argument to check
     * @return true if the argument is a help option, false otherwise
     */
    private static boolean isHelp(String arg) {
        final String[] helps = {"-h", "--help"};
        for (String help: helps) {
            if (arg.equals(help)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Check if the argument is a version option
     * @param arg argument to check
     * @return true if the argument is a version option, false otherwise
     */
    private static boolean isVersion(String arg) {
        final String[] versions = {"-v", "--version"};
        for (String version: versions) {
            if (arg.equals(version)) {
                return true;
            }
        }
        return false;
    }




    private static void testStuff() {
        System.out.println("=== Testing Login Functionality ===");

        // Test 1: Successful login
        try {
            System.out.println("\n--- Test 1: Valid credentials ---");
            UserDTO validCredentials = new UserDTO("user", "psw1");
            UserServiceImpl userService = new UserServiceImpl();

            UserDTO loggedInUser = userService.login(validCredentials);
            System.out.println("✓ Login successful!");
            System.out.println("User details: " + loggedInUser.toString());

        } catch (Exception e) {
            System.err.println("✗ Test 1 failed: " + e.getMessage());
        }

        // Test 2: Wrong password
        try {
            System.out.println("\n--- Test 2: Wrong password ---");
            UserDTO wrongPasswordCredentials = new UserDTO("user", "wrongpassword");
            UserServiceImpl userService = new UserServiceImpl();

            UserDTO result = userService.login(wrongPasswordCredentials);
            System.err.println("✗ Test 2 failed: Should have thrown SecurityException");

        } catch (SecurityException e) {
            System.out.println("✓ Test 2 passed: Correctly rejected wrong password - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Test 2 failed with unexpected exception: " + e.getMessage());
        }

        // Test 3: Non-existent user
        try {
            System.out.println("\n--- Test 3: Non-existent user ---");
            UserDTO nonExistentCredentials = new UserDTO("nonexistent", "anypassword");
            UserServiceImpl userService = new UserServiceImpl();

            UserDTO result = userService.login(nonExistentCredentials);
            System.err.println("✗ Test 3 failed: Should have thrown SecurityException");

        } catch (SecurityException e) {
            System.out.println("✓ Test 3 passed: Correctly rejected non-existent user - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Test 3 failed with unexpected exception: " + e.getMessage());
        }

        // Test 4: Null credentials
        try {
            System.out.println("\n--- Test 4: Null/empty credentials ---");
            UserDTO emptyCredentials = new UserDTO("", "");
            UserServiceImpl userService = new UserServiceImpl();

            UserDTO result = userService.login(emptyCredentials);
            System.err.println("✗ Test 4 failed: Should have thrown SecurityException");

        } catch (SecurityException e) {
            System.out.println("✓ Test 4 passed: Correctly rejected empty credentials - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Test 4 failed with unexpected exception: " + e.getMessage());
        }

        System.out.println("\n=== Login Testing Complete ===");
    }
}
