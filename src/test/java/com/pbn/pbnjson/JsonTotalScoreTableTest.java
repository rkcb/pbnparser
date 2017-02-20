package com.pbn.pbnjson;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.pbn.tools.ToolsTest;

public class JsonTotalScoreTableTest {

    private List<JsonEvent> events;

    public static class P {
        public static void on(String s) {
            System.out.println(s);
        }

        public static void o(String s) {
            System.out.print(s);
        }
    }

    @Test
    public void testTeamTotalScoreTable() {
        events = ToolsTest.rawEvents("team");
        assertTrue(events != null);
        JsonTotalScoreTable t = events.get(0).getTotalScoreTable();
        assertTrue(t.getHeader() != null);

        // master points extraction
        t.setCompetition(events.get(0).getCompetition());
        t.findMasterPoints();
        assertTrue(events.get(0).getCompetition().equals("Teams"));
        assertTrue(t.getMasterPoints("22") == 0.525); // 2.1 / 4 = 0.525
    }

}
