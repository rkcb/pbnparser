package com.pbn.pbnjson;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.junit.Test;

import com.pbn.tools.ToolsTest;

public class JsonScoreTableTest {

    private List<JsonEvent> events;

    public JsonScoreTableTest() {
    }

    public static class P {
        public static void on(String s) {
            System.out.println(s);
        }

        public static void o(String s) {
            System.out.print(s);
        }
    }

    /***
     * 1. set competion type 2. call setRowFilters 3. setMinMaxId 4. call
     * setIdIndexes 5. call setScoreTableHeader 6. call subrow
     */

    @Test
    public void indiTest() {

        events = ToolsTest.rawEvents("indi");

        assertTrue(events != null);
        JsonScoreTable t = events.get(0).getScoreTable();
        assertTrue(t.getHeader() != null);
        assertTrue(!events.get(0).getCompetition().isEmpty());
        t.initialize(events.get(0).getCompetition());
        assertTrue(events != null && !events.isEmpty());
        assertTrue(events.get(0).getCompetition().equals("Individuals"));
        // note that pbn file must be the indi.pbn
        List<String> l = new LinkedList<>();
        // Collections.addAll(l, "11", "3N", "W", "9", "CA", "400", "0.0", "0");
        // assertTrue(Objects.deepEquals(l, t.subrow("11")));
        // l.clear();
        for (String i : t.scoreTableHeader()) {
            l.add(i);
        }
        List<String> l2 = new LinkedList<>();
        Collections.addAll(l2, "PlayerId_South", "Contract", "Declarer",
                "Result", "Lead", "Score_NS", "MP_South", "Percentage_South");

        assertTrue(Objects.deepEquals(l, l2));
        assertTrue(t.getRows() != null);
    }

    @Test
    public void pairTest() {
        events = ToolsTest.rawEvents("sm1");

        assertTrue(events != null);
        JsonScoreTable t = events.get(0).getScoreTable();
        assertTrue(t.getHeader() != null);
        t.initialize(events.get(0).getCompetition());
        assertTrue(events.get(0).getCompetition().equals("Pairs"));
        // P.on(t.subrow("1").toString());
        for (String s : t.scoreTableHeader()) {
            P.o(s + ", ");
        }
        for (String s : JsonScoreTable.pairScoreItems("NS")) {
            P.o(s + ", ");
        }

        assertTrue(true);
    }

}
