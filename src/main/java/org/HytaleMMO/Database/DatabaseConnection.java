package org.HytaleMMO.Database;

import com.hypixel.hytale.logger.HytaleLogger;
import org.HytaleMMO.Database.Migrations.MigrationManager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

public class DatabaseConnection {
    private Connection connection;
    private final HytaleLogger logger;
    private Properties dbProperties;

    public DatabaseConnection(HytaleLogger logger) {
        this.logger = logger;
        loadDatabaseProperties();
    }

    /**
     * Loads database configuration from database.properties file
     */
    private void loadDatabaseProperties() {
        dbProperties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                logger.at(Level.WARNING).log("Unable to find database.properties, using defaults");
                setDefaultProperties();
                return;
            }
            dbProperties.load(input);
        } catch (IOException e) {
            logger.at(Level.SEVERE).log("Error loading database.properties: " + e.getMessage());
            setDefaultProperties();
        }
    }

    /**
     * Sets default database properties
     */
    private void setDefaultProperties() {
        dbProperties.setProperty("db.host", "localhost");
        dbProperties.setProperty("db.port", "3306");
        dbProperties.setProperty("db.name", "hytale_mmo");
        dbProperties.setProperty("db.user", "root");
        dbProperties.setProperty("db.password", "");
        dbProperties.setProperty("db.useSSL", "false");
    }

    /**
     * Establishes a connection to the MariaDB database
     * @return true if connection is successful, false otherwise
     */
    public boolean connect() {
        try {
            String host = dbProperties.getProperty("db.host");
            String port = dbProperties.getProperty("db.port");
            String database = dbProperties.getProperty("db.name");
            String user = dbProperties.getProperty("db.user");
            String password = dbProperties.getProperty("db.password");
            String useSSL = dbProperties.getProperty("db.useSSL");

            String url = "jdbc:mariadb://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL;

            logger.at(Level.INFO).log("Connecting to MariaDB database at " + host + ":" + port + "/" + database);
            
            connection = DriverManager.getConnection(url, user, password);
            
            logger.at(Level.INFO).log("Successfully connected to MariaDB database");
            
            // Run migrations after successful connection
            runMigrations();
            
            return true;
        } catch (SQLException e) {
            logger.at(Level.SEVERE).log("Failed to connect to MariaDB database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Runs database migrations
     */
    private void runMigrations() {
        if (connection != null) {
            MigrationManager migrationManager = new MigrationManager(connection, logger);
            migrationManager.runMigrations();
        }
    }

    /**
     * Gets the current database connection
     * @return the database connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Checks if the database connection is active
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Closes the database connection
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.at(Level.INFO).log("Disconnected from MariaDB database");
            }
        } catch (SQLException e) {
            logger.at(Level.SEVERE).log("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
