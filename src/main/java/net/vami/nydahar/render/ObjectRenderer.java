package net.vami.nydahar.render;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.util.MathUtil;
import net.vami.nydahar.util.Vec2;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ObjectRenderer {

    public void render(Graphics2D g, GameObject gameObject, BufferedImage sprite, double alpha) {
        if (sprite == null) return;

        Vec2 pos = gameObject.getPos();
        Vec2 prevPos = gameObject.getPrevPos();

        double x = MathUtil.lerp(prevPos.x, pos.x, alpha);
        double y = MathUtil.lerp(prevPos.y, pos.y, alpha);

        g.drawImage(sprite, (int) x, (int) y, null);
    }
}
