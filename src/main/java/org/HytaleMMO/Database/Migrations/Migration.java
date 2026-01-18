package org.HytaleMMO.Database.Migrations;

import java.sql.Connection;
import java.sql.SQLException;

public interface Migration {
    /**
     * Executes the migration to create or modify database tables
     * @param connection The database connection to use
     * @throws SQLException if the migration fails
     */
    void up(Connection connection) throws SQLException;
    
    /**
     * Reverts the migration (optional implementation)
     * @param connection The database connection to use
     * @throws SQLException if the rollback fails
     */
    void down(Connection connection) throws SQLException;
    
    /**
     * Returns the name of the migration
     * @return Migration name
     */
    String getName();
}
