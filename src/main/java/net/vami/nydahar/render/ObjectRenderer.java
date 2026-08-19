package net.vami.nydahar.render;

import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.render.sprite.Sprite;
import net.vami.nydahar.util.MathUtil;
import net.vami.nydahar.util.Vec2;

import java.awt.*;

public class ObjectRenderer {

    public void render(Graphics2D g, GameObject gameObject, double alpha) {
        Sprite sprite = gameObject.getSprite();
        if (sprite == null) return;

        Vec2 pos = gameObject.getPos();
        Vec2 prevPos = gameObject.getPrevPos();

        double x = MathUtil.lerp(prevPos.x, pos.x, alpha);
        double y = MathUtil.lerp(prevPos.y, pos.y, alpha);

        y += gameObject.getStepRenderOffset(); // smooth autostepping

        int width = (int) Math.round(gameObject.getScaledWidth());
        int height = (int) Math.round(gameObject.getScaledHeight());

        // pos is the CENTER
        int drawX = (int) Math.round(x - width / 2.0);
        int drawY = (int) Math.round(y - height / 2.0);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        g.drawImage(sprite.image(), drawX, drawY, width, height, null);
    }
}
