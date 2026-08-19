package net.vami.nydahar.object.tile;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.render.sprite.Sprite;
import net.vami.nydahar.render.sprite.Sprites;

public abstract class TileObject extends GameObject {

    public TileObject(double x, double y) {
        super(x, y);
    }

    public TileObject(double x, double y, String sprite) {
        super(x, y);
        setSprite(Sprites.get(sprite));
    }

    @Override
    public boolean hasGravity() {
        return false;
    }
}
