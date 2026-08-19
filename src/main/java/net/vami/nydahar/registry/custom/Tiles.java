package net.vami.nydahar.registry.custom;

import net.vami.nydahar.object.tile.BackgroundTile;
import net.vami.nydahar.object.tile.SolidTile;
import net.vami.nydahar.object.tile.TileObject;
import net.vami.nydahar.registry.Registries;
import net.vami.nydahar.registry.Registry;
import net.vami.nydahar.registry.type.TileObjectType;

public final class Tiles {
    private Tiles() {}


    public static final TileObjectType<BackgroundTile> RED_PILLAR =
            Registries.TILES.register("red_pillar", new TileObjectType<>(BackgroundTile::new));

    public static final TileObjectType<SolidTile> BLACK_FLOOR =
            Registries.TILES.register("black_floor", new TileObjectType<>(SolidTile::new));


}
