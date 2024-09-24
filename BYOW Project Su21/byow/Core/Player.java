package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.Core.FloorGenerator.*;

public class Player {

    public String name = "Purple Knight";
    public int x;
    public int y;
    public Engine engine;
    public TETile underTile;        //the tile the player is standing on

    public Player(Engine engine, String name) {
        this.engine = engine;
        this.name = name;
    }
}
