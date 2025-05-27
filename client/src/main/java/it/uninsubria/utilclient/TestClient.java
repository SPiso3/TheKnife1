package it.uninsubria.utilclient;

import it.uninsubria.dto.UserDTO;
import it.uninsubria.session.UserSession;
import java.sql.Date;

/**
 * Test class for the client-side components of TheKnife application.
 * This class verifies that the UserSession class works properly and
 * can be used for local testing during development.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class TestClient {

    /**
     * Main method to run client tests.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting client-side tests...");

        // Test UserSession functionality
        testUserSession();

        System.out.println("All client-side tests completed successfully!");
    }

    /**
     * Tests the UserSession class.
     * Verifies that the singleton pattern works correctly and that
     * login/logout functionality operates as expected.
     */
    private static void testUserSession() {
        System.out.println("\nTesting UserSession...");

        // Test singleton pattern
        UserSession session1 = UserSession.getInstance();
        UserSession session2 = UserSession.getInstance();

        // Verify that we get the same instance
        if (session1 == session2) {
            System.out.println("Singleton pattern test passed!");
        } else {
            System.err.println("Singleton pattern test failed: Multiple instances created!");
            System.exit(1);
        }

        // Verify initial state
        if (!session1.isLoggedIn()) {
            System.out.println("Initial logged-out state test passed!");
        } else {
            System.err.println("Initial logged-out state test failed!");
            System.exit(1);
        }

        // Create a test user
        UserDTO testUser = new UserDTO(
                "testuser",
                "password123",
                "Test",
                "User",
                "Italy",
                "Varese",
                "123 Test Street",
                45.8183,
                8.8239,
                Date.valueOf("1990-01-01"),
                "client"
        );

        // Test login functionality
        session1.login(testUser);

        if (session1.isLoggedIn() && session1.getCurrentUser() == testUser) {
            System.out.println("Login functionality test passed!");
        } else {
            System.err.println("Login functionality test failed!");
            System.exit(1);
        }

        // Test user role checks
        if (session1.isClient() && !session1.isOwner()) {
            System.out.println("User role check test passed!");
        } else {
            System.err.println("User role check test failed!");
            System.exit(1);
        }

        // Test user info retrieval
        if ("testuser".equals(session1.getUserId()) &&
                "Test".equals(session1.getUserName())) {
            System.out.println("User info retrieval test passed!");
        } else {
            System.err.println("User info retrieval test failed!");
            System.exit(1);
        }

        // Test coordinates retrieval
        double[] coords = session1.getUserCoordinates();
        if (coords != null && coords.length == 2 &&
                Math.abs(coords[0] - 45.8183) < 0.0001 &&
                Math.abs(coords[1] - 8.8239) < 0.0001) {
            System.out.println("User coordinates retrieval test passed!");
        } else {
            System.err.println("User coordinates retrieval test failed!");
            System.exit(1);
        }

        // Test logout functionality
        session1.logout();

        if (!session1.isLoggedIn() && session1.getCurrentUser() == null) {
            System.out.println("Logout functionality test passed!");
        } else {
            System.err.println("Logout functionality test failed!");
            System.exit(1);
        }

        // Test with restaurateur role
        UserDTO restaurateur = new UserDTO(
                "restowner",
                "securepass",
                "Restaurant",
                "Owner",
                "Italy",
                "Varese",
                "123 Test Street",
                45.8183,
                8.8239,
                Date.valueOf("1985-05-15"),
                "owner"
        );

        session1.login(restaurateur);

        if (session1.isOwner() && !session1.isClient()) {
            System.out.println("Restaurateur role check test passed!");
        } else {
            System.err.println("Restaurateur role check test failed!");
            System.exit(1);
        }

        // Clean up after tests
        session1.logout();

        System.out.println("All UserSession tests passed successfully!");
    }
}