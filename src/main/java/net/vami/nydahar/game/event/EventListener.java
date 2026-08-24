package net.vami.nydahar.game.event;

import java.lang.reflect.Method;

public record EventListener(Object instance, Method method) {

}
