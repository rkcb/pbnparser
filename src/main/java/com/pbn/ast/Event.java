package com.pbn.ast;

import java.util.HashMap;
import java.util.Objects;

/***
 * Event contains all PBN sections, tables and values: PbnObject represents all
 * of these
 */
public class Event extends Pbn {

    private HashMap<String, PbnObject> pbns = new HashMap<>(25);

    public Event() {
    }

    /***
     * add stores the object
     */
    public boolean add(PbnObject o) {
        Objects.requireNonNull(o);
        pbns.put(o.tag(), o);
        return true;
    }

    /***
     * get returns a pbn object associated with the tag if possible and
     * otwerwise returns null
     */
    public PbnObject get(String tag) {
        return pbns.getOrDefault(tag, null);
    }

    /***
     * deal returns pbn value of the deal; an empty string if not defined
     */
    public String board() {
        PbnObject o = pbns.getOrDefault("Board", null);
        return o != null ? o.value() : "";
    }
}
