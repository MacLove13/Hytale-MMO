package org.HytaleMMO;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.HytaleMMO.Events.MobDeathListener;
import org.HytaleMMO.Database.DatabaseConnection;

import java.util.logging.Level;
import javax.annotation.Nonnull;

public class Main extends JavaPlugin {
    private HytaleLogger logger = HytaleLogger.getLogger().getSubLogger("MMO");
    private DatabaseConnection databaseConnection;

    public Main(@Nonnull JavaPluginInit init) {
        super(init);

        logger.at(Level.INFO).log("Loading " + this.getName() + " | Version " + this.getManifest().getVersion().toString());
        
        // Initialize database connection
        databaseConnection = new DatabaseConnection(logger);
        if (databaseConnection.connect()) {
            logger.at(Level.INFO).log("Database connection established and migrations completed");
        } else {
            logger.at(Level.SEVERE).log("Failed to connect to database");
        }
    }

    @Override
    protected void setup() {
        // Register event listeners
        this.getEventRegistry().registerListener(new MobDeathListener());
        
        logger.at(Level.INFO).log("MobDeathListener registered successfully");
        
        // LOGGER.atInfo().log("Setting up plugin " + this.getName());
        // this.getCommandRegistry().registerCommand(new Claim());
    }
    
    @Override
    public void onDisable() {
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
}
