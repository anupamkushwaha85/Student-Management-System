/**
 * 
 */
/**
 * 
 */
module StudentManagementSystem {
	// requires java.sql;
	requires com.google.gson;
	//requires org.junit.jupiter.api;
	opens model to com.google.gson;
	opens constants to com.google.gson;
}