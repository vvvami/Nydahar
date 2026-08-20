package net.vami.nydahar.object.interaction.damage;

import net.vami.nydahar.object.GameObject;

public record HurtSource(GameObject source, DamageType type) {

    public HurtSource(DamageType type) {
        this(null, type);
    }
}
