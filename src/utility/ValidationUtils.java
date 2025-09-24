package utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * A utility class for validating and normalizing common user inputs.
 * <p>
 * This class provides a collection of static methods for tasks such as validating
 * emails, phone numbers, names, and dates. It also includes methods for normalizing
 * data into a standard format. As a utility class, it is not meant to be instantiated.
 *
 * @author Anupam Kushwaha
 */
public class ValidationUtils {
    /**
     * A regular expression for validating standard email addresses.
     */
    private static final Pattern EMAIL_PATTERN = 
    		Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    /**
     * A regular expression for validating names, supporting international characters.
     */
    private static final Pattern NAME_PATTERN =
    	    Pattern.compile("^[\\p{L} .'-]{2,50}$");
    /**
     * The standard formatter for parsing dates in ISO_LOCAL_DATE format (e.g., "2025-09-24").
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ValidationUtils() { }
    /**
     * Checks if the given email string is syntactically valid.
     *
     * @param email The email string to validate.
     * @return {@code true} if the email is not null and matches the standard email pattern,
     *         {@code false} otherwise.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    /**
     * Checks if the given phone number string is valid.
     * <p>
     * The phone number is first normalized using {@link #normalizePhone(String)}. The normalized
     * number is considered valid if it contains an optional '+' prefix followed by 7 to 15 digits.
     *
     * @param phone The phone string to validate.
     * @return {@code true} if the phone number is valid, {@code false} otherwise.
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String normalized = normalizePhone(phone);
        return normalized.matches("^\\+?[0-9]{7,15}$");
    }
    /**
     * Checks if the given name string is valid.
     * <p>
     * A valid name can contain international letters, spaces, periods, apostrophes,
     * and hyphens, and must be between 2 and 50 characters long.
     *
     * @param name The name string to validate.
     * @return {@code true} if the name is not null and matches the pattern, {@code false} otherwise.
     */
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }
    /**
     * Parses a date string into a {@link LocalDate} object, wrapped in an {@link Optional}.
     * <p>
     * This method provides a safe way to parse dates without throwing an exception.
     *
     * @param dob The date string to parse (expected format: 'yyyy-MM-dd').
     * @return An {@code Optional<LocalDate>} containing the parsed date if successful,
     *         or an empty Optional if parsing fails.
     */
    public static Optional<LocalDate> parseDob(String dob) {
        try {
            return Optional.of(LocalDate.parse(dob, DATE_FORMATTER));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }
    /**
     * Normalizes a phone number string by removing all whitespace, dashes, and parentheses.
     *
     * @param phone The phone string to normalize.
     * @return The normalized string, or {@code null} if the input was null.
     */
    public static String normalizePhone(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("[\\s\\-()]", "");
    }
    /**
     * Normalizes an email address by trimming whitespace and converting it to lowercase.
     *
     * @param email The email string to normalize.
     * @return The normalized string, or {@code null} if the input was null.
     */
    public static String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }


}

