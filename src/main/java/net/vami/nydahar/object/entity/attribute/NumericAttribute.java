package net.vami.nydahar.object.entity.attribute;

public class NumericAttribute extends Attribute<Double> {
    private final double min;
    private final double max;

    public NumericAttribute(String name, double value, double min, double max) {
        super(name, value);
        this.min = min;
        this.max = max;
    }

    public double clamp(double value) {
        return Math.clamp(value, min, max);
    }
}
