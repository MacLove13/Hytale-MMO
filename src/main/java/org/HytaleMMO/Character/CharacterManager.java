package org.HytaleMMO.Character;

import com.hypixel.hytale.logger.HytaleLogger;
import org.HytaleMMO.Database.Tables.Character;
import org.HytaleMMO.Database.Tables.CharacterRepository;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Manages character loading, creation, and saving for players
 */
public class CharacterManager {
    private final CharacterRepository repository;
    private final HytaleLogger logger;
    private final Map<UUID, Character> loadedCharacters;

    public CharacterManager(Connection connection, HytaleLogger logger) {
        this.repository = new CharacterRepository(connection, logger);
        this.logger = logger;
        this.loadedCharacters = new HashMap<>();
    }

    /**
     * Loads or creates a character for a player when they join
     * @param playerId The player's UUID
     * @param playerName The player's name (used as default character name)
     * @param location The player's spawn location (x, y, z, world)
     * @return The loaded or newly created character
     */
    public Character loadOrCreateCharacter(UUID playerId, String playerName, double[] location) {
        // Try to find existing character(s) for this player
        var characters = repository.findByPlayer(playerId);
        
        Character character;
        if (characters.isEmpty()) {
            // Create new character
            logger.at(Level.INFO).log("Creating new character for player: " + playerName);
            character = createNewCharacter(playerId, playerName, location);
            
            if (repository.save(character)) {
                logger.at(Level.INFO).log("New character created and saved for player: " + playerName);
            } else {
                logger.at(Level.WARNING).log("Failed to save new character for player: " + playerName);
            }
        } else {
            // Load the first character (in the future, this could be a character selection)
            character = characters.get(0);
            logger.at(Level.INFO).log("Loaded existing character for player: " + playerName);
            
            // Update last played timestamp
            character.setLastPlayed(System.currentTimeMillis());
            
            // Update position to current spawn/login location
            if (location != null && location.length >= 4) {
                character.setPosX(location[0]);
                character.setPosY(location[1]);
                character.setPosZ(location[2]);
                if (location.length > 3) {
                    // World is passed as a string representation
                    character.setWorld(String.valueOf((int)location[3]));
                }
            }
        }
        
        // Store in memory
        loadedCharacters.put(playerId, character);
        
        return character;
    }

    /**
     * Creates a new character with default values
     * @param playerId The player's UUID
     * @param playerName The player's name
     * @param location The initial spawn location
     * @return The newly created character
     */
    private Character createNewCharacter(UUID playerId, String playerName, double[] location) {
        Character character = new Character();
        character.setPlayerId(playerId);
        character.setCharacterName(playerName);
        character.setCharacterClass("Adventurer"); // Default class
        
        // Set initial position
        if (location != null && location.length >= 3) {
            character.setPosX(location[0]);
            character.setPosY(location[1]);
            character.setPosZ(location[2]);
            if (location.length > 3) {
                character.setWorld(String.valueOf((int)location[3]));
            }
        }
        
        return character;
    }

    /**
     * Saves a character to the database
     * @param playerId The player's UUID
     * @return true if successful, false otherwise
     */
    public boolean saveCharacter(UUID playerId) {
        Character character = loadedCharacters.get(playerId);
        
        if (character == null) {
            logger.at(Level.WARNING).log("Attempted to save character for player " + playerId + " but no character is loaded");
            return false;
        }
        
        // Update last played timestamp
        character.setLastPlayed(System.currentTimeMillis());
        
        // Update existing character
        if (character.getId() > 0) {
            return repository.update(character);
        } else {
            // This shouldn't happen normally, but handle it just in case
            return repository.save(character);
        }
    }

    /**
     * Saves all loaded characters to the database
     * @return The number of characters successfully saved
     */
    public int saveAllCharacters() {
        int savedCount = 0;
        
        for (UUID playerId : loadedCharacters.keySet()) {
            if (saveCharacter(playerId)) {
                savedCount++;
            }
        }
        
        if (savedCount > 0) {
            logger.at(Level.INFO).log("Auto-saved " + savedCount + " character(s)");
        }
        
        return savedCount;
    }

    /**
     * Updates character position
     * @param playerId The player's UUID
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param world World name
     */
    public void updateCharacterPosition(UUID playerId, double x, double y, double z, String world) {
        Character character = loadedCharacters.get(playerId);
        
        if (character != null) {
            character.setPosX(x);
            character.setPosY(y);
            character.setPosZ(z);
            character.setWorld(world);
        }
    }

    /**
     * Updates character health
     * @param playerId The player's UUID
     * @param health Current health
     */
    public void updateCharacterHealth(UUID playerId, int health) {
        Character character = loadedCharacters.get(playerId);
        
        if (character != null) {
            character.setHealth(health);
        }
    }

    /**
     * Removes a character from memory (when player disconnects)
     * @param playerId The player's UUID
     */
    public void unloadCharacter(UUID playerId) {
        loadedCharacters.remove(playerId);
        logger.at(Level.INFO).log("Unloaded character for player: " + playerId);
    }

    /**
     * Gets a loaded character
     * @param playerId The player's UUID
     * @return The character, or null if not loaded
     */
    public Character getCharacter(UUID playerId) {
        return loadedCharacters.get(playerId);
    }

    /**
     * Checks if a character is loaded for a player
     * @param playerId The player's UUID
     * @return true if loaded, false otherwise
     */
    public boolean isCharacterLoaded(UUID playerId) {
        return loadedCharacters.containsKey(playerId);
    }
}
