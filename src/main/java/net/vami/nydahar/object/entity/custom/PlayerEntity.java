package net.vami.nydahar.object.entity.custom;

import net.vami.nydahar.game.Game;
import net.vami.nydahar.input.Input;
import net.vami.nydahar.object.interaction.Direction;
import net.vami.nydahar.object.interaction.collision.Collider;
import net.vami.nydahar.object.entity.EntityObject;
import net.vami.nydahar.object.entity.attribute.Attributes;
import net.vami.nydahar.render.sprite.Sprites;

public class PlayerEntity extends EntityObject {
    public static final double ACCELERATION = 1000;

    private int dashCooldown = 0;

    private boolean upDash = true;

    private Direction direction;

    public PlayerEntity(double x, double y) {
        super(x, y);
        drag = 600;

        setCollider(new Collider(this,
                0.3, 0.2, 0.7, 1));
    }

    public void inputTick(double dt, Input input) {
        double direction = 0;

        upDash = upDash || isGrounded();

        if (input.left) {
            direction -= 1;
            this.direction = Direction.LEFT;
        }

        if (input.right) {
            direction += 1;
            this.direction = Direction.RIGHT;
        }

        double speed = this.attributes.get(Attributes.SPEED);
        double acceleration = PlayerEntity.ACCELERATION * dt;

        if (direction > 0) {
            if (vel.x < speed) {
                vel.x = Math.min(
                        speed,
                        vel.x + acceleration
                );
            }
        }
        else if (direction < 0) {
            if (vel.x > -speed) {
                vel.x = Math.max(
                        -speed,
                        vel.x - acceleration
                );
            }
        }

        if (input.up && isGrounded()) {
            double jumpStrength = attributes.get(Attributes.JUMP);

            push(0, -jumpStrength);
            grounded = false;
        }

        if (input.down && !isGrounded()) {
            push(0, 50);
        }

        if (input.space && dashCooldown <= 0) {
            dashCooldown = 30;
            switch (getHrzDirection()) {
                case LEFT -> push(-350, -10);
                case RIGHT -> push(350, -10);
                default -> {
                    if (upDash) {
                        push(0, -300);
                        upDash = false;
                    }
                }
            }
        }

        dashCooldown--;
        this.direction = Direction.NONE;
    }

    @Override
    public boolean canCollideWithOther() {
        return true;
    }

    @Override
    public void initAttributes() {
        attributes.set(Attributes.SPEED, 200d);
        attributes.set(Attributes.JUMP, 210d);
        attributes.set(Attributes.SCALE, 0d);
        attributes.set(Attributes.AUTO_STEP, 17d);
    }

    @Override
    public Direction getHrzDirection() {
        if (direction == null) {
            return super.getHrzDirection();
        }

        return direction;
    }

    @Override
    public void registerSprite() {
        setSprite(Sprites.get("entities/player/player"));
    }
}
