package com.pbn.pbnjson;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class JsonOptimumResultTable extends JsonTable {

    private static Comparator<List<String>> rowComp;
    private static int decli = -1;
    private static int denomi = -1;
    private static HashMap<String, Integer> declw = new HashMap<>();
    private static HashMap<String, Integer> denomw = new HashMap<>();

    /***
     * sortRows orders rows such that declarer is in the order W, N, E, S and
     * then denomination S, H, D, C, NT, for example first five rows: {W S 5} ,
     * {W H, 1} , {W D 6}, {W C 5}, {W NT 1}
     */
    public static void sortRows(JsonOptimumResultTable table) {

        final int decli = table.header.indexOf("Declarer");
        final int denomi = table.header.indexOf("Denomination");
        final int resulti = table.header.indexOf("Result");

        createRowComparator(decli, denomi);
        table.rows.sort(rowComp);
    }

    private static Function<List<String>, List<String>> swap(int i, int j) {
        return t -> {
            Collections.swap(t, i, j);
            return t;
        };
    }

    /***
     * getPermutation returns permutation needed to order the columns
     *
     * @param is
     *            list of indexes of the OptimumResultTable header items
     */
    private static Function<List<String>, List<String>> getPermutation(
            LinkedList<Integer> is) {
        int i = 0;

        if (is.get(0) > 0) {
            Collections.swap(is, 0, is.get(0));
            if (is.get(0) > 0) {
                Collections.swap(is, 0, is.get(0));
                // apply permutation 0 twice
                i = 1;
            }
        } else if (is.get(1) > 1) {
            Collections.swap(is, 1, is.get(1));
            // apply permutation 1 once
            i = 2;
        }

        if (i == 0) {
            return swap(0, 1);
        } else if (i == 1) {
            return swap(0, 1).compose(swap(0, 1));
        } else {
            return swap(1, 2);
        }

    }

    /*
     * sortColumns permutes the table such that header is { Declarer,
     * Denomination, Result }
     */
    public static void sortColumns(JsonOptimumResultTable table) {
        /* precondition: table is 20 x 3 and isValid() */

        int x = table.header.indexOf("Declarer");
        int y = table.header.indexOf("Denomination");
        int z = table.header.indexOf("Result");
        LinkedList<Integer> xs = new LinkedList<>();
        Collections.addAll(xs, x, y, z);

        if (x == 0 && y == 1 && z == 2) {
            return; // is sorted if isValid() == true
        } else {
        }

    }

    private static int compareDecl(String d1, String d2) {
        if (declw.getOrDefault(d1, -10) < declw.getOrDefault(d2, -10)) {
            return -1;
        } else if (declw.getOrDefault(d1, -10) == declw.getOrDefault(d2, -10)) {
            return 0;
        } else {
            return 1;
        }
    }

    private static int compareDenom(String d1, String d2) {
        if (denomw.getOrDefault(d1, -10) < denomw.getOrDefault(d2, -10)) {
            return -1;
        } else if (denomw.getOrDefault(d1, -10) == denomw.getOrDefault(d2,
                -10)) {
            return 0;
        } else {
            return 1;
        }
    }

    private static void createRowComparator(int declj, int denomj) {

        if (declw.isEmpty() && denomw.isEmpty()) {

            String[] x = { "W", "N", "E", "S" };
            String[] y = { "S", "H", "D", "C", "NT" };

            for (int i = 0; i < x.length; i++) {
                declw.put(x[i], i);
            }

            for (int i = 0; i < y.length; i++) {
                denomw.put(y[i], i);
            }
        }
        // position of the columns has changed
        if (declj != decli && denomj != denomi) {
            decli = declj;
            denomi = denomj;

            rowComp = (l1, l2) -> {
                String decl1 = l1.get(decli);
                String denom1 = l1.get(denomi);
                String decl2 = l2.get(decli);
                String denom2 = l2.get(denomi);

                int comp1 = compareDecl(decl1, decl2);
                int comp2 = compareDenom(denom1, denom2);

                if (comp1 < 0 || comp1 == 0 && comp2 < 0) {
                    return -1;
                } else if (comp1 == 0 && comp2 == 0) {
                    return 0;
                } else {
                    return -1;
                }
            };
        }

    }

    private boolean isValid() {
        final int decli = header.indexOf("Declarer");
        final int denomi = header.indexOf("Denomination");
        final int resulti = header.indexOf("Result");
        final boolean validHeader = header.size() == 3 && decli >= 0
                && denomi >= 0 && resulti >= 0 && rows.size() == 20;

        return true;
    }

    public JsonOptimumResultTable(List<String> header,
            List<List<String>> rows) {
        super(header, rows);
    }

}
