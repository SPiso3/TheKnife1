package it.uninsubria.dto;

import java.io.Serializable;

/**
 * Data Transfer Object for restaurant search criteria.
 * This class encapsulates the various search parameters that can be used to filter restaurants
 * in the searchRestaurant() functionality. It implements Serializable to allow
 * transfer between client and server components.
 *
 * This class implements the Builder pattern to allow for easy, modular construction
 * of search criteria with only the parameters that are needed.
 *
 * @author Sergio Enrico Pisoni, 755563, VA
 */
public class SearchCriteriaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public Double latitude;
    public Double longitude;

    public CuisineType cuisineType;
    public Double minPrice;
    public Double maxPrice;
    public Boolean deliveryAvailable;
    public Boolean onlineBookingAvailable;
    public Double minRating;

    /**
     * Private constructor used by the Builder.
     */
    private SearchCriteriaDTO() {
    }

    /**
     * Creates a new Builder instance for constructing a SearchCriteriaDTO.
     *
     * @return A new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Validates whether this search criteria has the mandatory location information.
     * The latitude and longitude coordinates must be provided.
     *
     * @return true if the criteria contains valid location coordinates, false otherwise
     */
    public boolean hasValidCoordinates() {
        return latitude != null && longitude != null;
    }

    /**
     * Nested Builder class for constructing SearchCriteriaDTO instances.
     */
    public static class Builder {
        private final SearchCriteriaDTO criteria;

        /**
         * Constructor for the Builder.
         */
        private Builder() {
            criteria = new SearchCriteriaDTO();
        }

        /**
         * Sets the geographic coordinates for the search.
         * This is a mandatory parameter for all searches.
         *
         * @param latitude Geographic latitude
         * @param longitude Geographic longitude
         * @return This Builder instance for method chaining
         */
        public Builder coordinates(Double latitude, Double longitude) {
            criteria.latitude = latitude;
            criteria.longitude = longitude;
            return this;
        }

        /**
         * Sets the cuisine type filter.
         *
         * @param cuisineType The type of cuisine to filter by
         * @return This Builder instance for method chaining
         */
        public Builder cuisineType(CuisineType cuisineType) {
            criteria.cuisineType = cuisineType;
            return this;
        }

        /**
         * Sets the price range filter.
         * Either minPrice or maxPrice can be null to indicate no lower or upper bound.
         * If both are provided, maxPrice must be greater than or equal to minPrice.
         *
         * @param minPrice Minimum price in euros (can be null)
         * @param maxPrice Maximum price in euros (can be null)
         * @return This Builder instance for method chaining
         * @throws IllegalArgumentException If maxPrice is less than minPrice when both are provided
         */
        public Builder priceRange(Double minPrice, Double maxPrice) {
            // check min<max
            if (minPrice != null && maxPrice != null && maxPrice < minPrice) {
                throw new IllegalArgumentException("Maximum price must be greater than or equal to minimum price");
            }

            criteria.minPrice = minPrice;
            criteria.maxPrice = maxPrice;
            return this;
        }

        /**
         * Sets the delivery service availability filter.
         *
         * @param available Whether delivery service should be available
         * @return This Builder instance for method chaining
         */
        public Builder deliveryAvailable(Boolean available) {
            criteria.deliveryAvailable = available;
            return this;
        }

        /**
         * Sets the online booking availability filter.
         *
         * @param available Whether online booking should be available
         * @return This Builder instance for method chaining
         */
        public Builder onlineBookingAvailable(Boolean available) {
            criteria.onlineBookingAvailable = available;
            return this;
        }

        /**
         * Sets the minimum rating filter.
         *
         * @param rating Minimum average rating (1-5 scale)
         * @return This Builder instance for method chaining
         */
        public Builder minRating(Double rating) {
            if (rating != null && rating >= 1.0 && rating <= 5.0) {
                criteria.minRating = rating;
            }
            return this;
        }

        /**
         * Builds and returns the constructed SearchCriteriaDTO instance.
         *
         * @return The constructed SearchCriteriaDTO
         * @throws IllegalStateException if mandatory coordinates are not provided
         */
        public SearchCriteriaDTO build() {
            if (criteria.latitude == null || criteria.longitude == null) {
                throw new IllegalStateException("Coordinates (latitude and longitude) are mandatory for search criteria");
            }
            return criteria;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SearchCriteriaDTO{");

        sb.append("coordinates=[").append(latitude).append(", ").append(longitude).append("]");

        if (cuisineType != null) {
            sb.append(", cuisineType=").append(cuisineType);
        }

        if (minPrice != null || maxPrice != null) {
            sb.append(", priceRange=[");
            sb.append(minPrice != null ? minPrice : "any");
            sb.append(" to ");
            sb.append(maxPrice != null ? maxPrice : "any");
            sb.append("]");
        }

        if (deliveryAvailable != null) {
            sb.append(", deliveryAvailable=").append(deliveryAvailable);
        }

        if (onlineBookingAvailable != null) {
            sb.append(", onlineBookingAvailable=").append(onlineBookingAvailable);
        }

        if (minRating != null) {
            sb.append(", minRating=").append(minRating);
        }

        sb.append('}');
        return sb.toString();
    }
}