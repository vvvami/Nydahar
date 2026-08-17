package net.vami.nydahar.object;

import net.vami.nydahar.util.Vec2;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class GameObject {
    private static final HashMap<UUID, GameObject> objectList = new HashMap<>();

    protected UUID uuid;

    protected Vec2 pos = new Vec2(0,0);
    protected Vec2 prevPos;

    protected Vec2 vel = new Vec2(0,0);
    protected double drag = 0;

    public GameObject(double x, double y) {
        this.uuid = UUID.randomUUID();

        pos.x = x;
        pos.y = y;

        prevPos = new Vec2(pos);

        objectList.put(uuid, this);
    }

    public void update(double dt) {
        prevPos.set(pos);

        move(vel.x * dt, vel.y * dt);

        applyDrag(dt);
    }

    private void applyDrag(double dt) {
        double speed = vel.length();

        if (speed > 0) {
            double newSpeed = Math.max(0, speed - drag * dt);

            vel.normalize();
            vel.multiply(newSpeed);
        }
    }

    private void move(double dx, double dy) {
        pos.x += dx;
        pos.y += dy;
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

    public void addVel(Vec2 vel) {
        this.vel.x += vel.x;
        this.vel.y += vel.y;
    }

    public void addVel(double x, double y) {
        this.vel.x += x;
        this.vel.y += y;
    }

    public Vec2 getVel() {return vel;}

    public static HashMap<UUID, GameObject> list() {
        return objectList;
    }

    public static Collection<GameObject> objects() {
        return objectList.values();
    }

    public static GameObject get(UUID uuid) {
        return objectList.get(uuid);
    }

}
