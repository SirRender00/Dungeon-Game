package byow.Entities;


import byow.Core.World;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class FriendlyMoogle extends NPC {

    TETile[][] icon = new TETile[1][1];

    World world;

    double percentMove = 0.4;

    public FriendlyMoogle(World world) {
        super(world, 10, 10,  0, 1, 0);
        this.world = world;
        mPolicy = MovePolicy.RANDOM;

        icon[0][0] = Tileset.TREE;
    }

    @Override
    public void takeAction() {
        moveRandomly(world, percentMove);
    }

    @Override
    public void interact(Entity other) {
        if (other instanceof Hero) {
            other.interact(this);
        }
    }

    @Override
    public TETile[][] getIcon() {
        return icon;
    }
}
