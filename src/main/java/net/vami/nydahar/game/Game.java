package net.vami.nydahar.game;

import net.vami.nydahar.input.Input;
import net.vami.nydahar.object.entity.custom.PlayerEntity;
import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.registry.RegistryBootstrap;
import net.vami.nydahar.registry.custom.Entities;
import net.vami.nydahar.registry.custom.Tiles;
import net.vami.nydahar.render.ObjectRenderer;
import net.vami.nydahar.render.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game implements Runnable {
    private static Game instance;

    private final JFrame frame;
    private final Canvas canvas;
    private Thread thread;

    private final int TPS = 60;
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
        player = Entities.PLAYER.create(100,0);

        for (int i = 0; i < 128; i += 16) {
            Tiles.BLACK_FLOOR.create(100 + i, 400 - i, "black_floor");
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

            waitForNextEvent(nanoSleepTime);

            if (now - statsTimer >= 1_000_000_000) {
                System.out.println("TPS: " + ticks + " | FPS: " + frames);

                ticks = 0;
                frames = 0;
                statsTimer = now;
            }
        }
    }

    private static final long SPIN_THRESHOLD = 1_000_000L; // 1 ms

    private void waitForNextEvent(long waitNanos) {
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
        player.inputTick(dt, input);

        for (GameObject gameObject : GameObject.objects()) {
            gameObject.update(dt);
        }
    }

    private void render(double alpha) {
        BufferStrategy bs = canvas.getBufferStrategy();

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        // clear the scene
        g.setColor(Color.black);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // rendering
        for (GameObject gameObject : GameObject.objects()) {
            if (!gameObject.hasSprite()) continue;

            spriteRenderer.render(g, gameObject, alpha);
        }

        // dispose graphics and show next drawn buffer
        g.dispose();
        bs.show();
    }

    public SpriteManager sprites() {
        return spriteManager;
    }

    public static Game getInstance() {
        return instance;
    }

}
