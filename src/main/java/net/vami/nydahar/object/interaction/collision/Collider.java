package net.vami.nydahar.object.interaction.collision;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.util.AABB;

import java.util.HashMap;
import java.util.Map;

public class Collider {
    private double left = 0;
    private double top = 1;
    private double right = 1;
    private double bottom = 0;

    private AABB collisionBox;

    private static final Map<Collider, GameObject> colliderMap = new HashMap<>();

    public Collider(GameObject object, double left, double top, double right, double bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        collisionBox = new AABB(
                object.getPos().x,
                object.getPos().y,
                object.getSprite().getWidth(),
                object.getSprite().getHeight());

        update(object);
        colliderMap.put(this, object);
    }

    public Collider(GameObject object) {
        this(object, 0, 1, 1, 0);
        update(object);
    }

    public AABB getBox() {
        return collisionBox;
    }

    public double getWidth() {
        return collisionBox.width;
    }
    public double getHeight() {
        return collisionBox.height;
    }

    public double getX() {
        return collisionBox.x;
    }

    public double getY() {
        return collisionBox.y;
    }

    public double getCenterX() {
        return collisionBox.getX() + collisionBox.getWidth() / 2;
    }

    public double getCenterY() {
        return collisionBox.getY() + collisionBox.getHeight() / 2;
    }

    public void setX(double x) {
        collisionBox.x = x;
    }

    public void setY(double y) {
        collisionBox.y = y;
    }

    public void setWidth(double width) {
        collisionBox.width = width;
    }

    public void setHeight(double height) {
        collisionBox.height = height;
    }

    public void setBox(AABB collisionBox) {
        this.collisionBox = collisionBox;
    }

    public void grow(float width, float height) {
        this.collisionBox.grow(width, height);
    }

    public void grow(float amount) {
        this.collisionBox.grow(amount, amount);
    }

    public double minX() {
        return collisionBox.x;
    }

    public double maxX() {
        return collisionBox.x + collisionBox.width;
    }

    public double minY() {
        return collisionBox.y;
    }

    public double maxY() {
        return collisionBox.y + collisionBox.height;
    }

    public boolean isCollidingWith(Collider collider) {
        return this.collisionBox.intersects(collider.collisionBox);
    }

    public static Map<Collider, GameObject> map() {
        return colliderMap;
    }

    public void update(GameObject object) {
        double width = object.getScaledWidth();
        double height = object.getScaledHeight();

        this.collisionBox.x = object.getPos().x + width * left - width / 2;
        this.collisionBox.y = object.getPos().y + height * bottom - height / 2;
        this.collisionBox.width = width * (right - left);
        this.collisionBox.height = height * (top - bottom);
    }
}