package net.vami.nydahar.registry.custom;

import net.vami.nydahar.object.entity.custom.PlayerEntity;
import net.vami.nydahar.object.entity.custom.WolfEntity;
import net.vami.nydahar.registry.Registries;
import net.vami.nydahar.registry.type.EntityObjectType;

public final class Entities {
    private Entities() {}


    public static final EntityObjectType<PlayerEntity> PLAYER =
            Registries.ENTITY.register("player", new EntityObjectType<>(PlayerEntity::new));

    public static final EntityObjectType<WolfEntity> WOLF =
            Registries.ENTITY.register("wolf", new EntityObjectType<>(WolfEntity::new));

    public static void register() {

    }
}
