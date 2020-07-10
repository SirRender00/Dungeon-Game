package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;


public class Corner extends Container {

    private static final long serialVersionUID = 328473298489L;

    private TETile[][] icon;

    /**
     * @param dimension Constructs a Rectangular container with Dimensions dimensions
     */
    public Corner(Dimension dimension) {
        super(dimension);
    }

    /**
     * @param dimension dimension Constructs a Rectangular container with Dimensions dimensions
     * @param point The location to put the container
     */
    public Corner(Dimension dimension, Point point) {
        super(dimension, point);
    }

    @Override
    public Rectangle getInnerBoundary() {
        if (icon == null) {
            return null;
        }

        return new Rectangle(new Point(x + 1, y + 1), new Dimension(1, 1));
    }

    @Override
    public TETile[][] getIcon() {
        return icon;
    }

    public static Corner generateCorner(Hallway hallway1, Hallway hallway2) {
        if (hallway2.isHorizontal()) {
            return generateCorner(hallway2, hallway1);
        }

        //check to see if corner can be generated
        Rectangle intersect = hallway1.intersection(hallway2);
        if (intersect.width != 3 || intersect.height != 3) {
            return null;
        }

        //if the "corner" sits atop the left corner of each hallway
        boolean iHallway1 = intersect.getLocation().equals(hallway1.getLocation());
        boolean iHallway2 = intersect.getLocation().equals(hallway2.getLocation());

        //compute corner
        Corner corner = new Corner(intersect.getSize(), intersect.getLocation());
        corner.icon = new TETile[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                corner.icon[i][j] = Tileset.WALL;
            }
        }
        corner.icon[1][1] = Tileset.FLOOR; //middle will always be floor

        List<Container> rHC = Engine.getRoomsHallwaysCorners();
        if (!Engine.getOverlaps(rHC,
                new Point(corner.x + 1, corner.y - 1)).isEmpty()) { //top
            corner.icon[1][0] = Tileset.FLOOR;
        }
        if (!Engine.getOverlaps(rHC,
                new Point(corner.x + 3, corner.y + 1)).isEmpty()) { //right
            corner.icon[2][1] = Tileset.FLOOR;
        }
        if (!Engine.getOverlaps(rHC,
                new Point(corner.x + 1, corner.y + 3)).isEmpty()) { //bottom
            corner.icon[1][2] = Tileset.FLOOR;
        }
        if (!Engine.getOverlaps(rHC,
                new Point(corner.x - 1, corner.y + 1)).isEmpty()) { //left
            corner.icon[0][1] = Tileset.FLOOR;
        }
        //set where the floors are
//        int h1Val = iHallway1 ? 1 : -1;
//        int h2Val = iHallway2 ? 1 : -1;
//
//        corner.icon[1 + h1Val][1] = Tileset.FLOOR;
//        corner.icon[1][1 + h2Val] = Tileset.FLOOR;

        return corner;
    }
}
