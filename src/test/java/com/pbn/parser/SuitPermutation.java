package com.pbn.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

public class SuitPermutation {
    private HashMap<String, Integer> index = new HashMap<>(4);

    public SuitPermutation() {
        index.put("N", 0); // NESW
        index.put("W", 1); // WNES
        index.put("S", 2); // SWNE
        index.put("E", 3); // ESWN
    }

    public LinkedList<LinkedList<String>> permute(String dir,
            LinkedList<LinkedList<String>> hands) {

        dir = dir.toUpperCase(Locale.ROOT);
        LinkedList<LinkedList<String>> tmp = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            tmp.add(hands.get((index.get(dir) + i) % 4));
        }
        return tmp;
    }
}
