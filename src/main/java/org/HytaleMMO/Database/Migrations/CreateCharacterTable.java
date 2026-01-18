package org.HytaleMMO.Database.Migrations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateCharacterTable implements Migration {
    
    @Override
    public void up(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS characters (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "player_id VARCHAR(36) NOT NULL, " +
                "character_name VARCHAR(50) NOT NULL, " +
                "level INT DEFAULT 1, " +
                "character_class VARCHAR(30), " +
                "experience INT DEFAULT 0, " +
                "health INT DEFAULT 100, " +
                "max_health INT DEFAULT 100, " +
                "mana INT DEFAULT 100, " +
                "max_mana INT DEFAULT 100, " +
                "pos_x DOUBLE DEFAULT 0.0, " +
                "pos_y DOUBLE DEFAULT 0.0, " +
                "pos_z DOUBLE DEFAULT 0.0, " +
                "world VARCHAR(50), " +
                "created_at BIGINT NOT NULL, " +
                "last_played BIGINT NOT NULL, " +
                "UNIQUE KEY unique_character (player_id, character_name), " +
                "INDEX idx_player_id (player_id)" +
                ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
    
    @Override
    public void down(Connection connection) throws SQLException {
        String sql = "DROP TABLE IF EXISTS characters";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
    
    @Override
    public String getName() {
        return "CreateCharacterTable";
    }
}
