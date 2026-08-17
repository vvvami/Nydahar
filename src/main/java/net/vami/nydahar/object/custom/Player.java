package net.vami.nydahar.object.custom;

import net.vami.nydahar.game.Game;
import net.vami.nydahar.input.Input;
import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.render.SpriteRenderable;
import net.vami.nydahar.util.Vec2;

public class Player extends GameObject implements SpriteRenderable {
    public static final double SPEED = 200;
    public static final double ACCELERATION = 800; // px/s²

    public Player(double x, double y) {
        super(x, y);
        drag = 600;
    }

    public void inputTick(double dt, Input input) {
        Vec2 vel = this.getVel();

        Vec2 direction = new Vec2(0, 0);

        if (input.left)  direction.x -= 1;
        if (input.right) direction.x += 1;
        if (input.up)    direction.y -= 1;
        if (input.down)  direction.y += 1;

        direction.normalize();

        direction.multiply(Player.ACCELERATION * dt);
        vel.add(direction);
        vel.limit(Player.SPEED);
    }

    @Override
    public String getSprite() {
        return "player";
    }
}
