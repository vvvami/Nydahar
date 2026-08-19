package net.vami.nydahar.object.tile;

import net.vami.nydahar.object.interaction.collision.Collider;

public class SolidTile extends TileObject {
    public SolidTile(double x, double y, String sprite) {
        super(x, y, sprite);

        collider = new Collider(this,
                0,0,1,1);
    }
}
