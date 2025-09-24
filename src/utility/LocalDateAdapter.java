package utility;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A Gson {@link TypeAdapter} for serializing and deserializing Java 8's {@link LocalDate}.
 * <p>
 * This adapter is required because Gson's default behavior for {@code LocalDate} is to
 * serialize it as a complex JSON object (e.g., {"year": 2025, "month": 9, "day": 24}).
 * This custom adapter ensures that {@code LocalDate} objects are consistently written to and
 * read from JSON as a simple string in the standard "yyyy-MM-dd" format.
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    /**
     * The formatter used for converting dates to and from the ISO_LOCAL_DATE format ("yyyy-MM-dd").
     */
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    /**
     * Serializes a {@link LocalDate} object into a string and writes it to the JSON stream.
     * <p>
     * If the provided {@code LocalDate} value is null, it writes a JSON null. Otherwise,
     * it formats the date into a "yyyy-MM-dd" string.
     *
     * @param out   The {@link JsonWriter} to write the JSON output to. Must not be null.
     * @param value The {@link LocalDate} object to be written. May be null.
     * @throws IOException if an error occurs while writing to the stream.
     */
    @Override
    public void write(final JsonWriter out, final LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }
    /**
     * Reads a date string from the JSON stream and deserializes it into a {@link LocalDate} object.
     * <p>
     * If the JSON token is null, this method returns null. Otherwise, it reads the
     * string and parses it using the "yyyy-MM-dd" format.
     *
     * @param in The {@link JsonReader} to read the JSON input from. Must not be null.
     * @return The parsed {@link LocalDate} object, or {@code null} if the JSON value was null.
     * @throws IOException if an error occurs while reading from the stream or if the
     *                     date string is in an invalid format.
     */
    @Override
    public LocalDate read(final JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            return LocalDate.parse(in.nextString(), formatter);
        }
    }
}
