package com.pbn.tools;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.pbn.ast.Events;
import com.pbn.ast.Pbn;
import com.pbn.parser.ParserTest;
import com.pbn.parser.PbnParser;
import com.pbn.pbnjson.JsonEvent;
import com.pbn.pbnjson.JsonTotalScoreTable;

public class ToolsTest {

    public static class P {
        public static void on(String s) {
            System.out.println(s);
        }

        public static void o(String s) {
            System.out.print(s);
        }
    }

    static private PbnParser parser = Parboiled.createParser(PbnParser.class);
    static private Gson gson = new GsonBuilder().create();

    /***
     * getResult parses the PBN string
     *
     * @param pbn
     *            PBN file as a string
     *
     * @return ParsingResult<PBN> which contains possible errors
     */
    public static ParsingResult<Pbn> getPbnResult(String pbn) {

        ReportingParseRunner<Pbn> runner = new ReportingParseRunner<>(
                parser.Events());
        ParsingResult<Pbn> result = runner.run(pbn);

        return result;
    }

    /***
     * getJson serializes List<JsonEvent> of the parsing result to Json string
     * using Gson
     */
    public static String toJson(ParsingResult<Pbn> result) {
        Events events = (Events) result.resultValue;
        List<JsonEvent> jevents = events.events().stream()
                .map(e -> Tools.event2JsonEvent(e))
                .collect(Collectors.toList());

        return gson.toJson(jevents);
    }

    /***
     * fromJson
     *
     * @param json
     *            string serialized from LinkedList<JsonEvent>
     * @return deserialization
     */
    public static LinkedList<JsonEvent> fromJson(String json) {

        JsonParser jsonParser = new JsonParser();
        JsonArray jarray = jsonParser.parse(json).getAsJsonArray();
        LinkedList<JsonEvent> jsonEvents = new LinkedList<>();
        // this deserialization is a recommended way by Gson documentation
        for (JsonElement e : jarray) {
            jsonEvents.add(gson.fromJson(e, JsonEvent.class));
        }

        return jsonEvents;
    }

    /***
     * pbnStringToJson conterts pbn to json string by first checking the
     * validity of pbn and then converting the pbn to json
     *
     * @return json se
     */
    public static String pbnToJson(String pbn) {
        if (pbn == null || pbn.trim().isEmpty()) {
            return "";
        }

        ParsingResult<Pbn> results = getPbnResult(pbn);
        return results.matched ? toJson(results) : "";
    }

    @Test
    public void testing() {
        String input = ParserTest.inputText("sm1");
        // parse pbn string
        ParsingResult<Pbn> result = getPbnResult(input);
        assertTrue(result.matched);

        // convert parsed pbn events to json string
        String json = toJson(result);
        assertTrue(json != null && !json.isEmpty());

        // deserialize json string to List<JsonEvent>
        List<JsonEvent> jevents = fromJson(json);

        // test the deserialization
        assertTrue(jevents != null);
        assertTrue(jevents.size() == 66);
        JsonTotalScoreTable t = jevents.get(0).getTotalScoreTable();
        assertTrue(t != null);
        assertTrue(!t.getHeader().isEmpty());
        assertTrue(!t.getRows().isEmpty());
    }

    /***
     * rawEvents computes raw JsonEvent list i.e. it does not filter event
     * tables yet
     */
    public static List<JsonEvent> rawEvents(String fileName) {
        String pbn = ParserTest.inputText(fileName);
        return fromJson(toJson(getPbnResult(pbn)));
    }

    public static Events pbnEvents(String fileName) {
        String pbn = ParserTest.inputText(fileName);
        return (Events) getPbnResult(pbn).resultValue;
    }

}
