package com.pbn.pbnjson;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

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
    public void testJsonEvents() {
        // impcross
        events = new JsonEvents(ToolsTest.rawEvents("impcross"));
        assertTrue(events.get(1).maxIMP() > 0);
        assertTrue(events.averageMaxIMP() < events.get(1).maxIMP());
        assertTrue(events.hasMasterPoints() == false);

        for (List<String> r : events.data("524")) {
            P.on(r.toString());
        }

        events = new JsonEvents(ToolsTest.rawEvents("team"));
        assertTrue(events.size() == 1);

    }
}
