package net.vami.nydahar.registry.type;

import net.vami.nydahar.object.tile.TileObject;
import net.vami.nydahar.registry.factory.TileObjectFactory;

public class TileObjectType<T extends TileObject> {

    private final TileObjectFactory<T> factory;

    public TileObjectType(TileObjectFactory<T> factory) {
        this.factory = factory;
    }

    public T create(double x, double y, String sprite) {
        String path = "tiles/" + sprite;
        return factory.create(x, y, path);
    }
}
