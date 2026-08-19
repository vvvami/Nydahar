package net.vami.nydahar.object.entity.attribute;

import java.util.HashMap;
import java.util.Map;

public class AttributeMap {
    private final Map<Attribute<?>, AttributeInstance<?>> attributes =  new HashMap<>();

    public <T> void set(Attribute<T> attribute, T value) {
        getInstance(attribute).set(value);
    }

    public <T> T get(Attribute<T> attribute) {
        return getInstance(attribute).get();
    }

    @SuppressWarnings("all")
    public <T> AttributeInstance<T> getInstance(Attribute<T> attribute) {
        return (AttributeInstance<T>) attributes.computeIfAbsent(
                attribute,
                a -> new AttributeInstance<>(attribute));
    }

    public boolean has(Attribute<?> attribute) {
        return attributes.containsKey(attribute);
    }

    public void remove(Attribute<?> attribute) {
        attributes.remove(attribute);
    }

    public void clear() {
        attributes.clear();
    }
}
