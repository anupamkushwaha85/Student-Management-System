package mainApp;

import repository.StudentRepo;
import repository.StudentRepoJdbc;
import service.StudentService;
import ui.MainMenu;
import utility.DatabaseConnector;

/**
 * The main entry point for the Student Management System application.
 * <p>
 * This class is responsible for initializing and wiring together all the major
 * components of the application, including the repository (StudentRepo),
 * the business logic layer (StudentService),
 * and the user interface (MainMenu). Once all components are initialized,
 * it starts the application's main menu loop.
 *
 * @author Anupam Kushwaha
 */
public class StudentManagementMain {

    /**
     * The main method that launches the application.
     * <p>
     * It follows these steps to start the system:
     * <ol>
     *     <li>Injects the storage into the {@link StudentRepoJdbc} to create the data repository.</li>
     *     <li>Injects the repository into the {@link StudentService} to handle business logic.</li>
     *     <li>Injects the service into the {@link MainMenu} to manage user interaction.</li>
     *     <li>Calls the {@code run()} method on the main menu to start the application loop.</li>
     * </ol>
     *
     * @param ignoredArgs Command-line arguments (not used in this application).
     */
        static void main(String[] ignoredArgs) {

            // Register shutdown hook to close HikariCP pool gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(DatabaseConnector::closePool));

            //Directly instantiate JDBC repository.
            StudentRepo studentRepo = new StudentRepoJdbc();

	        // Create the service layer, giving it the persistent repository.
	        // The service layer doesn't know or care that the data is coming
	        StudentService studentService = new StudentService(studentRepo);

	        // Create the menu, giving it the service
	        MainMenu mainMenu = new MainMenu(studentService);

	        // Start the application
	        mainMenu.run();
	    }
	    
	    
}
