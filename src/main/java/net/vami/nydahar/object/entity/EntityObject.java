package net.vami.nydahar.object.entity;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.object.interaction.collision.Collider;
import net.vami.nydahar.object.entity.attribute.AttributeMap;
import net.vami.nydahar.object.entity.attribute.Attributes;
import net.vami.nydahar.object.interaction.damage.DamageType;
import net.vami.nydahar.object.interaction.damage.HurtSource;
import net.vami.nydahar.render.RenderSettings;

public abstract class EntityObject extends GameObject {

    protected final AttributeMap attributes = new AttributeMap();

    protected double health;

    protected double iframes;

    public EntityObject(double x, double y) {
        super(x, y);
        initAttributes();
        health = attributes.get(Attributes.MAX_HEALTH);
        setCollider(new Collider(this));
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        if (iframes > 0) iframes--;

        if (isDead() && pos != null) {
            remove();
        }
    }

    public void hurt(HurtSource hurtSource, double amount) {
        if (iframes > 0) return;

        this.setHealth(getHealth() - amount);
        iframes += 30;
    }

    public void hurt(DamageType type, double amount) {
        hurt(new HurtSource(type), amount);
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public boolean canCollideWithOther() {
        return false;
    }

    public AttributeMap attributes() {
        return attributes;
    }

    public abstract void initAttributes();

    @Override
    public double getTotalScale() {
        return RenderSettings.SPRITE_SCALE * (scale + attributes.get(Attributes.SCALE));
    }
}
