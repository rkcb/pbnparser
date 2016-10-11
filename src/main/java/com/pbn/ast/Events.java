package com.pbn.ast;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class Events extends Pbn {

    private LinkedList<String> keys = new LinkedList<>();
    private HashMap<String, Event> events = new HashMap<>();

    public boolean add(Event e) {
        Objects.requireNonNull(e);
        keys.add(e.board());
        events.put(e.board(), e);
        return true;
    }

    /***
     * get the event in the parsing order (file top down)
     */
    public Event get(int i) {
        return events.getOrDefault(keys.get(i), null);
    }

    public int size() {
        return events.size();
    }
}
