package org.HytaleMMO.Character;

import com.hypixel.hytale.logger.HytaleLogger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

/**
 * Handles automatic character saving at regular intervals
 */
public class CharacterAutoSave {
    private final CharacterManager characterManager;
    private final HytaleLogger logger;
    private Timer autoSaveTimer;
    private final long saveIntervalMs;

    /**
     * Creates a new auto-save scheduler
     * @param characterManager The character manager to use for saving
     * @param logger The logger instance
     * @param saveIntervalMinutes The interval between saves in minutes (default: 10)
     */
    public CharacterAutoSave(CharacterManager characterManager, HytaleLogger logger, int saveIntervalMinutes) {
        this.characterManager = characterManager;
        this.logger = logger;
        this.saveIntervalMs = saveIntervalMinutes * 60 * 1000L; // Convert minutes to milliseconds
    }

    /**
     * Starts the auto-save timer
     */
    public void start() {
        if (autoSaveTimer != null) {
            logger.at(Level.WARNING).log("Auto-save timer is already running");
            return;
        }

        autoSaveTimer = new Timer("CharacterAutoSave", true); // daemon thread
        
        autoSaveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    logger.at(Level.INFO).log("Running auto-save for characters...");
                    int savedCount = characterManager.saveAllCharacters();
                    
                    if (savedCount == 0) {
                        logger.at(Level.INFO).log("No characters to auto-save");
                    }
                } catch (Exception e) {
                    logger.at(Level.SEVERE).log("Error during auto-save: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, saveIntervalMs, saveIntervalMs); // Initial delay and repeat interval

        logger.at(Level.INFO).log("Character auto-save started (interval: " + (saveIntervalMs / 60000) + " minutes)");
    }

    /**
     * Stops the auto-save timer
     */
    public void stop() {
        if (autoSaveTimer != null) {
            autoSaveTimer.cancel();
            autoSaveTimer = null;
            logger.at(Level.INFO).log("Character auto-save stopped");
        }
    }

    /**
     * Checks if the auto-save timer is running
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return autoSaveTimer != null;
    }
}
