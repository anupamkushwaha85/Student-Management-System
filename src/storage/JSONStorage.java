package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import model.Student; 
import utility.LocalDateAdapter;
import utility.StudentTypeAdapter;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class responsible for serializing and deserializing student data
 * to and from a JSON file.
 * <p>
 * This class abstracts all file I/O and JSON conversion logic using the Gson library.
 * It is configured with custom type adapters to correctly handle complex objects like
 * {@link LocalDate} and the immutable {@link Student} class, ensuring that the
 * rest of the application does not need to be aware of the persistence mechanism.
 */
public class JSONStorage {

    /**
     * The constant path for the JSON file where student data is stored.
     */
	 private static final String FILE_PATH = "students.json";
    /**
     * The Gson instance configured for handling student data serialization.
     */
     private final Gson gson;
    /**
     * Constructs and configures the JSONStorage utility.
     * <p>
     * This constructor initializes a {@link Gson} instance with custom type adapters
     * necessary for correctly processing the {@link Student} objects and their fields.
     * It registers adapters for {@link LocalDate} and the {@link Student} class itself.
     * Pretty printing is enabled to make the output JSON file human-readable.
     */
    public JSONStorage() {
        this.gson = new GsonBuilder()
                // The LocalDateAdapter is good to have for general use
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                // Tell Gson to use StudentTypeAdapter whenever it sees a Student class.
                .registerTypeAdapter(Student.class, new StudentTypeAdapter())
                .setPrettyPrinting()
                .create();
    }
    /**
     * Serializes a list of {@link Student} objects and writes it to the JSON file.
     * <p>
     * This method overwrites the existing file completely with the new data.
     * It uses a try-with-resources statement to ensure the file writer is closed properly.
     *
     * @param students The list of students to be saved.
     */
    public void saveStudents(List<Student> students) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            // 1. Use gson to convert the list directly to the file via the writer.
            gson.toJson(students, writer);
            System.out.println("Data successfully saved to " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error: Failed to save data to file '" + FILE_PATH + "'.");
            e.printStackTrace(); // Print the full error for debugging.
        }
    }
    /**
     * Deserializes the JSON file into a list of {@link Student} objects.
     * <p>
     * This method safely handles cases where the file doesn't exist (e.g., first run),
     * is empty, or contains invalid JSON. In any error condition, it returns an empty list.
     *
     * @return A {@code List<Student>} containing the loaded students. This method
     *         <b>never returns null</b>; it returns an empty list on failure or if no data exists.
     */
    public List<Student> loadStudents() {
        Path path = Paths.get(FILE_PATH);
        if (Files.notExists(path)) {
            System.out.println("Data file not found. Starting with an empty student list.");
            return new ArrayList<>();
        }
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            // Define the specific generic type for Gson: a List of Students.
            Type listType = new TypeToken<ArrayList<Student>>() {}.getType();
            List<Student> students = gson.fromJson(reader, listType);
            // Safety check: If the file is empty or contains "null", gson returns null.
            // We convert this to an empty list to prevent NullPointerExceptions.
            if (students == null) {
                return new ArrayList<>();
            }
            return students;
        } catch (IOException e) {
            System.err.println("Error: Failed to load data from file '" + FILE_PATH + "'.");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}

