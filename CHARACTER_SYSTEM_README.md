# Character System Implementation

This implementation adds character loading, saving, and auto-save functionality to the Hytale MMO plugin.

## Features Implemented

### 1. Character Loading on Player Join
- When a player connects, the system automatically loads their character from the database
- If no character exists, a new one is created with default values
- Character data includes: level, class, experience, health, mana, position, and timestamps

### 2. Auto-Save System
- Characters are automatically saved every 10 minutes
- The auto-save runs in a background thread to avoid blocking the main server thread
- On server shutdown, all loaded characters are saved before disconnecting

### 3. Save on Death
- When a player dies, their character is immediately saved to the database
- Character position is updated to the death location
- Health is reset to maximum for respawn

### 4. Save on Disconnect
- Characters are saved when players disconnect from the server
- Character data is unloaded from memory to free resources

## Components

### CharacterManager (`org.HytaleMMO.Character.CharacterManager`)
Central management class that handles:
- Loading or creating characters for players
- Saving character data to the database
- Tracking loaded characters in memory
- Updating character position and health

### PlayerEventListener (`org.HytaleMMO.Listeners.PlayerEventListener`)
Event handler class with methods for:
- `onPlayerJoin()` - Called when a player joins the server
- `onPlayerDisconnect()` - Called when a player leaves the server
- `onPlayerDeath()` - Called when a player dies
- Helper methods for updating position and health

### HytaleEventBridge (`org.HytaleMMO.Listeners.HytaleEventBridge`)
Example integration class that shows:
- How to connect PlayerEventListener to actual Hytale events
- Common patterns for event handling
- Template methods that can be uncommented and adapted

### CharacterAutoSave (`org.HytaleMMO.Character.CharacterAutoSave`)
Scheduled task that:
- Runs every 10 minutes (configurable)
- Saves all loaded characters automatically
- Uses a daemon timer thread for clean shutdown

## Integration with Hytale Events

⚠️ **Important**: The PlayerEventListener class contains handler methods that need to be connected to actual Hytale events.

### Quick Start

A template class `HytaleEventBridge.java` has been provided with example integration patterns. To complete the integration:

1. **Find the Hytale Event Classes**
   - Look for player join/login events (e.g., `PlayerJoinEvent`, `PlayerLoginEvent`)
   - Look for player death events (e.g., `PlayerDeathEvent`)
   - Look for player disconnect/quit events (e.g., `PlayerQuitEvent`, `PlayerDisconnectEvent`)

2. **Create Event Handler Methods**
   Add methods in Main.java or PlayerEventListener that use Hytale's event system:
   ```java
   @EventHandler  // or whatever annotation Hytale uses
   public void onJoinEvent(PlayerJoinEvent event) {
       Player player = event.getPlayer();
       UUID playerId = player.getUniqueId();
       String playerName = player.getName();
       // Get player position
       Location loc = player.getLocation();
       
       // Call our handler
       playerEventListener.onPlayerJoin(
           playerId, 
           playerName, 
           loc.getX(), 
           loc.getY(), 
           loc.getZ(), 
           loc.getWorld().getName()
       );
   }
   ```

3. **Register the Event Listener**
   In Main.java's `setup()` method, register your event handlers with Hytale's event system.

## Usage Example

Once integrated with Hytale events, the system works automatically:

```java
// The system automatically:
// 1. Loads/creates character when player joins
// 2. Auto-saves every 10 minutes
// 3. Saves on player death
// 4. Saves on player disconnect

// You can also manually interact with the character system:
Main plugin = ...; // your plugin instance
CharacterManager manager = plugin.getCharacterManager();

// Get a player's character
Character character = manager.getCharacter(playerUUID);

// Manually save a character
manager.saveCharacter(playerUUID);

// Update character stats
manager.updateCharacterHealth(playerUUID, 50);
manager.updateCharacterPosition(playerUUID, x, y, z, world);
```

## Configuration

The auto-save interval is currently set to 10 minutes. To change it, modify the value in `Main.java`:

```java
// Change the last parameter (10) to your desired interval in minutes
autoSave = new CharacterAutoSave(characterManager, logger, 10);
```

## Database

The character data is stored in the `characters` table created by the existing migration system. No additional database setup is required.

## Testing

To test without actual Hytale events:

1. Create a test command that manually calls the event handler methods
2. Use the command to simulate player joins, deaths, and disconnects
3. Check the database to verify characters are being saved correctly
4. Check the logs to see the auto-save running every 10 minutes

Example test command:
```java
@Command("testcharacter")
public void testCommand(CommandSender sender) {
    UUID testUUID = UUID.randomUUID();
    playerEventListener.onPlayerJoin(testUUID, "TestPlayer", 0, 64, 0, "world");
    sender.sendMessage("Test character created!");
}
```

## Troubleshooting

### Characters not saving
- Check database connection is established
- Check logs for SQL errors
- Verify database.properties is correctly configured

### Auto-save not running
- Check logs for "Character auto-save started" message
- Verify no exceptions in auto-save task
- Ensure plugin is not being reloaded which would stop the timer

### Events not firing
- Verify event handlers are properly registered with Hytale
- Check event listener is initialized in Main.java
- Look for errors in server logs
