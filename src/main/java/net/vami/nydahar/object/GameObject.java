package net.vami.nydahar.object;

import net.vami.nydahar.game.World;
import net.vami.nydahar.object.interaction.collision.Collider;
import net.vami.nydahar.object.entity.EntityObject;
import net.vami.nydahar.object.entity.attribute.Attributes;
import net.vami.nydahar.render.RenderLayer;
import net.vami.nydahar.render.RenderSettings;
import net.vami.nydahar.render.sprite.Sprite;
import net.vami.nydahar.render.sprite.SpriteAnimator;
import net.vami.nydahar.util.AABB;
import net.vami.nydahar.util.MathUtil;
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

        prevPos.set(pos);

        if (hasGravity()) applyGravity(dt);

        updateStepSmooth(dt);

        move(vel.x * dt, vel.y * dt);

        if (hasDrag()) applyDrag(dt);
    }

    public double getStepRenderOffset() {
        return stepRenderOffset;
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
        accelerate(0, World.GRAVITY, dt);

        vel.y = Math.min(vel.y, World.MAX_FALL_SPEED);
    }

    private void move(double dx, double dy) {
        moveX(dx);
        moveY(dy);
    }

    private void moveX(double dx) {
        if (dx == 0) return;

        pos.x += dx;
        collider.update(this);

        for (GameObject object : Collider.map().values()) {
            if (!object.canCollide()) continue;

            if (this instanceof EntityObject entity && object instanceof EntityObject otherEntity) {
                if (!entity.canCollideWithOther() || !otherEntity.canCollideWithOther()) continue;
            }

            Collider other = object.getCollider();
            if (other == null || other == collider) continue;

            if (!collider.isCollidingWith(other)) continue;

            if (isGrounded() && autoStep(other)) {
                continue;
            }

            AABB box = collider.getBox();
            AABB otherBox = other.getBox();

            if (dx > 0) {
                double pen = box.maxX() - otherBox.minX();
                pos.x -= pen;
            } else {
                double pen = otherBox.maxX() - box.minX();
                pos.x += pen;
            }

            vel.x = 0;
            collider.update(this);
        }
    }

    private boolean autoStep(Collider obstacle) {
        AABB box = collider.getBox();
        AABB obstacleBox = obstacle.getBox();

        double feetY = box.maxY();
        double obstacleTop = obstacleBox.minY();

        double stepHeight = feetY - obstacleTop;
        double autoStepHeight = this instanceof EntityObject entity ?
                entity.attributes().get(Attributes.AUTO_STEP) : 0;

        if (stepHeight <= 0 || stepHeight > autoStepHeight) return false;

        double oldY = pos.y;

        pos.y -= stepHeight;
        collider.update(this);

        if (isColliding()) {
            pos.y = oldY;
            collider.update(this);
            return false;
        }

        // for auto-step render smoothing
        prevPos.y -= stepHeight;
        stepRenderOffset += stepHeight;

        grounded = true;
        return true;
    }

    private void updateStepSmooth(double dt) {
        stepRenderOffset = MathUtil.lerp(stepRenderOffset, 0, 1.0 - Math.exp(-8.0 * dt));

        if (Math.abs(stepRenderOffset) < 0.1) {
            stepRenderOffset = 0;
        }
    }

    private void moveY(double dy) {
        if (dy == 0) return;

        pos.y += dy;

        if (collider == null) return;

        collider.update(this);

        for (GameObject object : Collider.map().values()) {
            if (!object.canCollide()) continue;

            if (this instanceof EntityObject entity && object instanceof EntityObject otherEntity) {
                if (!entity.canCollideWithOther() || !otherEntity.canCollideWithOther()) continue;
            }

            Collider other = object.getCollider();
            if (other == collider) continue;

            if (!collider.isCollidingWith(other)) continue;

            AABB box = collider.getBox();
            AABB otherBox = other.getBox();

            double pen;

            if (dy > 0) {
                pen = (box.y + box.height) - otherBox.y;
                pos.y -= pen;

                grounded = true;
            }
            else {
                pen = (otherBox.y + otherBox.height) - box.y;
                pos.y += pen;

                grounded = false;
            }

            vel.y = 0;
            collider.update(this);
        }
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
