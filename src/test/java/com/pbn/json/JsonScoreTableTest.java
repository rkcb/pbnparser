package com.pbn.json;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.junit.Test;

import com.pbn.pbnjson.JsonEvent;
import com.pbn.pbnjson.JsonScoreTable;
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

        t.initialize(events.get(0).getCompetition());
        assertTrue(events.get(0).getCompetition().equals("Individuals"));
        // note that pbn file must be the indi.pbn
        List<String> l = new LinkedList<>();
        Collections.addAll(l, "11", "3N", "W", "9", "CA", "400", "0.0", "0");
        assertTrue(Objects.deepEquals(l, t.subrow("11")));
        l.clear();
        for (String i : t.scoreTableHeader()) {
            l.add(i);
        }
        // P.on(t.subrow("7").toString());
        // P.on(t.subrow("27").toString());
        // P.on(t.subrow("44").toString());
        // P.on(t.subrow("11").toString());
        List<String> l2 = new LinkedList<>();
        Collections.addAll(l2, "PlayerId_South", "Contract", "Declarer",
                "Result", "Lead", "Score_NS", "MP_South", "Percentage_South");

        assertTrue(Objects.deepEquals(l, l2));
        assertTrue(t.getRows() != null);
    }

    @Test
    public void pairMPTest() {
        events = ToolsTest.rawEvents("sm1");

        assertTrue(events != null);
        JsonScoreTable t = events.get(0).getScoreTable();
        assertTrue(t.getHeader() != null);
        t.initialize(events.get(0).getCompetition());
        assertTrue(events.get(0).getCompetition().equals("Pairs"));

        LinkedList<String> l = new LinkedList<>();
        Collections.addAll(l, "32", "6N", "S", "13", "S9", "1020", "32.0",
                "94");
        assertTrue(Objects.deepEquals(t.subrow("5"), l));
        l.clear();
        Collections.addAll(l, "5", "6N", "S", "13", "S9", "-", "2.0", "6");
        assertTrue(Objects.deepEquals(t.subrow("32"), l));
    }

    @Test
    public void pairIMPTest() {
        events = ToolsTest.rawEvents("butler");

        assertTrue(events != null);
        JsonScoreTable t = events.get(0).getScoreTable();
        assertTrue(t.getHeader() != null);
        t.initialize(events.get(0).getCompetition());
        assertTrue(events.get(0).getCompetition().equals("Pairs"));

        LinkedList<String> l = new LinkedList<>();
        // result for NS is interesting
        Collections.addAll(l, "1", "2H", "W", "7", "SA", "50", "3.0");
        assertTrue(Objects.deepEquals(t.subrow("8"), l));

        l.clear();
        // result for EW is interesting
        Collections.addAll(l, "8", "2H", "W", "7", "SA", "-", "-3.0");
        assertTrue(Objects.deepEquals(t.subrow("1"), l));
    }

    // all bam tests lack because lack of test files

    @Test
    public void teamTest() {
        events = ToolsTest.rawEvents("team");

        assertTrue(events != null);
        JsonScoreTable t = events.get(0).getScoreTable();
        assertTrue(t.getHeader() != null);
        t.initialize(events.get(0).getCompetition());
        assertTrue(events.get(0).getCompetition().equals("Teams"));

        LinkedList<String> l = new LinkedList<>();
        // result for "Home" is interesting
        Collections.addAll(l, "1", "2", "15.75");
        assertTrue(Objects.deepEquals(t.subrow("1"), l));
        l.clear();
        // result for "Away"
        Collections.addAll(l, "1", "1", "4.25");
        assertTrue(Objects.deepEquals(t.subrow("2"), l));

    }

}
