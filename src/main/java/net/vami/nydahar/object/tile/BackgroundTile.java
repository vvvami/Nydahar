package net.vami.nydahar.object.tile;

public class BackgroundTile extends TileObject {
    public BackgroundTile(double x, double y, String sprite) {
        super(x, y, sprite);
    }

    @Override
    public boolean canCollide() {
        return false;
    }
}
