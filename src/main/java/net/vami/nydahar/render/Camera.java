package net.vami.nydahar.render;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.util.MathUtil;
import net.vami.nydahar.util.Vec2;

public class Camera {

    private final Vec2 pos = new Vec2(0, 0);
    private final Vec2 prevPos = new Vec2(0, 0);

    private static final double CAM_BOUNDS_WIDTH = 350;
    private static final double CAM_BOUNDS_HEIGHT = 150;

    private static final double CAM_FOLLOW_SPEED = 10;

    public void update(GameObject target, double dt) {
        prevPos.set(pos);

        double targetX = target.getPos().x;
        double targetY = target.getPos().y + target.getStepRenderOffset();

        double halfWidth = CAM_BOUNDS_WIDTH / 2;
        double halfHeight = CAM_BOUNDS_HEIGHT / 2;

        double desiredX = pos.x;
        double desiredY = pos.y;

        if (targetX > pos.x + halfWidth) desiredX = targetX - halfWidth;
        else if (targetX < pos.x - halfWidth) desiredX = targetX + halfWidth;

        if (targetY > pos.y + halfHeight) desiredY = targetY - halfHeight;
        else if (targetY < pos.y - halfHeight) desiredY = targetY + halfHeight;


        double t = 1 - Math.exp(-CAM_FOLLOW_SPEED * dt);

        pos.x = MathUtil.lerp(pos.x, desiredX, t);
        pos.y = MathUtil.lerp(pos.y, desiredY, t);
    }

    public Vec2 getPos() {
        return pos;
    }

    public Vec2 getPrevPos() {
        return prevPos;
    }

    public void setPos(double x, double y) {
        pos.x = x;
        pos.y = y;
    }

    public void setPos(Vec2 v) {
        pos.x = v.x;
        pos.y = v.y;
    }
}