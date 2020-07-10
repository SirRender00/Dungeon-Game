package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class Room extends Container {

    private static final long serialVersionUID = 2794541351248693742L;

    private TETile[][] icon;

    /**
     * @param dimension Constructs a Rectangular container with Dimensions dimensions
     */
    public Room(Dimension dimension) {
        this(dimension, new Point(0, 0));
    }

    /**
     * @param dimension dimension Constructs a Rectangular container with Dimensions dimensions
     * @param point The location to put the container
     */
    public Room(Dimension dimension, Point point) {
        super(dimension, point);
        icon = new TETile[width][height];

        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                icon[i][j] = Tileset.FLOOR;
            }
        }

        for (int j = 0; j < height; j++) {
            icon[0][j] = Tileset.WALL;
            icon[width - 1][j] = Tileset.WALL;
        }

        for (int i = 0; i < width; i++) {
            icon[i][0] = Tileset.WALL;
            icon[i][height - 1] = Tileset.WALL;
        }
    }

    @Override
    public Rectangle getInnerBoundary() {
        return new Rectangle(new Point(x + 1, y + 1), new Dimension(width - 2, height - 2));
    }

    @Override
    public TETile[][] getIcon() {

        return icon;
    }
}
