package org.HytaleMMO.Database.Tables;

import com.hypixel.hytale.logger.HytaleLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class CharacterRepository {
    private final Connection connection;
    private final HytaleLogger logger;

    public CharacterRepository(Connection connection, HytaleLogger logger) {
        this.connection = connection;
        this.logger = logger;
    }

    /**
     * Saves a character to the database
     * @param character The character to save
     * @return true if successful, false otherwise
     */
    public boolean save(Character character) {
        String sql = "INSERT INTO characters (player_id, character_name, level, character_class, " +
                "experience, health, max_health, mana, max_mana, pos_x, pos_y, pos_z, world, " +
                "created_at, last_played) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, character.getPlayerId().toString());
            pstmt.setString(2, character.getCharacterName());
            pstmt.setInt(3, character.getLevel());
            pstmt.setString(4, character.getCharacterClass());
            pstmt.setInt(5, character.getExperience());
            pstmt.setInt(6, character.getHealth());
            pstmt.setInt(7, character.getMaxHealth());
            pstmt.setInt(8, character.getMana());
            pstmt.setInt(9, character.getMaxMana());
            pstmt.setDouble(10, character.getPosX());
            pstmt.setDouble(11, character.getPosY());
            pstmt.setDouble(12, character.getPosZ());
            pstmt.setString(13, character.getWorld());
            pstmt.setLong(14, character.getCreatedAt());
            pstmt.setLong(15, character.getLastPlayed());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        character.setId(generatedKeys.getInt(1));
                    }
                }
                logger.at(Level.INFO).log("Character saved: " + character.getCharacterName());
                return true;
            }
        } catch (SQLException e) {
            logger.at(Level.SEVERE).log("Error saving character: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing character in the database
     * @param character The character to update
     * @return true if successful, false otherwise
     */
    public boolean update(Character character) {
        String sql = "UPDATE characters SET level = ?, character_class = ?, experience = ?, " +
                "health = ?, max_health = ?, mana = ?, max_mana = ?, pos_x = ?, pos_y = ?, " +
                "pos_z = ?, world = ?, last_played = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, character.getLevel());
            pstmt.setString(2, character.getCharacterClass());
            pstmt.setInt(3, character.getExperience());
            pstmt.setInt(4, character.getHealth());
            pstmt.setInt(5, character.getMaxHealth());
            pstmt.setInt(6, character.getMana());
            pstmt.setInt(7, character.getMaxMana());
            pstmt.setDouble(8, character.getPosX());
            pstmt.setDouble(9, character.getPosY());
            pstmt.setDouble(10, character.getPosZ());
            pstmt.setString(11, character.getWorld());
            pstmt.setLong(12, character.getLastPlayed());
            pstmt.setInt(13, character.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.at(Level.INFO).log("Character updated: " + character.getCharacterName());
                return true;
            }
        } catch (SQLException e) {
            logger.at(Level.SEVERE).log("Error updating character: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Finds a character by player ID and character name
     * @param playerId The player's UUID
     * @param characterName The character's name
     * @return The character if found, null otherwise
     */
    public Character findByPlayerAndName(UUID playerId, String characterName) {
        String sql = "SELECT * FROM characters WHERE player_id = ? AND character_name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId.toString());
            pstmt.setString(2, characterName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCharacter(rs);
                }
            }
        } catch (SQLException e) {
            logger.at(Level.SEVERE).log("Error finding character: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds all characters for a specific player
     * @param playerId The player's UUID
     * @return List of characters
     */
    public List<Character> findByPlayer(UUID playerId) {
        List<Character> characters = new ArrayList<>();
        String sql = "SELECT * FROM characters WHERE player_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    characters.add(mapResultSetToCharacter(rs));
                }
            }
        } catch (SQLException e) {
            logger.at(Level.SEVERE).log("Error finding characters: " + e.getMessage());
            e.printStackTrace();
        }
        return characters;
    }

    /**
     * Deletes a character by ID
     * @param characterId The character's ID
     * @return true if successful, false otherwise
     */
    public boolean delete(int characterId) {
        String sql = "DELETE FROM characters WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, characterId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.at(Level.INFO).log("Character deleted with ID: " + characterId);
                return true;
            }
        } catch (SQLException e) {
            logger.at(Level.SEVERE).log("Error deleting character: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a Character object
     * @param rs The ResultSet
     * @return A Character object
     * @throws SQLException if there's an error reading the ResultSet
     */
    private Character mapResultSetToCharacter(ResultSet rs) throws SQLException {
        Character character = new Character();
        character.setId(rs.getInt("id"));
        character.setPlayerId(UUID.fromString(rs.getString("player_id")));
        character.setCharacterName(rs.getString("character_name"));
        character.setLevel(rs.getInt("level"));
        character.setCharacterClass(rs.getString("character_class"));
        character.setExperience(rs.getInt("experience"));
        character.setHealth(rs.getInt("health"));
        character.setMaxHealth(rs.getInt("max_health"));
        character.setMana(rs.getInt("mana"));
        character.setMaxMana(rs.getInt("max_mana"));
        character.setPosX(rs.getDouble("pos_x"));
        character.setPosY(rs.getDouble("pos_y"));
        character.setPosZ(rs.getDouble("pos_z"));
        character.setWorld(rs.getString("world"));
        character.setCreatedAt(rs.getLong("created_at"));
        character.setLastPlayed(rs.getLong("last_played"));
        return character;
    }
}
