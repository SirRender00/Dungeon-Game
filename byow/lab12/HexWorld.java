package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int s = 3;
    private static final int WIDTH = 8 * s + 3 + 3;
    private static final int HEIGHT = 10 * s;
    private static final Random random = new Random();

    private static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static void fillWithHexagons(TETile[][] world) {

    }

    private static void addHexagon(TETile[][] world, TETile tile, Point p, int s) {
        int dr, db, dg;
        dr = 30; db = 20; dg = 30;

        for (int j = p.y; j < p.y + s; j++) {
            int delta = j - p.y;
            int blank = s - 1 - delta;
            for (int i = p.x + blank; i < p.x + blank + s + 2 * delta; i++) {
                world[i][j] = TETile.colorVariant(tile, dr, db, dg, random);
            }
        }

        for (int j = p.y + s; j < p.y + 2 * s; j++) {
            int delta = p.y + 2 * s - j;
            int blank = s - delta;
            for (int i = p.x + blank; i < p.x + blank + s + 2 * (delta - 1); i++) {
                world[i][j] = TETile.colorVariant(tile, dr, db, dg, random);
            }
        }
    }

    private static void fillEmpty(TETile[][] world) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[j][i] = Tileset.NOTHING;
            }
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[HEIGHT][WIDTH];
        fillEmpty(world);

        for (int j = 0; j < 3; j++) {
            for (int i = 2 - j; i < 8 + j; i += 2) {
                addHexagon(world, Tileset.MOUNTAIN, new Point(j * (2 * s - 1), s * i), s);
            }
        }

        for (int j = 1; j >= 0; j--) {
            for (int i = 2 - j; i < 8 + j; i += 2) {
                addHexagon(world, Tileset.WATER, new Point((4 - j) * (2 * s - 1), s * i), s);
            }
        }

        ter.renderFrame(world);
    }
}
