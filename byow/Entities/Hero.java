package byow.Entities;

import byow.Core.World;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashMap;
import java.util.Map;

public class Hero extends Player {

    private TETile[][] icon = new TETile[1][1];

    private static final long serialVersionUID = 19284721893729189L;
    private int score = 0;

    private Map<Item, Integer> items = new HashMap<>();

    public Hero(World world) {
        super(world, 100, 100, 0, 5, 2);

        world.setHero(this);

        icon[0][0] = Tileset.AVATAR;
    }

    @Override
    public void interact(Entity other) {
        if (other instanceof Item) {
            pickupItem((Item) other);
        } else if (other instanceof FriendlyMoogle) {
            ((FriendlyMoogle) other).die();
            score += 1;
        }
    }

    public int getScore() {
        return score;
    }

    @Override
    public void tick(int tick) {
        super.tick(tick);
    }

    private void pickupItem(Item item) {
        if (items.containsKey(item)) {
            items.put(item, items.get(item) + 1);
        } else {
            items.put(item, 1);
        }

        world.removeChild(item);
    }

    @Override
    public void die() {
        this.setHitPoints(0);
    }

    @Override
    public void takeAction() {

    }

    @Override
    public TETile[][] getIcon() {
        return icon;
    }

    public World getWorld() {
        return world;
    }
}
