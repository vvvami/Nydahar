package net.vami.nydahar.game.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventBus {
    
    // the event bus is basically storing a map
    // the map is an EventClass, and a list of EventListeners (which includes a Method)
    // then we run post(), we're just looping thru all those methods and running em
    
    private static final Map<Class<? extends Event>, List<EventListener>> listeners = new HashMap<>();

    public static void register(Object object) {

        // we getting all the methods in the class
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Event.class)) continue; // if it doesnt have @Event, kill it

            Class<?>[] parameters = method.getParameterTypes(); // getting the method parameters

            // if it has more than just the event as a parameter...
            if (parameters.length != 1) {
                throw new IllegalArgumentException("@Event needs one dxmn parameter only: " + method);
            }

            Class<?> parameter = parameters[0]; // we check if the only parameter is also only an Event
            if (!Event.class.isAssignableFrom(parameter)) {
                throw new IllegalArgumentException("@Event requires an Event.class or inheritor: " + method);
            }

            // get the event type based on the parameter
            Class<? extends Event> eventType = (Class<? extends Event>) parameter;

            // access opening so even if the method is private, we can touch it
            method.setAccessible(true);

            listeners.computeIfAbsent(eventType,
                            key -> new ArrayList<>())
                    .add(new EventListener(object, method));
        }
    }

    public static void post(Event event) {
        // get the list of the methods
        List<EventListener> eventListeners = listeners.get(event.getClass());

        if (eventListeners == null) return; // if there aren't any, kill it

        // loop thru the methods and call em
        for (EventListener listener : eventListeners) {
            try {
                listener.method().invoke(listener.instance(), event);
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to invoke event listener", e);
            }
        }
    }
}
