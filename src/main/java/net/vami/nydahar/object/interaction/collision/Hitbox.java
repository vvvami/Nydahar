package net.vami.nydahar.object.interaction.collision;

import net.vami.nydahar.object.GameObject;

public class Hitbox {

    private final GameObject source;
    private final Collider bounds;

    public Hitbox(GameObject source, Collider bounds) {
        this.source = source;
        this.bounds = bounds;
    }

    public Hitbox(Collider bounds) {
        this(null, bounds);
    }
}
