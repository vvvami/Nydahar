package net.vami.nydahar.render.sprite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class SpriteManager {

    private final Map<String, Sprite> sprites = new HashMap<>();
    private final Map<String, SpriteAnimation> animations = new HashMap<>();

    public void register() {
        try {
            URL assetsUrl = getClass()
                    .getClassLoader()
                    .getResource("assets");

            if (assetsUrl == null) throw new IllegalStateException("Could not find assets folder");


            switch (assetsUrl.getProtocol()) {
                case "file" -> loadFromDirectory(assetsUrl);
                case "jar" -> loadFromJar(assetsUrl);

                default -> throw new IllegalStateException("Unsupported asset protocol: " + assetsUrl.getProtocol());
            }

            registerAnimations();

            System.out.println("Loaded " + sprites.size() + " sprites");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load sprites", e);
        }
    }

    private void register(String key, BufferedImage image) {
        if (sprites.containsKey(key)) {
            throw new IllegalArgumentException("Sprite already registered: " + key);
        }

        Sprite sprite = new Sprite(key, image);

        sprites.put(key, sprite);
    }

    public Sprite get(String key) {
        Sprite sprite = sprites.get(key);

        if (sprite == null) throw new IllegalArgumentException("Unknown sprite: " + key);

        return sprite;
    }

    public boolean hasSprite(String key) {
        return sprites.containsKey(key);
    }

    private void loadFromDirectory(URL assetsUrl) throws Exception {
        Path assetsPath = Paths.get(assetsUrl.toURI());

        try (Stream<Path> paths = Files.walk(assetsPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(path ->
                            path.getFileName()
                                    .toString()
                                    .toLowerCase()
                                    .endsWith(".png"))
                    .forEach(path -> {
                        try {
                            String key = assetsPath
                                    .relativize(path)
                                    .toString()
                                    .replace('\\', '/')
                                    .replaceFirst(
                                            "(?i)\\.png$",
                                            ""
                                    );

                            BufferedImage image =
                                    ImageIO.read(path.toFile());

                            register(key, image);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    private void loadFromJar(URL assetsUrl) throws Exception {
        JarURLConnection connection = (JarURLConnection) assetsUrl.openConnection();

        try (JarFile jar = connection.getJarFile()) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                String name = entry.getName();

                if (entry.isDirectory()) continue;
                if (!name.startsWith("assets/")) continue;
                if (!name.toLowerCase().endsWith(".png")) continue;

                String key = name
                        .substring("assets/".length())
                        .replaceFirst("(?i)\\.png$", "");

                try (InputStream stream =
                             getClass()
                                     .getClassLoader()
                                     .getResourceAsStream(name)) {

                    if (stream == null) throw new IOException("Could not open sprite: " + name);

                    BufferedImage image = ImageIO.read(stream);
                    register(key, image);
                }
            }
        }
    }

    private void registerAnimations() {

        Map<String, TreeMap<Integer, Sprite>> animationFrames = new HashMap<>();

        for (Map.Entry<String, Sprite> entry : sprites.entrySet()) {

            String key = entry.getKey();

            int slash = key.lastIndexOf('/');

            if (slash == -1) continue;

            String directory = key.substring(0, slash);
            String fileName = key.substring(slash + 1);

            int frameIndex;

            try {
                frameIndex = Integer.parseInt(fileName);
            } catch (NumberFormatException ignored) {
                continue;
            }

            TreeMap<Integer, Sprite> frames = animationFrames.computeIfAbsent(directory, k -> new TreeMap<>());

            if (frames.put(frameIndex, entry.getValue()) != null) {
                throw new IllegalStateException("Duplicate animation frame " + frameIndex + " for animation " + directory);
            }
        }

        for (Map.Entry<String, TreeMap<Integer, Sprite>> entry : animationFrames.entrySet()) {

            String animationKey = entry.getKey();
            TreeMap<Integer, Sprite> frameSprites = entry.getValue();

            if (frameSprites.size() < 2) continue;

            for (int i = 1; i <= frameSprites.size(); i++) {
                if (!frameSprites.containsKey(i)) {
                    throw new IllegalStateException("Missing frame " + i + " in animation " + animationKey);
                }
            }

            double frameDuration = 0.1;

            SpriteFrame[] frames = frameSprites.values()
                    .stream()
                    .map(sprite ->
                            new SpriteFrame(sprite, frameDuration))
                    .toArray(SpriteFrame[]::new);

            SpriteAnimation animation = new SpriteAnimation(frameDuration, AnimationMode.LOOP, frames);

            animations.put(animationKey, animation);
        }
    }

    public Map<String, SpriteAnimation> getAnimations() {
        return animations;
    }
}
