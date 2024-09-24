package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.IOException;
import java.io.File;

import java.util.Random;

/** @source for file reading & writing: https://www.w3schools.com/java/java_files_create.asp */

public class StringInput {

    public Engine engine;
    // saveString is the String loaded from the save file/inputted via the interactWithInputString method. NOT the engine saveTxt.
    public String inputString;

    public StringInput(Engine engine, String inputString) {
        this.engine = engine;
        this.inputString = inputString;
    }

    public void strInput(boolean playState) {
        if(!playState) {
            pregame();
        } else {
            gameInput(); }
    }

    /** either loads from a save string, or initializes the said save string. */
    private void pregame() {
        char init = Character.toLowerCase(inputString.charAt(0));
        inputString = inputString.substring(1);
        if(init == 'n') {
            engine.saveMove("n");
            loadSeed();
        } else if(init == 'l') {
            loadFromSave();
        }
    }

    /** Loads the seed from the save file. */
    private void loadSeed() {
        long seed = 0;
        for(int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);
            if(Character.isDigit(c)) {
                seed = seed * 10 + Character.getNumericValue(c);
            } else if(Character.toLowerCase(c) == 's') {
                inputString = inputString.substring(i + 1);
                engine.seed = seed;
                engine.r = new Random(seed);
                engine.playState = true;
                engine.player = new Player(engine, engine.name);
                for(int j = 0; j < engine.level; j++) {
                    engine.flgen = new FloorGenerator(engine);
                    engine.world = engine.flgen.generateFloor();
                }
                engine.saveMove(seed + "s");
                engine.enc = new Encounter(engine);

                // If there are more characters in the input string, continue looping through them. Otherwise, initiate the engine loop.
                gameInput();
                return;
            }
        }
    }

    private void loadFromSave() {
        StdDraw.clear(Color.black);
        StdDraw.text(256, 256, "Loading from save file...");
        StdDraw.show();
        StdDraw.pause(1000);
        String saveString = "";
        try {
            File saveFile = new File("byow/Core/save.txt");
            Scanner saveReader = new Scanner(saveFile);
            if(saveReader.hasNext()) {
                saveString = saveReader.next();
            }
            if(saveReader.hasNext()) {
                engine.score = Integer.parseInt(saveReader.next());
            }
            if(saveReader.hasNext()) {
                engine.name = saveReader.next();
            }
            if(saveReader.hasNext()) {
                engine.level = Integer.parseInt(saveReader.next());
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred: could not find the save file.");
        }
        saveString = saveString.concat(inputString);
        inputString = saveString;
        if(inputString.length() == 0) {
            System.out.println("Nothing found in save file. Could not load.");
            StdDraw.clear(Color.black);
            StdDraw.text(256, 256, "Nothing detected in save file. Please close the game and load new.");
            StdDraw.show();
            return;
        }
        pregame();
    }


    /** Handles controls that happened during a save file.
     * Upon reaching the end of the loaded string, the method should initialize the true engine loop and keyboard input. */
    private void gameInput() {
        if(inputString.length() == 0) {
            engine.engineLoop();
            return;
        }
        for(int index = 0; index < inputString.length(); index++) {
            char c = Character.toLowerCase(inputString.charAt(index));
            if (c == ':') {
                index++;
                c = Character.toLowerCase(inputString.charAt(index));
                if (c == 'q') {
                    engine.quit();
                    return;
                }
            } else if(c == 'w') {
                moveUp();
            } else if(c == 'a') {
                moveLeft();
            } else if(c == 's') {
                moveDown();
            } else if(c == 'd') {
                moveRight();
            }
        }
        engine.engineLoop();
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