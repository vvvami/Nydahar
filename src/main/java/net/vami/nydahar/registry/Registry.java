package net.vami.nydahar.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Registry<T> {

    private final Map<String, T> entries = new HashMap<>();

    public <S extends T> S register(String id, S value) {
        if (entries.containsKey(id)) {
            throw new IllegalArgumentException("Entry with id " + id + " already exists");
        }

        entries.put(id, value);
        return value;
    }

    public T get(String id) {
        T value = entries.get(id);

        if (value == null) {
            throw new IllegalArgumentException("Entry with id " + id + " does not exist");
        }

        return value;
    }
    public boolean contains(String id) {
        return entries.containsKey(id);
    }

    public Collection<T> values() {
        return Collections.unmodifiableCollection(entries.values());
    }
}
