package net.vami.nydahar.object.entity;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.object.interaction.collision.Collider;
import net.vami.nydahar.object.entity.attribute.AttributeMap;
import net.vami.nydahar.object.entity.attribute.Attributes;
import net.vami.nydahar.object.interaction.damage.Damager;
import net.vami.nydahar.render.RenderSettings;

public abstract class EntityObject extends GameObject {

    protected final AttributeMap attributes = new AttributeMap();

    protected double health;

    public EntityObject(double x, double y) {
        super(x, y);
        health = attributes.get(Attributes.MAX_HEALTH);
        setCollider(new Collider(this));
    }

    public void hurt(Damager damager, double amount) {
        this.setHealth(getHealth() - amount);
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public boolean canCollideWithOther() {
        return false;
    }

    public AttributeMap attributes() {
        return attributes;
    }

    @Override
    public double getTotalScale() {
        return RenderSettings.SPRITE_SCALE * (scale + attributes.get(Attributes.SCALE));
    }
}
