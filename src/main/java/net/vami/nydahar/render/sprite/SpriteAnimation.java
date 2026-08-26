package net.vami.nydahar.render.sprite;

public record SpriteAnimation(double frameDuration, AnimationMode mode, SpriteFrame... frames) {

    public Sprite getFrame(int index) {
        return frames[index].sprite();
    }

    public int getFrameCount() {
        return frames.length;
    }
}
