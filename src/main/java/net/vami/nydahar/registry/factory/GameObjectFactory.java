package net.vami.nydahar.registry.factory;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.object.collision.Collider;

public interface GameObjectFactory<T extends GameObject> {
    T create(double x, double y);
}
