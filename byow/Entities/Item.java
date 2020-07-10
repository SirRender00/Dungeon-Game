package byow.Entities;

import byow.Core.World;

public abstract class Item extends Entity {

    public Item(World world) {
        super(world);
    }

    @Override
    public void interact(Entity other) {
        if (!(other instanceof Item)) {
            other.interact(this);
        }
    }

    public abstract void use();
}
