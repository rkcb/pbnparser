package com.pbn.tools;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.pbn.ast.Event;
import com.pbn.ast.Events;
import com.pbn.ast.Pbn;
import com.pbn.parser.ParserTest;
import com.pbn.parser.PbnParser;
import com.pbn.pbnjson.JsonEvent;

public class ToolsTest {

    public static class P {
        public static void on(String s) {
            System.out.println(s);
        }

        public static void o(String s) {
            System.out.print(s);
        }
    }

    private PbnParser parser = Parboiled.createParser(PbnParser.class);

    private ParsingResult<Pbn> getResult(String input, Rule rule) {
        ReportingParseRunner<Pbn> runner = new ReportingParseRunner<>(rule);
        ParsingResult<Pbn> result = runner.run(input);
        return result;
    }

    @Test
    public void ToJsonObjectsTest() {
        String input = ParserTest.inputText("sm1");
        ParsingResult<Pbn> result = getResult(input, parser.Events());
        Events es = (Events) result.resultValue;

        List<Event> evs = es.events();

        Gson gson = new GsonBuilder().create();

        Function<Event, JsonEvent> f = e -> Tools.event2JsonEvent(e);

        List<JsonEvent> l = evs.stream().map(f).collect(Collectors.toList());
        JsonEvent jevent = l.get(1);
        String json = gson.toJson(l);

        // this is a recommended Gson way to deserialize
        JsonParser parser = new JsonParser();
        JsonArray jarray = parser.parse(json).getAsJsonArray();
        JsonEvent jevent2 = gson.fromJson(jarray.get(1), JsonEvent.class);

        if (jevent.getScoreTable() != null) {
            assertTrue(Objects.deepEquals(jevent.getScoreTable().getHeader(),
                    jevent2.getScoreTable().getHeader()));
        }
        assertTrue(Objects.deepEquals(jevent.getBoard(), jevent2.getBoard()));

    }
}
