package org.HytaleMMO;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.HytaleMMO.Commands.SpawnNPC;
import org.HytaleMMO.NPC.NpcHandler;
import org.HytaleMMO.Character.CharacterAutoSave;
import org.HytaleMMO.Character.CharacterManager;
import org.HytaleMMO.Events.MobDeathListener;
import org.HytaleMMO.Database.DatabaseConnection;
import org.HytaleMMO.Listeners.PlayerEventListener;

import java.util.logging.Level;
import javax.annotation.Nonnull;

public class Main extends JavaPlugin {
    private HytaleLogger logger = HytaleLogger.getLogger().getSubLogger("MMO");
    private NpcHandler npcHandler;
    private DatabaseConnection databaseConnection;
    private CharacterManager characterManager;
    private PlayerEventListener playerEventListener;
    private CharacterAutoSave autoSave;

    public Main(@Nonnull JavaPluginInit init) {
        super(init);

        logger.at(Level.INFO).log("Loading " + this.getName() + " | Version " + this.getManifest().getVersion().toString());
        
        // Initialize database connection
        databaseConnection = new DatabaseConnection(logger);
        if (databaseConnection.connect()) {
            logger.at(Level.INFO).log("Database connection established and migrations completed");
            
            // Initialize character management system
            characterManager = new CharacterManager(databaseConnection.getConnection(), logger);
            logger.at(Level.INFO).log("Character manager initialized");
            
            // Initialize event listener
            playerEventListener = new PlayerEventListener(characterManager, logger);
            logger.at(Level.INFO).log("Player event listener initialized");
            
            // Start auto-save timer (10 minutes interval)
            autoSave = new CharacterAutoSave(characterManager, logger, 10);
            autoSave.start();
            
        } else {
            logger.at(Level.SEVERE).log("Failed to connect to database");
        }
    }

    @Override
    protected void setup() {
        logger.at(Level.INFO).log("Setting up plugin " + this.getName());
        
        // Initialize NPC Handler
        this.npcHandler = new NpcHandler();
        
        // Register commands
        this.getCommandRegistry().registerCommand(new SpawnNPC(npcHandler));
        
        logger.at(Level.INFO).log("NPC commands registered successfully!");
    }

    public NpcHandler getNpcHandler() {
        return npcHandler;
        // Register event listeners
        this.getEventRegistry().registerListener(new MobDeathListener());
        
        logger.at(Level.INFO).log("MobDeathListener registered successfully");
        
        // LOGGER.atInfo().log("Setting up plugin " + this.getName());
        // this.getCommandRegistry().registerCommand(new Claim());
    }
    
    @Override
    public void onDisable() {
        // Stop auto-save timer
        if (autoSave != null) {
            autoSave.stop();
        }
        
        // Save all loaded characters before shutdown
        if (characterManager != null) {
            logger.at(Level.INFO).log("Saving all characters before shutdown...");
            characterManager.saveAllCharacters();
        }
        
        // Disconnect from database when plugin is disabled
        if (databaseConnection != null) {
            databaseConnection.disconnect();
        }
    }
    
    /**
     * Gets the database connection instance
     * @return the database connection
     */
    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }
    
    /**
     * Gets the character manager instance
     * @return the character manager
     */
    public CharacterManager getCharacterManager() {
        return characterManager;
    }
    
    /**
     * Gets the player event listener instance
     * @return the player event listener
     */
    public PlayerEventListener getPlayerEventListener() {
        return playerEventListener;
    }
}
