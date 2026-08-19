package net.vami.nydahar.object.entity.attribute;

public class Attributes {

    public static final Attribute<Integer> LEVEL =
            new Attribute<>("level", 1);

    public static final Attribute<Double> MAX_HEALTH =
            new Attribute<>("max_health", 10d);

    public static final Attribute<Double> DAMAGE =
            new Attribute<>("damage", 1d);

    public static final NumericAttribute AUTO_STEP =
            new NumericAttribute("auto_step",
                    16, 0, 1024);

    public static final NumericAttribute JUMP =
            new NumericAttribute("jump",
                    110, 0, 1024);

    public static final NumericAttribute SPEED =
            new NumericAttribute("speed",
                    200, 0, 2048);

    public static final NumericAttribute SCALE =
            new NumericAttribute("scale",
                    0, 0, 64);

}
