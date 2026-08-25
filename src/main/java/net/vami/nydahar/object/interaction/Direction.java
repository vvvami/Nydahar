package net.vami.nydahar.object.interaction;

import net.vami.nydahar.util.Vec2;

public enum Direction {
    LEFT(new Vec2(-1, 0)),
    UP(new Vec2(0, 1)),
    RIGHT(new Vec2(1, 0)),
    DOWN(new Vec2(0, -1));

    final Vec2 vec;

    Direction(Vec2 vec) {
        this.vec = vec;
    }

    public Vec2 get() {
        return vec;
    }

    public boolean isHorizontal() {
        return this == LEFT || this == RIGHT;
    }
}
