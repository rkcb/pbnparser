package com.pbn.pbnjson;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.parboiled.support.ParsingResult;

import com.pbn.ast.Events;
import com.pbn.ast.Pbn;
import com.pbn.parser.ParserTest;
import com.pbn.tools.Tools;
import com.pbn.tools.ToolsTest;

public class JsonEventsTest {

    private JsonEvents events;

    public static class P {
        public static void on(String s) {
            System.out.println(s);
        }

        public static void o(String s) {
            System.out.print(s);
        }
    }

    @Test
    public void testEvents() {
        Events pbnEvents = ToolsTest.pbnEvents("impcross");
        assertTrue(pbnEvents.get(0).get("Event").value().length() > 0);
    }

    @Test
    public void testJsonEvents() {
        // impcross
        events = new JsonEvents(ToolsTest.rawEvents("impcross"));
        // board 2
        assertTrue(events.get(1).maxIMP() == 508);
        assertTrue(events.averageMaxIMP() < events.get(1).maxIMP());
        assertTrue(events.hasMasterPoints() == false);

        assertTrue(events.competion().equals("Pairs"));
        // tests that initialization data is successfully copied from the 1st
        // event to other events

        // test comparison data row size with the header size
        assertTrue(!events.get(0).getEvent().isEmpty());

        events = new JsonEvents(ToolsTest.rawEvents("team"));

        assertTrue(events.size() == 1);
    }

    @Test
    public void testNumbercolumns() {
        events = new JsonEvents(ToolsTest.rawEvents("impcross"));
        JsonTotalScoreTable tst = events.get(0).getTotalScoreTable();
        assertTrue(tst.competition.equals("Pairs"));
        assertTrue(tst != null);

        // total score table
        HashSet<String> numberColumns0 = new HashSet<>();
        Collections.addAll(numberColumns0, "Rank", "TotalScoreMP",
                "TotalPercentage", "TotalScore", "TotalIMP", "TotalMP",
                "NrBoards", "MeanScore", "MeanIMP", "MeanMP");
        // verify that number items exists as expected
        Iterator<Object> it0 = tst.getRows().get(0).iterator();
        for (String s : tst.getHeader()) {
            Object o = it0.next();
            if (numberColumns0.contains(s)) {
                assertTrue(o instanceof Double);
            }
        }

        // score table
        JsonScoreTable st = events.get(1).getScoreTable();
        HashSet<String> numberColumns = new HashSet<>(20);
        Collections.addAll(numberColumns, "Rank", "Result", "Score_NS",
                "Score_EW", "IMP_NS", "IMP_EW", "MP_NS", "MP_EW",
                "Percentage_NS", "Percentage_EW", "Percentage_North",
                "Percentage_East", "Percentage_South", "Percentage_West",
                "Multiplicity");

        // verify that number items exists as expected
        Iterator<Object> it = st.getRows().get(0).iterator();
        for (String s : st.getHeader()) {
            Object o = it.next();
            if (numberColumns.contains(s)) {
                assertTrue(o instanceof Double);
            }
        }
    }

    @Test
    public void masterPoints() {
        // case for teams
        events = new JsonEvents(ToolsTest.rawEvents("team"));
        assertTrue(events != null && events.hasMasterPoints()
                && events.totalScoreTableExists());
        double mps = events.masterPointsEarned("1438");
        // kauko got 0.525 points while the team got 2.1 points
        assertTrue(mps == 0.525);
    }

    @Test
    public void columnTypes() {
        events = new JsonEvents(ToolsTest.rawEvents("sm1"));
        assertTrue(events.get(0).getScoreTable().numberColumns() != null);
        assertTrue(events.get(0).getScoreTable().htmlColumns() != null);
        assertTrue(events != null);
    }

    @Test
    public void JsonEventsGeneral() {
        String input = ParserTest.inputText("team");
        ParsingResult<Pbn> result = Tools.getPbnResult(input);
        assertTrue(result.matched);
        JsonEvents jevents = new JsonEvents(
                Tools.fromJson(Tools.toJson(result)));
        assertTrue(jevents != null);
        assertTrue(jevents.totalScoreTable() != null);
        assertTrue(jevents.totalScoreTableExists());
    }

    @Test
    public void dealsTest() {
        // no deals
        events = new JsonEvents(ToolsTest.rawEvents("butler"));
        assertFalse(events.dealsExists());
        // deals exists
        events = new JsonEvents(ToolsTest.rawEvents("sm1"));
        assertTrue(events.dealsExists());
    }

    @Test
    public void scoringTest() {
        events = new JsonEvents(ToolsTest.rawEvents("sm1"));
        assertTrue(events.mpScoring());
        events = new JsonEvents(ToolsTest.rawEvents("butler"));
        assertTrue(events.impScoring());

        events = new JsonEvents(ToolsTest.rawEvents("team"));
        assertTrue(events.vpScoring());
    }

    @Test
    public void scoreTest() {
        events = new JsonEvents(ToolsTest.rawEvents("team"));
        P.on(events.get(0).getScoreTable().header.toString());
        for (List<Object> l : events.scoreData("1")) {
            P.on(l.toString());
        }
        assertTrue(events.scoreData("1").size() == 6);
    }

    @Test
    public void idToNameTest() {

    }

}
