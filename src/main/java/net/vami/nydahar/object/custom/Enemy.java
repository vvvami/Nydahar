package net.vami.nydahar.object.custom;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.render.SpriteRenderable;

public class Enemy extends GameObject implements SpriteRenderable {
    public static final double SPEED = 200;

    public Enemy(double x, double y) {
        super(x, y);
    }

    @Override
    public String getSprite() {
        return "enemy";
    }
}
