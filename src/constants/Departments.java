package constants;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents the academic departments available at the institution.
 * <p>
 * Each department is defined by a short code (e.g., "CSE") and a full
 * display name (e.g., "Computer Science & Engineering"). This provides a
 * controlled, type-safe vocabulary for departments within the system.
 */
public enum Departments {
    /** Computer Science & Engineering */
    CSE("CSE", "Computer Science & Engineering"),
    /** Artificial Intelligence */
    AI("AI", "Artificial Intelligence"),
    /** Machine Learning */
    ML("ML", "Machine Learning"),
    /** Electronics & Communication Engineering */
    ECE("ECE", "Electronics & Communication"),
    /** Electrical Engineering */
    EE("EE", "Electrical Engineering"),
    /** Mechanical Engineering */
    ME("ME", "Mechanical Engineering"),
    /** Civil Engineering */
    CE("CE", "Civil Engineering"),
    /** Information Technology */
    IT("IT", "Information Technology"),
    /** Chemical Engineering */
    CHE("CHE", "Chemical Engineering"),
    /** Biotechnology */
    BT("BT", "Biotechnology"),
    /** Aerospace Engineering */
    AE("AE", "Aerospace Engineering");


    private final String code;
    private final String displayName;
    /**
     * Private constructor for enum constants.
     *
     * @param code        The short, machine-readable code for the department.
     * @param displayName The full, human-readable name of the department.
     */
    Departments(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    /**
     * Gets the short code for the department.
     *
     * @return The department's code (e.g., "CSE").
     */
    public String getCode() {
        return code;
    }
    /**
     * Gets the full display name of the department.
     *
     * @return The department's full name (e.g., "Computer Science & Engineering").
     */
    public String getDisplayName() {
        return displayName;
    }
    /**
     * Safely finds a department from a given code or name string.
     * <p>
     * This method performs a case-insensitive search against both the department's
     * short code (e.g., "CSE") and its enum constant name (e.g., {@code CSE}).
     *
     * @param code The string identifier to search for.
     * @return An {@code Optional<Departments>} containing the matched department,
     *         or an empty Optional if no match is found.
     */
    public static Optional<Departments> fromCodeOptional(String code) {
        return Arrays.stream(Departments.values())
                     .filter(d -> d.getCode().equalsIgnoreCase(code) || d.name().equalsIgnoreCase(code))
                     .findFirst();
    }

    /**
     * Returns the full, human-readable name of the department.
     *
     * @return The department's display name.
     */
    @Override
    public String toString() {
        return displayName;
    }
}

