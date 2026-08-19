package net.vami.nydahar.registry.factory;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.object.entity.EntityObject;

public interface EntityObjectFactory<T extends EntityObject> {
    T create(double x, double y);
}
