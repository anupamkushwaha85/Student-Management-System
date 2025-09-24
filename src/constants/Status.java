package constants;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents the various enrollment statuses a student can have.
 * <p>
 * Each status has a machine-readable code (e.g., "ACTIVE") and a
 * human-readable display name (e.g., "Active"). This enum provides
 * a type-safe way to manage student status throughout the application.
 */
public enum Status {
    /** The student is currently enrolled and attending classes. */
    ACTIVE("ACTIVE", "Active"),

    /** The student is temporarily not enrolled but may return. */
    INACTIVE("INACTIVE", "Inactive"),

    /** The student has successfully completed their course of study. */
    GRADUATED("GRADUATED", "Graduated"),

    /** The student has officially withdrawn from the institution. */
    DROPPED("DROPPED", "Dropped");

    private final String code;
    private final String displayName;
    /**
     * Private constructor for initializing an enum constant.
     * Enums constructors are implicitly private.
     *
     * @param code        The non-localized, machine-readable code for the status.
     * @param displayName The user-friendly name for display purposes.
     */
    Status(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    /**
     * Gets the machine-readable code for the status.
     *
     * @return The status code (e.g., "ACTIVE").
     */
    public String getCode() {
        return code;
    }
    /**
     * Gets the human-readable name for the status.
     *
     * @return The display name (e.g., "Active").
     */
    public String getDisplayName() {
        return displayName;
    }
    /**
     * Finds a Status constant that matches the given code.
     * <p>
     * This method performs a case-insensitive search against both the enum's
     * constant name (e.g., "ACTIVE") and its internal code field.
     *
     * @param code The code or name of the status to find.
     * @return an {@code Optional<Status>} containing the matched status,
     *         or an empty Optional if no match is found.
     */
    public static Optional<Status> fromCode(String code) {
        return Arrays.stream(Status.values())
                     .filter(s -> s.code.equalsIgnoreCase(code) || s.name().equalsIgnoreCase(code))
                     .findFirst();
    }
    /**
     * Returns the human-readable display name of the status.
     *
     * @return The display name.
     */
    @Override
    public String toString() {
        return displayName;
    }
}
