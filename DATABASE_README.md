# MariaDB Database Integration

This implementation adds MariaDB database support to the Hytale MMO plugin.

## Structure

### Database Configuration
- **Location**: `src/main/resources/database.properties`
- **Purpose**: Contains database connection parameters (host, port, database name, user, password)
- **Note**: Update these values to match your MariaDB installation

### Core Components

#### DatabaseConnection (`src/main/java/org/HytaleMMO/Database/DatabaseConnection.java`)
- Manages the MariaDB connection lifecycle
- Loads configuration from `database.properties`
- Automatically runs migrations on connection
- Provides connection access to other components

#### Migration System (`src/main/java/org/HytaleMMO/Database/Migrations/`)
- **Migration.java**: Interface for all migrations
- **MigrationManager.java**: Manages and executes migrations
- **CreateCharacterTable.java**: Initial migration to create the characters table

### Character Table (`src/main/java/org/HytaleMMO/Database/Tables/`)

#### Character.java
POJO (Plain Old Java Object) representing a character with the following fields:
- `id`: Auto-incremented primary key
- `playerId`: UUID of the player who owns the character
- `characterName`: Name of the character
- `level`: Character's level (default: 1)
- `characterClass`: Character's class/profession
- `experience`: Experience points (default: 0)
- `health`: Current health points (default: 100)
- `maxHealth`: Maximum health points (default: 100)
- `mana`: Current mana points (default: 100)
- `maxMana`: Maximum mana points (default: 100)
- `posX`, `posY`, `posZ`: Character's position in the world
- `world`: Name of the world where the character is located
- `createdAt`: Timestamp when the character was created
- `lastPlayed`: Timestamp when the character was last played

#### CharacterRepository.java
Data access layer for character operations:
- `save(Character)`: Insert a new character
- `update(Character)`: Update an existing character
- `findByPlayerAndName(UUID, String)`: Find a specific character
- `findByPlayer(UUID)`: Get all characters for a player
- `delete(int)`: Delete a character by ID

## Usage

### Initialization
The database connection is automatically initialized when the plugin loads (in `Main.java`):
```java
databaseConnection = new DatabaseConnection(logger);
if (databaseConnection.connect()) {
    // Connection successful, migrations run automatically
}
```

### Using the Character Repository
```java
// Get the database connection from the main plugin instance
Connection conn = getDatabaseConnection().getConnection();
CharacterRepository repo = new CharacterRepository(conn, logger);

// Create a new character
Character character = new Character();
character.setPlayerId(playerUUID);
character.setCharacterName("MyHero");
character.setCharacterClass("Warrior");
repo.save(character);

// Find a character
Character found = repo.findByPlayerAndName(playerUUID, "MyHero");

// Update a character
found.setLevel(2);
found.setExperience(150);
repo.update(found);

// Get all characters for a player
List<Character> allCharacters = repo.findByPlayer(playerUUID);
```

## Dependencies

The following dependency was added to `build.gradle`:
```gradle
implementation 'org.mariadb.jdbc:mariadb-java-client:3.3.2'
```

## Migration System

Migrations are automatically run when the database connection is established. The system:
1. Creates a `migrations` table to track executed migrations
2. Checks which migrations have been run
3. Executes any pending migrations in order
4. Records successful migrations

To add a new migration:
1. Create a class implementing the `Migration` interface
2. Implement the `up()` method (creates/modifies tables)
3. Implement the `down()` method (reverts changes)
4. Add the migration to `MigrationManager.migrations` list

## Security Note

The SQL queries in `CharacterRepository.java` use prepared statements to prevent SQL injection attacks. The `MigrationManager.java` uses basic string concatenation but only for internal migration tracking, not user input.
