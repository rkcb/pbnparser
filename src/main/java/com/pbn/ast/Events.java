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

    public LinkedList<Event> events() {
        LinkedList<Event> es = new LinkedList<>();
        for (int i = 0; i < events.size(); i++) {
            es.add(get(i));
        }
        return es;
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
