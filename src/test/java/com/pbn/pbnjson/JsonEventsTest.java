package com.pbn.pbnjson;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.pbn.ast.Events;
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
        assertTrue(events.get(1).maxIMP() > 0);
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
}
