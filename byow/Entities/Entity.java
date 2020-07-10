package byow.Entities;

import byow.Core.Container;
import byow.Core.Placeable;
import byow.Core.World;
import byow.TileEngine.TETile;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Dimension;

public abstract class Entity implements Placeable {

    private Point position = new Point(0, 0);
    private Container parent;

    private static final long serialVersionUID = 2100751760189902326L;

    private World world;

    public Entity(World world) {
        this.world = world;
    }

    public void move(Point to) {
        if (World.isImpassable(world.getTile(to.x, to.y))) {
            return;
        }

        TETile tile = world.getTile(to.x, to.y);
//        if (World.isPlayer(tile)) {
//
//        }
        world.update(this, to);
    }

    public abstract void interact(Entity other);

    public abstract void tick(int tick);

    @Override
    public boolean has(Point p) {
        return getPosition().equals(p);
    }

    @Override
    public boolean has(Rectangle rect) {
        if (rect.width != 1 || rect.height != 1) {
            return false;
        }

        return has(rect.getLocation());
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public Container getParentContainer() {
        return parent;
    }

    @Override
    public void setParentContainer(Container container) {
        parent = container;
    }

    @Override
    public void setPosition(Point point) {
        position = point;
    }

    @Override
    public Dimension getDimensions() {
        return new Dimension(1, 1);
    }
}
