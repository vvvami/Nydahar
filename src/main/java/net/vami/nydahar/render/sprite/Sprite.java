package net.vami.nydahar.render.sprite;

import net.vami.nydahar.game.Game;

import java.awt.image.BufferedImage;

public record Sprite(String id, BufferedImage image) {

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }
}
