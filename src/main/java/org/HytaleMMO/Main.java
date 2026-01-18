package org.HytaleMMO;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.HytaleMMO.Events.MobDeathListener;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class Main extends JavaPlugin {
    private HytaleLogger logger = HytaleLogger.getLogger().getSubLogger("MMO");

    public Main(@Nonnull JavaPluginInit init) {
        super(init);

        logger.at(Level.INFO).log("Loading " + this.getName() + " | Version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        // Register event listeners
        this.getEventRegistry().registerListener(new MobDeathListener());
        
        logger.at(Level.INFO).log("MobDeathListener registered successfully");
        
        // LOGGER.atInfo().log("Setting up plugin " + this.getName());
        // this.getCommandRegistry().registerCommand(new Claim());
    }
}
