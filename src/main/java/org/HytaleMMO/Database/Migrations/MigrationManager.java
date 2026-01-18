package org.HytaleMMO.Database.Migrations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.hypixel.hytale.logger.HytaleLogger;
import java.util.logging.Level;

public class MigrationManager {
    private final Connection connection;
    private final HytaleLogger logger;
    private final List<Migration> migrations;

    public MigrationManager(Connection connection, HytaleLogger logger) {
        this.connection = connection;
        this.logger = logger;
        this.migrations = new ArrayList<>();
        
        // Register migrations here
        migrations.add(new CreateCharacterTable());
    }

    /**
     * Creates the migrations tracking table if it doesn't exist
     */
    private void createMigrationsTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS migrations (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL UNIQUE, " +
                "executed_at BIGINT NOT NULL" +
                ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    /**
     * Checks if a migration has already been executed
     */
    private boolean isMigrationExecuted(String migrationName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM migrations WHERE name = '" + migrationName + "'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Records that a migration has been executed
     */
    private void recordMigration(String migrationName) throws SQLException {
        String sql = "INSERT INTO migrations (name, executed_at) VALUES ('" + 
                migrationName + "', " + System.currentTimeMillis() + ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    /**
     * Runs all pending migrations
     */
    public void runMigrations() {
        try {
            createMigrationsTable();
            
            for (Migration migration : migrations) {
                if (!isMigrationExecuted(migration.getName())) {
                    logger.at(Level.INFO).log("Running migration: " + migration.getName());
                    migration.up(connection);
                    recordMigration(migration.getName());
                    logger.at(Level.INFO).log("Migration completed: " + migration.getName());
                } else {
                    logger.at(Level.INFO).log("Migration already executed: " + migration.getName());
                }
            }
            
            logger.at(Level.INFO).log("All migrations completed successfully");
        } catch (SQLException e) {
            logger.at(Level.SEVERE).log("Error running migrations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Rolls back the last migration
     */
    public void rollbackLastMigration() {
        try {
            if (migrations.isEmpty()) {
                logger.at(Level.WARNING).log("No migrations to rollback");
                return;
            }
            
            Migration lastMigration = migrations.get(migrations.size() - 1);
            if (isMigrationExecuted(lastMigration.getName())) {
                logger.at(Level.INFO).log("Rolling back migration: " + lastMigration.getName());
                lastMigration.down(connection);
                
                String sql = "DELETE FROM migrations WHERE name = '" + lastMigration.getName() + "'";
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate(sql);
                }
                
                logger.at(Level.INFO).log("Rollback completed: " + lastMigration.getName());
            } else {
                logger.at(Level.WARNING).log("Migration not executed: " + lastMigration.getName());
            }
        } catch (SQLException e) {
            logger.at(Level.SEVERE).log("Error rolling back migration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
