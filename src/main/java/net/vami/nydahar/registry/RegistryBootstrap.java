package net.vami.nydahar.registry;

import net.vami.nydahar.game.Game;

public class RegistryBootstrap {

    public static void register() {
        Game.getInstance().sprites().register();
    }
}
