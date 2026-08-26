package net.vami.nydahar.render.sprite;

import java.util.ArrayList;

public record SpriteFrame(Sprite sprite, double duration) {

    public static Builder anim() {
        return new Builder();
    }

    public static class Builder {

        private final ArrayList<SpriteFrame> frames = new ArrayList<>();

        public Builder add(String key, double duration) {
            frames.add(new SpriteFrame(Sprites.get(key), duration));

            return this;
        }

        public SpriteFrame[] build() {
            return frames.toArray(SpriteFrame[]::new);
        }
    }
}
