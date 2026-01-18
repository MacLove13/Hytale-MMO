package org.HytaleMMO.Events;

import com.hypixel.hytale.event.EventListener;
import com.hypixel.hytale.event.entity.EntityDeathEvent;
import com.hypixel.hytale.world.entity.Entity;
import com.hypixel.hytale.world.entity.LivingEntity;
import com.hypixel.hytale.world.entity.Mob;
import com.hypixel.hytale.world.entity.player.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Event listener for handling mob death events.
 * Sends a chat message to the player whenever they kill an enemy mob.
 */
public class MobDeathListener {

    /**
     * Handles entity death events and sends a message when a player kills a mob.
     * 
     * @param event the EntityDeathEvent containing information about the death
     */
    @EventListener
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        
        // Check if the killed entity is a mob (enemy)
        if (entity instanceof Mob) {
            Mob mob = (Mob) entity;
            
            // Get the entity that killed the mob
            LivingEntity killer = event.getKiller();
            
            // Null check: ensure there is a killer and it's a player
            if (killer == null || !(killer instanceof Player)) {
                return;
            }
            
            Player player = (Player) killer;
            
            // Get the mob's name or type for display
            // Null check: ensure mob type is available
            if (mob.getType() == null) {
                return;
            }
            
            String mobName = mob.getType().getName();
            
            // Create and send a message to the player in Portuguese
            Component message = Component.text("Você matou um ", NamedTextColor.GREEN)
                .append(Component.text(mobName, NamedTextColor.YELLOW))
                .append(Component.text("!", NamedTextColor.GREEN));
            
            player.sendMessage(message);
        }
    }
}
