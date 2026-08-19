package net.vami.nydahar.object.entity.attribute;

public class Attribute<T> {
    private final String name;
    private final T defaultValue;

    public Attribute(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return this.name;
    }
    public T getDefaultValue() {
        return this.defaultValue;
    }
}
