package net.vami.nydahar.render;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class SpriteManager {
    private final HashMap<String, BufferedImage> sprites = new HashMap<>();

    private static final String SPRITE_PATH = "/assets/sprites/";

    public BufferedImage getSprite(String name) {
        String path = SPRITE_PATH + name + ".png";
        if (!sprites.containsKey(path)) {
            sprites.put(path, AssetLoader.loadImage(path));
        }

        return sprites.get(path);
    }
}
