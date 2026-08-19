package net.vami.nydahar.object.tile;

import net.vami.nydahar.render.RenderLayer;

public class BackgroundTile extends TileObject {
    public BackgroundTile(double x, double y, String sprite) {
        super(x, y, sprite);
        renderLayer = RenderLayer.BACKGROUND;
    }

    @Override
    public boolean canCollide() {
        return false;
    }
}
