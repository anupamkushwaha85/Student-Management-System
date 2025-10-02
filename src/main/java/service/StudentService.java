package service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import constants.Departments;
import constants.Status;
import model.Student;
import repository.StudentRepo;
import utility.ValidationUtils;
/**
 * Provides business logic and orchestration for student management operations.
 * <p>
 * This service layer acts as a bridge between the presentation layer (e.g., {@code MainMenu})
 * and the data persistence layer ({@link StudentRepo}). It is responsible for validating
 * user input, transforming data, and coordinating calls to the repository.
 * It operates exclusively on the {@link StudentRepo} interface, ensuring it remains
 * decoupled from specific data storage implementations.
 */
public class StudentService {
	/** A private, final field to hold the repository */
    private final StudentRepo studentRepo;
    /**
     * Constructs a new StudentService with a repository dependency.
     * <p>
     * This uses constructor injection to receive a concrete implementation of the
     * {@link StudentRepo} interface, promoting loose coupling and testability.
     *
     * @param studentRepo The data repository implementation to be used for all
     *                    student persistence operations.
     */
    public StudentService(StudentRepo studentRepo) {
        this.studentRepo = studentRepo;
    }
    /**
     * Creates a new student after validating and converting raw input strings.
     * <p>
     * This method takes all inputs as strings, performs necessary conversions for dates
     * and enums, and then delegates the creation to the repository.
     *
     * @param name       The student's full name.
     * @param email      The student's email address.
     * @param phone      The student's phone number.
     * @param dobString  The student's date of birth as a string (e.g., "yyyy-MM-dd").
     * @param address    The student's physical address.
     * @param deptCode   The code for the student's department (e.g., "CSE").
     * @param statusCode The code for the student's status (e.g., "ACTIVE").
     * @return The newly created {@link Student} object, complete with its generated ID.
     * @throws IllegalArgumentException if the date format is invalid, or if the
     *                                  department or status codes are not found.
     */
    public Student createStudent(String name, String email, String phone, String dobString, String address, String deptCode, String statusCode) {
        // 1. Handle date conversion (already done)
        Optional<LocalDate> dobOptional = ValidationUtils.parseDob(dobString);
        if (dobOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid date of birth format.");
        }
        LocalDate validDob = dobOptional.get();
        // 2. Handle department conversion
        Optional<Departments> deptOptional = Departments.fromCodeOptional(deptCode);
        if (deptOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid department code.");
        }
        Departments validDept = deptOptional.get();
        // 3. Handle status conversion
        Optional<Status> statusOptional = Status.fromCode(statusCode);
        if (statusOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid status code.");
        }
        Status validStatus = statusOptional.get();
        // 4. Call the repository with clean, valid data
        return studentRepo.save(name, email, phone, validDob, address, validDept, validStatus);
    }
    /**
     * Updates the phone number of an existing student.
     *
     * @param studentId The ID of the student to update.
     * @param newPhone  The new phone number for the student.
     * @return An {@link Optional} containing the updated student, or an empty Optional if not found.
     */
    public Optional<Student> updateStudentPhone(int studentId, String newPhone) {
        return studentRepo.findById(studentId)
                .map(student -> student.withPhone(newPhone))
                .flatMap(studentRepo::update);
    }
    /**
     * Updates the name of an existing student.
     * <p>
     * This method follows a functional approach: it finds the student, creates an updated
     * immutable copy using {@link Student#withName(String)}, and then passes that new
     * object to the repository to be persisted.
     *
     * @param studentId The ID of the student to update.
     * @param newName   The new name for the student.
     * @return An {@link Optional} containing the updated student, or an empty Optional
     *         if no student with the given ID was found.
     */
    public Optional<Student> updateStudentName(int studentId, String newName) {
        return studentRepo.findById(studentId)
                .map(student -> student.withName(newName))
                .flatMap(studentRepo::update);
    }
    /**
     * Updates the email of an existing student.
     *
     * @param studentId The ID of the student to update.
     * @param newEmail  The new email for the student.
     * @return An {@link Optional} containing the updated student, or an empty Optional if not found.
     */
    public Optional<Student> updateStudentEmail(int studentId, String newEmail) {
        return studentRepo.findById(studentId)
                .map(student -> student.withEmail(newEmail))
                .flatMap(studentRepo::update);
    }
    /**
     * Updates the address of an existing student.
     *
     * @param studentId  The ID of the student to update.
     * @param newAddress The new address for the student.
     * @return An {@link Optional} containing the updated student, or an empty Optional if not found.
     */
    public Optional<Student> updateStudentAddress(int studentId, String newAddress) {
        return studentRepo.findById(studentId)
                .map(student -> student.withAddress(newAddress))
                .flatMap(studentRepo::update);
    }
    /**
     * Updates the department of an existing student from a department code string.
     *
     * @param studentId      The ID of the student to update.
     * @param departmentCode The code for the new department (e.g., "ECE").
     * @return An {@link Optional} containing the updated student, or an empty Optional
     *         if the student ID or department code is not found.
     */
    public Optional<Student> updateStudentDepartment(int studentId, String departmentCode) {
        // Step 1: Try to convert the user's string into a valid department.
        Optional<Departments> departmentOptional = Departments.fromCodeOptional(departmentCode);
        // Step 2: Chain the next operations. This code will only run if the department was found.
        // The .flatMap() method is used here to chain operations that return an Optional.
        return departmentOptional.flatMap(department -> 
            // Step 2a: Find the student
            studentRepo.findById(studentId)
                // Step 2b: Create the new version with the new department
                .map(student -> student.withDepartment(department))
                // Step 2c: Save the new version
                .flatMap(studentRepo::update)
        );
    }
    /**
     * Updates the status of an existing student from a status code string.
     *
     * @param studentId The ID of the student to update.
     * @param newStatus The code for the new status (e.g., "INACTIVE").
     * @return An {@link Optional} containing the updated student, or an empty Optional
     *         if the student ID or status code is not found.
     */
    public Optional<Student> updateStudentStatus(int studentId, String newStatus) {
    	
    	Optional<Status> statusOptional = Status.fromCode(newStatus);

        return statusOptional.flatMap(status -> studentRepo.findById(studentId)
                .map(student -> student.withStatus(status))
                .flatMap(studentRepo::update));
    }
    /**
     * Deletes a student from the system by their ID.
     * <p>
     * This method delegates the call directly to the student repository.
     *
     * @param studentId The ID of the student to delete.
     * @return {@code true} if a student was found and deleted, {@code false} otherwise.
     */
    public boolean deleteStudentById(int studentId) {
        return this.studentRepo.deleteById(studentId);
    }
    /**
     * Finds a student by their unique ID.
     * <p>
     * This method delegates the call directly to the student repository.
     *
     * @param studentId The ID of the student to find.
     * @return An {@link Optional} containing the student if found, or an empty Optional otherwise.
     */
    public Optional<Student> findStudentById(int studentId) {
        return this.studentRepo.findById(studentId);
    }
    /**
     * Retrieves a list of all students by delegating directly to the repository.
     *
     * @return A {@link List} containing all students in the system. Returns an empty
     *         list if no students are present.
     */
    public List<Student> findAllStudents() {
        // The service layer calls the corresponding method on the repository.
        return this.studentRepo.findAll();
    }

}
