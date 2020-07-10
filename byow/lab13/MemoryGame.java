package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;
import edu.princeton.cs.introcs.Stopwatch;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        int seed = Integer.parseInt(args[0]);

        MemoryGame game = new MemoryGame(seed, 40, 40);
        game.startGame();
    }

    public MemoryGame(int seed, int width, int height) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < n; i++) {
            result.append((char) RandomUtils.uniform(rand, 97, 123));
        }

        return result.toString();
    }

    private Font font = new Font("Regular", Font.BOLD, 30);

    public void drawFrame(String s) {
        StdDraw.clear();
        StdDraw.setFont(font);
        StdDraw.text(width / 2, height / 2, s);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        for (char c : letters.toCharArray()) {
            drawFrame(c + "");
            delay(1);
            StdDraw.clear();
            delay(0.5);
        }
    }

    private void delay(double time) {
        Stopwatch sw = new Stopwatch();
        while (sw.elapsedTime() < time) {

        }
    }

    public String solicitNCharsInput(int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            while (!StdDraw.hasNextKeyTyped()) {

            }
            result.append(StdDraw.nextKeyTyped());
            drawFrame(result.toString());
        }
        return result.toString();
    }

    public void startGame() {
        int round = 1;

        while (true) {
            drawFrame("Round: " + round);
            delay(1);

            String expected = generateRandomString(round);
            flashSequence(expected);

            String actual = solicitNCharsInput(round);
            if (!expected.equals(actual)) {
                drawFrame("Game Over! You made it round: " + round);
                break;
            }

            round += 1;
        }
    }

}
