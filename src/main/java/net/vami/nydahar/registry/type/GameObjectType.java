package net.vami.nydahar.registry.type;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.registry.factory.GameObjectFactory;

public class GameObjectType<T extends GameObject> {

    private final GameObjectFactory<T> factory;

    public GameObjectType(GameObjectFactory<T> factory) {
        this.factory = factory;
    }

    public T create(double x, double y) {
        return factory.create(x, y);
    }
}
