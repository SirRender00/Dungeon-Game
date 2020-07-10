package byow.Entities;

import byow.Core.World;

public abstract class Player extends Entity {

    World world;

    private static final long serialVersionUID = -2450264179209814218L;

    private int maxHitPoints;
    private int hitPoints;
    private int armor;
    private int regenSpeed;
    private int attack;

    public Player(World world, int maxHitPoints,
                  int hitPoints, int armor, int regenSpeed,
                  int attack) {
        super(world);
        this.maxHitPoints = maxHitPoints;
        this.hitPoints = hitPoints;
        this.armor = armor;
        this.regenSpeed = regenSpeed;
        this.attack = attack;
    }

    public boolean isAlive() {
        return hitPoints > 0;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public void attackOther(Player player) {
        unitAttack(player);

        if (player.hitPoints > 0) {
            player.unitAttack(this);
        }
    }

    private void unitAttack(Player player) {
        int hitsLeft = attack;

        if (player.armor >= hitsLeft) {
            player.armor -= attack;
        } else {
            hitsLeft -= player.armor;
            player.armor = 0;

            hitPoints -= hitsLeft;
        }
    }

    public void die() {
        getParentContainer().removeChild(this);
    }

    @Override
    public void tick(int tick) {
        if (hitPoints <= 0) {
            die();
        } else if (tick % regenSpeed == 0) {
            hitPoints += regenSpeed;
            if (hitPoints > maxHitPoints) {
                hitPoints = maxHitPoints;
            }
        }
    }

    public abstract void takeAction();

    public void setMaxHitPoints(int maxHitPoints) {
        this.maxHitPoints = maxHitPoints;
    }

    public void setRegenSpeed(int regenSpeed) {
        this.regenSpeed = regenSpeed;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }
}
