package it.uninsubria.dto;

import java.io.Serializable;

/**
 * Enumeration of main cuisine types for restaurants in TheKnife system.
 * This enum contains 30 of the most common cuisine types and mirrors the SQL ENUM implementation.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public enum CuisineType implements Serializable {
    AMERICAN("American"),
    ASIAN("Asian"),
    BARBECUE("Barbecue"),
    CHINESE("Chinese"),
    CONTEMPORARY("Contemporary"),
    EUROPEAN("European"),
    FRENCH("French"),
    FUSION("Fusion"),
    GERMAN("German"),
    GREEK("Greek"),
    INDIAN("Indian"),
    INTERNATIONAL("International"),
    ITALIAN("Italian"),
    JAPANESE("Japanese"),
    KOREAN("Korean"),
    LEBANESE("Lebanese"),
    MEDITERRANEAN("Mediterranean Cuisine"),
    MEXICAN("Mexican"),
    MIDDLE_EASTERN("Middle Eastern"),
    MOROCCAN("Moroccan"),
    PIZZA("Pizza"),
    SEAFOOD("Seafood"),
    SPANISH("Spanish"),
    STEAKHOUSE("Steakhouse"),
    STREET_FOOD("Street Food"),
    SUSHI("Sushi"),
    THAI("Thai"),
    TURKISH("Turkish"),
    VEGAN("Vegan"),
    VIETNAMESE("Vietnamese");

    private static final long serialVersionUID = 1L;

    private final String displayName;

    /**
     * Constructor for CuisineType enum.
     *
     * @param displayName The display name for the cuisine type
     */
    CuisineType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the cuisine type.
     *
     * @return The human-readable name of the cuisine type
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Finds a CuisineType by its display name.
     *
     * @param displayName The display name to search for
     * @return The corresponding CuisineType or null if not found
     */
    public static CuisineType fromDisplayName(String displayName) {
        for (CuisineType cuisineType : values()) {
            if (cuisineType.getDisplayName().equalsIgnoreCase(displayName)) {
                return cuisineType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}