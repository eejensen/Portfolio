# Build Your Own World Design Document

**Partner 1: Emma Jensen**

**Partner 2:**

## Classes and Data Structures
**Engine**: The main engine of the game.
* WIDTH: Tile width of the game board.
* HEIGHT: Tile height of the game board.
* Random r: The random used by all game classes to randomly generate numbers and outcomes.
* playState: Boolean indicating when the game is in the interactive dungeon stage.
* seed: long variable used as seed for engine Random. Is stored at the beginning of the save file.
* flgen: Floor generator instance that generates pseudorandom dungeon floors according to the seed.
* keyIn: KeyboardInput instance. Used to respond to keyboard controls.
* strIn: String input instance. Used to read string inputs and the save file.
* player: Player instance of the player in the game. Is stored in the save file, at token position 2.
* name: String of the player's name, stored in the engine until Player instance is instantiated.
* world: TETile[][] that stores the tiles of the current dungeon floor.
* saveString: Concatenates keyboard inputs to send to FileWriter upon quitting the game. Stored at token position 0.
* level: int for dungeon level. Is stored in the save file at token position 3.  
* score: int that tallies up points from encounters. Is stored in the save file at token position 1.
* enc: Encounter instance that handles pseudorandom encounters, using engine Random r.

**StringInput**: Handles string input, either passed as an argument or read from the save file.
* engine: Pointer to the engine instance it belongs to.
* inputString: String read as input, whether from an argument passed to main or a save file.

**KeyboardInput**: Handles keyboard input during the game.
* engine: Pointer to the engine instance it belongs to.

**FloorGenerator**: Generates a dungeon level.
* HEIGHT: int, member of Engine.
* WIDTH: int, member of Engine.
* Random r: member of Engine.
* TETile[][] world: Tile grid being generated.
* Stack<TilePackage> fringe: stack of TilePackage objects to be used by hallway() and floor() as starting points.
* Direction[] directions = array of the 4 directions (as enums).

**TilePackage**: private helper class in FloorGenerator. Abstracts tile instance and tile's position into one object.
* tile: the TETile.
* xPos: X-coordinate of the specified tile.
* yPos: Y-coordinate of the specified tile.

**Direction**: an enum class of the 4 primary directions.

**Player**: A class to store the player's name, position on the board, and the tile the player is standing over.
* x: Player's x-position.
* y: Player's y-position.
* engine: Pointer to the engine instance it belongs to.
* underTile: TETile currently being overridden by the player's avatar on the board.

**Encounter**: Handles the random occurrence of encounters while in the dungeon.
* engine: Pointer to the engine instance it belongs to.
* textLevel: Standard text level used to simplify formatting.

## Algorithms
**Engine**
* interactWithKeyboard: Calls keyInput from the engine's KeyboardInput instance, passes in playState as an argument.
* interactWithInputString: Initializes StringInput instance, and calls strInput with playState passed in as an argument. After processing input string, it initializes the engine loop.
* engineLoop: Main loop of the game. Updates HUD, checks for keyboard input, and updates board rendering. Pauses loop if an encounter occurs or if the level is completed. Loop occurs every 0.1 seconds.
* Update HUD: Once per engine loop, checks mouse position to see what tile it is hovering over. Using StdDraw, it displays the tile description in the top left corner.
* levelComplete: Method called whenever the player stands on an unlocked door. It updates the level number, generates a new dungeon floor, and displays text indicating the completion of a level. If level exceeds 5 (the last level), it calls endGame().
* intro: Method called after inputting the starting information. Initializes the TERenderer, displays narrative text, and then initializes the engineLoop for the first floor.
* endGame: Displays a narrative ending that is determined by the player's cumulative score. Finishes by Calling quit().
* textPrinter: Helper function to make displaying text in a typing style simpler. Takes a string, displays it one character more at a time, then returns.
* saveMove: appends the most recent move to the saveString as a character representing its keyboard input.
* quit: Uses a FileWriter instance to write saveString to "save.txt." Shows "game quit" message.

**StringInput**
* StringInput: Constructor that sets instance variables.
* strInput: Primary string input handler. Inputs to pregame() if the game has not yet begun, otherwise calls gameInput().
* pregame: Takes the first character of inputString and removes it. If the character is 'n', it initializes creating a new game by calling loadSeed(). If the character is 'l', it calls loadFromSave().
* loadSeed: Gets all characters that are digits from the beginning of inputString. Upon reaching character 's', the seed is completed and the engine parts are initialized based on the seed.
* loadFromSave: Uses a Scanner instance to read the contents of "save.txt". The first token is used as the input seed and subsequent instructions, the second denotates the score, the third stores the player's name, and the fourth indicates the level number at the point of saving. Initializes loading the save file by calling pregame().

**FloorGenerator**
* FloorGenerator: Constructor that initializes tile[][] world, initializes fringe and pushes the center tile to the stack.
* world: returns the TETile[][] world. Used to send a finished floor to the TERenderer.
* generateFloor: Generates new floor by initializing tiles and calling methods floor(), clean(), walls(), decorate(), doorway(), and placePlayer(). Returns the TETile[][] world that it generates. 
* floor: Generates the floor using Random r and helper methods room() and hallway(). It alternates pseudorandomly in generating hallways and floors, pushing key tile points (ie. the end of a hallway) to the fringe. Upon completing, it uses randomStack to determine if the board is populated enough, pushing a random tile to the fringe if not.
* room: Generates a room of dimensions (4-6) x (4-5), starting at the popped TilePackage plus a random offset of max (-4, -4). Pushes a random tile from within the generated room to the fringe.
  Exits the room creation loop if the current tile reaches an edge case. Ends by pushing a central room tile to the stack.
* hallway: Creates a random hallway in a random direction. Truncates hallway before reaching the very edge, or when adjacent to another floor tile. upon completion, the current tile is pushed to the stack. At random, another hallway may be generated.
* randomStack: To ensure fuller board generation, a random TilePackage may be pushed to the stack. randomStack tries 3 times to find a valid tile, pushing to fringe if successful.
* clean: Removes any unconnected (non-adjacent) floor tiles from the 2D tile array by overwriting world.
* walls: Generates walls around the floor space.
* decorate: Decorates floor tiles according to a randomly-decided biome.
* biomeHelper: Uses randomly decided biome from decorate to populate the dungeon level with certain TETiles according to theme.
* doorway: Selects a random coordinate in world. If the selected location is a wall tile, it places a doorway there.
* placePlayer: Selects a random coordinate in world. If the selected tile is a valid walkable tile(not Tileset.nothing, wall, or water), it places the avatar there, updates the player's location, and stores the tile underneath.
  
    **TilePackage**
    * equals: Overridden equals method returns true if x and y positions are the same.
    * neighbors: returns an array of the 8 tiles surrounding the specified tile, indexed left -> right, top -> bottom (not including itself).
    * neighborContains: checks to see if the neighboring tiles of a specified tile include a given tile type.
    * isAdjacentTo: check if a tile is adjacent to any tiles of a specified type. Adjacent refers to the tiles directly above, below, left, or right of the tile.
    * numAdjacent: returns the number of tiles of an indicated type that are adjacent to the given tile.
    * adjTiles: returns an array of a TilePackage's 4 adjacent TilePackages.
    
**
## Persistence
