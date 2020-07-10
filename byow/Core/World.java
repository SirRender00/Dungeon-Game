package byow.Core;

import byow.Entities.Entity;
import byow.Entities.Hero;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class World extends Container {

    private static final long serialVersionUID = 6583527003639409966L;

    private int width;
    private int height;

    private TETile[][] grid;

    private int currTick = 0;
    private static final int MAX_TICK = 100;

    private Set<Entity> entities = new HashSet<>();

    private Hero hero;

    private long seed;

    public World(Dimension dimension) {
        super(dimension, new Point(0, 0));
        width = dimension.width;
        height = dimension.height;

        grid = new TETile[width][height];
        fillWithEmpty(grid, this);
    }

    public void tick() {
        List<Entity> entitiesCopy = new ArrayList<>(entities);
        for (Entity entity : entitiesCopy) {
            entity.tick(currTick);
            if (!entity.getParentContainer().contains(entity)) {
                entities.remove(entity);
            }
        }
        updateAll();

        currTick += 1;
        currTick %= MAX_TICK;
    }

    @Override
    public void addPlaceable(Placeable placeable, Point p) throws ContainerException {
        super.addPlaceable(placeable, p);
        paint(placeable);
    }

    @Override
    public Rectangle getInnerBoundary() {
        return getBounds();
    }

    private static void fillWithEmpty(TETile[][] map, Rectangle rect) {
        Point lowerRight = new Point(rect.x + rect.width, rect.y + rect.height);
        for (int i = rect.x; i < lowerRight.x; i++) {
            for (int j = rect.y; j < lowerRight.y; j++) {
                map[i][j] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Paint the object onto the world. Only works if
     * <code>contains(placeable)</code> is true.
     * @param placeable The placeable object to paint
     */
    private void paint(Placeable placeable) {
        if (contains(placeable)) {
            if (placeable instanceof Container) {
                fillWithEmpty(grid, ((Container) placeable));
                paintContainer(placeable, placeable.getDimensions(), placeable.getPosition());
            } else if (placeable instanceof Entity) {
                paintEntity(placeable, placeable.getPosition());
                entities.add((Entity) placeable);
            }
        }
    }

    public Map<Point, TETile> getNeighbors(Point p) {
        Map<Point, TETile> result = new HashMap<>();

        if (!has(p)) {
            return result;
        }

        for (int i = -1; i <= 1; i += 2) {
            Point point = new Point(p.x + i, p.y);
            if (has(point)) {
                result.put(point, getTile(p.x + i, p.y));
            }
        }

        for (int j = -1; j <= 1; j += 2) {
            Point point = new Point(p.x, p.y + j);
            if (has(point)) {
                result.put(point, getTile(p.x, p.y + j));
            }
        }

        return result;
    }

    /**
     * Updates all placeable objects in this world and repaints them.
     * @returns if all updates were successful.
     */
    public boolean updateAll() {
        fillWithEmpty(grid, this);

        boolean allSuccess = true;
        for (Placeable child : getChildren()) {
            if (!update(child)) {
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    public boolean update(Placeable placeable) {
        return update(placeable, placeable.getPosition());
    }

    /**
     * Update an EXISTING placeable object in the world. Will not work if
     * it is not placed in the world. Repaints everything inside if it
     * is a container.
     * @param placeable The placeable object to update in the world
     * @return True if the update was successful, false otherwise
     */
    public boolean update(Placeable placeable, Point point) {
        if (contains(placeable)) {
            Container oldCont = placeable.getParentContainer();
            Point oldPos = placeable.getPosition();

            try {
                oldCont.removeChild(placeable);
                addPlaceable(placeable, point);
            } catch (ContainerException e) {
                e.printStackTrace();
                oldCont.getChildren().add(placeable);
                placeable.setPosition(oldPos);
                return false;
            }
            paint(oldCont);

            return true;
        } else {
            return false;
        }
    }

    private void paintEntity(Placeable placeable, Point p) {
        TETile[][] obj = placeable.getIcon();

        grid[p.x][p.y] = obj[0][0];
    }

    private void paintContainer(Placeable placeable, Dimension dim, Point p) {
        TETile[][] obj = placeable.getIcon();

        int w = dim.width;
        int h = dim.height;

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                grid[p.x + i][p.y + j] = obj[i][j];
            }
        }

        for (Placeable place : ((Container) placeable).getChildren()) {
            paint(place);
        }
    }

    public TETile getTile(int x, int y) {
        return grid[x][y];
    }

    public static boolean isImpassable(TETile tile) {
        return tile.description().equals(Tileset.NOTHING.description())
                || tile.description().equals(Tileset.WALL.description())
                || tile.description().equals(Tileset.WATER.description())
                || tile.description().equals(Tileset.LOCKED_DOOR.description())
                || tile.description().equals(Tileset.MOUNTAIN.description());
    }

    public static boolean isPlayer(TETile tile) {
        return tile.description().equals(Tileset.AVATAR.description());
    }

    private void removeContainer(Placeable placeable) {
        Dimension obj = placeable.getDimensions();

        Point p = placeable.getPosition();
        Rectangle rect = new Rectangle(p, obj);

        fillWithEmpty(grid, rect);
    }

    private void removeEntity(Placeable placeable) {
        Point p = placeable.getPosition();

        grid[p.x][p.y] = Tileset.NOTHING;

        entities.remove(placeable);
    }

    @Override
    public void setParentContainer(Container container) throws ContainerException {
        throw new ContainerException(this + " cannot be placed into " + container);
    }

    @Override
    public Container getParentContainer() {
        return null;
    }

    @Override
    public Container getRootContainer() {
        return null;
    }

    @Override
    public void setPosition(Point point) {

    }

    @Override
    public TETile[][] getIcon() {
        TETile[][] world = new TETile[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                world[i][j] = getTile(i, j);
            }
        }

        return world;
    }

    public TETile[][] getNightIcon() {
        fillWithEmpty(grid, this);
        Rectangle rect = new Rectangle(hero.getParentContainer());
        rect.setLocation(rect.x - 1, rect.y - 1);
        rect.setSize(rect.width + 2, rect.height + 2);
        List<Placeable> containers = getChildren(rect);
        for (Placeable placeable : containers) {
            if (placeable instanceof Container) {
                paintContainer(placeable, placeable.getDimensions(), placeable.getPosition());
            }
        }
        return grid;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public long getSeed() {
        return seed;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public static void removeImpassable(Map<Point, TETile> tiles) {
        for (Point p : new HashSet<>(tiles.keySet())) {
            if (isImpassable(tiles.get(p))) {
                tiles.remove(p);
            }
        }
    }

    public static void removePlayers(Map<Point, TETile> tiles) {
        for (Point p : new HashSet<>(tiles.keySet())) {
            if (isPlayer(tiles.get(p))) {
                tiles.remove(p);
            }
        }
    }
}
