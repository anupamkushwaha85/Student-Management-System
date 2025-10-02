package repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import constants.Departments;
import constants.Status;
import model.Student;

/**
 * Defines the contract for data persistence operations related to {@link Student} entities.
 * <p>
 * This interface abstracts the underlying data storage mechanism (e.g., in-memory, database, file)
 * and provides a standard set of CRUD (Create, Read, Update, Delete) operations.
 * Implementations are responsible for handling the actual data storage and retrieval logic.
 */
public interface StudentRepo {
    /**
     * Creates and persists a new student record based on the provided details.
     * The implementation is responsible for generating and assigning a unique ID to the new student.
     *
     * @param name       The student's full name.
     * @param email      The student's email address.
     * @param phone      The student's phone number.
     * @param dob        The student's date of birth.
     * @param address    The student's physical address.
     * @param department The student's academic department.
     * @param status     The student's initial enrollment status.
     * @return The newly created {@link Student} object, including its generated ID.
     */
    Student save(String name, String email, String phone, LocalDate dob, String address, Departments department, Status status);
    /**
     * Finds a single student by their unique identifier.
     *
     * @param studentId The unique ID of the student to find.
     * @return An {@link Optional} containing the {@link Student} if found, otherwise an empty Optional.
     */
    Optional<Student> findById(int studentId);
    /**
     * Updates an existing student's record in the data store.
     * The student to be updated is identified by the ID within the provided Student object.
     *
     * @param student The {@link Student} object containing the ID of the record to update
     *                and the new data.
     * @return An {@link Optional} containing the updated {@link Student} if the operation was successful,
     *         or an empty Optional if no student with the specified ID was found.
     */
    Optional<Student> update(Student student);
    /**
     * Deletes a student record from the data store using their unique identifier.
     *
     * @param studentId The unique ID of the student to be deleted.
     * @return {@code true} if a student was found and successfully deleted, {@code false} otherwise.
     */
    boolean deleteById(int studentId);
    /**
     * Retrieves all student records from the data store.
     *
     * @return A {@link List} of all {@link Student} objects. If no students exist,
     *         this method should return an empty list, never null.
     */
    List<Student> findAll();


}
