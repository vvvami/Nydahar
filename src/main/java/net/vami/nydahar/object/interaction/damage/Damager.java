package net.vami.nydahar.object.interaction.damage;

import net.vami.nydahar.object.GameObject;

public class Damager {
    private final GameObject source;
    private final DamageType type;

    public Damager(GameObject source, DamageType type) {
        this.source = source;
        this.type = type;
    }

    public Damager(DamageType type) {
        this(null, type);
    }

    public DamageType getType() {
        return type;
    }

    public GameObject getSource() {
        return source;
    }
}
