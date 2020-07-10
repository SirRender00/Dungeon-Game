package byow.Core;

import byow.Entities.Entity;
import byow.Entities.Hero;
import byow.IO.InputSource;
import byow.IO.KeyboardInputSource;
import byow.IO.StringInputDevice;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

import static byow.Core.RandomUtils.uniform;
import static byow.IO.WorldLoader.loadWorld;
import static byow.IO.WorldLoader.saveWorld;



public class Engine {

//    TERenderer te = new TERenderer();

    public static final int WIDTH = 80;
    public static final int HEIGHT = 35;
    public static final int MIN_ROOM_DIM = 5;
    public static final int MAX_ROOM_DIM = 12;
    public static final int ROOM_TRIES = 15 * (HEIGHT * WIDTH) / (MAX_ROOM_DIM * MAX_ROOM_DIM);

    private Hero hero;
    private Random rand;
    private long seed;
    private static List<Container> roomsHallwaysCorners;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        executeSimulation(new KeyboardInputSource());
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        return executeSimulation(new StringInputDevice(input)).getIcon();
    }

    private World executeSimulation(InputSource source) {
        World currWorld = null;

        char c = source.getNextKeyCleaned();

        boolean set = false;

        outer:
        while (true) {
            switch (c) {
                case 'N':
                    try {
                        if (!set) {
                            setRandom(source);
                            set = true;
                        }
                        currWorld = generateNewWorld();
                    } catch (Container.ContainerException | NullPointerException e) {
                        rand = new Random(seed += 1);
                        continue outer;
                    }
                    currWorld.setSeed(seed);
                    break outer;
                case 'L':
                    currWorld = loadWorld();
                    hero = currWorld.getHero();
                    break outer;
                case 'Q': return currWorld;
                default: break;
            }

            c = source.getNextKeyCleaned();
        }

        //main loop
        outer:
        while (source.possibleNextInput()) {
            c = source.getNextKeyCleaned();

            switch (c) {
                case '\u0000': break;
                case ':':
                    c = source.getNextKeyCleaned();
                    switch (c) {
                        case 'M': executeSimulation(new KeyboardInputSource()); break;
                        case 'B': continue outer;
                        case 'Q':
                            saveWorld(currWorld);
                            return currWorld;
                        default: break;
                    }
                    break;

                case 'W': move(0, 1); break;
                case 'A': move(-1, 0); break;
                case 'S': move(0, -1); break;
                case 'D': move(1, 0); break;
                default: break;
            }
        }

        return currWorld;
    }

    private final Object monitor = new Object();

    private void move(int deltaX, int deltaY) {
        Point curr = hero.getPosition();
        Point prospect = new Point(curr.x + deltaX, curr.y + deltaY);

        hero.move(prospect);
    }

    private World generateNewWorld() throws Container.ContainerException {
        World currWorld = new World(new Dimension(WIDTH, HEIGHT));

        List<Room> rooms = generateRooms();

        roomsHallwaysCorners = new ArrayList<>(rooms);
        generateHallwaysAndCorners(roomsHallwaysCorners);

        for (Container container : new ArrayList<>(roomsHallwaysCorners)) {
            if (container instanceof Hallway) {
                if (container.height <= 0 || container.width <= 0) {
                    roomsHallwaysCorners.remove(container);
                    continue;
                }
                pokeHoles(roomsHallwaysCorners, (Hallway) container);
            } else if (container instanceof Corner) {
                pokeHoles(roomsHallwaysCorners, (Corner) container);
            }
        }

        for (Placeable placeable : roomsHallwaysCorners) {
            currWorld.addPlaceable(placeable);
        }

        //randomly place the hero in a room
        hero = new Hero(currWorld);
        placeEntityRandom(rooms, hero);
        return currWorld;
    }

    private Room placeEntityRandom(List<Room> rooms, Entity entity) {
        Room room = rooms.get(uniform(rand, rooms.size()));
        Rectangle inner = room.getInnerBoundary();
        Point p = new Point(inner.x + uniform(rand, 0, inner.width),
                inner.y + uniform(rand, 0, inner.height));
        try {
            room.addPlaceable(entity, p);
        } catch (Container.ContainerException e) {
            placeEntityRandom(rooms, entity);
        }

        return room;
    }

    private void generateHallwaysAndCorners(List<Container> rHC)
            throws Container.ContainerException {
        WeightedQuickUnionUF wquuf = new WeightedQuickUnionUF(rHC.size());

        //while the containers are not all connected
        //get a random ccntaine and connect it to a container it is not connected to
        int index = uniform(rand, 0, wquuf.size());
        int connectTo;
        while ((connectTo = isAllConnected(index, wquuf)) != index) {
            for (List<Hallway> hallwayList : connectContainersWithHallways(
                    rHC.get(index), rHC.get(connectTo), rHC,  wquuf)) {

                List<Container> newContainers = new ArrayList<>(hallwayList);
                for (Container container : new ArrayList<>(newContainers)) {
                    if (container.width <= 0 || container.height <= 0) {
                        newContainers.remove(container);
                    }
                }

                if (newContainers.size() == 2) {
                    Corner corner = hallwayList.get(0).connectHallway(hallwayList.get(1));
                    newContainers.add(corner);
                }

                for (Container cont : newContainers) {
                    rHC.add(cont);

                    int newI = wquuf.size();
                    wquuf.addVertex();
                    wquuf.union(index, newI);
                }
            }
            wquuf.union(index, connectTo);

            index = uniform(rand, 0, wquuf.size());
        }
    }

    /**
     * @param index the index to check allConnected with
     * @param wquuf
     * @return index if is all connected, or an int which index is not connected to
     */
    private int isAllConnected(int index, WeightedQuickUnionUF wquuf) {
        for (int i = 0; i < wquuf.size(); i++) {
            if (index != i && !wquuf.connected(index, i)) {
                return i;
            }
        }

        return index;
    }

    /**
     * @return A list of pairs of hallways that should be constructed to connect
     * container1 with container2, including any intermediary containers. Pairs
     * with both elements should use <code>Hallway.connect(Hallway)</code>
     * to generate corners. Lone hallways don't need corners.
     */
    private List<List<Hallway>> connectContainersWithHallways(
            Container container1, Container container2,
            List<Container> rHC, WeightedQuickUnionUF wquuf) throws Container.ContainerException {

        List<List<Hallway>> result = new ArrayList<>();
        List<Hallway> straightPath = generateStraightHallways(container1, container2);

        //if there is a conflict
        //gather the conflicts
        Set<Container> conflicts = new HashSet<>();
        for (Hallway hallway : straightPath) {
            conflicts.addAll(getOverlaps(rHC, hallway));
        }

        List<Container> containerConflicts = new ArrayList<>(conflicts);

        //no conflict
        if (containerConflicts.size() == 0) {
            result.add(straightPath);
            return result;
        }

        //construct a container path from container1 to container2 and everything in between
        //i.e. this whole list needs to be connected
        List<Container> curr = new ArrayList<>();
        curr.add(container1);
        curr.addAll(containerConflicts);
        curr.add(container2);

        for (int i = 1; i < curr.size(); i++) {
            Container cont1 = curr.get(i - 1);
            Container cont2 = curr.get(i);

            int index1 = rHC.indexOf(cont1);
            int index2 = rHC.indexOf(cont2);

            if (!wquuf.connected(index1, index2)) {
                result.addAll(connectContainersWithHallways(cont1, cont2, rHC, wquuf));
                wquuf.union(index1, index2);
            }
        }

        return result;
    }

    public static List<Container> getOverlaps(List<Container> others, Container container) {
        List<Container> result = new ArrayList<>();
        for (Container cont : others) {
            if (cont.intersects(container)) {
                result.add(cont);
            }
        }
        return result;
    }

    public static List<Container> getOverlaps(List<Container> others, Point p) {
        List<Container> result = new ArrayList<>();
        for (Container cont : others) {
            if (cont.has(p)) {
                result.add(cont);
            }
        }
        return result;
    }

    /**
     * @param container1
     * @param container2
     * @return One or two straight line hallways that would connect container 1 and container 2
     * regardless of other things in its way
     */
    private List<Hallway> generateStraightHallways(
            Container container1, Container container2) throws Container.ContainerException {
        List<Hallway> hallways = new ArrayList<>();
//        if (container2.y - (container1.y + container1.height) < 2 || ) {
//            throw new Container.ContainerException("containers too close to connect");
//        }
        int y = calculateRandomY(container1, container2);
        int x = calculateRandomX(container1, container2);
        if (container1.y != container2.y) {
            int widthV = 3;
            int lengthV = Math.abs(y - container2.y)
                    + (int) (1.5 - 1.5 * sign(container2.y - container1.y));
            int xVPoint = x;
            int yVPoint = Math.min(container2.y, y);

            hallways.add(new Hallway(new Dimension(widthV, lengthV),
                    new Point(xVPoint, yVPoint), false));
        }
        if (container1.x != container2.x) {
            int widthH = Math.abs(x - container1.x)
                    + (int) (1.5 + 1.5 * sign(container2.x - container1.x));
            int lengthH = 3;
            int xHPoint = Math.min(x, container1.x);
            int yHPoint = y;

            hallways.add(new Hallway(new Dimension(widthH, lengthH),
                    new Point(xHPoint, yHPoint), true));
        }

        for (Hallway hallway : hallways) {
            hallway.resizeHallway(container1);
            hallway.resizeHallway(container2);
        }

        return hallways;
    }

    private void pokeHoles(List<Container> rHC, Corner corner) throws Container.ContainerException {
        List<Container> contList;
        if (corner.getIcon()[1][0]  == Tileset.FLOOR) { // top
            contList = getOverlaps(rHC, new Point(corner.x + 1, corner.y - 1));
            if (!contList.isEmpty()) {
                Container cont = contList.get(0);
                cont.getIcon()[corner.x + 1 - cont.x][cont.height - 1] = Tileset.FLOOR;
            } else {
                throw new Container.ContainerException("Corner goes nowhere");
            }
        }
        if (corner.getIcon()[2][1] == Tileset.FLOOR) { // right
            contList = getOverlaps(rHC, new Point(corner.x + corner.width, corner.y + 1));
            if (!contList.isEmpty()) {
                Container cont = contList.get(0);
                cont.getIcon()[0][corner.y + 1 - cont.y] = Tileset.FLOOR;
            } else {
                throw new Container.ContainerException("Corner goes nowhere");
            }
        }
        if (corner.getIcon()[1][2] == Tileset.FLOOR) { // bottom
            contList = getOverlaps(rHC, new Point(corner.x + 1, corner.y + corner.height));
            if (!contList.isEmpty()) {
                Container cont = contList.get(0);
                cont.getIcon()[corner.x + 1 - cont.x][0] = Tileset.FLOOR;
            } else {
                throw new Container.ContainerException("Corner goes nowhere");
            }
        }
        if (corner.getIcon()[0][1] == Tileset.FLOOR) { // left
            contList = getOverlaps(rHC, new Point(corner.x - 1, corner.y + 1));
            if (!contList.isEmpty()) {
                Container cont = contList.get(0);
                cont.getIcon()[cont.width - 1][corner.y + 1 - cont.y] = Tileset.FLOOR;
            } else {
                throw new Container.ContainerException("Corner goes nowhere");
            }
        }
    }

    private void pokeHoles(List<Container> rHC, Hallway hallway)
            throws Container.ContainerException {
        if (!hallway.isHorizontal()) {
            List<Container> cont1 = getOverlaps(rHC, new Point(hallway.x + 1, hallway.y - 1));
            if (!cont1.isEmpty()) { // top
                Container cont = cont1.get(0);
                int deltaX = hallway.x + 1 - cont.x;
                cont.getIcon()[deltaX][cont.height - 1] = Tileset.FLOOR;
            } else {
                throw new Container.ContainerException("Corner goes nowhere");
            }
            List<Container> cont2 = getOverlaps(rHC,
                    new Point(hallway.x + 1, hallway.y + hallway.height));
            if (!cont2.isEmpty()) { //bottom
                Container cont = cont2.get(0);
                int deltaX = hallway.x + 1 - cont.x;
                cont.getIcon()[deltaX][0] = Tileset.FLOOR;
            } else {
                throw new Container.ContainerException("Corner goes nowhere");
            }
        } else {
            List<Container> cont1 = getOverlaps(rHC, new Point(hallway.x - 1, hallway.y + 1));
            if (!cont1.isEmpty()) { //left
                Container cont = cont1.get(0);
                int deltaY = hallway.y + 1 - cont.y;
                cont.getIcon()[cont.width - 1][deltaY] = Tileset.FLOOR;
            } else {
                throw new Container.ContainerException("Corner goes nowhere");
            }
            List<Container> cont2 = getOverlaps(rHC,
                    new Point(hallway.x + hallway.width, hallway.y + 1));
            if (!cont2.isEmpty()) { //right
                Container cont = cont2.get(0);
                int deltaY = hallway.y + 1 - cont.y;
                cont.getIcon()[0][deltaY] = Tileset.FLOOR;
            } else {
                throw new Container.ContainerException("Corner goes nowhere");
            }
        }
    }

    public static List<Container> getRoomsHallwaysCorners() {
        return roomsHallwaysCorners;
    }

    private int calculateRandomY(Container container1, Container container2) {
        int y;
        if (inRange(container2.y, container1.y, container1.y + container1.height)) {
            if (container1.y + container1.height - container2.y > 2) {
                y = uniform(rand, container2.y, container1.y + container1.height - 2);
            } else {
                y = uniform(rand, container1.y, container2.y - 2);
            }
        } else if (inRange(container2.y + container2.height,
                container1.y, container1.y + container1.height)) {
            if (container2.y + container2.height - container1.y > 2) {
                y = uniform(rand, container1.y, container2.y + container2.height - 2);
            } else {
                y = uniform(rand, container2.y + container2.height,
                        container1.y + container1.height - 2);
            }
        } else {
            y = uniform(rand, container1.y, container1.y + container1.height - 2);
        }
        return y;
    }

    private int calculateRandomX(Container container1, Container container2) {
        int x;
        if (inRange(container1.x, container2.x, container2.x + container2.width)) {
            if (container2.x + container2.width - container1.x > 2) {
                x = uniform(rand, container1.x, container2.x + container2.width - 2);
            } else {
                x = uniform(rand, container2.x, container1.x - 2);
            }
        } else if (inRange(container1.x + container1.width,
                container2.x, container2.x + container2.width)) {
            if (container1.x + container1.width - container2.x > 2) {
                x = uniform(rand, container2.x, container1.x + container1.width - 2);
            } else {
                x = uniform(rand, container1.x + container1.width,
                        container2.x + container2.width - 2);
            }
        } else {
            x = uniform(rand, container2.x, container2.x + container2.width - 2);
        }
        return x;
    }

    public static boolean inRange(int x, int a, int b) {
        if (x < a || x > b) {
            return false;
        }
        return true;
    }

    private int sign(int x) {
        if (x < 0) {
            return -1;
        } else {
            return 1;
        }
    }

    private List<Room> generateRooms() {
        List<Room> result = new ArrayList<>();

        for (int i = 0; i < ROOM_TRIES; i++) {
            int width = uniform(rand, MIN_ROOM_DIM, MAX_ROOM_DIM);
            int height = uniform(rand, MIN_ROOM_DIM, MAX_ROOM_DIM);

            int x = uniform(rand, 0, WIDTH - width);
            int y = uniform(rand, 0, HEIGHT - height);

            result.add(new Room(
                    new Dimension(width, height),
                    new Point(x, y)
            ));
        }

        //remove overlapping rooms
        outer:
        while (true) {
            for (Room r1 : result) {
                for (Room r2 : result) {
                    if (!r1.equals(r2)) {
                        Rectangle r = new Rectangle(r1.x - 4, r1.y - 4,
                                r1.width + 8, r1.height + 8);
                        if (r.intersects(r2)) {
                            result.remove(r1);
                            continue outer;
                        }
                    }
                }
            }

            break;
        }
        return result;
    }

    private void setRandom(InputSource source) {
        //get seed
        StringBuilder stringSeed = new StringBuilder();
        char c = source.getNextKeyCleaned();

        while (c != 'S') {
            if (Character.isDigit(c)) {
                stringSeed.append(c);
            }

            c = source.getNextKeyCleaned();
        }

        //set random
        seed = Long.parseLong(stringSeed.toString());
        rand = new Random(seed);
    }
}
