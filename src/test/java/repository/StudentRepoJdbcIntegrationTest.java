package repository;

import constants.Departments;
import constants.Status;
import model.Student;
import org.junit.jupiter.api.*;
import utility.DatabaseConnector;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link StudentRepoJdbc} using an in-memory H2 database.
 *
 * <p>These tests verify end-to-end behavior of the JDBC repository by:
 * <ul>
 *   <li>Bootstrapping a clean, in-memory H2 schema before tests run</li>
 *   <li>Executing repository CRUD operations against real SQL</li>
 *   <li>Asserting round-trip correctness for all {@link Student} fields</li>
 * </ul>
 *
 * <p>Test database configuration should point DatabaseConnector to an H2 URL via
 * a test-only db.properties on the test classpath, for example:
 * <pre>
 * jdbc.url=jdbc:h2:mem:student_system_test;MODE=MySQL;DB_CLOSE_DELAY=-1
 * jdbc.user=sa
 * jdbc.password=
 * hikari.poolName=TestPool
 * hikari.maximumPoolSize=4
 * hikari.connectionTimeout=20000
 * </pre>
 *
 * <p>Note: MODE=MySQL provides MySQL compatibility for SQL syntax, and DB_CLOSE_DELAY=-1
 * keeps the in-memory database alive for the JVM session to support multi-connection tests.
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
class StudentRepoJdbcIntegrationTest {

    private StudentRepo repo;

    /**
     * Initialize database schema once per test run.
     * Reads schema.sql from the classpath and executes each statement safely.
     */
    @BeforeAll
    static void initSchema() throws Exception {
        // Load schema_test.sql from classpath without converting to a file system path
        String schemaSql;
        try (InputStream in = StudentRepoJdbcIntegrationTest.class
                .getClassLoader()
                .getResourceAsStream("schema_test.sql")) {
            assertNotNull(in, "schema.sql must be present on the test classpath");
            schemaSql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Execute statements in a transaction; split on ';' to handle multiple commands
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(false);
            for (String part : schemaSql.split(";")) {
                String sql = part.trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }
            conn.commit();
        }
    }

    /**
     * Prepare a clean table state before each test to ensure test independence.
     */
    @BeforeEach
    void setUp() throws Exception {
        repo = new StudentRepoJdbc();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            // Fast reset for a single-table schema
            stmt.executeUpdate("DELETE FROM students");
        }
    }

    /**
     * Verifies that saving a student returns a populated {@link Student} with a generated ID,
     * and that a subsequent read by ID round-trips all fields exactly.
     */
    @Test
    @DisplayName("1) save + findById round-trip all fields")
    void save_and_findById_roundTripsAllFields() {
        // Arrange
        var name = "Anaya Love";
        var email = "anaa.love@example.com";
        var phone = "5874267800";
        var dob = LocalDate.of(1915, 10, 13);
        var address = "London, England";
        var department = Departments.CSE;
        var status = Status.ACTIVE;

        // Act: save now returns Student directly
        var saved = repo.save(name, email, phone, dob, address, department, status);

        // Assert: non-null and DB-generated ID
        assertNotNull(saved, "Saved student should not be null");
        assertTrue(saved.id() > 0, "Saved student should have a positive ID");

        // Act 2: fetch from DB
        var found = repo.findById(saved.id()).orElseThrow();

        // Assert 2: field-by-field equality
        assertEquals(name, found.name());
        assertEquals(email, found.email());
        assertEquals(phone, found.phone());
        assertEquals(dob, found.DOB());
        assertEquals(address, found.address());
        assertEquals(department, found.department());
        assertEquals(status, found.status());
    }

    /**
     * Ensures repository returns empty when looking up a non-existent student ID.
     */
    @Test
    @DisplayName("2) findById returns empty for missing ID")
    void findById_returnsEmpty_forMissingId() {
        assertTrue(repo.findById(999_999).isEmpty(), "Missing ID should return Optional.empty");
    }

    /**
     * Confirms update persists changes and returns the updated entity.
     */
    @Test
    @DisplayName("3) update modifies existing row")
    void update_changesPersistedRow() {
        var base = repo.save("Bobs", "b@x.com", "9925412399",
                LocalDate.of(2000, 1, 1), "Address", Departments.ECE, Status.ACTIVE);

        var updated = new Student(
                base.id(),
                "Bobby",
                base.email(),
                base.phone(),
                base.DOB(),
                "New Address",
                Departments.ME,
                Status.INACTIVE
        );

        var result = repo.update(updated);
        assertTrue(result.isPresent(), "Update should return updated Optional<Student>");

        var fetched = repo.findById(base.id()).orElseThrow();
        assertEquals("Bobby", fetched.name());
        assertEquals("New Address", fetched.address());
        assertEquals(Departments.ME, fetched.department());
        assertEquals(Status.INACTIVE, fetched.status());
    }

    /**
     * Verifies deleteById returns false for IDs that do not exist.
     */
    @Test
    @DisplayName("4) deleteById returns false on missing row")
    void deleteById_returnsFalse_forMissingRow() {
        assertFalse(repo.deleteById(123_456), "Deleting a missing row should return false");
    }

    /**
     * Verifies the repository enforces the unique email constraint by throwing a RuntimeException
     * when attempting to insert a second student with an email that already exists.
     *
     * <p>This test assumes {@code StudentRepoJdbc.save(...)} throws a {@link RuntimeException}
     * upon database constraint violations (e.g., UNIQUE index on email).
     */
    @Test
    @DisplayName("5) unique email constraint enforced")
    void uniqueEmail_constraint_enforced() {
        // Arrange: persist a student with a unique email
        repo.save("Student A", "unique@example.com", "3625412541",
                LocalDate.of(2001, 1, 1), "", Departments.CSE, Status.ACTIVE);

        // Act & Assert: the second save with the same email must throw RuntimeException
        assertThrows(RuntimeException.class, () -> repo.save(
                "Student B", "unique@example.com", "2236521254",
                LocalDate.of(2002, 2, 2), "", Departments.CSE, Status.ACTIVE
        ), "Saving a student with a duplicate email should throw a RuntimeException.");
    }

}

