package net.vami.nydahar.render.sprite;

import java.util.ArrayList;

public record AnimationFrame(Sprite sprite, double duration) {

    public static Builder animation() {
        return new Builder();
    }

    public static class Builder {

        private final ArrayList<AnimationFrame> frames = new ArrayList<>();

        public Builder add(String key, double duration) {
            frames.add(new AnimationFrame(Sprites.get(key), duration));

            return this;
        }

        public AnimationFrame[] build() {
            return frames.toArray(AnimationFrame[]::new);
        }
    }
}
