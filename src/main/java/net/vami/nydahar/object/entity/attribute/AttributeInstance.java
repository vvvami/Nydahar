package net.vami.nydahar.object.entity.attribute;

public class AttributeInstance<T> {
    private final Attribute<T> attribute;
    private T value;

    public AttributeInstance(Attribute<T> attribute) {
        this.attribute = attribute;
        this.value = attribute.getDefaultValue();
    }

    public AttributeInstance(Attribute<T> attribute, T value) {
        this.attribute = attribute;
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public Attribute<T> getAttribute() {
        return attribute;
    }
}
