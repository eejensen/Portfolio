package byow.Core;

import java.util.*;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class FloorGenerator {
    public Engine engine;
    private int HEIGHT = Engine.HEIGHT;
    private int WIDTH = Engine.WIDTH;
    private Random r;
    private TETile[][] world;
    private Stack<TilePackage> fringe;
    private Direction[] directions = {Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN};

   public FloorGenerator(Engine engine) {
       this.engine = engine;
       this.r = engine.r;
        this.fringe = new Stack<TilePackage>();
   }

   /** returns world */
   public TETile[][] world() { return this.world; }

    /** Step 1: Generate room -> hallway to adjacent rooms -> hallways etc.
     * Step 2: Walls: borders around floor space
     * Step 3: any optional aesthetics, ie. puddles, flowers, etc.
     * directly edits world (the generated floor)
     *
     * RETURNS a valid starting tile.
     */

    public TETile[][] generateFloor() {
        //initialize tiles
        this.world = new TETile[WIDTH][HEIGHT];
        for(int col = 0; col < WIDTH; col++) {
            for(int row = 0; row < HEIGHT; row++) {
                world[col][row] = Tileset.NOTHING;
            }
        }
        //gives the stack its first tilePackage
        fringe.push(new TilePackage(world[WIDTH / 2][HEIGHT / 2], WIDTH / 2, HEIGHT / 2));

        floor();
        TilePackage marker = clean();
        walls(marker);
        decorate();
        doorway();
        placePlayer();
        return this.world();
    }

    public TETile[][] floor() {
        while(!fringe.isEmpty()) {
            if(RandomUtils.bernoulli(r, 0.6)) {
                room();
            } else {
                hallway();
            }
        }
       randomStack();
       if(!fringe.isEmpty()) {
           floor();
       }
       return this.world;
   }

   private void room() {
       if(fringe.isEmpty()) {
           return;
       }
       TilePackage curr = fringe.pop();
       int xStart = curr.xPos - RandomUtils.uniform(r, 1, 5);
       int yStart = curr.yPos - RandomUtils.uniform(r, 1, 5);
       int width = RandomUtils.uniform(r, 4, 7);
       int height = RandomUtils.uniform(r, 4, 6);
       for (int x = xStart; x < xStart + width; x++) {
           for (int y = yStart; y < yStart + height; y++) {
               //skip any edge cases (neighbors include null tiles)
               if (x > WIDTH - 1 || y > HEIGHT - 1 || x <= 0 || y <= 0) {
                   return;
               }
               if (curr.isAdjacentTo(null)) {
                   //bypass loops
                   x = WIDTH * 3;
                   y = HEIGHT * 3;
                   break;
               }
               world[x][y] = Tileset.FLOOR;
               curr = new TilePackage(world[x][y], x, y);
           }
       }
       int rx = RandomUtils.uniform(r, xStart, xStart + width - 1);
       int ry = RandomUtils.uniform(r, yStart, yStart + height - 1);
       if(rx < WIDTH - 1 && ry < HEIGHT - 1) {
           fringe.push(new TilePackage(world[rx][ry], rx, ry));
       }
   }

   /** generates a hallway */
   private void hallway() {
       if(fringe.isEmpty()) {
           return;
       }
       Direction direction = directions[RandomUtils.uniform(r, 0, 4)];
       TilePackage curr = fringe.pop();
       while (!curr.neighborContains(null)) {
           int x = curr.xPos;
           int y = curr.yPos;
           //check to see if curr is already adjacent to floor
           if(curr.numAdjacent(Tileset.FLOOR) == 2) {
               world[x][y] = Tileset.FLOOR;
               return;
           }
           if (direction == Direction.RIGHT) {
               world[x][y] = Tileset.FLOOR;
               curr = new TilePackage(world[x + 1][y], x + 1, y);
           }
           if (direction == Direction.LEFT) {
               world[x][y] = Tileset.FLOOR;
               curr = new TilePackage(world[x - 1][y], x - 1, y);
           }
           if (direction == Direction.UP) {
               world[x][y] = Tileset.FLOOR;
               curr = new TilePackage(world[x][y + 1], x, y + 1);
           }
           if (direction == Direction.DOWN) {
               world[x][y] = Tileset.FLOOR;
               curr = new TilePackage(world[x][y - 1], x, y - 1);
           }
       }
       if(!curr.neighborContains(null)) {
           fringe.push(curr);
       }
       if(RandomUtils.bernoulli(r, 0.6)) {
           hallway();
       }
   }

   /** if stack is empty, tries to push a random tile to fringe. */
   public void randomStack() {
       for(int i = 3; i > 0; i--) {
           int x = RandomUtils.uniform(r, 1, WIDTH - 1);
           int y = RandomUtils.uniform(r, 1, HEIGHT - 1);
           TilePackage tile = new TilePackage(world[x][y], x, y);
           if(tile.neighborContains(null)) {
               continue;
           } else if(tile.numAdjacent(Tileset.FLOOR) > 2) {
               continue;
           } else { fringe.push(tile); }
       }
   }
   /** cleans up the floor tiles. returns the valid start tile for walls() to use */
   public TilePackage clean() {
       boolean[][] visited = new boolean[WIDTH][HEIGHT];
       for(int i = 0; i < visited.length; i++) {
           Arrays.fill(visited[i], false);
       }
       Stack<TilePackage> tileStack = new Stack<>();

       //initialize temp tile grid
       TETile[][] cleaned = new TETile[WIDTH][HEIGHT];
       for(int i = 0; i < cleaned.length; i++) {
           Arrays.fill(cleaned[i], Tileset.NOTHING);
       }
       TilePackage start = new TilePackage(world[WIDTH / 2][HEIGHT / 2], WIDTH / 2, HEIGHT / 2);
       //ensure that starting tile is a floor tile
       while(start.tile != Tileset.FLOOR) {
           int rx = RandomUtils.uniform(r, 30, 50);
           int ry = RandomUtils.uniform(r, 10, 20);
           start = new TilePackage(world[rx][ry], rx, ry);
       }
       tileStack.push(start);
       while(!tileStack.isEmpty()) {
           TilePackage curr = tileStack.pop();
           if(curr.tile == null || curr.xPos >= WIDTH || curr.yPos >= HEIGHT || curr.xPos < 0 || curr.yPos < 0) {
               continue;
           } else if(visited[curr.xPos][curr.yPos]) {
               continue;
           } else if(curr.isAdjacentTo(null)) {
               cleaned[curr.xPos][curr.yPos] = Tileset.NOTHING;
           } else if(curr.tile == Tileset.FLOOR && curr.isAdjacentTo(Tileset.FLOOR)) {
               cleaned[curr.xPos][curr.yPos] = Tileset.FLOOR;
               TilePackage[] currAdj = curr.adjTiles();
               for(TilePackage tile : currAdj) {
                   tileStack.push(tile);
               }
           }
           visited[curr.xPos][curr.yPos] = true;
       }
       this.world = cleaned;
       return start;
   }

   /** set up walls after cleaning floor layout */
   public void walls(TilePackage start) {
       boolean[][] visited = new boolean[WIDTH][HEIGHT];
       for(int i = 0; i < visited.length; i++) {
           Arrays.fill(visited[i], false);
       }
       Stack<TilePackage> tileStack = new Stack<>();

       tileStack.push(start);
       while(!tileStack.isEmpty()) {
           TilePackage curr = tileStack.pop();
           if(curr.tile == null || curr.xPos >= WIDTH || curr.yPos >= HEIGHT || curr.xPos < 0 || curr.yPos < 0) {
               continue;
           } else if(visited[curr.xPos][curr.yPos]) {
               continue;
           } else if(curr.tile == Tileset.NOTHING && curr.isAdjacentTo(Tileset.FLOOR)) {
               world[curr.xPos][curr.yPos] = Tileset.WALL;
           } else if(curr.tile == Tileset.FLOOR) {
               TilePackage[] currAdj = curr.adjTiles();
               for(TilePackage tile : currAdj) {
                   tileStack.push(tile);
               }
           }
           visited[curr.xPos][curr.yPos] = true;
       }
   }

   public void decorate() {
       int biome = RandomUtils.uniform(r, 0, 4);
       if(biome == 0) {
           biomeHelper(Tileset.GRASS, Tileset.FLOWER);
       } else if(biome == 1) {
           biomeHelper(Tileset.SAND, Tileset.MOUNTAIN);
       } else if(biome == 2) {
           biomeHelper(Tileset.TREE, Tileset.GRASS);
       } else if(biome == 3) {
           biomeHelper(Tileset.MOUNTAIN, Tileset.TREE);
       } else {biomeHelper(Tileset.FLOOR, Tileset.GRASS); }
   }

   private void biomeHelper(TETile tile1, TETile tile2) {
       for (int w = 0; w < WIDTH; w++) {
           for (int h = 0; h < HEIGHT; h++) {
               TilePackage curr = new TilePackage(world[w][h], w, h);
               if (curr.tile == Tileset.WALL || curr.tile == Tileset.NOTHING) {
                   continue;
               }
               if(!curr.neighborContains(Tileset.WALL) && ((RandomUtils.bernoulli(r, 0.1) || (RandomUtils.bernoulli(r, 0.3) && curr.neighborContains(Tileset.WATER))))) {
                   world[w][h] = Tileset.WATER;
               } else if(curr.tile == Tileset.FLOOR && RandomUtils.bernoulli(r, 0.7)) {
                   world[w][h] = tile1;
               } else if(curr.tile == Tileset.FLOOR && RandomUtils.bernoulli(r, 0.6)) {
                   world[w][h] = tile2;
               }
           }
       }
   }

   /** Places a doorway on a random wall tile */
   public void doorway() {
       while(true) {
           int x = RandomUtils.uniform(r, 0, WIDTH);
           int y = RandomUtils.uniform(r, 0, HEIGHT);
           if(world[x][y] == Tileset.WALL) {
               world[x][y] = Tileset.UNLOCKED_DOOR;
               return;
           } else {
               continue;
           }
       }
   }

   public void placePlayer() {
       while(true) {
           int x = RandomUtils.uniform(r, 0, WIDTH);
           int y = RandomUtils.uniform(r, 0, HEIGHT);
           if(world[x][y] != Tileset.WALL && world[x][y] != Tileset.NOTHING && world[x][y] != Tileset.WATER) {
               engine.player.x = x;
               engine.player.y = y;
               engine.player.underTile = world[x][y];
               world[x][y] = Tileset.AVATAR;
               return;
           }
       }
   }

    /** Packages together a tile and its coordinates */
   public class TilePackage {
       public TETile tile;
       public int xPos;
       public int yPos;

       public TilePackage(TETile tile, int x, int y) {
           this.tile = tile;
           this.xPos = x;
           this.yPos = y;
       }

       /** @Source https://www.geeksforgeeks.org/overriding-equals-method-in-java/ */
       @Override
       public boolean equals(Object o) {
           if(o == this) {
               return true;
           }
           if(o == null) {
               return false;
           }
           if(!(o instanceof TilePackage)) {
               return false;
           }
           TilePackage c = (TilePackage) o;
           return(this.xPos == c.xPos && this.yPos == c.yPos);
       }

       //returns an array of all neighbors to tile, including corners
       public TETile[] neighbors() {
           TETile[] neighbors = new TETile[8];
           int pos = 0;
           for(int y = yPos + 1; y >= yPos - 1; y--) {
               for(int x = xPos - 1; x <= xPos + 1; x++) {
                   //if neighbor coord is out of bounds
                   if(x > WIDTH - 1 || y > HEIGHT - 1 || x < 0 || y < 0) {
                       neighbors[pos] = null;
                       pos++;
                   //if neighbor is itself
                   } else if(x == xPos && y == yPos) {
                       continue;
                   } else {
                       neighbors[pos] = world[x][y];
                       pos++;
                   }
               }
           }
           return neighbors;
       }

        /** checks if neighboring tiles contain a certain type */
        public boolean neighborContains(TETile tile) {
            TETile[] neighbors = this.neighbors();
            for(TETile neighbor : neighbors) {
                if(tile == neighbor) {
                    return true;
                }
            }
            return false;
        }

        public boolean isAdjacentTo(TETile tile) {
            TilePackage[] neighbors = this.adjTiles();
            return(neighbors[0].tile == tile || neighbors[1].tile == tile || neighbors[2].tile == tile || neighbors[3].tile == tile);
        }

        /** returns the number of neighbor tiles of type tile */
        public int numAdjacent(TETile tile) {
            int count = 0;
            TETile[] adjacent = new TETile[4];
            TETile[] neighbors = this.neighbors();
            adjacent[0] = neighbors[1];
            adjacent[1] = neighbors[3];
            adjacent[2] = neighbors[4];
            adjacent[3] = neighbors[6];
            for(TETile adj : adjacent) {
                if(adj == tile) {
                    count++;
                }
            }
            return count;
        }

        public TilePackage[] adjTiles() {
            TilePackage[] tiles = new TilePackage[4];
            TETile[] neighbors = neighbors();
            tiles[0] = new TilePackage(neighbors[1], xPos, yPos + 1);
            tiles[1] = new TilePackage(neighbors[3], xPos - 1, yPos);
            tiles[2] = new TilePackage(neighbors[4], xPos + 1, yPos);
            tiles[3] = new TilePackage(neighbors[6], xPos, yPos - 1);
            return tiles;
        }
   }
}
