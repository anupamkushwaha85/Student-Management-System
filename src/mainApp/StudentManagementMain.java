package mainApp;


import repository.StudentRepo;
import repository.StudentRepoInMemory;
import service.StudentService;
import storage.JSONStorage;
import ui.MainMenu;

/**
 * The main entry point for the Student Management System application.
 * <p>
 * This class is responsible for initializing and wiring together all the major
 * components of the application, including the data storage layer (JSONStorage),
 * the repository (StudentRepo), the business logic layer (StudentService),
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
     *     <li>Initializes the {@link JSONStorage} for data persistence.</li>
     *     <li>Injects the storage into the {@link StudentRepoInMemory} to create the data repository.</li>
     *     <li>Injects the repository into the {@link StudentService} to handle business logic.</li>
     *     <li>Injects the service into the {@link MainMenu} to manage user interaction.</li>
     *     <li>Calls the {@code run()} method on the main menu to start the application loop.</li>
     * </ol>
     *
     * @param args Command-line arguments (not used in this application).
     */
	    public static void main(String[] args) {

	        // 1. Create an instance of the JSONStorage utility. This object knows
	        //    how to read and write the students.json file.
	        JSONStorage jsonStorage = new JSONStorage();

	        // 2. Create an instance of your repository, passing the jsonStorage
	        //    object to its constructor. This is "injecting the dependency."
	        //    The repository now has the tool it needs to load and save data.
	        StudentRepo studentRepo = new StudentRepoInMemory(jsonStorage);

	        // 3. Create the service layer, giving it the persistent repository.
	        //    The service layer doesn't know or care that the data is coming
	        //    from a JSON file; it only knows it has a StudentRepo.
	        StudentService studentService = new StudentService(studentRepo);

	        // 3. Create the menu, giving it the service
	        MainMenu mainMenu = new MainMenu(studentService);

	        // 4. Start the application
	        mainMenu.run();
	    }
	    
	    
}
