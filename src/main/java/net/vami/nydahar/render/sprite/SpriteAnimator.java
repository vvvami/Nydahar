package net.vami.nydahar.render.sprite;

public class SpriteAnimator {

    private SpriteAnimation animation;

    private int frame;
    private double timer;

    private boolean playing;
    private boolean finished;

    private double speed = 1;

    public void play(SpriteAnimation animation, float speed) {
        if (this.animation == animation && playing) return;

        this.animation = animation;
        this.frame = 0;
        this.timer = 0;
        this.playing = true;
        this.finished = false;
        this.speed = speed;
    }

    public void playFromStart(SpriteAnimation animation, float speed) {
        this.animation = animation;
        this.frame = 0;
        this.timer = 0;
        this.playing = true;
        this.finished = false;
    }

    public void restart() {
        if (animation == null) return;

        frame = 0;
        timer = 0;
        playing = true;
        finished = false;
    }

    public void stop() {
        playing = false;
    }

    public void update(double dt) {
        if (animation == null || !playing || finished) return;

        timer += dt * getSpeed();

        while (timer >= animation.frameDuration()) {
            timer -= animation.frameDuration();
            nextFrame();
        }
    }

    private void nextFrame() {
        frame++;

        if (frame < animation.getFrameCount()) {
            return;
        }

        switch (animation.mode()) {
            case LOOP -> frame = 0;

            case ONCE, HOLD -> {
                frame = animation.getFrameCount() - 1;
                playing = false;
                finished = true;
            }

        }
    }

    public void setFrame(int frame) {
        this.frame = Math.clamp(frame, 0, animation.getFrameCount() - 1);
        timer = 0;
    }

    public Sprite getSprite() {
        if (animation == null) return null;

        return animation.getFrame(frame);
    }

    public boolean isFinished() {
        return finished;
    }

    public int getFrame() {
        return frame;
    }

    public SpriteAnimation getAnimation() {
        return animation;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return this.speed;
    }

}
