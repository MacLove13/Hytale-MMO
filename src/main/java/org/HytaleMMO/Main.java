package org.HytaleMMO;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.HytaleMMO.Commands.SpawnNPC;
import org.HytaleMMO.NPC.NpcHandler;

import java.util.logging.Level;
import javax.annotation.Nonnull;

public class Main extends JavaPlugin {
    private HytaleLogger logger = HytaleLogger.getLogger().getSubLogger("MMO");
    private NpcHandler npcHandler;

    public Main(@Nonnull JavaPluginInit init) {
        super(init);

        logger.at(Level.INFO).log("Loading " + this.getName() + " | Version " + this.getManifest().getVersion().toString());
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
    }
}
