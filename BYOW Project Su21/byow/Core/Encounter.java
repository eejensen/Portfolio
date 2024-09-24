package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Encounter {

    public Engine engine;
    public int textLevel;

    public Encounter(Engine engine) {
        this.engine = engine;
        this.textLevel = engine.HEIGHT / 2 + 3;
    }

    public void encounterCheck() {
        if(RandomUtils.bernoulli(engine.r, 0.01)) {
            encounter();
        }
    }

    public void encounter() {
        StdDraw.clear(Color.black);
        StdDraw.text(engine.WIDTH / 2, engine.HEIGHT / 2 + 5, "ENCOUNTER");
        StdDraw.show();
        StdDraw.pause(1000);
        int i = RandomUtils.uniform(engine.r, 0, 5);
        if(i == 0) {
            enemy();
        } else if(i == 1) {
            child();
        } else if(i == 2) {
            bear();
        } else if(i == 3) {
            thief();
        } else {
            townie();
        }
    }

    public void drawOptions(String str1, String str2, String str3, String str4) {
        StdDraw.text(35, textLevel - 1, "(1) " + str1);
        StdDraw.text(45, textLevel - 1, "(2) " + str2);
        StdDraw.text(35, textLevel - 2, "(3) " + str3);
        StdDraw.text( 45, textLevel - 2, "(4) " + str4);
        StdDraw.show();
    }

    public void displayResult(String flavor, int points) {
        StdDraw.clear(Color.black);
        StdDraw.text(engine.WIDTH / 2, engine.HEIGHT / 2 + 1, flavor);
        StdDraw.text(engine.WIDTH / 2, engine.HEIGHT / 2  - 1, "You got " + points + " knight points.");
        StdDraw.show();
        StdDraw.pause(4000);
        engine.score += points;
    }

    public void enemy() {
        StdDraw.text(engine.WIDTH / 2, textLevel, "It's an enemy knight! Their weapon is already drawn.");
        StdDraw.show();
        StdDraw.pause(1000);
        drawOptions("attack", "defend", "parry", "run");
        while(true) {
            if(StdDraw.isKeyPressed(KeyEvent.VK_1)) {
                if(RandomUtils.bernoulli(engine.r, 0.7)) {
                    displayResult("Success! You won the duel.", 5);
                } else {  displayResult("The enemy knight won... you take a while to recover.", -2); }
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_2)) {
                if (RandomUtils.bernoulli(engine.r, 0.9)) {
                    displayResult("You successfully defended the attack, but the enemy escaped.", 2);
                } else { displayResult("You shielded through the attack, just barely.", 0); }
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_3)) {
                if (RandomUtils.bernoulli(engine.r, 0.6)) {
                    displayResult("A perfect parry! You beat the enemy knight soundly.", 7);
                } else { displayResult("The enemy knight disarms you. You spend 10 minutes painfully limping around for where your sword flew off to.", -3); }
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_4)) {
                displayResult("The enemy knight easily catches up to you and gets a decisive attack from behind.", -10);
                return;
            }
        }
    }

    public void child() {
        StdDraw.text(engine.WIDTH / 2, textLevel, "It's a lone kid, they seem a little frightened.");
        StdDraw.show();
        StdDraw.pause(1000);
        drawOptions("talk", "comfort", "slap", "ignore");
        while(true) {
            if(StdDraw.isKeyPressed(KeyEvent.VK_1)) {
                if(RandomUtils.bernoulli(engine.r, 0.8)) {
                    displayResult("The kid seems to recognize you by your armor. They say they want to be a knight too one day.", 2);
                } else {  displayResult("The kid screams \"STRANGER DANGER!\" and runs away.", 0); }
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_2)) {
                displayResult("You speak softly at eye level. The kid seems more relaxed, and their parent finally finds them.", 3);
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_3)) {
                displayResult("Why would you do that?? Some nearby villagers saw too....", -10);
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_4)) {
                displayResult("You keep walking since you're in a hurry, hoping someone else will find the kid...", -1);
                return;
            }
        }
    }

    public void bear() {
        StdDraw.text(engine.WIDTH / 2, textLevel, "It's a bear! It looks kinda cute though...");
        StdDraw.show();
        StdDraw.pause(1000);
        drawOptions("attack", "yell", "sneak away", "run");
        while(true) {
            if(StdDraw.isKeyPressed(KeyEvent.VK_1)) {
                displayResult("Why would you attack a bear???? You wake up 2 hours later with bruised ribs.", -3);
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_2)) {
                if(RandomUtils.bernoulli(engine.r, 0.8)) {
                    displayResult("You yell and wave your arms. It seems to be mildly surprised and walks away.", 1);
                } else { displayResult("That just made it angry.... you get bear slapped.", -4); }
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_3)) {
                displayResult("You successfully sneak off.", 0);
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_4)) {
                displayResult("It chases you! You escape up a tree, but the bear lingers below for a while...", -1);
                return;
            }
        }
    }

    public void thief() {
        StdDraw.text(engine.WIDTH / 2, textLevel, "You sense something waiting in the shadows...");
        StdDraw.show();
        StdDraw.pause(1000);
        drawOptions("attack", "defend", "outwit", "run");
        while(true) {
            if(StdDraw.isKeyPressed(KeyEvent.VK_1)) {
                if(RandomUtils.bernoulli(engine.r, 0.7)) {
                    displayResult("You draw your sword, but it seems the thief didn't want the trouble and runs off.", 1);
                } else if(RandomUtils.bernoulli(engine.r, 0.6)) {
                    displayResult("You swipe in its direction only to nearly hit a family of bunnies! :(", -2);
                } else { displayResult("You're attacked before you can react... the thief takes some of your money.", -2); }
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_2)) {
                displayResult("You ready your shield but nothing happens... guess it pays to be careful.", 1);
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_3)) {
                if(RandomUtils.bernoulli(engine.r, 0.5)) {
                    displayResult("You scan the environment and see a trap laid ahead, which you easily avoid.", 5);
                } else { displayResult("While looking into every nook and cranny, someone lunges from behind and steals some money.", -3); }
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_4)) {
                displayResult("You run right into a trap! A thief easily snags some of your coins.", -3);
                return;
            }
        }
    }

    public void townie() {
        StdDraw.text(engine.WIDTH / 2, textLevel, "A friendly-looking townsperson waves in your direction.");
        StdDraw.show();
        StdDraw.pause(1000);
        drawOptions("chat", "offer help", "pickpocket", "ignore");
        while(true) {
            if(StdDraw.isKeyPressed(KeyEvent.VK_1)) {
                if(RandomUtils.bernoulli(engine.r, 0.6)) {
                    displayResult("You chat about the country and learn new perspectives.", 3);
                } else if(RandomUtils.bernoulli(engine.r, 0.5)) {
                    displayResult("You hear their grievances, and promise to do better as Highness's knight-to-be.", 4);
                } else { displayResult("You lose an hour of travel time discussing regional teatime etiquette.", 0); }
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_2)) {
                displayResult("You help around their garden... it reflects well on the crown.", 5);
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_3)) {
                displayResult("You gain 5 rusty coppers, but at what cost?", -8);
                return;
            } else if(StdDraw.isKeyPressed(KeyEvent.VK_4)) {
                displayResult("Acting all high-and-mighty reflects poorly on the crown.", -4);
                return;
            }
        }
    }
}
