package com.pbn.parser;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Position;

import com.pbn.ast.Events;
import com.pbn.ast.Pbn;
import com.pbn.ast.PbnObject;

public class ParserTest {

    private TestPbnParser parser = Parboiled.createParser(TestPbnParser.class);

    private ParsingResult<Pbn> getResult(String input, Rule rule) {
        ReportingParseRunner<Pbn> runner = new ReportingParseRunner<>(rule);
        ParsingResult<Pbn> result = runner.run(input);
        return result;
    }

    private boolean matched(String input, Rule rule) {
        return getResult(input, rule).matched;
    }

    @Test
    public void CommentTest() {
        String com = "; [Tag  	\"MyValue\" \r\n";
        String com2 = "{ sd sdfsf s f \r\n \r\n }  \r\n   ";
        String comments = com + com + com2 + com;
        assertTrue(matched(com, parser.TestComments()));
        assertTrue(matched(com2, parser.TestComments()));
        assertTrue(matched(comments, parser.TestComments()));
    }

    @Test
    public void PbnValueTest() {
        String pbnvalue = "[Event \"H6 20.8.2013\"]\r\n";
        String com = "; comment\r\n";
        assertTrue(matched(pbnvalue, parser.TestValue()));
        assertTrue(matched(pbnvalue + com, parser.TestValue()));
    }

    @Test
    public void PbnSectionTest() {
        String header = "[Tag \"Col1\\1r; Col2\"]\r\n";
        String row = "135 4% \" escobar tihonox\" \r\n";
        String com = "% comment comment\r\n";
        String input = header + com + row + row + com;
        assertTrue(matched(input, parser.TestSection()));
    }

    @Test
    public void PbnEventTest() {
        String header = "[ Tag \"Col1; Col2\"] \r\n";
        String row = "135 4% \" escobar tihonox\" \r\n";
        String pbnvalue = "[ Tag \"MyValue\"]";

        String event = header + row + row + pbnvalue;
        assertTrue(matched(event, parser.TestEvent()));
    }

    @Test
    public void pbnEventsTest() {
        String header = "[ Tag \"Col\" ] \r\n";
        String row = "135 4% \" escobar tihonox\" \r\n";
        String emptyline = "\r\n";

        String event = header + row + emptyline + header + row;
        // reportFailure(header, parser.TestEvents());
        assertTrue(matched(event, parser.TestEvents()));
    }

    /***
     * pbnFileTest tests parsing a full PBN file
     */
    @Test
    public void pbnFileTest() {
        String input = inputText("test");
        assertTrue(matched(input, parser.TestEvents()));
    }

    @Test
    public void columnNamesTest() {
        String cols = "Rank\\1r ; PairId\\1R;Table\\1R;Direction\\5R;TotalScoreIMP\\5R;Names\\34L;NrBoards\\2R;Club\\15L;MemberID1\\6R;MemberID2\\6R;Sex1\\3R;Sex2\\3R";
        assertTrue(matched(cols, parser.TestColumnNames()));
    }

    @Test
    public void String() {
        String s = "\"\\\"x\\\"\"";
        LinkedList<String> l = new LinkedList<>();
        assertTrue(matched(s, parser.TestString(l)));
        // simon \"brave\" maps to simon "brave"
        assertTrue(l.get(0).equals("\"x\""));
    }

    @Test
    public void MultiLine() {
        String h = "[Event \"H.6\"]  [Site \"a\"]";
        // reportFailure(h, parser.TestEvs());
        assertTrue(matched(h, parser.TestEvents()));
    }

    @Test
    public void pbnValueObjectTest() {
        String v = "[ Tag \"Value\"]";
        ParsingResult<Pbn> result = getResult(v, parser.TestEvents());
        assertTrue(result.matched);
        assertTrue(result.valueStack.size() == 1);
    }

    @Test
    public void pbnSectionObjectTest() {
        String h = "[Tag \"Rank\\1r ; PairId\\1R;Table\\1R\"] \r\n";
        String r = "132 34% \"escobar tsehov\"\r\n";
        ParsingResult<Pbn> result = getResult(h + r, parser.TestSection());
        assertTrue(result.matched);
        assertTrue(result.valueStack.size() == 1);

        // tag
        PbnObject o = (PbnObject) result.resultValue;
        assertTrue(o.tag().equals("Tag"));

        // header
        LinkedList<String> cols = new LinkedList<>();
        Collections.addAll(cols, "Rank", "PairId", "Table");
        assertTrue(Objects.deepEquals(cols, o.header()));

        // rows
        LinkedList<String> rw = new LinkedList<>();
        Collections.addAll(rw, "132", "34%", "escobar tsehov");

        assertTrue(o.rows().size() == 1);
    }

    @Test
    public void pbnFileObjectTest() {
        String input = inputText("test");
        ParsingResult<Pbn> result = getResult(input, parser.TestEvents());
        Events evs = (Events) result.resultValue;
        if (!result.matched) {
            int i0 = result.parseErrors.get(0).getStartIndex();
            int i1 = result.parseErrors.get(0).getEndIndex();
            int line1 = result.inputBuffer.getPosition(i0).line;
            int pos1 = result.inputBuffer.getPosition(i0).column;

            int line2 = result.inputBuffer.getPosition(i1).line;
            int pos2 = result.inputBuffer.getPosition(i1).column;

            if (line1 != line2) {
                o("Error starts at line " + line1 + " and column " + pos1
                        + " and ends at line " + line2 + " and column " + pos2
                        + ": >>>" + result.inputBuffer.charAt(i0) + "<<<");
            } else {
                o("Error starts at line " + line1 + " and column " + pos1
                        + " and ends by the column " + pos2);
            }
        }
        assertTrue(result.matched);
        assertTrue(evs.size() == 66);
    }

    @Test
    public void TestDeal() {
        String d = "W:KQT2.AT.J6542.85 - A8654.KQ5..QJT6 -";
        ParsingResult<Pbn> result = getResult(d, parser.TestDeal());
        PbnObject o = (PbnObject) result.resultValue;
        // reportFailure(d, parser.TestDeal());
        assertTrue(result.matched);
        assertTrue(o.hands().size() == 4);

        LinkedList<LinkedList<String>> suits = new LinkedList<>();
        LinkedList<String> w = new LinkedList<>();
        LinkedList<String> e = new LinkedList<>();
        LinkedList<String> n = new LinkedList<>();
        LinkedList<String> s = new LinkedList<>();

        Collections.addAll(w, "KQT2", "AT", "J6542", "85");
        Collections.addAll(e, "A8654", "KQ5", "", "QJT6");

        Collections.addAll(suits, w, n, e, s);
        assertTrue(Objects.deepEquals(n, o.hands().get(0)));
        assertTrue(Objects.deepEquals(e, o.hands().get(1)));
        assertTrue(Objects.deepEquals(s, o.hands().get(2)));
        assertTrue(Objects.deepEquals(w, o.hands().get(3)));
    }

    @Test
    public void TestPredefinedValues() {
        String deal = "[Deal \"W:KQT2.AT.J6542.85 - A8654.KQ5..QJT6 -\"]";
        String vul = "[Vulnerable \"None\"]";
        String dealer = "[Dealer \"W\"]";
        String date = "[Date \"2016.01.01\"]";

        // reportFailure(d, parser.TestDeal());
        ParsingResult<Pbn> result = getResult(deal,
                parser.TestPredefinedValues());
        assertTrue(result.matched);

        result = getResult(deal, parser.TestPredefinedValues());
        assertTrue(result.matched);
        result = getResult(vul, parser.TestPredefinedValues());
        assertTrue(result.matched);
        result = getResult(dealer, parser.TestPredefinedValues());
        assertTrue(result.matched);
        result = getResult(date, parser.TestPredefinedValues());
        assertTrue(result.matched);

    }

    @Test
    public void TestPredefinedTable() {
        String header = "[ScoreTable \"Col1; Col2\"]";
        String row = "x1 x2\r\n";
        String row2 = "x3 x4";
        String input = header + row + row2;
        ParsingResult<Pbn> result = getResult(input,
                parser.TestPredefinedTable());
        reportFailure(input, parser.TestPredefinedTable());
        assertTrue(result.matched);

        LinkedList<String> r1 = new LinkedList<>();
        LinkedList<String> r2 = new LinkedList<>();

        Collections.addAll(r1, "x1", "x2");
        Collections.addAll(r2, "x3", "x4");

        PbnObject o = (PbnObject) result.resultValue;

        assertTrue(Objects.deepEquals(o.rows().get(0), r1));
        assertTrue(Objects.deepEquals(o.rows().get(1), r2));

    }

    @Test
    public void x() {

    }

    public static void o(String s) {
        if (s == null) {
            return;
        } else {
            System.out.println(s);
        }
    }

    /***
     * reportFailure reports the error context and position of the error
     */

    @SuppressWarnings("unused")
    private void reportFailure(String input, Rule rule) {

        ReportingParseRunner<Object> runner = new ReportingParseRunner<>(rule);
        ParsingResult<Object> result = runner.run(input);

        if (!result.matched) {
            o("report buffer where error occurred:\n");
            ParseError error = result.parseErrors.get(0);

            String str = "";

            int i0 = error.getStartIndex();
            int i1 = error.getEndIndex();

            Position pos = error.getInputBuffer().getPosition(i0);

            int lines = Integer.min(3, error.getInputBuffer().getLineCount());

            String errContext = "";

            for (int i = 1; i <= lines; i++) {
                errContext += error.getInputBuffer().extractLine(i) + "\n";
            }

            o("error line(s): \n\n" + errContext + "\n");
            o("error >>>" + "" + error.getInputBuffer().charAt(i0) + "<<<"
                    + " at line: " + pos.line + " and column: " + pos.column);
        }
    }

    public static String inputText(String fileName) {
        Charset charset = Charset.forName("ISO-8859-1");
        if (fileName == null) {
            return null;
        }

        String uri = "/home/esa/Documents/pbn/" + fileName + ".pbn";

        String line = null;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(uri),
                charset)) {

            StringBuilder builder = new StringBuilder("");

            while ((line = reader.readLine()) != null) {

                builder.append(line).append("\r\n");
            }

            line = builder.toString();

        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        return line;
    }

}
