package net.vami.nydahar.registry.type;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.object.entity.EntityObject;
import net.vami.nydahar.registry.factory.EntityObjectFactory;
import net.vami.nydahar.registry.factory.GameObjectFactory;

public class EntityObjectType<T extends EntityObject> {

    private final EntityObjectFactory<T> factory;

    public EntityObjectType(EntityObjectFactory<T> factory) {
        this.factory = factory;
    }

    public T create(double x, double y) {
        return factory.create(x, y);
    }
}
