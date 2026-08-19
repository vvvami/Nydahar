package net.vami.nydahar.registry.factory;

import net.vami.nydahar.object.tile.TileObject;

public interface TileObjectFactory<T extends TileObject> {
    T create(double x, double y, String sprite);
}
