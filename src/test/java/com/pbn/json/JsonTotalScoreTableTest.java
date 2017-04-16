package com.pbn.json;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.support.ParsingResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pbn.ast.Pbn;
import com.pbn.parser.ParserTest;
import com.pbn.parser.PbnParser;
import com.pbn.pbnjson.JsonEvents;
import com.pbn.pbnjson.JsonTotalScoreTable;
import com.pbn.tools.ToolsTest;

public class JsonTotalScoreTableTest {

    static private PbnParser parser = Parboiled.createParser(PbnParser.class);
    static private Gson gson = new GsonBuilder().create();

    public static class P {
        public static void on(String s) {
            System.out.println(s);
        }

        public static void o(String s) {
            System.out.print(s);
        }
    }

    @Test
    public void test() {
        String input = ParserTest.inputText("sm1");
        // parse pbn string
        ParsingResult<Pbn> result = ToolsTest.getPbnResult(input);
        assertTrue(result.matched);

        // convert parsed pbn events to json string
        String json = ToolsTest.toJson(result);
        assertTrue(json != null && !json.isEmpty());

        // deserialize json string to List<JsonEvent>
        JsonEvents jevents = new JsonEvents(ToolsTest.rawEvents("sm1"));
        JsonTotalScoreTable t = jevents.totalScoreTable();

    }

}
