package net.vami.nydahar.registry;

import net.vami.nydahar.registry.type.EntityObjectType;
import net.vami.nydahar.registry.type.TileObjectType;

public class Registries {

    public static final Registry<EntityObjectType<?>> ENTITY = new Registry<>();
    public static final Registry<TileObjectType<?>> TILES = new Registry<>();

}
