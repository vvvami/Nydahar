package net.vami.nydahar.game;

import net.vami.nydahar.input.Input;
import net.vami.nydahar.object.entity.custom.PlayerEntity;
import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.object.interaction.damage.Hitbox;
import net.vami.nydahar.registry.RegistryBootstrap;
import net.vami.nydahar.registry.custom.Entities;
import net.vami.nydahar.registry.custom.Tiles;
import net.vami.nydahar.render.Camera;
import net.vami.nydahar.render.ObjectRenderer;
import net.vami.nydahar.render.SpriteManager;
import net.vami.nydahar.util.MathUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;

public class Game implements Runnable {
    private static Game instance;

    private final JFrame frame;
    private final Canvas canvas;
    private Thread thread;

    private Camera camera = new Camera();

    public static final int TPS = 60;
    private final int FPS = 60;

    private boolean running = false;

    // RENDERING
    private final ObjectRenderer spriteRenderer = new ObjectRenderer();
    private final SpriteManager spriteManager = new SpriteManager();

    // PLAYER
    private PlayerEntity player;

    // INPUT
    private final Input input = new Input();

    public Game() {
        instance = this;
        frame = new JFrame();

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(800, 600));
        canvas.addKeyListener(input);
        canvas.addMouseListener(input);
        canvas.addMouseMotionListener(input);
        canvas.setFocusable(true);
        canvas.requestFocus();

        frame.add(canvas);
        frame.pack();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void run() {
        RegistryBootstrap.register(); // register everything

        player = Entities.PLAYER.create(100,500);
        Entities.WOLF.create(400, 500);
        for (int i = 0; i < 128; i += 16) {
            Tiles.BLACK_FLOOR.create(500 + i, 200 + i, "black_floor");
        }

        for (int i = 0; i < 1024; i += 16) {
            Tiles.BLACK_FLOOR.create(100 + i, 272, "black_floor");
        }

        // create buffers for rendering
        canvas.createBufferStrategy(3);

        long statsTimer = System.nanoTime();
        int ticks = 0;
        int frames = 0;

        // simulation speed == TPS
        final double tickStep = (double) 1 / TPS;
        // render speed == FPS
        final double frameStep = (double) 1 / FPS;

        double tickAccumulator = 0;
        double frameAccumulator = 0;
        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            long elapsedNanos = now - lastTime;

            lastTime = now;

            double elapsed = (double) elapsedNanos / 1_000_000_000;
            tickAccumulator += elapsed;
            frameAccumulator += elapsed;

            while (tickAccumulator >= tickStep) {
                update(tickStep);
                ticks++;

                tickAccumulator -= tickStep;
            }

            if (frameAccumulator >= frameStep) {
                double alpha = tickAccumulator / tickStep;

                render(alpha);
                frames++;

                frameAccumulator %= frameStep;
            }

            double untilNextTick = tickStep - tickAccumulator;
            double untilNextFrame = frameStep - frameAccumulator;

            double sleepTime = Math.min(untilNextTick, untilNextFrame);
            long nanoSleepTime = (long) (sleepTime * 1_000_000_000L);

            sleep(nanoSleepTime);

            if (now - statsTimer >= 1_000_000_000) {
                System.out.println("TPS: " + ticks + " | FPS: " + frames);

                ticks = 0;
                frames = 0;
                statsTimer = now;
            }
        }
    }

    private static final long SPIN_THRESHOLD = 1_000_000L; // 1 ms

    private void sleep(long waitNanos) {
        if (waitNanos <= 0) return;

        long deadline = System.nanoTime() + waitNanos;

        while (true) {
            long remaining = deadline - System.nanoTime();

            if (remaining <= 0) return;

            if (remaining > SPIN_THRESHOLD) {

                long sleepNanos = remaining - SPIN_THRESHOLD;

                long millis = sleepNanos / 1_000_000L;
                int nanos = (int) (sleepNanos % 1_000_000L);

                try {
                    Thread.sleep(millis, nanos);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

            } else {
                Thread.onSpinWait();
            }
        }
    }

    public void start() {
        running = true;

        thread = new Thread(this);
        thread.start();
    }

    private void update(double dt) {

        camera.update(player, dt);

        player.inputTick(dt, input);

        for (GameObject gameObject : GameObject.objects()) {
            gameObject.update(dt);
        }

        for (Hitbox hitbox : Hitbox.map().keySet()) {
            hitbox.update(dt);
        }
        Hitbox.map().keySet().removeIf(Hitbox::isFinished);
    }

    private void render(double alpha) {
        BufferStrategy bs = canvas.getBufferStrategy();
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        Graphics2D worldG = (Graphics2D) g.create();

        double camX = MathUtil.lerp(camera.getPrevPos().x, camera.getPos().x, alpha);
        double camY = MathUtil.lerp(camera.getPrevPos().y, camera.getPos().y, alpha);

        worldG.translate(canvas.getWidth() / 2d, canvas.getHeight() / 2d);

        worldG.scale(1, -1);

        worldG.translate(-camX, -camY);

        ArrayList<GameObject> objects = new ArrayList<>(GameObject.objects());
        objects.sort(Comparator.comparing(GameObject::getRenderLayer));

        for (GameObject object : objects) {
            if (!object.hasSprite()) continue;

            spriteRenderer.render(worldG, object, alpha);
        }

        worldG.dispose();

        // draw hud down HERE using g:

        g.dispose();
        bs.show();
    }

    public static void drawImage(Graphics2D g, BufferedImage image, double x, double y, double width, double height) {
        Graphics2D graphics = (Graphics2D) g.create();

        graphics.translate(x, y + height);
        graphics.scale(1, -1);

        graphics.drawImage(image, 0, 0, (int) width, (int) height, null);

        graphics.dispose();
    }

    public static void drawString(Graphics2D g, String text, double x, double y) {
        Graphics2D graphics = (Graphics2D) g.create();

        graphics.translate(x, y);
        graphics.scale(1, -1);

        graphics.drawString(text, 0, 0);

        graphics.dispose();
    }

    public Camera getCamera() {
        return camera;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public SpriteManager sprites() {
        return spriteManager;
    }

    public static Game getInstance() {
        return instance;
    }

}
