package byow.Entities;

import byow.Core.World;
import byow.TileEngine.TETile;

import java.awt.Point;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import static byow.Core.RandomUtils.uniform;

public abstract class NPC extends Player {

    public enum MovePolicy {

        RANDOM(10), PASSIVE(6), AGGRESSIVE(3);

        int tick;

        MovePolicy(int tick) {
            this.tick = tick;
        }
    }

    MovePolicy mPolicy;

    public NPC(World world, int maxHitPoints,
            int hitPoints, int armor, int regenSpeed,
            int attack) {
        super(world, maxHitPoints, hitPoints, armor, regenSpeed, attack);
        mPolicy = MovePolicy.RANDOM;
    }

    public void setMovePolicy(MovePolicy policy) {
        mPolicy = policy;
    }

    @Override
    public void tick(int tick) {
        super.tick(tick);

        if (tick % mPolicy.tick == 0) {
            takeAction();
        }
    }

    public void moveRandomly(World world, double percentChance) {
        Random rand = new Random();
        if (rand.nextDouble() > percentChance) {
            return;
        }

        Map<Point, TETile> neighbors = world.getNeighbors(getPosition());
        World.removeImpassable(neighbors);
        World.removePlayers(neighbors);

        if (!neighbors.isEmpty()) {
            move((Point) getRandomObject(rand, neighbors.keySet()));
        }
    }

    private Object getRandomObject(Random rand, Collection from) {
        int i = uniform(rand, from.size());
        return from.toArray()[i];
    }
}
