package net.vami.nydahar.render.sprite;

import net.vami.nydahar.game.Game;

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

    public static SpriteAnimation getAnimation(String key) {
        SpriteAnimation animation = manager().getAnimations().get(key);

        if (animation == null) throw new IllegalArgumentException("Unknown animation: " + key);

        return animation;
    }

    public static boolean hasAnimation(String key) {
        return manager().getAnimations().containsKey(key);
    }

}
