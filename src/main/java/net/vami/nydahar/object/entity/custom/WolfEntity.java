package net.vami.nydahar.object.entity.custom;

import net.vami.nydahar.game.Game;
import net.vami.nydahar.object.entity.EntityObject;
import net.vami.nydahar.object.entity.attribute.Attributes;
import net.vami.nydahar.object.interaction.damage.DamageType;
import net.vami.nydahar.object.interaction.damage.HurtSource;
import net.vami.nydahar.render.sprite.Sprites;

public class WolfEntity extends EntityObject {
    private int count;

    public WolfEntity(double x, double y) {
        super(x, y);
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        count++;

        if (count % Game.TPS == 0) {
            hurt(new HurtSource(new DamageType()), 1);
        }
    }

    @Override
    public void initAttributes() {
        attributes.set(Attributes.MAX_HEALTH, 10d);
    }

    @Override
    public void registerSprite() {
        setSprite(Sprites.get("entities/enemy"));
    }
}
