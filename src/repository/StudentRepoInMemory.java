package repository;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import constants.Departments;
import constants.Status;
import model.Student;
import storage.JSONStorage;
/**
 * An in-memory implementation of the {@link StudentRepo} interface that provides
 * persistence by saving data to a JSON file.
 * <p>
 * This repository uses a {@link Map} for fast, O(1) lookups of students by their ID.
 * It is responsible for loading initial data from a JSON file upon startup and
 * persisting the in-memory state back to the file after any data modification (create, update, delete).
 * This class is designed to be thread-safe for ID generation using {@link AtomicInteger}.
 */
public class StudentRepoInMemory implements StudentRepo {

	    private final Map<Integer, Student> students ;
	    private final AtomicInteger idCounter;
	    private final JSONStorage jsonStorage;
    /**
     * Initializes the in-memory repository with data persistence.
     * <p>
     * This constructor loads any existing students from a JSON file via the
     * {@link JSONStorage} utility, populates the internal map, and critically, sets the
     * ID counter to the highest existing ID to prevent future ID conflicts.
     *
     * @param jsonStorage An instance of the utility class responsible for
     *                    reading from and writing to the JSON file.
     */
	    public StudentRepoInMemory(JSONStorage jsonStorage) {
            // 1. Initialize the necessary fields for the repository.
            this.jsonStorage = jsonStorage;
            this.idCounter = new AtomicInteger();

            // 2. Load the initial list of students from the JSON file.
            List<Student> initialStudents = jsonStorage.loadStudents();

            // 3. Populate the internal HashMap for fast, key-based access.
            // This uses a stream for a modern and efficient conversion from List to Map.
            this.students = initialStudents.stream()
                    .collect(Collectors.toMap(Student::id, student -> student));

            // 4. Find the highest ID among the loaded students. This is the crucial
            //    step to ensure the ID counter starts at the correct position.
            int maxId = initialStudents.stream()
                    .mapToInt(Student::id)  // Create a stream of integer IDs
                    .max()                     // Find the largest integer in the stream
                    .orElse(100);              // If the list was empty, default to 100.
            // This ensures the first new ID will be 101.

            // 5. Set the counter's starting point to the highest ID found.
            this.idCounter.set(maxId);
        }
    /**
     * A private helper method that persists the current state of the in-memory
     * student map to the JSON file.
     * <p>
     * It includes a null check to prevent {@link NullPointerException} during
     * unit tests that are initialized without a {@link JSONStorage} instance.
     */
	    private void persistData() {
	        // Only attempt to save the data if jsonStorage is not null.
	        // This allows our unit tests (which use a null jsonStorage) to run without error.
	        if (this.jsonStorage != null) {
	            jsonStorage.saveStudents(new ArrayList<>(students.values()));
	        }
	    }
    /**
     * {@inheritDoc}
     * <p>
     * This implementation generates a new unique ID, creates a new {@link Student} object,
     * adds it to the in-memory map, and then persists the updated map to the JSON file.
     */
		@Override
		public Student save(String name, String email, String phone, LocalDate dob, String address,
				Departments department, Status status) {
			// Get the next available ID
			int newId = this.idCounter.incrementAndGet(); // If counter is 105, this returns 106.
			 
			//creates the new student object 
			Student newStudent = new Student(newId, name, email, phone, dob, address, department, status);
			students.put(newId, newStudent );
			persistData(); // **Save the new state to the file**
			return newStudent;
		}
    /**
     * {@inheritDoc}
     * <p>
     * This implementation performs a fast lookup from the internal {@link Map}.
     */
		@Override
		public Optional<Student> findById(int studentId) {
			return Optional.ofNullable(students.get(studentId));
		}
    /**
     * {@inheritDoc}
     * <p>
     * This implementation replaces the existing student entry in the map with the new
     * student object and persists the changes to the JSON file.
     */
		@Override
		public Optional<Student> update(Student student) {
		    // First, check if a student with this ID exists.
		    if (students.containsKey(student.id())) {
		        // If yes, replace the old entry with the new student object.
		        students.put(student.id(), student);
		        persistData(); // **Save the new state to the file**
		        // Return an Optional containing the updated student.
		        return Optional.of(student);
		    } else {
		        // If no student with that ID was found, return an empty Optional.
		        return Optional.empty();
		    }
		}
    /**
     * {@inheritDoc}
     * <p>
     * This implementation removes the student from the in-memory map and persists
     * the changes to the JSON file.
     */
		@Override
		public boolean deleteById(int id) {
		    // .remove() returns the object that was removed, or null if the key didn't exist.
		    if (students.remove(id) != null) {
		        persistData(); // **Save the new state to the file**
		        return true;
		    }
		    return false;
		}
    /**
     * {@inheritDoc}
     * <p>
     * This implementation returns a new {@link ArrayList} containing all student
     * objects currently in the repository.
     */
		@Override
		public List<Student> findAll() {
			// The .values() method returns a Collection of all Student objects.
			// We pass this collection directly to the ArrayList constructor
			// to create and return a new list containing all students.
			return new ArrayList<>(students.values());
		}
		
}
