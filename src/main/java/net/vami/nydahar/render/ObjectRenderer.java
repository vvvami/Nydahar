package net.vami.nydahar.render;

import net.vami.nydahar.game.Game;
import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.object.entity.EntityObject;
import net.vami.nydahar.object.interaction.collision.Collider;
import net.vami.nydahar.render.sprite.Sprite;
import net.vami.nydahar.util.MathUtil;
import net.vami.nydahar.util.Ray2;
import net.vami.nydahar.util.Vec2;

import javax.swing.text.StyleConstants;
import java.awt.*;

public class ObjectRenderer {

    private static final BasicStroke STROKE = new BasicStroke(3);

    public void render(Graphics2D g, GameObject gameObject, double alpha) {
        Sprite sprite = gameObject.getSprite();
        if (sprite == null) return;

        Vec2 pos = gameObject.getPos();
        Vec2 prevPos = gameObject.getPrevPos();

        if (pos == null) return;

        double x = MathUtil.lerp(prevPos.x, pos.x, alpha);
        double y = MathUtil.lerp(prevPos.y, pos.y, alpha);

        double stepYOffset = MathUtil.lerp(gameObject.getPrevStepRenderOffset(), gameObject.getStepRenderOffset(), alpha);
        y += stepYOffset;

        int width = (int) Math.round(gameObject.getScaledWidth());
        int height = (int) Math.round(gameObject.getScaledHeight());

        // pos is the CENTER
        int drawX = (int) Math.round(x - width / 2.0);
        int drawY = (int) Math.round(y - height / 2.0);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        Game.drawImage(g, sprite.image(), drawX, drawY, width, height);

        Collider collider = gameObject.getCollider();
        g.setColor(Color.red);
        g.setStroke(STROKE);
        g.drawRect((int) collider.getX(), (int) collider.getY(),
                (int) collider.getWidth(), (int) collider.getHeight());


        if (!(gameObject instanceof EntityObject)) return;

        g.setColor(Color.yellow);

        if (gameObject.isGrounded()) {
            g.drawRect(drawX, drawY,
                    (int) gameObject.getScaledWidth(), (int) gameObject.getScaledHeight());
        }

        Vec2 origin = new Vec2(collider.getCenterX(), collider.getCenterY());
        Vec2 point = Ray2.cast(origin, Vec2.DOWN, 100, gameObject).getPoint();
        if (point != null) {
            g.drawLine((int) collider.getCenterX(), (int) collider.getCenterY(),
                    (int) point.x, (int) point.y);
        }
    }
}
