package ui;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;


import model.Student;
import service.StudentService;

/**
 * The main user interface (UI) controller for the Student Management System.
 * <p>
 * This class is responsible for displaying the main menu, handling user input,
 * and orchestrating the application's overall workflow. It contains the primary
 * {@link #run()} method that starts the application loop.
 * It depends on the {@link StudentService} to perform all business logic operations.
 */
public class MainMenu {
    /**
     * A reference to the service layer, which provides the business logic for
     * all student-related operations. This field is final and must be provided
     * via the constructor.
     */
    private final StudentService studentService;
    /**
     * The scanner instance used for reading all user input from the console.
     * A single instance is created and reused throughout the application's
     * lifecycle to ensure consistency and proper resource management.
     */
    private final Scanner scanner;
    /**
     * Constructs a new MainMenu instance.
     * <p>
     * This constructor initializes the menu with a required {@link StudentService} dependency
     * and creates a new {@link Scanner} instance to read input from the console.
     *
     * @param studentService The service layer that provides the business logic for
     *                       student operations. It must not be null.
     */
    public MainMenu(StudentService studentService) {
        this.studentService = studentService;
        this.scanner = new Scanner(System.in);
    }
    /**
     * Starts and manages the main application loop for the user interface.
     * <p>
     * This method is the primary entry point for the console UI. It continuously
     * displays the main menu, prompts the user for input, and dispatches commands
     * to the appropriate handler methods based on the user's choice.
     * <p>
     * The loop includes robust input validation to handle non-numeric entries and
     * provides feedback for invalid menu selections. The loop terminates only when
     * the user explicitly chooses the "Exit" option, at which point it closes
     * system resources and displays a final greeting.
     */
    public void run() {
        boolean exit = false;
        // It continues until the user chooses to exit.
        while (!exit) {
            // 1. Print all the menu options to the console.
            System.out.println("\n(:=== Student ^_^anagement System Menu ===:)");
            System.out.println("1. Add a New Student");
            System.out.println("2. Find a Student by ID");
            System.out.println("3. Update a Student's Information");
            System.out.println("4. Delete a Student");
            System.out.println("5. Display All Students");
            System.out.println("0. Exit Application");

            int choice;
            // 2. Read the user's choice safely.
            try {
                System.out.print("\nEnter your choice: ");
                // read the whole line and then parse it to an integer.
                // This is safer and avoids issues with the scanner's buffer.
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                // This block runs if the user enters text that is not a number.
                System.err.println("Error: Invalid input. Please enter a number.");
                continue; // Skip the rest of the loop and show the menu again.
            }

            // A switch statement to act on the user's choice.
            switch (choice) {
                case 1 -> addStudentUI();

                case 2 -> findStudentByIdUI();

                case 3 -> updateStudentUI();

                case 4 -> deleteStudentUI();

                case 5 -> displayAllStudentsUI();

                case 0 -> {
                    exit = true; // This will cause the while loop to terminate.
                    System.out.println("\nThank you for using the Student Management System. " + getExitGreeting());
                }

                default ->
                    // This runs if the user enters a number that is not a valid menu option.
                        System.err.println("Error: Invalid choice. Please select an option from the menu.");
            }
        }
        scanner.close(); // Close the scanner resource when the application is finished.
    }
    /**
     * Manages the user interface flow for adding a new student to the system.
     * <p>
     * This method guides the user through the process of entering all required details
     * for a new student, including name, email, date of birth, and department. Upon
     * collecting the raw string inputs, it invokes the {@link StudentService#createStudent}
     * method to perform validation, conversion, and persistence.
     * <p>
     * It includes robust error handling:
     * <ul>
     *     <li>If the service layer throws an {@link IllegalArgumentException} due to invalid data,
     *     a specific error message is displayed to the user.</li>
     *     <li>Any other unexpected errors are caught and a generic failure message is shown.</li>
     * </ul>
     * On success, it confirms the creation and displays the new student's system-generated ID.
     */
    private void addStudentUI() {
        System.out.println("\n--- Add New Student ---");

        try {
            System.out.print("Enter Student Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Student Email: ");
            String email = scanner.nextLine();

            System.out.print("Enter Student Phone Number: ");
            String phone = scanner.nextLine();

            System.out.print("Enter Student Date of Birth (YYYY-MM-DD): ");
            String dob = scanner.nextLine();

            System.out.print("Enter Student Address: ");
            String address = scanner.nextLine();

            System.out.print("Enter Department (e.g., COMPUTER_SCIENCE, MECHANICAL): ");
            String departmentStr = scanner.nextLine();

            System.out.print("Enter Status (e.g., ACTIVE, INACTIVE): ");
            String statusStr = scanner.nextLine();

            Student newStudent = studentService.createStudent(name, email, phone, dob, address, departmentStr, statusStr);

            System.out.println("\nSUCCESS: Student added successfully!");
            System.out.println("Assigned Student ID: " + newStudent.getId());
        } catch (IllegalArgumentException e) {
            System.err.println("\nERROR: Could not add student. " + e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected errors
            System.err.println("\nAn unexpected error occurred. Please try again.");
        }
    }
    /**
     * Manages the user interface flow for finding and displaying a single student by their ID.
     * <p>
     * This method prompts the user to enter a student ID, validates that the input is a
     * valid number, and then calls the {@link StudentService} to search for the student.
     * It uses the {@code Optional.ifPresentOrElse} method to elegantly handle both possible
     * outcomes:
     * <ul>
     *     <li>If the student is found, their details are printed to the console.</li>
     *     <li>If the student is not found, a user-friendly error message is displayed.</li>
     * </ul>
     */
    private void findStudentByIdUI() {
        System.out.println("\n--- Find a Student by ID ---");

        int studentId;
        try {
            System.out.print("Enter the Student ID to search for: ");
            studentId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid ID. Please enter a valid number.");
            return; // Exit the method
        }
        Optional<Student> studentOptional = studentService.findStudentById(studentId);
        studentOptional.ifPresentOrElse(
                student -> {
                    System.out.println("\n--- Student Found ---");
                    System.out.println(student);
                },
                () -> System.err.println("\nError: No student found with ID " + studentId)
        );
    }
    /**
     * Initiates the user interface flow for updating a student's information.
     * <p>
     * This method first prompts the user to enter the ID of the student they wish to modify.
     * It validates the input and then uses the {@link StudentService} to find the corresponding
     * student. If a student is found, this method delegates control to the
     * {@link #showUpdateSubMenu(Student)} method to handle the detailed update process.
     * If no student is found, it displays an error message to the user.
     */
    private void updateStudentUI() {
        System.out.println("\n--- Update a Student's Information ---");

        System.out.print("Enter the ID of the student you want to update: ");
        int studentId;
        try {
            studentId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid ID. Please enter a valid number.");
            return;
        }
        Optional<Student> studentOptional = studentService.findStudentById(studentId);
        studentOptional.ifPresentOrElse(
                this::showUpdateSubMenu, // A new method to handle the sub-menu
                () -> System.err.println("Error: No student found with ID " + studentId)
        );
    }
    /**
     * Displays a sub-menu for updating the fields of a specific student.
     * <p>
     * This method creates a dedicated, interactive loop for a single student. It continuously
     * displays the student's current details and presents a menu of fields that can be
     * updated (e.g., Name, Email, Address). The method handles user input for each
     * field, calls the appropriate update method on the {@link StudentService}, and provides
     * immediate feedback on the success or failure of the operation.
     * <p>
     * Critically, if an update is successful, the local {@code student} object is replaced
     * with the new, updated object returned from the service. This ensures that the menu
     * header immediately reflects the changes (e.g., a new name) on the next loop iteration.
     * The loop continues until the user chooses to go back to the main menu.
     *
     * @param student The student object to be updated. This object's state is updated
     *                internally as the user makes changes.
     */
    private void showUpdateSubMenu(Student student) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Updating Student: " + student.getName() + " (ID: " + student.getId() + ") ---");
            System.out.println("Which field would you like to update?");
            System.out.println("1. Name");
            System.out.println("2. Email");
            System.out.println("3. Phone Number");
            System.out.println("4. Address");
            System.out.println("5. Department");
            System.out.println("6. Status");
            System.out.println("0. Back to Main Menu");

            System.out.print("Enter your choice: ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.err.println("Error: Invalid choice. Please enter a number.");
                continue;
            }
            switch (choice) {
                // Handle Name Update
                case 1 -> {
                    System.out.print("Enter the new name: ");
                    String newName = scanner.nextLine();
                    try {
                        Optional<Student> updated = studentService.updateStudentName(student.getId(), newName);
                        if (updated.isPresent()) {
                            student = updated.get(); // Update local student object
                            System.out.println("\nSUCCESS: Name updated successfully.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }
                // Handle Email Update
                case 2 -> {
                    System.out.print("Enter the new email: ");
                    String newEmail = scanner.nextLine();
                    try {
                        Optional<Student> updated = studentService.updateStudentEmail(student.getId(), newEmail);
                        if (updated.isPresent()) {
                            student = updated.get();
                            System.out.println("\nSUCCESS: Email updated successfully.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }
                // Handle Phone Update
                case 3 -> {
                    System.out.print("Enter the new phone number: ");
                    String newPhone = scanner.nextLine();
                    try {
                        Optional<Student> updated = studentService.updateStudentPhone(student.getId(), newPhone);
                        if (updated.isPresent()) {
                            student = updated.get();
                            System.out.println("\nSUCCESS: Phone number updated successfully.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }
                // Handle Address Update
                case 4 -> {
                    System.out.print("Enter the new address: ");
                    String newAddress = scanner.nextLine();
                    Optional<Student> updated = studentService.updateStudentAddress(student.getId(), newAddress);
                    if (updated.isPresent()) {
                        student = updated.get();
                        System.out.println("\nSUCCESS: Address updated successfully.");
                    }
                }
                // Handle Department Update
                case 5 -> {
                    System.out.print("Enter the new department: ");
                    String newDepartment = scanner.nextLine();
                    try {
                        Optional<Student> updated = studentService.updateStudentDepartment(student.getId(), newDepartment);
                        if (updated.isPresent()) {
                            student = updated.get();
                            System.out.println("\nSUCCESS: Department updated successfully.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }
                // Handle Status Update
                case 6 -> {
                    System.out.print("Enter the new status: ");
                    String newStatus = scanner.nextLine();
                    try {
                        Optional<Student> updated = studentService.updateStudentStatus(student.getId(), newStatus);
                        if (updated.isPresent()) {
                            student = updated.get();
                            System.out.println("\nSUCCESS: Status updated successfully.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }
                // Exit to Main Menu
                case 0 -> back = true;
                default -> System.err.println("Error: Invalid choice. Please try again.");
            }
        }
    }
    /**
     * Manages the user interface flow for deleting a student.
     * <p>
     * This method prompts the user to enter the ID of the student they wish to delete.
     * It includes input validation to ensure the ID is a valid number. After receiving a
     * valid ID, it delegates the deletion logic to the {@link StudentService}.
     * Finally, it provides clear feedback to the user, confirming whether the deletion
     * was successful or if an error occurred (e.g., the student ID was not found).
     */
    private void deleteStudentUI() {
        System.out.println("\n--- Delete a Student ---");
        int studentId;
        try {
            System.out.print("Enter the ID of the student to delete: ");
            studentId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid ID. Please enter a valid number.");
            return; // Exit the method if the input is not a number.
        }
        boolean wasDeleted = studentService.deleteStudentById(studentId);
        if (wasDeleted) {
            System.out.println("\nSUCCESS: Student with ID " + studentId + " was deleted successfully.");
        } else {
            System.err.println("\nERROR: Could not delete student. No student found with ID " + studentId + ".");
        }
    }
    /**
     * Fetches all students from the service layer and prints them to the console.
     * <p>
     * This method acts as a UI function to display a complete list of students.
     * It first retrieves the list from the {@link StudentService}. If the list is empty,
     * it prints a user-friendly message. Otherwise, it iterates through the list
     * and prints the details of each student, relying on the {@link Student#toString()}
     * method for formatting.
     */
    private void displayAllStudentsUI() {
        System.out.println("\n--- Displaying All Students ---");
        List<Student> students = studentService.findAllStudents();
        if (students.isEmpty()) {
            System.out.println("There are no students in the system to display.");
            return; // Exit the method early.
        }
        System.out.println("Total students: " + students.size());
        for (Student student : students) {
            System.out.println(student);
            System.out.println("------------------------------------------:)");
        }
    }
    /**
     * Generates a time-sensitive greeting.
     * <p>
     * This method determines the current hour of the day and returns an appropriate
     * greeting string ("Good Morning!", "Have a great day!", or "Good Night!") based
     * on predefined time slots.
     *
     * @return A greeting string appropriate for the current time of day.
     */
    private String getExitGreeting() {
        // Get the current hour of the day (0-23)
        int hour = java.time.LocalTime.now().getHour();
        if (hour >= 4 && hour < 12) { // 4:00 AM to 11:59 AM
            return "Good Morning!";
        } else if (hour >= 12 && hour < 20) { // 12:00 PM (noon) to 7:59 PM (19:59)
            return "Have a great day!";
        } else { // All other times: 8 PM (20:00) through 3:59 AM
            return "Good Night!";
        }
    }

}
