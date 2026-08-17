package net.vami.nydahar.game;

import net.vami.nydahar.input.Input;
import net.vami.nydahar.object.custom.Enemy;
import net.vami.nydahar.object.custom.Player;
import net.vami.nydahar.object.GameObject;
import net.vami.nydahar.render.ObjectRenderer;
import net.vami.nydahar.render.SpriteManager;
import net.vami.nydahar.render.SpriteRenderable;
import net.vami.nydahar.util.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Game implements Runnable {
    private final JFrame frame;
    private final Canvas canvas;
    private Thread thread;

    private final int TPS = 60;

    private boolean running = false;

    // PLAYER
    private Player player = new Player(0,0);

    // INPUT
    private final Input input = new Input();

    // RENDERING
    private final ObjectRenderer spriteRenderer = new ObjectRenderer();
    private final SpriteManager spriteManager = new SpriteManager();

    public Game() {
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

        // create buffers for rendering
        canvas.createBufferStrategy(3);

        // milli for simulation speed
        final double step = (double) 1 / TPS;

        double accumulator = 0;
        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();

            long elapsedNanos = now - lastTime;
            lastTime = now;

            double elapsed = (double) elapsedNanos / 1_000_000_000;
            accumulator += elapsed;

            while (accumulator >= step) {
                update(step);
                accumulator -= step;
            }

            double alpha = accumulator / step;

            render(alpha);
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
            if (!(gameObject instanceof SpriteRenderable renderable)) continue;

            BufferedImage sprite = spriteManager.getSprite(renderable.getSprite());
            spriteRenderer.render(g, gameObject, sprite, alpha);
        }

        // dispose graphics and show drawn buffer
        g.dispose();
        bs.show();
    }

}
