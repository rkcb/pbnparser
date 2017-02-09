package com.pbn.json;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.pbn.pbnjson.JsonEvent;
import com.pbn.pbnjson.JsonScoreTable;
import com.pbn.tools.ToolsTest;

public class JsonScoreTableTest {

    public static class P {
        public static void on(String s) {
            System.out.println(s);
        }

        public static void o(String s) {
            System.out.print(s);
        }
    }

    public JsonScoreTableTest() {
    }

    @Test
    public void IndiScoreTableTest() {
        List<JsonEvent> events = ToolsTest.rawEvents("indi");
        JsonEvent e = events.get(0);

        JsonScoreTable t = e.getScoreTable();
        assertTrue(t != null);
        assertTrue(e.getCompetition().equals("Individuals"));
        JsonScoreTable.setCompetition(e.getCompetition());
        t.setScoreTableHeader();
        assertTrue(JsonScoreTable.scoreTableHeader() != null);

        P.on(t.getHeader().toString());

    }
}
