package net.vami.nydahar.object;

import net.vami.nydahar.game.World;
import net.vami.nydahar.object.interaction.Direction;
import net.vami.nydahar.object.interaction.collision.Collider;
import net.vami.nydahar.object.entity.EntityObject;
import net.vami.nydahar.object.entity.attribute.Attributes;
import net.vami.nydahar.render.RenderLayer;
import net.vami.nydahar.render.RenderSettings;
import net.vami.nydahar.render.sprite.Sprite;
import net.vami.nydahar.render.sprite.SpriteAnimator;
import net.vami.nydahar.util.AABB;
import net.vami.nydahar.util.MathUtil;
import net.vami.nydahar.util.Ray2;
import net.vami.nydahar.util.Vec2;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public abstract class GameObject {
    private static final HashMap<UUID, GameObject> objectMap = new HashMap<>();

    protected UUID uuid;

    protected Vec2 pos = new Vec2(0,0);
    protected Vec2 prevPos;

    protected Vec2 vel = new Vec2(0,0);
    protected double drag = 0;

    protected boolean hasGravity = true;

    protected Sprite sprite;
    protected SpriteAnimator animator;

    protected RenderLayer renderLayer = RenderLayer.ENTITY;

    protected Collider collider;
    protected boolean hasCollision = true;

    protected boolean grounded = false;

    private double stepRenderOffset;
    private double prevStepRenderOffset;

    protected double scale = 1;

    public GameObject(double x, double y) {
        this.uuid = UUID.randomUUID();

        pos.x = x;
        pos.y = y;

        prevPos = new Vec2(pos);

        objectMap.put(uuid, this);

        registerSprite();
    }

    public void update(double dt) {
        if (animator != null) {
            animator.update(dt);
        }

        if (pos == null) return;

        grounded = false;

        prevPos.set(pos);
        prevStepRenderOffset = stepRenderOffset;

        if (hasGravity()) applyGravity(dt);

        move(vel.x * dt, vel.y * dt);

        updateStepSmooth(dt);

        if (hasDrag()) applyDrag(dt);
    }

    private void applyDrag(double dt) {
        double amount = drag * dt;

        if (Math.abs(vel.x) <= amount) {
            vel.x = 0;
        } else {
            vel.x -= Math.signum(vel.x) * amount;
        }
    }

    private void applyGravity(double dt) {
        accelerate(0, -World.GRAVITY, dt);

        vel.y = Math.max(vel.y, -World.MAX_FALL_SPEED);
    }

    private boolean checkGrounded(GameObject object) {
        Collider collider = object.getCollider();

        Vec2 origin1 = new Vec2(collider.maxX(), collider.minY());
        Vec2 origin2 = new Vec2(collider.minX(), collider.minY());

        return Ray2.cast(origin1, Vec2.DOWN, 0, object).isHit()
                || Ray2.cast(origin2, Vec2.DOWN, 0, object).isHit();
    }

    private void move(double dx, double dy) {
        moveX(dx);
        moveY(dy);
    }

    private void moveX(double vx) {
        double remaining = vx;

        if (remaining == 0 || collider == null) return;

        int safety = 0;

        while (Math.abs(remaining) > 0.0001 && safety++ < 8) {
            collider.update(this);

            AABB box = collider.getBox();

            Collider nearest = null;
            double allowed = remaining;

            for (GameObject object : Collider.map().values()) {
                if (!object.canCollide()) continue;

                if (this instanceof EntityObject entity && object instanceof EntityObject otherEntity) {
                    if (!entity.canCollideWithOther() || !otherEntity.canCollideWithOther()) continue;
                }

                Collider other = object.getCollider();

                if (other == null || other == collider) continue;

                AABB otherBox = other.getBox();

                boolean verticalOverlap = box.maxY() > otherBox.minY() && box.minY() < otherBox.maxY();
                // player-maxY > floor-minY AND player-minY < floor-maxY
                if (!verticalOverlap) continue;

                if (remaining > 0) {
                    double gap = otherBox.minX() - box.maxX();

                    if (gap >= 0 && gap <= allowed) {
                        allowed = gap;
                        nearest = other;
                    }
                } else {
                    double gap = otherBox.maxX() - box.minX();

                    if (gap <= 0 && gap >= allowed) {
                        allowed = gap;
                        nearest = other;
                    }
                }
            }

            if (nearest == null) {
                pos.x += remaining;
                collider.update(this);
                return;
            }

            pos.x += allowed;
            collider.update(this);

            remaining -= allowed;

            if (checkGrounded(this) && vel.y <= 0 && autoStep(nearest)) {
                continue;
            }

            vel.x = 0;
            collider.update(this);

            return;
        }
    }

    private boolean autoStep(Collider obstacle) {
        AABB box = collider.getBox();
        AABB obstacleBox = obstacle.getBox();

        double stepHeight = obstacleBox.maxY() - box.minY();

        double autoStepHeight = this instanceof EntityObject entity ? entity.attributes().get(Attributes.AUTO_STEP) : 0;

        if (stepHeight <= 0 || stepHeight > autoStepHeight) return false;

        double oldY = pos.y;

        pos.y += stepHeight;
        collider.update(this);

        if (isColliding()) {
            pos.y = oldY;
            collider.update(this);
            return false;
        }

        stepRenderOffset -= stepHeight / 1.5; // optimal smoothing w/o being too much in the collider below

        grounded = true;
        return true;
    }

    private void updateStepSmooth(double dt) {
        stepRenderOffset = MathUtil.lerp(stepRenderOffset, 0, 1.0 - Math.exp(-8.0 * dt));

        if (Math.abs(stepRenderOffset) < 0.1) stepRenderOffset = 0;
    }

    public double getStepRenderOffset() {
        return stepRenderOffset;
    }

    public double getPrevStepRenderOffset() {
        return prevStepRenderOffset;
    }

    private void moveY(double vy) {

        if (vy == 0 || collider == null) return;

        collider.update(this);

        AABB box = collider.getBox();

        Collider nearest = null;
        double allowed = vy;

        for (GameObject object : Collider.map().values()) {
            if (!object.canCollide()) continue;

            if (this instanceof EntityObject entity && object instanceof EntityObject otherEntity) {
                if (!entity.canCollideWithOther() || !otherEntity.canCollideWithOther()) continue;
            }

            Collider other = object.getCollider();

            if (other == null || other == collider) continue;

            AABB otherBox = other.getBox();

            boolean horizontalOverlap = box.maxX() > otherBox.minX() && box.minX() < otherBox.maxX();

            if (!horizontalOverlap) continue;

            if (vy > 0) {
                double gap = otherBox.minY() - box.maxY();

                if (gap >= 0 && gap <= allowed) {
                    allowed = gap;
                    nearest = other;
                }
            }
            else {
                double gap = otherBox.maxY() - box.minY();

                if (gap <= 0 && gap >= allowed) {
                    allowed = gap;
                    nearest = other;
                }
            }
        }

        if (nearest == null) {
            pos.y += vy;
            collider.update(this);
            return;
        }

        pos.y += allowed;
        collider.update(this);

        grounded = vy <= 0;
        vel.y = 0;
    }

    public void setPos(double x, double y) {
        pos.x = x;
        pos.y = y;
    }

    public void setPos(Vec2 pos) {
        this.pos = pos;
    }

    public Vec2 getPos() {
        return pos;
    }

    public Vec2 getPrevPos() {
        return prevPos;
    }

    public void setVel(Vec2 vel) {
        this.vel = vel;
    }

    public void setVel(double x, double y) {
        this.vel.x = x;
        this.vel.y = y;
    }

    public void push(Vec2 vel) {
        this.vel.x += vel.x;
        this.vel.y += vel.y;
    }

    public void push(double x, double y) {
        this.vel.x += x;
        this.vel.y += y;
    }

    public void accelerate(double x, double y, double dt) {
        vel.x += x * dt;
        vel.y += y * dt;
    }

    public Vec2 getVel() {
        return vel;
    }

    public Direction getHrzDirection() {
        if (vel.x > 0) return Direction.RIGHT;

        else if (vel.x < 0) return Direction.LEFT;

        return Direction.NONE;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public boolean hasDrag() {
        return drag > 0;
    }

    public boolean hasGravity() {
        return hasGravity;
    }

    public void doGravity(boolean gravity) {
        this.hasGravity = gravity;
    }

    public void delete() {
        remove();
        objectMap.remove(this.uuid);
    }

    public void remove() {
        this.pos = null;

        doCollision(false);
        doGravity(false);
    }

    public UUID getUUID() {
        return uuid;
    }

    public Collider getCollider() {
        return collider;
    }

    public void setCollider(Collider collider) {
        this.collider = collider;
    }

    public boolean canCollide() {
        return hasCollision;
    }

    private boolean isColliding() {
        for (GameObject object : Collider.map().values()) {
            if (!object.canCollide()) continue;

            Collider other = object.getCollider();
            if (other == null || other == collider) continue;

            if (getCollider().isCollidingWith(other)) {
                return true;
            }
        }

        return false;
    }

    public boolean isCollidingWith(GameObject other) {
        for (GameObject object : Collider.map().values()) {
            if (object != other) continue;
            if (!object.canCollide()) continue;

            Collider objectCollider = object.getCollider();
            if (getCollider().isCollidingWith(objectCollider)) {
                return true;
            }
        }
        return false;
    }

    public void doCollision(boolean collision) {
        hasCollision = collision;
    }

    public Sprite getSprite() {
        if (animator == null) return sprite;

        return animator.getSprite();
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public boolean hasSprite() {
        return sprite != null;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getScale() {
        return scale;
    }

    public double getTotalScale() {
        return scale * RenderSettings.SPRITE_SCALE;
    }

    public double getScaledWidth() {
        if (sprite == null) return 0;

        return sprite.getWidth() * getTotalScale();
    }

    public double getScaledHeight() {
        if (sprite == null) return 0;

        return sprite.getHeight() * getTotalScale();
    }

    public RenderLayer getRenderLayer() {
        return renderLayer;
    }

    public static HashMap<UUID, GameObject> map() {
        return objectMap;
    }

    public static Collection<GameObject> objects() {
        return objectMap.values();
    }

    public static GameObject get(UUID uuid) {
        return objectMap.get(uuid);
    }

    public void registerSprite() {

    }

}
