package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import static byow.Core.Corner.generateCorner;

public class Hallway extends Container {

    private static final long serialVersionUID = 9876218665331416L;

    private boolean isHorizontal = false;
    private TETile[][] icon;

    /**
     * @param dimension Constructs a Rectangular container with Dimensions dimensions
     */
    public Hallway(Dimension dimension, boolean isHorizontal) throws ContainerException {
        this(dimension, new Point(0, 0), isHorizontal);
        if (dimension.height == 0 || dimension.width == 0) {
            throw new ContainerException("hallway has area 0");
        }
        this.isHorizontal = isHorizontal;
    }

    /**
     * @param dimension dimension Constructs a Rectangular container with Dimensions dimensions
     * @param point The location to put the container
     */
    public Hallway(Dimension dimension, Point point,
                   boolean isHorizontal) throws ContainerException {
        super(dimension, point);
        if (dimension.height == 0 || dimension.width == 0) {
            throw new ContainerException("hallway has area 0");
        }
        this.isHorizontal = isHorizontal;
        initIcon();
    }

    @Override
    public Rectangle getInnerBoundary() {
        if (isHorizontal()) {
            return new Rectangle(new Point(x, y + 1), new Dimension(width, 1));
        } else {
            return new Rectangle(new Point(x + 1, y), new Dimension(1, height));
        }
    }

    /**
     * This resizes the hallways to remove overlap and returns the corner that
     * the overlap represents.
     * @param hallway The hallway to connect this to
     * @return The corner that connects the two hallways
     */
    public Corner connectHallway(Hallway hallway) throws ContainerException {
        Corner corner = generateCorner(this, hallway);

        resizeHallway(corner);
        hallway.resizeHallway(corner);

        return corner;
    }

    private void resizeHallway(Corner corner) throws ContainerException {
        if (isHorizontal()) {
            if (corner.getLocation().equals(this.getLocation())) {
                this.translate(corner.width, 0);
            }

            this.setSize(width - corner.width, height);
        } else {
            if (corner.getLocation().equals(this.getLocation())) {
                this.translate(0, corner.height);
            }

            this.setSize(width, height - corner.height);
        }

        initIcon();
    }

    /**
     * Resizes this hallway to avoid overlapping the placeable object
     * @param container The placeable object intersecting this
     */
    public void resizeHallway(Container container) {
        Rectangle inter = this.intersection(container);

        if (inter.height <= 0 || inter.width <= 0) {
            return;
        }

        if (!isHorizontal()) {
            if (Engine.inRange(this.y, container.y, container.y + container.height)) {
                this.translate(0, inter.height);
            }
            this.setSize(width, height - inter.height);
        } else {
            if (Engine.inRange(this.x, container.x, container.x + container.width)) {
                this.translate(inter.width, 0);
            }
            this.setSize(width - inter.width, height);
        }
    }

    @Override
    public TETile[][] getIcon() {
        return icon;
    }

    public void initIcon() throws ContainerException {
        icon = new TETile[width][height];
        List<Container> rHC = Engine.getRoomsHallwaysCorners();
        if (isHorizontal()) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (j == 1) {
                        icon[i][j] = Tileset.FLOOR;
                    } else {
                        icon[i][j] = Tileset.WALL;
                    }
                }
            }
            if (Engine.getOverlaps(rHC,
                    new Point(this.x - 1, this.y)).isEmpty()
                    && !Engine.getOverlaps(rHC,
                            new Point(this.x - 1, this.y + 2)).isEmpty()) {
                throw new ContainerException("Hallway goes nowhere");
            } else if (!Engine.getOverlaps(rHC,
                    new Point(this.x + this.width, this.y)).isEmpty()
                    && Engine.getOverlaps(rHC,
                            new Point(this.x + this.width, this.y + 2)).isEmpty()) {
                throw new ContainerException("Hallway goes nowhere");
            }
        } else {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (i == 1) {
                        icon[i][j] = Tileset.FLOOR;
                    } else {
                        icon[i][j] = Tileset.WALL;
                    }
                }
            }
            if (Engine.getOverlaps(rHC,
                    new Point(this.x, this.y - 1)).isEmpty()
                    && !Engine.getOverlaps(rHC,
                            new Point(this.x + 2, this.y - 1)).isEmpty()) {
                throw new ContainerException("Hallway goes nowhere");
            } else if (!Engine.getOverlaps(rHC,
                    new Point(this.x, this.y + this.height)).isEmpty()
                    && Engine.getOverlaps(rHC,
                            new Point(this.x + 2, this.y + this.height)).isEmpty()) {
                throw new ContainerException("Hallway goes nowhere");
            }
        }
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }
}
