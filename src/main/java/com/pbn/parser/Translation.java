package com.pbn.parser;

import java.util.Hashtable;

public class Translation {
    static private Hashtable<String, String> words = new Hashtable<>(10);

    public Translation() {
        if (words.isEmpty()) {
            words.put("-", "None");
            words.put("Love", "None");
            words.put("Both", "All");
        }
    }

    public String get(String w) {
        return words.getOrDefault(w, "");
    }
}
