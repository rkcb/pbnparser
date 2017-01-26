package com.pbn.tools;

import java.util.Comparator;
import java.util.HashMap;

public class RankOrdering {

    static HashMap<Character, Integer> ordinals;

    static String isValid(String suit) {

        if (ordinals == null) {
            ordinals = new HashMap<>();
            final String ranks = "23456789TJQKA";
            for (int i = 0; i < ranks.length(); i++) {
                ordinals.put(ranks.charAt(i), i);
            }
        }

        Comparator<Character> comp = (x, y) -> {
            return ordinals.getOrDefault(x, -100)
                    - ordinals.getOrDefault(y, -100);
        };

        return "";
    }
}
