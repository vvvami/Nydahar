package net.vami.nydahar.render;

import net.vami.nydahar.render.sprite.Sprite;

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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class SpriteManager {

    private final Map<String, Sprite> sprites = new HashMap<>();

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
}
