package net.vami.nydahar.object.interaction.damage;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.object.entity.EntityObject;
import net.vami.nydahar.object.interaction.Direction;
import net.vami.nydahar.object.interaction.collision.Collider;
import net.vami.nydahar.util.AABB;
import net.vami.nydahar.util.MathUtil;
import net.vami.nydahar.util.Vec2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Attack {
    private static final HashMap<Attack, GameObject> hitboxMap = new HashMap<>();

    private final GameObject owner;

    private final double width;
    private final double height;

    private final double amount;

    private final double duration;
    private double age;

    private Vec2[] positions;
    private int index = 0;
    private Direction direction;

    private boolean finished;

    private AABB box;

    private final Set<UUID> hitObjects = new HashSet<>();

    private Attack(GameObject owner, double width, double height, double amount, double duration) {
        this.owner = owner; // who owns the attack

        this.amount = amount; // the damage to deal
        this.duration = duration; // the duration of the attack

        this.width = width; // width of the attack
        this.height = height; // height of the attack

        hitboxMap.put(this, owner);
    }

    public static Builder hit(GameObject owner, double width, double height, double amount, double duration) {
        return new Builder(owner, width, height, amount, duration);
    }

    public static class Builder {
        private final Attack attack;

        private Builder(GameObject owner, double width, double height, double amount, double duration) {
            attack = new Attack(owner, width, height, amount, duration);
        }

        public Builder directional() {
            attack.direction = attack.owner.getHrzDirection();
            return this;
        }

        public Attack pos(Vec2 ... positions) {
            attack.positions = positions;
            attack.box = new AABB(attack.positions[0].x, attack.positions[0].y, attack.width, attack.height);
            return attack;
        }

        public Attack fromTo(Vec2 from, Vec2 to, int length) {
            if (length <= 1) {
                throw new IllegalArgumentException("Length of Attack.Builder.fromTo must be bigger than 1!");
            }

            Vec2[] result = new Vec2[length];

            for (int i = 0; i < length; i++) {
                double t = (double) i / (length - 1);

                result[i] = new Vec2(
                        MathUtil.lerp(from.x, to.x, t),
                        MathUtil.lerp(from.y, to.y, t)
                );
            }

            attack.positions = result;
            attack.box = new AABB(attack.positions[0].x, attack.positions[0].y, attack.width, attack.height);

            return attack;
        }

    }

    public void update(double dt) {
        if (positions.length == 0) return;

        age += dt;
        double progress = Math.min(age / duration, 1);

        double ownerX = owner.getPos().x;
        double ownerY = owner.getPos().y;

        if (direction != null) {
            ownerX += direction.get().x * owner.getScaledWidth();
            ownerY += direction.get().y * owner.getScaledHeight();
        }

        if (index < positions.length) {
            int prevIndex = Math.max(index - 1, 0);

            double offsetX = MathUtil.lerp(positions[prevIndex].x, positions[index].x, progress);
            double offsetY = MathUtil.lerp(positions[prevIndex].y, positions[index].y, progress);

            double x = ownerX + offsetX - width / 2;
            double y = ownerY + offsetY - height / 2;

            box.x = x;
            box.y = y;
            box.width = width;
            box.height = height;

            index++;
        }

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

    public static HashMap<Attack, GameObject> map() {
        return hitboxMap;
    }
}
