# NPC Command Documentation

## Overview
This plugin adds the ability for administrators (OPs) to spawn NPCs that remain stationary at their spawn location.

## Command Usage

### `/spawnnpc <name>`
Spawns an NPC at the player's current location.

**Parameters:**
- `<name>` - The display name for the NPC (required)

**Permission:** `hytale.mmo.npc.spawn`

**Requirements:**
- Player must have OP permissions or the specific permission node
- Player must be in-game (cannot be used from console)

## Examples

```
/spawnnpc Merchant
```
This will spawn an NPC named "Merchant" at your current location.

```
/spawnnpc Guard
```
This will spawn an NPC named "Guard" at your current location.

## Features

### NPC Characteristics
- **Stationary:** NPCs spawned with this command will not move from their spawn location
- **Named:** Each NPC displays its custom name above its head
- **Invulnerable:** NPCs cannot be damaged or killed
- **No AI:** NPCs do not have artificial intelligence and will not react to players or environment

### NPC Handler
The `NpcHandler` class manages all spawned NPCs:
- Tracks all spawned NPC UUIDs
- Provides methods to remove individual NPCs or all NPCs
- Can be extended to add more NPC management features in the future

## Technical Details

### Files Created
1. **SpawnNPC.java** - Command implementation in `src/main/java/org/HytaleMMO/Commands/`
2. **NpcHandler.java** - NPC management in `src/main/java/org/HytaleMMO/NPC/`

### Integration
The command is automatically registered in the `Main.java` plugin setup method.

## Future Enhancements
Potential features that could be added:
- `/removenpc` - Remove an NPC by targeting it
- `/listnpcs` - List all spawned NPCs
- `/tpnpc <name> <player>` - Teleport an NPC to a player's location
- Custom skins/models for NPCs
- Interactive NPCs with dialogue
- NPC persistence across server restarts

## Troubleshooting

### "You don't have permission to use this command!"
Make sure you are an operator on the server. Have an admin run:
```
/op <your_username>
```

### "This command can only be used by players!"
This command cannot be executed from the server console. You must be in-game.

### "Failed to spawn NPC: [error message]"
Check the server logs for detailed error information. Common issues:
- Invalid location (player in a non-loaded chunk)
- Server lag or performance issues
- Conflicting plugins

## API Notes
The implementation uses standard Hytale Server API classes:
- `com.hypixel.hytale.server.api.command.Command`
- `com.hypixel.hytale.server.api.entity.Entity`
- `com.hypixel.hytale.server.api.world.Location`

Note: Some API method names may need adjustment based on the actual Hytale API when tested in a real environment.
