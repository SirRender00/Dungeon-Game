package byow.Core;

import byow.TileEngine.TETile;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.Serializable;

/**
 * This class represents any object that can be placed in the World
 */
public interface Placeable extends Serializable {

    /**
     * @return The point of the Placeable object in x,y space
     */
    Point getPosition();

    /**
     * @return The Container holding this Placeable object. Null if nothing contains this
     */
    Container getParentContainer();

    /**
     * @param container @param container The container to place this Placeable object
     * @throws Container.ContainerException If the container cannot contain this
     */
    void setParentContainer(Container container) throws Container.ContainerException;

    void setPosition(Point point);

    /**
     * @return The Dimensions of this placeable
     */
    Dimension getDimensions();

    default Rectangle getRectangle() {
        return new Rectangle(getPosition(), getDimensions());
    }

    /**
     * @param p A point
     * @return If this placeable is on this point
     */
    boolean has(Point p);

    /**
     * @param rect A rectangle
     * @return If this placeable fully contains this rectangle
     */
    boolean has(Rectangle rect);

    /**
     * @return A TETile[][] grid array containing the tiles to draw this object
     */
    TETile[][] getIcon();

    /**
     * @return The Root container of this object
     */
    default Container getRootContainer() {
        if (getParentContainer() == null) {
            return null;
        } else if (getParentContainer().getParentContainer() == null) {
            return getParentContainer();
        } else {
            return getParentContainer().getRootContainer();
        }
    }
}
