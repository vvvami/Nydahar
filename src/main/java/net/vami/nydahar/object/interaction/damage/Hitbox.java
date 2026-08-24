package net.vami.nydahar.object.interaction.damage;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.object.entity.EntityObject;
import net.vami.nydahar.object.interaction.Direction;
import net.vami.nydahar.object.interaction.collision.Collider;
import net.vami.nydahar.util.AABB;
import net.vami.nydahar.util.MathUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Hitbox {
    private static final HashMap<Hitbox, GameObject> hitboxMap = new HashMap<>();

    private final GameObject owner;

    private final double width;
    private final double height;

    private final double amount;

    private final double duration;
    private double age;

    private final double startX;
    private final double endX;

    private final double startY;
    private final double endY;

    private AABB box;

    private boolean finished;

    private final Set<UUID> hitObjects = new HashSet<>();

    public Hitbox(GameObject owner, double width, double height, double amount, double duration, double rangeX, double rangeY, Direction direction) {
        this.owner = owner;
        this.amount = amount;
        this.duration = duration;

        double actualWidth = width;
        double actualHeight = height;

        if (direction == Direction.UP || direction == Direction.DOWN) {
            actualWidth = height;
            actualHeight = width;
        }

        this.width = actualWidth;
        this.height = actualHeight;

        Collider collider = owner.getCollider();

        switch (direction) {
            case RIGHT -> {
                startX = collider.maxX();
                endX = startX + rangeX;

                startY = collider.minY() + (collider.maxY() - collider.minY()) / 2 - this.height / 2;
                endY = startY;
            }

            case LEFT -> {
                startX = collider.minX() - this.width;
                endX = startX - rangeX;

                startY = collider.minY() + (collider.maxY() - collider.minY()) / 2 - this.height / 2;
                endY = startY;
            }

            case UP -> {
                double centerX = (collider.minX() + collider.maxX()) / 2;

                startX = centerX - this.width / 2;
                endX = startX;

                startY = collider.maxY();
                endY = startY + rangeY;
            }

            case DOWN -> {
                double centerX = (collider.minX() + collider.maxX()) / 2;

                startX = centerX - this.width / 2;
                endX = startX;

                startY = collider.minY() - this.height;
                endY = startY - rangeY;
            }

            default -> throw new IllegalArgumentException("Direction is null");
        }

        box = new AABB(startX, startY, this.width, this.height);

        hitboxMap.put(this, owner);
    }

    public void update(double dt) {
        age += dt;

        double progress = Math.min(age / duration, 1);

        double x = MathUtil.lerp(startX, endX, progress);
        double y = MathUtil.lerp(startY, endY, progress);

        box = new AABB(x, y, width, height);


        checkHits();

        if (progress >= 1) {
            finished = true;
        }
    }

    private void checkHits() {
        for (GameObject object : Collider.map().values()) {
            if (object == owner)
                continue;

            if (!(object instanceof EntityObject entity))
                continue;

            if (hitObjects.contains(object.getUUID()))
                continue;

            Collider collider = object.getCollider();

            if (collider == null)
                continue;

            if (!box.intersects(collider.getBox()))
                continue;

            hitObjects.add(object.getUUID());

            entity.hurt(new DamageType(), amount);
        }
    }

    public AABB getBox() {
        return box;
    }

    public boolean isFinished() {
        return finished;
    }

    public static HashMap<Hitbox, GameObject> map() {
        return hitboxMap;
    }
}
