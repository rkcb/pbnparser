package com.pbn.parser;

import java.util.Comparator;
import java.util.Hashtable;

public class RankOrder implements Comparator<Character> {

    private Hashtable<Character, Integer> rank = new Hashtable<>();

    public RankOrder() {
        rank.put('2', 2);
        rank.put('3', 3);
        rank.put('4', 4);
        rank.put('5', 5);
        rank.put('6', 6);
        rank.put('7', 7);
        rank.put('8', 8);
        rank.put('9', 9);
        rank.put('T', 10);
        rank.put('J', 11);
        rank.put('Q', 12);
        rank.put('K', 13);
        rank.put('A', 14);
    }

    @Override
    public int compare(Character r1, Character r2) {
        if (rank.get(r1) == rank.get(r2)) {
            return 0;
        } else {
            return rank.get(r1) < rank.get(r2) ? -1 : 1;
        }

    }

}
