package org.HytaleMMO.Listeners;

import com.hypixel.hytale.logger.HytaleLogger;
import org.HytaleMMO.Character.CharacterManager;

import java.util.UUID;
import java.util.logging.Level;

/**
 * Handles player-related events such as join, disconnect, and death
 * Note: Event handling methods will need to be connected to actual Hytale events
 * using the appropriate event system when available
 */
public class PlayerEventListener {
    private final CharacterManager characterManager;
    private final HytaleLogger logger;

    public PlayerEventListener(CharacterManager characterManager, HytaleLogger logger) {
        this.characterManager = characterManager;
        this.logger = logger;
    }

    /**
     * Handles when a player joins the server
     * This method should be called from the actual event handler
     * 
     * @param playerId The player's UUID
     * @param playerName The player's name
     * @param spawnX The spawn X coordinate
     * @param spawnY The spawn Y coordinate
     * @param spawnZ The spawn Z coordinate
     * @param world The world name
     */
    public void onPlayerJoin(UUID playerId, String playerName, double spawnX, double spawnY, double spawnZ, String world) {
        try {
            logger.at(Level.INFO).log("Player joining: " + playerName + " (" + playerId + ")");
            
            double[] location = new double[]{spawnX, spawnY, spawnZ};
            
            // Load or create character
            var character = characterManager.loadOrCreateCharacter(playerId, playerName, location);
            
            if (character != null) {
                logger.at(Level.INFO).log("Character loaded for player: " + playerName + 
                        " | Level: " + character.getLevel() + 
                        " | Class: " + character.getCharacterClass());
            } else {
                logger.at(Level.SEVERE).log("Failed to load or create character for player: " + playerName);
            }
        } catch (Exception e) {
            logger.at(Level.SEVERE).log("Error handling player join: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles when a player disconnects from the server
     * This method should be called from the actual event handler
     * 
     * @param playerId The player's UUID
     * @param playerName The player's name
     */
    public void onPlayerDisconnect(UUID playerId, String playerName) {
        try {
            logger.at(Level.INFO).log("Player disconnecting: " + playerName + " (" + playerId + ")");
            
            // Save character before disconnect
            if (characterManager.isCharacterLoaded(playerId)) {
                boolean saved = characterManager.saveCharacter(playerId);
                
                if (saved) {
                    logger.at(Level.INFO).log("Character saved for disconnecting player: " + playerName);
                } else {
                    logger.at(Level.WARNING).log("Failed to save character for disconnecting player: " + playerName);
                }
                
                // Unload character from memory
                characterManager.unloadCharacter(playerId);
            }
        } catch (Exception e) {
            logger.at(Level.SEVERE).log("Error handling player disconnect: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles when a player dies
     * This method should be called from the actual event handler
     * 
     * @param playerId The player's UUID
     * @param playerName The player's name
     * @param deathX The death location X coordinate
     * @param deathY The death location Y coordinate
     * @param deathZ The death location Z coordinate
     * @param world The world name
     */
    public void onPlayerDeath(UUID playerId, String playerName, double deathX, double deathY, double deathZ, String world) {
        try {
            logger.at(Level.INFO).log("Player died: " + playerName + " (" + playerId + ")");
            
            // Update character position to death location
            characterManager.updateCharacterPosition(playerId, deathX, deathY, deathZ, world);
            
            // Reset character health to max (respawn)
            var character = characterManager.getCharacter(playerId);
            if (character != null) {
                characterManager.updateCharacterHealth(playerId, character.getMaxHealth());
            }
            
            // Save character on death
            boolean saved = characterManager.saveCharacter(playerId);
            
            if (saved) {
                logger.at(Level.INFO).log("Character saved after death: " + playerName);
            } else {
                logger.at(Level.WARNING).log("Failed to save character after death: " + playerName);
            }
        } catch (Exception e) {
            logger.at(Level.SEVERE).log("Error handling player death: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates a player's character position
     * Can be called periodically or on specific events
     * 
     * @param playerId The player's UUID
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param world World name
     */
    public void updatePlayerPosition(UUID playerId, double x, double y, double z, String world) {
        characterManager.updateCharacterPosition(playerId, x, y, z, world);
    }

    /**
     * Updates a player's character health
     * Can be called when health changes
     * 
     * @param playerId The player's UUID
     * @param health Current health value
     */
    public void updatePlayerHealth(UUID playerId, int health) {
        characterManager.updateCharacterHealth(playerId, health);
    }
}
