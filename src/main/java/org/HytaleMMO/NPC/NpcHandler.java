package org.HytaleMMO.NPC;

import com.hypixel.hytale.server.api.entity.Entity;
import com.hypixel.hytale.server.api.entity.EntityType;
import com.hypixel.hytale.server.api.world.Location;
import com.hypixel.hytale.server.api.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

/**
 * Handles NPC spawning and management
 */
public class NpcHandler {
    private final Map<UUID, Entity> spawnedNPCs;

    public NpcHandler() {
        this.spawnedNPCs = new HashMap<>();
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
        spawnedNPCs.put(npcId, npc);
        
        return npcId;
    }

    /**
     * Removes an NPC by UUID
     * @param npcId The UUID of the NPC to remove
     * @return true if the NPC was found and removed, false otherwise
     */
    public boolean removeNPC(UUID npcId) {
        Entity npc = spawnedNPCs.remove(npcId);
        if (npc != null) {
            npc.remove();
            return true;
        }
        return false;
    }

    /**
     * Gets the list of spawned NPC UUIDs
     * @return List of NPC UUIDs
     */
    public List<UUID> getSpawnedNPCs() {
        return new ArrayList<>(spawnedNPCs.keySet());
    }

    /**
     * Removes all NPCs from the world and clears the tracking list
     */
    public void removeAllNPCs() {
        for (Entity npc : spawnedNPCs.values()) {
            npc.remove();
        }
        spawnedNPCs.clear();
    }

    /**
     * Gets an NPC entity by UUID
     * @param npcId The UUID of the NPC
     * @return The NPC entity, or null if not found
     */
    public Entity getNPC(UUID npcId) {
        return spawnedNPCs.get(npcId);
    }
}
