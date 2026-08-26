package net.vami.nydahar.object.entity.custom;

import net.vami.nydahar.game.Game;
import net.vami.nydahar.input.Input;
import net.vami.nydahar.object.interaction.Direction;
import net.vami.nydahar.object.interaction.collision.Collider;
import net.vami.nydahar.object.entity.EntityObject;
import net.vami.nydahar.object.entity.attribute.Attributes;
import net.vami.nydahar.object.interaction.damage.Attack;
import net.vami.nydahar.render.sprite.SpriteAnimation;
import net.vami.nydahar.render.sprite.SpriteAnimator;
import net.vami.nydahar.render.sprite.Sprites;
import net.vami.nydahar.util.Vec2;

public class PlayerEntity extends EntityObject {
    public static final double ACCELERATION = 1000;

    private int dashCooldown = 0;
    private boolean upDash = true;

    private int attackCooldown = 0;
    private int comboIndex = 0;
    private int comboTimer = 0;

    private Direction direction;

    public PlayerEntity(double x, double y) {
        super(x, y);
        drag = 600;

        setCollider(new Collider(this,
                0.3, 0.8, 0.7, 0));

    }

    public void inputTick(double dt, Input input) {
        double dirVel = 0;

        upDash = upDash || isGrounded();

        if (input.left) {
            dirVel -= 1;
            this.direction = Direction.LEFT;
        }

        if (input.right) {
            dirVel += 1;
            this.direction = Direction.RIGHT;
        }

        double speed = this.attributes.get(Attributes.SPEED);
        double acceleration = PlayerEntity.ACCELERATION * dt;

        if (dirVel > 0) {
            if (vel.x < speed) {
                vel.x = Math.min(speed, vel.x + acceleration);
            }
        }
        else if (dirVel < 0) {
            if (vel.x > -speed) {
                vel.x = Math.max(-speed, vel.x - acceleration);
            }
        }

        if (input.up_down) {
            direction = Direction.UP;

            if (isGrounded()) {
                double jumpStrength = attributes.get(Attributes.JUMP);

                push(0, jumpStrength);
                grounded = false;
            }
        }

        if (input.down && !isGrounded()) {
            push(0, -50);
        }

        if (input.space && dashCooldown <= 0) {
            dashCooldown = 30;
            switch (getHrzDirection()) {
                case Direction.LEFT -> push(-350, 10);
                case Direction.RIGHT -> push(350, 10);
                default -> {
                    if (upDash) {
                        push(0, 300);
                        upDash = false;
                    }
                }
            }
        }

        if (dashCooldown > 0) dashCooldown--;

        Game instance = Game.getInstance();

        double mouseWorldX = instance.getCamera().getPos().x + input.mouseX - instance.getCanvas().getWidth() / 2d;
        double mouseWorldY = instance.getCamera().getPos().y + instance.getCanvas().getHeight() / 2d - input.mouseY;

        double dx = mouseWorldX - this.getPos().x;
        double dy = mouseWorldY - this.getPos().y;

        if (input.left_click_pressed) {
            input.left_click_pressed = false;

            if (attackCooldown > 0) return;
            attackCooldown = 20;

            if (Math.abs(dx) > Math.abs(dy)) {
                this.direction = dx < 0 ? Direction.LEFT : Direction.RIGHT;
            } else {
                this.direction = dy < 0 ? Direction.DOWN : Direction.UP;
            }

            switch (direction) {
                case LEFT, RIGHT -> sideSweepAttack();
                case UP -> topAttack(50, 75);
                case DOWN -> downAttack(50, 75);
            }
        }

        if (attackCooldown > 0) attackCooldown--;
        if (comboTimer > 0) {
            comboTimer--;
            if (comboTimer == 0) {
                comboIndex = 0;
            }
        }
    }

    public void sideSweepAttack() {
        switch (comboIndex) {
            case 0 -> sideAttack1(75, 50);
            case 1 -> sideAttack2(75, 50);
            case 2 -> sideAttack3(75, 50);
        }

        comboIndex = (comboIndex + 1) % 3;
        comboTimer = 60;
    }

    public void sideAttack1(double width, double height) {
        Attack.hit(this, width, height, 5, 0.25).directional().fromTo(
                new Vec2(0, this.getScaledHeight()),
                new Vec2(0, 0),
                5
        );
    }

    public void sideAttack2(double width, double height) {
        Attack.hit(this, width, height, 5, 0.25).directional().fromTo(
                new Vec2(0, 0),
                new Vec2(0, this.getScaledHeight()),
                5
        );
    }

    public void sideAttack3(double width, double height) {
        Attack.hit(this, width, height, 5, 0.25).fromTo(
                new Vec2(this.getScaledWidth() / 2 * direction.get().x, 0),
                new Vec2(this.getScaledWidth() * direction.get().x, 0),
                5
        );
    }

    public void topAttack(double width, double height) {
        Attack.hit(this, width, height, 5, 0.25).fromTo(
                new Vec2(0, this.getScaledHeight()),
                new Vec2(0, this.getScaledHeight() * 1.5),
                5
        );
    }

    public void downAttack(double width, double height) {
        Attack.hit(this, width, height, 5, 0.25).fromTo(
                new Vec2(0, -this.getScaledHeight()),
                new Vec2(0, -this.getScaledHeight() * 1.5),
                5
        );
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
        super.registerSprite();
        animator.play(Sprites.getAnimation("entities/player/player_idle"), 0.5f);
    }
}
