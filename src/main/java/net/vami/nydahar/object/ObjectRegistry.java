package net.vami.nydahar.object;

import net.vami.nydahar.object.custom.Enemy;
import net.vami.nydahar.object.custom.Player;

public class ObjectRegistry {
    private static Player player;

    public static void register() {
        player = new Player(0,0);
        Enemy enemy = new Enemy(0,0);
    }

    public static Player getPlayer() {
        return player;
    }
}
