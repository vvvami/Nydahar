package net.vami.nydahar.util;

public class Vec2 {
    public double x;
    public double y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2(Vec2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    public void set(Vec2 v) {
        x = v.x;
        y = v.y;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        double length = length();

        if (length == 0) return;

        x /= length;
        y /= length;
    }

    public void add(Vec2 v) {
        x  += v.x;
        y  += v.y;
    }

    public void add(double dx, double dy) {
        x  += dx;
        y  += dy;
    }

    public void multiply(double amount) {
        x *= amount;
        y *= amount;
    }

    public void limit(double maxLength) {
        double length = length();

        if (length > maxLength) {
            normalize();
            multiply(maxLength);
        }
    }

    public boolean zeroed() {
        return x == 0 && y == 0;
    }

    public static Vec2 DOWN = new Vec2(0,1);
    public static Vec2 UP = new Vec2(0,-1);
    public static Vec2 LEFT = new Vec2(-1,0);
    public static Vec2 RIGHT = new Vec2(1,0);

}
