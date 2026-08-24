package net.vami.nydahar.input;

import java.awt.event.*;

public class Input implements KeyListener, MouseListener, MouseMotionListener {
    // ------ KEYBOARD -------
    public boolean up;
    public boolean down;
    public boolean left;
    public boolean right;

    public boolean space;

    // ------ MOUSE ------

    public volatile boolean left_click_down;
    public volatile boolean left_click_pressed;

    public volatile boolean right_click_down;
    public volatile boolean right_click_pressed;

    public volatile int mouseX;
    public volatile int mouseY;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> up = true;
            case KeyEvent.VK_S -> down = true;
            case KeyEvent.VK_A -> left = true;
            case KeyEvent.VK_D -> right = true;
            case KeyEvent.VK_SPACE -> space = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> up = false;
            case KeyEvent.VK_S -> down = false;
            case KeyEvent.VK_A -> left = false;
            case KeyEvent.VK_D -> right = false;
            case KeyEvent.VK_SPACE -> space = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}



    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1 ->  {
                left_click_down = true;
                left_click_pressed = true;
            }
            case MouseEvent.BUTTON3 -> {
                right_click_pressed = true;
                right_click_down = true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1 -> left_click_down = false;
            case MouseEvent.BUTTON3 -> right_click_down = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

}
