package utility;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages a high-performance database connection pool using HikariCP.
 * <p>
 * This class reads connection details from a 'db.properties' file,
 * initializes a connection pool (DataSource), and provides a static method
 * to obtain connections from the pool. It is implemented as a singleton
 * to ensure only one connection pool exists for the application.
 * <p>
 * Usage example:
 * <pre>
 * try (Connection conn = DatabaseConnector.getConnection()) {
 *     // Use the connection
 * }
 * </pre>
 * <p>
 * Make sure to call {@link #closePool()} on application shutdown or register
 * a shutdown hook (example below) to close the pool gracefully:
 * <pre>
 * Runtime.getRuntime().addShutdownHook(new Thread(DatabaseConnector::closePool));
 * </pre>
 */
public class DatabaseConnector {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnector.class.getName());
    private static final HikariDataSource dataSource;

    static {
        Properties props = new Properties();

        try (InputStream input = DatabaseConnector.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                LOGGER.severe("Sorry, unable to find db.properties");
                throw new RuntimeException("Configuration file 'db.properties' not found in the classpath.");
            }
            props.load(input);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error loading db.properties file", ex);
            throw new RuntimeException("Error loading configuration.", ex);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("jdbc.url"));
        config.setUsername(props.getProperty("jdbc.user"));  // Property name unified here
        config.setPassword(props.getProperty("jdbc.password"));
        config.setPoolName(props.getProperty("hikari.poolName", "MyPool"));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("hikari.maximumPoolSize", "10")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("hikari.connectionTimeout", "30000")));

        config.setMinimumIdle(Integer.parseInt(props.getProperty("hikari.minimumIdle", "2")));

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
        LOGGER.info("HikariCP connection pool initialized successfully.");
    }

    private DatabaseConnector() {
        // Utility class: prevent instantiation
    }

    /**
     * Obtains a database connection from the connection pool.
     * The caller is responsible for closing this connection when done.
     *
     * @return a pooled {@link Connection} object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Closes the Hikari connection pool and releases all resources.
     * Should be called once at application shutdown.
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.info("HikariCP connection pool closed.");
        }
    }
}
