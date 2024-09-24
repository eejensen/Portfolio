package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public Random r;
    public boolean playState = false;
    public long seed;
    public FloorGenerator flgen;
    public KeyboardInput keyIn = new KeyboardInput(this);
    public StringInput strIn;
    public Player player;
    public String name;
    public TETile[][] world;
    public String saveString = "";
    public int level = 1;
    public int score = 0;
    public Encounter enc;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        keyIn.keyInput(playState);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        strIn = new StringInput(this, input);
        strIn.strInput(playState);
        engineLoop();
        return world;
    }

    /** Main loop of the engine. Handles movement cycles, events, etc. */
    public void engineLoop() {
        ter.initialize(WIDTH, HEIGHT);
        while(playState) {
            keyIn.keyInput(playState);
            ter.renderFrame(world, player);
            updateHUD();
            enc.encounterCheck();
            if(player.underTile == Tileset.UNLOCKED_DOOR) {
                levelComplete();
            }
            StdDraw.pause(100);
        }
    }

    public void updateHUD() {
        if(StdDraw.mouseX() >= WIDTH || StdDraw.mouseX() < 0 || StdDraw.mouseY() >= HEIGHT || StdDraw.mouseY() < 0) {
            return;
        }
        TETile hover = world[(int) StdDraw.mouseX()][(int) StdDraw.mouseY()];
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(0, HEIGHT - 0.5, "mouse currently on tile: " + hover.description());
        StdDraw.show();
    }

    public void levelComplete() {
        level++;
        if(level > 5) {
            endGame();
            return;
        }
        flgen = new FloorGenerator(this);
        world = flgen.generateFloor();

        textPrinter("Region complete. Only " + (6 - level) + " more to go!");
        textPrinter("Now entering: region " + level + ".");
    }

    public void intro() {
        ter.initialize(WIDTH, HEIGHT);
        textPrinter("Dearest " + player.name + ",");
        textPrinter("I hope you have been successful in spreading peace in our lands.");
        textPrinter("I do yearn for your swift return home, as the coronation is yet upon us.");
        textPrinter("After all, my coronation would not be complete without my beloved knight-to-be!");
        textPrinter("Do hurry back, the castle has not been the same without you.");
        textPrinter("Wishing you a swift and steady journey.");
        textPrinter("Your crown princess, Azura.");
        StdDraw.clear(Color.black);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "NARRATOR HINT: Find the doorway hidden in the walls of each region.");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Use WASD to move around the map via the purple knight avatar.");
        StdDraw.show();
        StdDraw.pause(5000);
        textPrinter("Now entering: region " + level + ".");
        engineLoop();
    }

    public void endGame() {
        textPrinter("Finally, you reach the castle. You join the royal promenade just in time.");
        if(score > 8) {
            textPrinter("The citizens look upon you with confidence, trusting in a better future through your courage and good deeds.");
            textPrinter("The kingdom's prospects are bright.");
        } else if(score < -2) {
            textPrinter("As you ride by on your royal steed, the citizens glare upward with disdain.");
            textPrinter("Their future governed by Princess Azura is marred by your poor reputation.");
            textPrinter("One can only hope conditions will improve, in time.");
        } else {
            textPrinter("The citizens look upon you with curiosity, unsure of how their lives will change with you at the helm.");
            textPrinter("Only time may tell what the future holds.");
        }
        textPrinter("Your score: " + score + " points.");
        quit();
        return;
    }

    public void textPrinter(String s) {
        StdDraw.setPenColor(Color.white);
        for(int i = 0; i < s.length(); i++) {
            String soFar = s.substring(0, i + 1);
            StdDraw.clear(Color.black);
            StdDraw.text(WIDTH / 2, HEIGHT / 2, soFar);
            StdDraw.show();
            StdDraw.pause(50);
        }
        StdDraw.pause(1500);
    }

    public void saveMove(String s) {
        saveString = saveString.concat(s);
    }

    /** Quits the game and writes saveString to save.txt. */
    public void quit() {
        try {
            File saveFile = new File("byow/Core/save.txt");
            FileWriter saveWriter = new FileWriter(saveFile);
            saveWriter.write(saveString);
            saveWriter.write(" " + Integer.toString(score));
            saveWriter.write(" " + player.name);
            saveWriter.write(" " + level);
            saveWriter.close();
        } catch (IOException e) { System.out.println("Error in saving to file."); }
        playState = false;
        StdDraw.clear(Color.black);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Game progress saved. Please close the game window.");
        StdDraw.show();
        StdDraw.pause(1000000);
    }
}
