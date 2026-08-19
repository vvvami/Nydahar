package net.vami.nydahar.object.entity;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.object.entity.attribute.AttributeMap;
import net.vami.nydahar.object.entity.attribute.Attributes;
import net.vami.nydahar.render.RenderSettings;

public abstract class EntityObject extends GameObject {

    protected final AttributeMap attributes = new AttributeMap();

    public EntityObject(double x, double y) {
        super(x, y);
    }

    public AttributeMap attributes() {
        return attributes;
    }

    @Override
    public double getTotalScale() {
        return RenderSettings.SPRITE_SCALE * (scale + attributes.get(Attributes.SCALE));
    }
}
