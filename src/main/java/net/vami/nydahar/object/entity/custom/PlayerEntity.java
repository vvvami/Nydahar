package net.vami.nydahar.object.entity.custom;

import net.vami.nydahar.input.Input;
import net.vami.nydahar.object.interaction.collision.Collider;
import net.vami.nydahar.object.entity.EntityObject;
import net.vami.nydahar.object.entity.attribute.Attributes;
import net.vami.nydahar.render.sprite.Sprites;

public class PlayerEntity extends EntityObject {
    public static final double ACCELERATION = 800;

    public PlayerEntity(double x, double y) {
        super(x, y);
        drag = 600;

        attributes.set(Attributes.SPEED, 200d);
        attributes.set(Attributes.JUMP, 210d);
        attributes.set(Attributes.SCALE, 0d);
        attributes.set(Attributes.AUTO_STEP, 17d);

        setCollider(new Collider(this,
                0.3, 0.2, 0.7, 1));
    }

    public void inputTick(double dt, Input input) {
        double direction = 0;

        if (input.left) direction -= 1;
        if (input.right) direction += 1;

        vel.x += direction * PlayerEntity.ACCELERATION * dt;

        double speed = this.attributes.get(Attributes.SPEED);

        vel.x = Math.clamp(vel.x, -speed, speed);

        if (input.up && grounded) {
            double jumpStrength = attributes.get(Attributes.JUMP);

            vel.y = -jumpStrength;
            grounded = false;
        }
    }

    @Override
    public boolean canCollideWithOther() {
        return true;
    }

    @Override
    public void registerSprite() {
        setSprite(Sprites.get("entities/player/player"));
    }
}
