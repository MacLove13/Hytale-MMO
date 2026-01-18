package org.HytaleMMO.NPC;

import com.hypixel.hytale.server.api.entity.Entity;
import com.hypixel.hytale.server.api.entity.EntityType;
import com.hypixel.hytale.server.api.world.Location;
import com.hypixel.hytale.server.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles NPC spawning and management
 */
public class NpcHandler {
    private final List<UUID> spawnedNPCs;

    public NpcHandler() {
        this.spawnedNPCs = new ArrayList<>();
    }

    /**
     * Spawns an NPC at the specified location
     * @param location The location to spawn the NPC
     * @param name The name of the NPC
     * @return The spawned entity UUID
     */
    public UUID spawnNPC(Location location, String name) {
        World world = location.getWorld();
        
        // Spawn an NPC entity (using a human-like entity type)
        // Note: The exact entity type may need to be adjusted based on Hytale API
        Entity npc = world.spawnEntity(location, EntityType.NPC);
        
        // Set the NPC name
        npc.setCustomName(name);
        npc.setCustomNameVisible(true);
        
        // Make the NPC stationary (disable AI)
        npc.setAI(false);
        npc.setGravity(true);
        npc.setInvulnerable(true);
        
        // Track the NPC
        UUID npcId = npc.getUniqueId();
        spawnedNPCs.add(npcId);
        
        return npcId;
    }

    /**
     * Removes an NPC by UUID
     * @param npcId The UUID of the NPC to remove
     */
    public void removeNPC(UUID npcId) {
        if (spawnedNPCs.contains(npcId)) {
            spawnedNPCs.remove(npcId);
            // Additional cleanup if needed
        }
    }

    /**
     * Gets the list of spawned NPC UUIDs
     * @return List of NPC UUIDs
     */
    public List<UUID> getSpawnedNPCs() {
        return new ArrayList<>(spawnedNPCs);
    }

    /**
     * Removes all NPCs
     */
    public void removeAllNPCs() {
        spawnedNPCs.clear();
    }
}
