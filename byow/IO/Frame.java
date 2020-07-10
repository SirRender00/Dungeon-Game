package byow.IO;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;
import java.awt.Color;


public class Frame {

    private static void defaultSetup(int width, int height) {
        StdDraw.setCanvasSize(width * 16, height * 16);
        Font font = new Font("Cinzel Black", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.enableDoubleBuffering();
    }

    public static void showMenuScreen(int width, int height) {
        defaultSetup(width, height);
        StdDraw.text(width / 2, height * 2 / 3, "CS 61B: The Game");

        StdDraw.setFont(new Font("Cinzel Black", Font.PLAIN, 20));
        StdDraw.text(width / 2, height / 2, "(N) New Game");
        StdDraw.text(width / 2, height / 2 - 2, "(L) Load Game");
        StdDraw.text(width / 2, height / 2 - 4, "(Q) Quit");


        StdDraw.show();
    }

    public static void showEnterSeed(int width, int height) {
        defaultSetup(width, height);
        StdDraw.text(width / 2, height * 2 / 3, "Enter Seed:");

        StdDraw.setFont(new Font("Cinzel Black", Font.PLAIN, 20));

        StdDraw.show();
    }

    public static void updateEnterSeed(int width, int height, String s) {
        StdDraw.clear(Color.BLACK);

        Font font = new Font("Cinzel Black", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(width / 2, height * 2 / 3, "Enter Seed:");

        StdDraw.setFont(new Font("Cinzel Black", Font.PLAIN, 20));
        StdDraw.text(width / 2, height / 2 - 2, s);
        StdDraw.show();
    }

    public static void showQuitScreen(int width, int height) {
        defaultSetup(width, height);
        StdDraw.text(width / 2, height * 2 / 3, "Quit?");

        StdDraw.setFont(new Font("Cinzel Black", Font.PLAIN, 20));
        StdDraw.text(width / 2, height / 2 - 2, "(M) Main Menu");
        StdDraw.text(width / 2, height / 2 - 4, "(B) Back");
        StdDraw.text(width / 2, height / 2 - 6, "(Q) Quit");


        StdDraw.show();
    }

    public static void showModeScreen(int width, int height) {
        defaultSetup(width, height);
        StdDraw.text(width / 2, height * 2 / 3, "Choose Your Mode:");
        StdDraw.setFont(new Font("Cinzel Black", Font.PLAIN, 20));
        StdDraw.text(width / 2, height / 2 - 2, "(S) Standard Mode");
        StdDraw.text(width / 2, height / 2 - 4, "(N) Night Mode");

        StdDraw.show();
    }

    public static void showPickHeroNameScreen(int width, int height) {
        defaultSetup(width, height);
        StdDraw.text(width / 2, height * 2 / 3, "Pick Your Hero's Name:");
        StdDraw.setFont(new Font("Cinzel Black", Font.PLAIN, 20));

        StdDraw.show();
    }

    public static void updateHeroName(int width, int height, String s) {
        StdDraw.clear(Color.BLACK);

        Font font = new Font("Cinzel Black", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(width / 2, height * 2 / 3, "Pick Your Hero's Name:");

        StdDraw.setFont(new Font("Cinzel Black", Font.PLAIN, 20));
        StdDraw.text(width / 2, height / 2 - 2, s);
        StdDraw.show();
    }

    public static void showDeathScreen(int width, int height) {
        defaultSetup(width, height);
        StdDraw.text(width / 2, height * 2 / 3, "You Died. You Suck.");

        StdDraw.setFont(new Font("Cinzel Black", Font.PLAIN, 20));
        StdDraw.text(width / 2, height / 2 - 2, "(M) Main Menu");
        StdDraw.text(width / 2, height / 2 - 4, "(Q) Quit");

        StdDraw.show();
    }

    public static void showWinScreen(int width, int height) {
        defaultSetup(width, height);
        StdDraw.text(width / 2, height * 2 / 3, "You Win!");

        StdDraw.setFont(new Font("Cinzel Black", Font.PLAIN, 20));
        StdDraw.text(width / 2, height / 2 - 2, "(M) Main Menu");
        StdDraw.text(width / 2, height / 2 - 4, "(Q) Quit");

        StdDraw.show();

    }
}
