package net.vami.nydahar.object.entity.custom;

import net.vami.nydahar.object.entity.EntityObject;
import net.vami.nydahar.render.sprite.Sprites;

public class WolfEntity extends EntityObject {

    public WolfEntity(double x, double y) {
        super(x, y);
    }

    @Override
    public void registerSprite() {
        setSprite(Sprites.get("entities/enemy"));
    }
}
