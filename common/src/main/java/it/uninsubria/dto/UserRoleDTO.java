package it.uninsubria.dto;

import java.io.Serializable;

public enum UserRoleDTO implements Serializable {
    CLIENT("client"),
    OWNER("owner"),;

    private static final long serialVersionUID = 1L;

    private final String displayName;

    /**
     * Constructor for UserRoleDTO enum.
     *
     * @param displayName The display name for the user role
     */
    UserRoleDTO(String displayName) {
        this.displayName = displayName;
    }

    public static UserRoleDTO fromDisplayName(String roleString) {
        for (UserRoleDTO role : values()) {
            if (role.getDisplayName().equalsIgnoreCase(roleString)) {
                return role;
            }
        }
        return null;
    }

    /**
     * Gets the display name of the user role.
     *
     * @return The human-readable name of the user role
     */
    public String getDisplayName() {
        return displayName;
    }
}
