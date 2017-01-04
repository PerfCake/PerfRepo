package org.perfrepo.web.adapter.dummy_impl.storage;

import java.util.*;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestToAlertStorage {

    // first is test id, second is user id
    private Set<Map.Entry<Long, Long>> table = new HashSet<>();

    public void remove(Long testId, Long userId) {
        table.remove(new AbstractMap.SimpleEntry<>(testId, userId));
    }

    public void add(Long testId, Long userId) {
        table.add(new AbstractMap.SimpleEntry<>(testId, userId));
    }

    public boolean contains(Long testId, Long userId) {
        return table.contains(new AbstractMap.SimpleEntry<>(testId, userId));
    }
}
