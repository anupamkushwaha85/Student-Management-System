package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import constants.Departments;
import constants.Status;
import model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.StudentRepo;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

/**
 * Test suite for the {@link StudentService}.
 *
 * <p>This class uses JUnit 5 and Mockito to test the business logic within the
 * StudentService. The {@link StudentRepo} dependency is mocked to isolate the
 * service layer and ensure that tests are focused solely on the service's behavior,
 * such as data validation, orchestration of repository calls, and handling of different
 * outcomes (e.g., success, not found, invalid input).
 *
 * @see StudentService
 * @see StudentRepo
 * @see MockitoExtension
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    /**
     * Mocks the {@link StudentRepo} to simulate database interactions without
     * needing a live database connection. This allows for predictable and
     * controlled testing of the service layer.
     */
    @Mock
    private StudentRepo studentRepo;
    /**
     * Creates an instance of {@link StudentService} and injects the mocked
     * dependencies (e.g., {@code studentRepo}) into it.
     */
    @InjectMocks
    private StudentService studentService;
    /**
     * Tests the successful creation of a student.
     *
     * <p><b>Scenario:</b> A new student is created with valid data.
     * <p><b>Expected Outcome:</b> The service correctly calls the repository's {@code save} method
     * and returns a {@link Student} object with a non-null ID, confirming that the
     * creation process was initiated successfully.
     */
    @Test
    void testCreateStudent_Success() {
        // ARRANGE
        when(studentRepo.save(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new Student(1, "John Doe", "john.doe@example.com", "1234567890",
                        LocalDate.parse("2005-05-10"), "123 Main St", Departments.CSE, Status.ACTIVE));

        // ACT
        Student createdStudent = studentService.createStudent(
                "John Doe", "john.doe@example.com", "1234567890", "2005-05-10",
                "123 Main St", "CSE", "ACTIVE"
        );

        // ASSERT
        assertNotNull(createdStudent);
        assertEquals(1, createdStudent.getId());
    }
    /**
     * Tests student creation with an invalid email address.
     *
     * <p><b>Scenario:</b> An attempt is made to create a student with a malformed email.
     * <p><b>Expected Outcome:</b> The service should throw an {@link IllegalArgumentException}
     * before any interaction with the repository's {@code save} method occurs. This
     * verifies that input validation is working correctly.
     */
    @Test
    void testCreateStudent_InvalidEmail_ThrowsException() {
        // ARRANGE & ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            studentService.createStudent(
                    "Jane Doe", "invalid-email", "9876543210", "2006-01-15",
                    "456 Oak Ave", "MECH", "ACTIVE"
            );
        });

        verify(studentRepo, never()).save(any(), any(), any(), any(), any(), any(), any());
    }
    /**
     * Tests finding a student by an ID that exists.
     *
     * <p><b>Scenario:</b> A request is made for a student with a valid and existing ID.
     * <p><b>Expected Outcome:</b> The service should return an {@code Optional} containing the
     * correct {@link Student} object, as returned by the repository.
     */
    @Test
    void testGetStudentById_Found() {
        // ARRANGE
        Student student = new Student(1, "John Doe", "john.doe@example.com", "1234567890",
                LocalDate.parse("2005-05-10"), "123 Main St", Departments.CSE, Status.ACTIVE);
        when(studentRepo.findById(1)).thenReturn(Optional.of(student));

        // ACT
        Optional<Student> foundStudent = studentService.findStudentById(1);

        // ASSERT
        assertTrue(foundStudent.isPresent());
        assertEquals(1, foundStudent.get().getId());
        assertEquals("John Doe", foundStudent.get().getName());
    }
    /**
     * Tests finding a student by an ID that does not exist.
     *
     * <p><b>Scenario:</b> A request is made for a student with an ID that is not in the repository.
     * <p><b>Expected Outcome:</b> The service should return an empty {@code Optional},
     * indicating that no student was found.
     */
    @Test
    void testGetStudentById_NotFound() {
        // ARRANGE
        when(studentRepo.findById(anyInt())).thenReturn(Optional.empty());

        // ACT
        Optional<Student> foundStudent = studentService.findStudentById(999);

        // ASSERT
        assertFalse(foundStudent.isPresent());
    }
    /**
     * Tests the successful deletion of a student.
     *
     * <p><b>Scenario:</b> A request is made to delete a student with an existing ID.
     * <p><b>Expected Outcome:</b> The service should call the repository's {@code deleteById}
     * method and return {@code true} to indicate the deletion was successful.
     */
    @Test
    void testDeleteStudent_Success() {
        // ARRANGE
        when(studentRepo.deleteById(1)).thenReturn(true);

        // ACT
        boolean result = studentService.deleteStudentById(1);

        // ASSERT
        assertTrue(result);
        verify(studentRepo, times(1)).deleteById(1);
    }
    /**
     * Tests the deletion of a student that does not exist.
     *
     * <p><b>Scenario:</b> A request is made to delete a student with a non-existent ID.
     * <p><b>Expected Outcome:</b> The service should return {@code false}, indicating that
     * no student was found with the given ID to delete.
     */
    @Test
    void testDeleteStudent_NotFound() {
        // ARRANGE
        when(studentRepo.deleteById(999)).thenReturn(false);

        // ACT
        boolean result = studentService.deleteStudentById(999);

        // ASSERT
        assertFalse(result);
    }
    /**
     * Tests fetching all students from the repository.
     *
     * <p><b>Scenario:</b> A request is made to retrieve all student records.
     * <p><b>Expected Outcome:</b> The service should return a complete list of all {@link Student}
     * objects provided by the repository's {@code findAll} method. The test verifies
     * the list is not null and contains the expected number of items.
     */
    @Test
    void testGetAllStudents() {
        // ARRANGE
        List<Student> studentList = List.of(
                new Student(1, "John Doe", "john.doe@example.com", "1234567890",
                        LocalDate.parse("2005-05-10"), "123 Main St", Departments.CSE, Status.ACTIVE),
                new Student(2, "Jane Smith", "jane.smith@example.com", "0987654321",
                        LocalDate.parse("2004-11-20"), "456 Oak Ave", Departments.ECE, Status.INACTIVE)
        );
        when(studentRepo.findAll()).thenReturn(studentList);

        // ACT
        List<Student> result = studentService.findAllStudents();

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size()); // Check that the list contains two students.
        assertEquals("Jane Smith", result.get(1).getName());
    }
    /**
     * Tests the successful update of a student's name.
     *
     * <p><b>Scenario:</b> A valid new name is provided for an existing student.
     * <p><b>Expected Outcome:</b> The service first fetches the student, then calls the
     * repository's {@code update} method with the modified student object. It returns
     * an {@code Optional} containing the updated student.
     */
    @Test
    void testUpdateName_Success() {
        // ARRANGE
        Student existingStudent = new Student(1, "John Doe", "john.doe@example.com", "1234567890",
                LocalDate.parse("2005-05-10"), "123 Main St", Departments.CSE, Status.ACTIVE);

        Student updatedStudent = new Student(1, "Johnathan Doe", "john.doe@example.com", "1234567890",
                LocalDate.parse("2005-05-10"), "123 Main St", Departments.CSE, Status.ACTIVE);

        when(studentRepo.findById(1)).thenReturn(Optional.of(existingStudent));
        when(studentRepo.update(any(Student.class))).thenReturn(Optional.of(updatedStudent));

        // ACT
        Optional<Student> result = studentService.updateStudentName(1, "Johnathan Doe");

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals("Johnathan Doe", result.get().getName());
        verify(studentRepo).findById(1);
        verify(studentRepo).update(any(Student.class));
    }
    /**
     * Tests updating a student who cannot be found.
     *
     * <p><b>Scenario:</b> An attempt is made to update the name of a student with a
     * non-existent ID.
     * <p><b>Expected Outcome:</b> The service should return an empty {@code Optional}
     * after failing to find the student. Crucially, the repository's {@code update}
     * method should never be called.
     */
    @Test
    void testUpdateName_NotFound() {
        // ARRANGE
        when(studentRepo.findById(999)).thenReturn(Optional.empty());

        // ACT
        Optional<Student> result = studentService.updateStudentName(999, "Any Name");

        // ASSERT
        assertFalse(result.isPresent());
        verify(studentRepo, never()).update(any(Student.class));
    }
    /**
     * Tests updating a student's name with invalid input.
     *
     * <p><b>Scenario:</b> An attempt is made to update an existing student's name with a
     * value that violates validation rules (e.g., contains special characters).
     * <p><b>Expected Outcome:</b> The service should throw an {@link IllegalArgumentException}
     * after finding the student but before calling the repository's {@code update} method.
     * This confirms that validation logic in the domain model (or service) is triggered.
     */
    @Test
    void testUpdateName_InvalidName_ThrowsException() {
        // ARRANGE
        Student existingStudent = new Student(1, "John Doe", "john.doe@example.com", "1234567890",
                LocalDate.parse("2005-05-10"), "123 Main St", Departments.CSE, Status.ACTIVE);

        when(studentRepo.findById(1)).thenReturn(Optional.of(existingStudent));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            studentService.updateStudentName(1, "Invalid@Name");
        });

        verify(studentRepo, never()).update(any(Student.class));
    }






}
