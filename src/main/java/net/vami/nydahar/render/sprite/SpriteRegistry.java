package net.vami.nydahar.render.sprite;

import java.util.HashMap;
import java.util.Map;

public final class SpriteRegistry {

    private final Map<String, Sprite> sprites = new HashMap<>();

    public void register(String name, Sprite sprite) {
        if (sprites.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Sprite already registered: " + name
            );
        }

        sprites.put(name, sprite);
    }

    public Sprite get(String name) {
        Sprite sprite = sprites.get(name);

        if (sprite == null) {
            throw new IllegalArgumentException(
                    "Unknown sprite: " + name
            );
        }

        return sprite;
    }

    public boolean contains(String name) {
        return sprites.containsKey(name);
    }
}