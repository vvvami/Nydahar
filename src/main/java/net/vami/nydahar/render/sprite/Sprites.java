package net.vami.nydahar.render.sprite;

import net.vami.nydahar.game.Game;
import net.vami.nydahar.render.SpriteManager;

public class Sprites {
    public static Sprite get(String key) {
        return manager().get(key);
    }

    public static boolean has(String key) {
        return manager().hasSprite(key);
    }

    private static SpriteManager manager() {
        return Game.getInstance().sprites();
    }
}
