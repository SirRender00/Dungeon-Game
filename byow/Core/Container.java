package byow.Core;

import byow.Entities.Entity;

import java.awt.Rectangle;
import java.awt.Point;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.awt.Dimension;

public abstract class Container extends Rectangle implements Placeable {

    private Container parent;
    private List<Placeable> children = new ArrayList<>();

    private static final long serialVersionUID = 19263038925744274L;

    /**
     * @param dimensions Constructs a Rectangular container with these dimensions
     */
    public Container(Dimension dimensions) {
        super(dimensions);
    }

    /**
     * @param dimensions Constructs a Rectangular container with Dimensions dimensions
     * @param point The location to put the container
     */
    public Container(Dimension dimensions, Point point) {
        super(point, dimensions);
    }

    @Override
    public Container getParentContainer() {
        return parent;
    }

    @Override
    public boolean has(Point p) {
        return this.contains(p);
    }

    @Override
    public boolean has(Rectangle rect) {
        return this.contains(rect);
    }

    @Override
    public Point getPosition() {
        return this.getLocation();
    }

    @Override
    public void setPosition(Point point) {
        this.setLocation(point);
    }

    @Override
    public Dimension getDimensions() {
        return new Dimension((int) getWidth(), (int) getHeight());
    }

    @Override
    public void setParentContainer(Container container) throws ContainerException {
        this.parent = container;
    }

    /**
     * Assumes the placeable object already has its position set
     * @param placeable The placeable object to place in this container
     * @throws ContainerException If the container cannot fit the placeable object,
     * if the placeable object intersects another object already in the container
     */
    public void addPlaceable(Placeable placeable) throws ContainerException {
        addPlaceable(placeable, placeable.getPosition());
    }

    /**
     * Places the placeable object in this container. If this is another container,
     * checks to see if it both can fit it, and that it does not intersect with another container.
     * If this is an entity, will make entities interact with each other if they overlap.
     * @param placeable The placeable object to place in this container
     * @param point The position to add this object
     * @throws ContainerException If the container cannot fit the placeable object, or
     * if the placeable object intersects another object already in the container
     */
    public void addPlaceable(Placeable placeable, Point point) throws ContainerException {
        if (!this.canContain(placeable, point)) {
            throw new ContainerException(placeable + " cannot fit in " + this);
        } else {
            for (Placeable child : children) {
                if (child.has(point)) {
                    if (child instanceof Container) {
                        ((Container) child).addPlaceable(placeable, point);
                    } else if (child instanceof Entity && placeable instanceof Entity) {
                        ((Entity) placeable).interact((Entity) child);
                        addPlaceable(placeable);
                    } else {
                        throw new ContainerException(
                                "Could not place " + placeable + "in " + child);
                    }

                    return;
                }
            }

            addChild(placeable);
            placeable.setParentContainer(this);
            placeable.setPosition(point);
        }
    }

    /**
     * @param placeable The placeable object
     * @param p The location to add the placeable object
     * @return if this container can contain this object
     */
    public boolean canContain(Placeable placeable, Point p) {
        if (placeable instanceof Container) {
            Rectangle test = new Rectangle(p, placeable.getDimensions());
            return this.contains(test);
        } else if (placeable instanceof Entity) {
            return this.contains(p);
        }

        return false;
    }

    /**
     * @param rect The rectangle to check children for
     * @return All children that intersects the given rectangle
     */
    public List<Placeable> getChildren(Rectangle rect) {
        List<Placeable> result = new ArrayList<>();

        for (Placeable child : children) {
            Rectangle test = new Rectangle(child.getPosition(), child.getDimensions());
            if (rect.intersects(test)) {
                result.add(child);
            }

            if (child instanceof Container) {
                result.addAll(((Container) child).getChildren(rect));
            }
        }

        return result;
    }

    public boolean isPlaceConflict(Placeable placeable) {
        return isPlaceConflict(placeable, placeable.getPosition());
    }

    public boolean isPlaceConflict(Placeable placeable, Point p) {
        return !getChildren(new Rectangle(p, placeable.getDimensions())).isEmpty();
    }

    public boolean contains(Placeable placeable) {
        if (this.equals(placeable)) {
            return false;
        }

        for (Placeable child : children) {
            if (child.equals(placeable)) {
                return true;
            } else if (child instanceof Container && ((Container) child).contains(placeable)) {
                return true;
            }
        }

        return false;
    }

    public List<Placeable> getChildren() {
        return new ArrayList<>(children);
    }

    /**
     * @param point The point to check children for
     * @return A list of the children that has the specified point,
     * starting from most specific (i.e. entities) to least
     */
    public List<Placeable> getChildren(Point point) {
        List<Placeable> result = new ArrayList<>();

        for (Placeable placeable : this.getChildren()) {
            if (placeable.has(point)) {
                result.add(placeable);
            }

            if (placeable instanceof Container) {
                result.addAll(((Container) placeable).getChildren(point));
            }
        }

        Collections.reverse(result);
        return result;
    }

    private void addChild(Placeable placeable) {
        children.add(placeable);
    }

    /**
     * @return The Rectangle which represents where things can be placed
     */
    public abstract Rectangle getInnerBoundary();

    public void removeChild(Placeable placeable) {
        children.remove(placeable);
    }

    public boolean isWorld() {
        return parent == null && (this instanceof World);
    }

    public static class ContainerException extends Exception {
        ContainerException(String message) {
            super(message);
        }
    }
}
