package net.vami.nydahar.util;

public class MathUtil {
    public static float dist(double x1, double y1, double x2, double y2) {
        return (float) Math.sqrt(sqr(x2 - x1) + sqr(y2 - y1));
    }

    public static float dist(double num1, double num2) {
        return (float) Math.abs(num1 - num2);
    }

    public static double sqr(double num) {
        return num * num;
    }

    public static float lerp(float start, float end, float alpha) {
        return start + (end - start) * alpha;
    }

    public static double lerp(double start, double end, double alpha) {
        return start + (end - start) * alpha;
    }
}
