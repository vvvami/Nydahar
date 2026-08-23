package net.vami.nydahar.util;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.object.interaction.collision.Collider;

import java.util.Collection;

public class Ray2 {
    public static Result cast(Vec2 origin, Vec2 dir, Vec2 a, Vec2 b) {
        Result res = new Result();

        // line 1 (wall segment): from a to b
        double x1 = a.x, y1 = a.y;
        double x2 = b.x, y2 = b.y;

        // line 2 (ray): origin to origin + dir
        double x3 = origin.x, y3 = origin.y;
        double x4 = origin.x + dir.x, y4 = origin.y + dir.y;

        double den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (Math.abs(den) < 1e-9) return res; // parallel

        double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / den;
        double u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / den;

        // t is the position along the line segment [0..1]
        // u is the forward multiplier along dir
        // it equals physical distance only if dir is normalized
        if (t >= 0 && t <= 1 && u >= 0) {
            res.hit = true;
            res.distance = u;
            res.point = new Vec2(x1 + t * (x2 - x1), y1 + t * (y2 - y1));
        }

        return res;
    }

    public static Result cast(Vec2 origin, Vec2 dir, Collider collider) {
        Result closest = new Result();

        double left = collider.getX();
        double right = collider.getX() + collider.getWidth();
        double top = collider.getY();
        double bottom = collider.getY() + collider.getHeight();

        Vec2 topLeft = new Vec2(left, top);
        Vec2 topRight = new Vec2(right, top);
        Vec2 bottomLeft = new Vec2(left, bottom);
        Vec2 bottomRight = new Vec2(right, bottom);

        checkClosest(closest, cast(origin, dir, topLeft, topRight));
        checkClosest(closest, cast(origin, dir, topRight, bottomRight));
        checkClosest(closest, cast(origin, dir, bottomRight, bottomLeft));
        checkClosest(closest, cast(origin, dir, bottomLeft, topLeft));

        return closest;
    }

    private static void checkClosest(Result closest, Result candidate) {
        if (!candidate.isHit()) return;

        if (!closest.isHit() || candidate.getDistance() < closest.getDistance()) {
            closest.hit = true;
            closest.distance = candidate.distance;
            closest.point = candidate.point;
        }
    }

    public static Result cast(Vec2 origin, Vec2 dir, double maxDistance, GameObject ignored) {
        Result closest = new Result();

        for (Collider collider : Collider.map().keySet()) {
            if (collider == ignored.getCollider()) continue;

            Result hit = cast(origin, dir, collider);

            if (!hit.isHit()) continue;
            if (hit.getDistance() > maxDistance) continue;

            if (!closest.isHit() || hit.getDistance() < closest.getDistance()) {
                closest.hit = true;
                closest.distance = hit.distance;
                closest.point = hit.point;
            }
        }

        return closest;
    }

    public static class Result {
        private boolean hit;
        private double distance;
        private Vec2 point;

        public Result() {

        }

        public boolean isHit() {
            return hit;
        }

        public double getDistance() {
            return distance;
        }

        public Vec2 getPoint() {
            return point;
        }
    }
}
