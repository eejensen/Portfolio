package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;


public class KeyboardInput {

    public Engine engine;

    public KeyboardInput(Engine engine) {
        this.engine = engine;
    }

    public void keyInput(boolean playState) {
        if(!playState) {
            pregame();
        }
        else { gameInput(); }
    }

    /** Handles keyboard input in the GUI. */
    private void pregame() {

        //setting up the GUI canvas
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.PLAIN, 16);
        StdDraw.setFont(font);
        StdDraw.setScale(0, 512);

        StdDraw.text(256, 256 + 32, "coronation");
        StdDraw.text(256, 256 + 16, "(N) New Game");
        StdDraw.text(256, 256, "(L) Load Game");
        StdDraw.text(256, 256 - 16, "(Q) Quit");
        StdDraw.show();

        while(true) {
            if(StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if(Character.toLowerCase(c) == 'n') {
                    String name = askForName();
                    engine.saveMove("n");
                    engine.name = name;
                    askForSeed();
                } else if(Character.toLowerCase(c) == 'l') {
                    engine.strIn = new StringInput(engine, "l");
                    engine.strIn.strInput(engine.playState);
                } else if(Character.toLowerCase(c) == 'q') {
                    StdDraw.clear(Color.black);
                    StdDraw.text(256, 256, "Program has quit. Please close the game window.");
                    StdDraw.show();
                }
            }
        }
    }

    /** Currently unused. Theoretically could be used to create multiple save files. */
    private String askForName() {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(256, 256, "Please enter your character's name, then hit Enter to submit.");
        StdDraw.show();

        String name = "";
        while(true) {
            if(StdDraw.isKeyPressed(KeyEvent.VK_ENTER)) {
                if(name.length() <= 0) {
                    StdDraw.clear(Color.black);
                    StdDraw.text(256, 256, "Please enter a valid name.");
                    StdDraw.show();
                    continue;
                } else {
                    StdDraw.clear(Color.black);
                    StdDraw.text(256, 256, "Welcome, " + name + ".");
                    StdDraw.show();
                    StdDraw.pause(1500);
                    return name;
                }
            }
            if(StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                name = name.concat(String.valueOf(c));
                StdDraw.clear(Color.black);
                StdDraw.text(256, 256, name);
                StdDraw.show();
            }
        }
    }
    /** Prompts the player to enter a seed upon loading a new game. Sets the seed and related engine variables. */
    private void askForSeed() {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(256, 256, "Please enter a seed, then hit \'s\' to submit.");
        StdDraw.show();
        long seed = 0;
        while(true) {
            if(StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if(c == 's') {
                    if(seed <= 0) {
                        StdDraw.clear(Color.BLACK);
                        StdDraw.text(256, 256, "Must be a valid seed of at least 1 number.");
                        StdDraw.show();
                        StdDraw.pause(3000);
                        continue;
                    }
                    StdDraw.clear(Color.BLACK);
                    StdDraw.text(256, 256, "Now generating world with seed " + seed + ".");
                    StdDraw.show();
                    StdDraw.pause(1500);

                    //engine setup (should be 1 time only)
                    engine.seed = seed;
                    engine.r = new Random(seed);
                    engine.player = new Player(engine, engine.name);
                    engine.playState = true;
                    engine.flgen = new FloorGenerator(engine);
                    engine.world = engine.flgen.generateFloor();
                    engine.saveMove(engine.seed + "s");
                    engine.enc = new Encounter(engine);
                    engine.intro();
                }
                if(Character.isDigit(c)) {
                    StdDraw.clear(Color.BLACK);
                    seed = seed * 10 + Character.getNumericValue(c);
                    if(seed <= 0) {
                        StdDraw.clear(Color.BLACK);
                        StdDraw.text(256, 256, "Invalid seed. Please try again.");
                        StdDraw.show();
                        StdDraw.pause(3000);
                        seed = 0;
                        continue;
                    }
                    StdDraw.text(256, 256, Long.toString(seed));
                    StdDraw.show();
                }
            }
        }
    }

    /** handles controls that happen during the game */
    private void gameInput() {
        if(StdDraw.isKeyPressed(KeyEvent.VK_SHIFT) && StdDraw.isKeyPressed(KeyEvent.VK_SEMICOLON)) {
            while(true) {
                if (StdDraw.isKeyPressed(KeyEvent.VK_Q)) {
                    engine.quit();
                }
            }
        } else if(StdDraw.isKeyPressed(KeyEvent.VK_W)) {
            moveUp();
        } else if(StdDraw.isKeyPressed(KeyEvent.VK_A)) {
            moveLeft();
        } else if(StdDraw.isKeyPressed(KeyEvent.VK_S)) {
            moveDown();
        } else if(StdDraw.isKeyPressed(KeyEvent.VK_D)) {
            moveRight();
        }
    }

    private void moveUp() {
        int x = engine.player.x;
        int y = engine.player.y + 1;
        if(engine.world[x][y] == Tileset.WALL) {
            return;
        } if(engine.world[x][y] == Tileset.NOTHING) {
            return;
        } if(engine.world[x][y] == Tileset.WATER) {
            return;
        }
        TETile temp = engine.world[x][y];
        engine.world[x][y] = Tileset.AVATAR;
        engine.world[x][y - 1] = engine.player.underTile;
        engine.player.underTile = temp;
        engine.player.y = y;
        engine.saveMove("w");
    }

    private void moveDown() {
        int x = engine.player.x;
        int y = engine.player.y - 1;
        if(engine.world[x][y] == Tileset.WALL) {
            return;
        } if(engine.world[x][y] == Tileset.NOTHING) {
            return;
        } if(engine.world[x][y] == Tileset.WATER) {
            return;
        }
        TETile temp = engine.world[x][y];
        engine.world[x][y] = Tileset.AVATAR;
        engine.world[x][y + 1] = engine.player.underTile;
        engine.player.underTile = temp;
        engine.player.y = y;
        engine.saveMove("s");
    }

    private void moveLeft() {
        int x = engine.player.x - 1;
        int y = engine.player.y;
        if(engine.world[x][y] == Tileset.WALL) {
            return;
        } if(engine.world[x][y] == Tileset.NOTHING) {
            return;
        } if(engine.world[x][y] == Tileset.WATER) {
            return;
        }
        TETile temp = engine.world[x][y];
        engine.world[x][y] = Tileset.AVATAR;
        engine.world[x + 1][y] = engine.player.underTile;
        engine.player.underTile = temp;
        engine.player.x = x;
        engine.saveMove("a");
    }

    private void moveRight() {
        int x = engine.player.x + 1;
        int y = engine.player.y;
        if(engine.world[x][y] == Tileset.WALL) {
            return;
        } if(engine.world[x][y] == Tileset.NOTHING) {
            return;
        } if(engine.world[x][y] == Tileset.WATER) {
            return;
        }
        TETile temp = engine.world[x][y];
        engine.world[x][y] = Tileset.AVATAR;
        engine.world[x - 1][y] = engine.player.underTile;
        engine.player.underTile = temp;
        engine.player.x = x;
        engine.saveMove("d");
    }
}
