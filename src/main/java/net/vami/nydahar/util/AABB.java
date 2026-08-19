package net.vami.nydahar.util;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class AABB {
    public double x;
    public double y;
    public double width;
    public double height;

    public AABB(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public AABB(Rectangle rect) {
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
    }

    public AABB(double width, double height) {
        this(0, 0, width, height);
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double minX() {
        return x;
    }

    public double maxX() {
        return x + width;
    }

    public double minY() {
        return y;
    }

    public double maxY() {
        return y + height;
    }

    public Rectangle2D.Double toRect() {
        return new Rectangle2D.Double(this.x, this.y, this.width, this.height);
    }

    public boolean intersects(AABB o) {
        return this.x < o.x + o.width &&
                this.x + this.width > o.x &&
                this.y < o.y + o.height &&
                this.y + this.height > o.y;
    }

    public void grow(float h, float v) {
        double x0 = this.x;
        double y0 = this.y;
        double x1 = this.width;
        double y1 = this.height;
        x1 += x0;
        y1 += y0;

        x0 -= h;
        y0 -= v;
        x1 += h;
        y1 += v;

        if (x1 < x0) {
            // Non-existent in X direction
            // Final width must remain negative so subtract x0 before
            // it is clipped so that we avoid the risk that the clipping
            // of x0 will reverse the ordering of x0 and x1.
            x1 -= x0;
            if (x1 < Float.MIN_VALUE) x1 = Float.MIN_VALUE;
            if (x0 < Float.MIN_VALUE) x0 = Float.MIN_VALUE;
            else if (x0 > Float.MAX_VALUE) x0 = Float.MAX_VALUE;
        } else { // (x1 >= x0)
            // Clip x0 before we subtract it from x1 in case the clipping
            // affects the representable area of the rectangle.
            if (x0 < Float.MIN_VALUE) x0 = Float.MIN_VALUE;
            else if (x0 > Float.MAX_VALUE) x0 = Float.MAX_VALUE;
            x1 -= x0;
            // The only way x1 can be negative now is if we clipped
            // x0 against MIN and x1 is less than MIN - in which case
            // we want to leave the width negative since the result
            // did not intersect the representable area.
            if (x1 < Float.MIN_VALUE) x1 = Float.MIN_VALUE;
            else if (x1 > Float.MAX_VALUE) x1 = Float.MAX_VALUE;
        }

        if (y1 < y0) {
            // Non-existent in Y direction
            y1 -= y0;
            if (y1 < Float.MIN_VALUE) y1 = Float.MIN_VALUE;
            if (y0 < Float.MIN_VALUE) y0 = Float.MIN_VALUE;
            else if (y0 > Float.MAX_VALUE) y0 = Float.MAX_VALUE;
        } else { // (y1 >= y0)
            if (y0 < Float.MIN_VALUE) y0 = Float.MIN_VALUE;
            else if (y0 > Float.MAX_VALUE) y0 = Float.MAX_VALUE;
            y1 -= y0;
            if (y1 < Float.MIN_VALUE) y1 = Float.MIN_VALUE;
            else if (y1 > Float.MAX_VALUE) y1 = Float.MAX_VALUE;
        }

        reshape(x0, y0, x1, y1);
    }

    public void reshape(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean inside(Point p) {
        return inside(p.x, p.y);
    }

    public boolean inside(Point2D.Float p) {
        return inside(p.x, p.y);
    }

    public boolean inside(double X, double Y) {
        double w = this.width;
        double h = this.height;
        if (w < 0 || h < 0) {
            // At least one of the dimensions is negative...
            return false;
        }
        // Note: if either dimension is zero, tests below must return false...
        double x = this.x;
        double y = this.y;
        if (X < x || Y < y) {
            return false;
        }
        w += x;
        h += y;
        //    overflow || intersect
        return ((w < x || w > X) &&
                (h < y || h > Y));
    }

    public AABB copy() {
        return new AABB(this.x, this.y, this.width, this.height);
    }

    public AABB intersection(AABB r) {
        double tx1 = this.x;
        double ty1 = this.y;
        double rx1 = r.x;
        double ry1 = r.y;
        double tx2 = tx1;
        tx2 += this.width;
        double ty2 = ty1;
        ty2 += this.height;
        double rx2 = rx1;
        rx2 += r.width;
        double ry2 = ry1;
        ry2 += r.height;
        if (tx1 < rx1) tx1 = rx1;
        if (ty1 < ry1) ty1 = ry1;
        if (tx2 > rx2) tx2 = rx2;
        if (ty2 > ry2) ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        // tx2,ty2 will never overflow (they will never be
        // larger than the smallest of the two source w,h)
        // they might underflow, though...
        if (tx2 < Float.MIN_VALUE) tx2 = Float.MIN_VALUE;
        if (ty2 < Float.MIN_VALUE) ty2 = Float.MIN_VALUE;
        return new AABB(tx1, ty1, tx2, ty2);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
    }
}