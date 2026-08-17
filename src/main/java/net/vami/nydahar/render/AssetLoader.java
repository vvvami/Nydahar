package net.vami.nydahar.render;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class AssetLoader {

    public static BufferedImage loadImage(String path) {
        try (InputStream stream = AssetLoader.class.getResourceAsStream(path)) {

            if (stream == null) {
                throw new RuntimeException("Could not find image: " + path);
            }

            return ImageIO.read(stream);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + path, e);
        }
    }
}
