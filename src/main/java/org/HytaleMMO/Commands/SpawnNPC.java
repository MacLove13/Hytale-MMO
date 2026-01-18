package org.HytaleMMO.Commands;

import com.hypixel.hytale.server.api.command.Command;
import com.hypixel.hytale.server.api.command.CommandContext;
import com.hypixel.hytale.server.api.command.CommandSender;
import com.hypixel.hytale.server.api.entity.Player;
import org.HytaleMMO.NPC.NpcHandler;

import javax.annotation.Nonnull;

/**
 * Command to spawn an NPC at the player's location
 * Only admins (OP) can use this command
 */
public class SpawnNPC extends Command {
    private final NpcHandler npcHandler;

    public SpawnNPC(NpcHandler npcHandler) {
        super("spawnnpc");
        this.npcHandler = npcHandler;
        this.setDescription("Spawns an NPC at your location");
        this.setUsage("/spawnnpc <name>");
        this.setPermission("hytale.mmo.npc.spawn");
    }

    @Override
    public void execute(@Nonnull CommandSender sender, @Nonnull CommandContext context) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return;
        }

        Player player = (Player) sender;

        // Check if player has permission (OP)
        if (!player.hasPermission(this.getPermission())) {
            player.sendMessage("You don't have permission to use this command!");
            return;
        }

        // Get the NPC name from arguments (support multi-word names)
        String[] args = context.getArgs();
        if (args.length < 1) {
            player.sendMessage("Usage: " + this.getUsage());
            return;
        }

        // Join all arguments to support multi-word names
        String npcName = String.join(" ", args);

        try {
            // Spawn the NPC at player's location
            npcHandler.spawnNPC(player.getLocation(), npcName);
            player.sendMessage("NPC '" + npcName + "' spawned successfully!");
        } catch (Exception e) {
            player.sendMessage("Failed to spawn NPC: " + e.getMessage());
        }
    }
}
