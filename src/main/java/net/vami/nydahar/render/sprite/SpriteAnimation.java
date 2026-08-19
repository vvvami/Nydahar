package net.vami.nydahar.render.sprite;

import net.vami.nydahar.game.Game;

public record SpriteAnimation(double frameDuration, AnimationMode mode, AnimationFrame ... frames) {

    public Sprite getFrame(int index) {
        return frames[index].sprite();
    }

    public int getFrameCount() {
        return frames.length;
    }
}
