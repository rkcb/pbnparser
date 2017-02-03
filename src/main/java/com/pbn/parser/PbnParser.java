package com.pbn.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;

import com.pbn.ast.Event;
import com.pbn.ast.Events;
import com.pbn.ast.Pbn;
import com.pbn.ast.PbnObject;

public class PbnParser extends BaseParser<Pbn> {

    protected final Translation tr = new Translation();
    protected final Rule LQT = LeftQuote();
    protected final Rule RQT = RightQuote();
    protected final Rule LBR = LeftBracket();
    protected final Rule RBR = RightBracket();
    protected final Rule SC = Semicolon();
    protected final HashMap<String, Rule> predefined = new HashMap<>(20);
    protected final Rule DIRECTION = Dealer();
    protected final Rule PREVALS = PredefinedValue();
    protected final Rule PRETABLES = PredefinedTable();

    /***
     * PRETABLES only verifies that table has correct header and data format; it
     * does not verify that data makes any sense
     */

    // ----- reserved (predefined) pbn tags --------------

    protected boolean DateExists(String s) {
        DateFormat f = new SimpleDateFormat("yyyy.MM.dd");
        f.setLenient(false);
        boolean isValid = true;
        try {
            f.parse(s);
        } catch (ParseException e) {
            isValid = false;
        }
        return isValid;
    }

    protected Rule Date() {
        return Sequence(Sequence(NTimes(4, Digit()), '.', NTimes(2, Digit()),
                '.', NTimes(2, Digit())), DateExists(match()));
    }

    protected Rule Dealer() {
        return AnyOf("NESW");
    }

    protected Rule Board() {
        // zero is excluded and zero prefixed numbers
        return Sequence(CharRange('1', '9'), ZeroOrMore(Digits()));
    }

    protected Rule Rank() {
        return FirstOf(CharRange('2', '9'), AnyOf("TJQKA"), AnyOf("tjqka"));
    }

    /***
     * isSuit checks for rank duplicates
     */
    protected boolean isSuit(String s) {

        if (s.isEmpty()) {
            return true;
        }

        if (s.length() > 13) {
            return false;
        }

        s.toUpperCase(Locale.ROOT);
        return s.chars().distinct().count() == s.length();
    }

    protected Rule Suit(Var<LinkedList<String>> hand) {
        return Sequence(FirstOf(OneOrMore(Rank()), ""), isSuit(match()),
                hand.get().add(match().toUpperCase(Locale.ROOT)));
    }

    protected Rule Hand(Var<LinkedList<LinkedList<String>>> hands) {
        Var<LinkedList<String>> hand = new Var<>(new LinkedList<>());
        return Sequence(FirstOf('-', NTimes(4, Suit(hand), '.')),
                hands.get().add(hand.get()));
    }

    protected boolean isDealValid(LinkedList<LinkedList<String>> hands) {
        if (hands.size() == 4) {

            return true;
        } else {
            return false;
        }

    }

    protected Rule Deal() {
        Var<LinkedList<LinkedList<String>>> hands = new Var<>(
                new LinkedList<>());
        StringVar dir = new StringVar();
        return Sequence(DIRECTION, dir.set(match()), ':',
                NTimes(4, Hand(hands), LineSpace()), isDealValid(hands.get()),
                push(PbnObject.pbnDeal(dir.get(), hands.get())));
    }

    protected Rule VulVals() {
        return FirstOf("None", "NS", "EW", "All");
    }

    protected Rule VulExtraVals() {
        return FirstOf("Love", "-", "Both");
    }

    protected Rule Vulnerable() {
        StringVar vul = new StringVar();
        return FirstOf(Sequence(VulVals(), vul.set(match())),
                Sequence(VulExtraVals(), vul.set(tr.get(match()))));
    }

    // ------------------------------------------

    protected Rule LeftBracket() {
        return Sequence('[', LineSpace());
    }

    protected Rule RightBracket() {
        return Sequence(']', LineSpace());
    }

    protected Rule LeftQuote() {
        return Sequence(LineSpace(), '"');
    }

    protected Rule RightQuote() {
        return Sequence('"', LineSpace());
    }

    protected Rule Semicolon() {
        return Sequence(LineSpace(), ';', LineSpace());
    }

    protected Rule LineEnd() {
        return FirstOf(Sequence("\r\n", LineSpace()), LineSpace(), EOI);
    }

    protected Rule LineComment() {
        return OneOrMore(Sequence(';', ZeroOrMore(NoneOf("\r\n")), LineEnd()));
    }

    protected Rule Escapes() {
        return OneOrMore(Sequence('%', ZeroOrMore(NoneOf("\r\n")), LineEnd()));
    }

    protected Rule MultiComment() {
        return Sequence("{", ZeroOrMore(NoneOf("}")), "}", LineSpace(),
                LineEnd());
    }

    protected Rule Comment() {
        return OneOrMore(FirstOf(LineComment(), MultiComment()));
    }

    protected Rule WhiteSpace() {
        return ZeroOrMore(AnyOf(" \t\f\r\n"));
    }

    protected Rule LineSpace() {
        return ZeroOrMore(AnyOf(" \t\f"));
    }

    protected Rule Letter() {
        return FirstOf(CharRange('A', 'Z'), CharRange('a', 'z'));
    }

    protected Rule CapitalLetter() {
        return CharRange('A', 'Z');
    }

    protected Rule Digit() {
        return CharRange('0', '9');
    }

    protected Rule Digits() {
        return OneOrMore(Digit());
    }

    protected Rule NameToken() {
        return Sequence(CapitalLetter(),
                ZeroOrMore(FirstOf(Letter(), Digits(), '_')));
    }

    protected Rule Predefined(String tag) {
        return NOTHING;
    }

    /***
     * 1 Alignment e.g. 4R describes column width and alignment
     */
    protected Rule Alignment() {
        return Sequence(Digits(), AnyOf("rlRL"));
    }

    protected Rule ColumnSpec() {
        return Sequence('\\', Alignment());
    }

    protected Rule Order() {
        return FirstOf('+', '-');
    }

    protected Rule NewLine() {
        return Sequence("\r\n", LineSpace());
    }

    protected Rule SpaceOrComment() {
        return OneOrMore(FirstOf(NewLine(), Comment()));
    }

    protected Rule EmptyLine() {
        return FirstOf(Sequence("\r\n", LineSpace()), EOI);
    }

    protected Rule NotRow() {
        return FirstOf('[', EOI, ';', '{');
    }

    protected Rule ValueEnd() {
        return Sequence(RQT, RBR, LineEnd(), Optional(Comment()),
                Test(NotRow()));
    }

    protected Rule HeaderEnd() {
        return Sequence(RQT, RBR, LineEnd(), Optional(Comment()));
    }

    protected Rule Value() {
        // the top item in the stack must be a pbn tag
        StringVar tag = new StringVar();
        StringVar val = new StringVar();
        return Sequence(Str(val), tag.set(((PbnObject) pop()).tag()),
                push(PbnObject.pbnValue(tag.get(), match())), ValueEnd());
    }

    protected Rule ColumnName(Var<LinkedList<String>> cs) {
        return Sequence(Optional(Order()), NameToken(), cs.get().add(match()),
                Optional(ColumnSpec()));
    }

    protected Rule ColumnNames() {
        Var<LinkedList<String>> cols = new Var<>(new LinkedList<>());
        // the top item in the stack must be a pbn tag
        return Sequence(ColumnName(cols), ZeroOrMore(SC, ColumnName(cols)),
                Optional(SC),
                push(PbnObject.pbnHeader((PbnObject) pop(), cols.get())));
    }

    protected Rule NonString() {
        return NoneOf("{}[]\"\r\n\t\f ");
    }

    protected Rule RowNonString(Var<LinkedList<String>> rw) {
        return Sequence(OneOrMore(NonString()), rw.get().add(match()));
    }

    protected Rule Str(StringVar s) {
        return ZeroOrMore(FirstOf(Sequence(NoneOf("\\\""), s.append(match())),
                Sequence("\\\"", s.append('"')),
                Sequence("\\", s.append('\\'))));
    }

    protected Rule String(Var<LinkedList<String>> rw) {
        StringVar s = new StringVar("");
        return Sequence(Str(s), rw.get().add(s.get()));
    }

    protected Rule RowString(Var<LinkedList<String>> rw) {
        return Sequence('"', String(rw), '"');
    }

    protected Rule Row(Var<LinkedList<String>> rw) {
        return Sequence(
                OneOrMore(FirstOf(RowNonString(rw), RowString(rw)),
                        LineSpace()),
                LineEnd(), push(PbnObject.addRow((PbnObject) pop(), rw.get())));
    }

    protected Rule TableRow(Var<LinkedList<String>> rw, int size) {
        // the top stack item must contain a nonnull table header
        return Sequence(
                NTimes(size, FirstOf(RowNonString(rw), RowString(rw)),
                        LineSpace()),
                LineEnd(), push(PbnObject.addRow((PbnObject) pop(), rw.get())));
    }

    // protected Rule TableRow(Var<LinkedList<String>> rw) {
    // // the top stack item must contain a nonnull table header
    // return Sequence(
    // OneOrMore(FirstOf(RowNonString(rw), RowString(rw)),
    // LineSpace()),
    // LineEnd(), push(PbnObject.addRow((PbnObject) pop(), rw.get())));
    // }

    protected int headerSize(Pbn pbn) {
        return ((PbnObject) pbn).header().size();
    }

    protected Rule TableRow() {
        Var<LinkedList<String>> rw = new Var<>(new LinkedList<>());
        Var<Integer> size = new Var<>();
        // the top stack item must contain a nonnull header
        return Sequence(size.set(((PbnObject) peek()).header().size()),
                OneOrMore(FirstOf(RowNonString(rw), RowString(rw)),
                        LineSpace()),
                LineEnd(), size.get() == rw.get().size(),
                push(PbnObject.addRow((PbnObject) pop(), rw.get())));
    }

    protected Rule Rows() {
        Var<LinkedList<String>> rw = new Var<>(new LinkedList<>());
        // the top item in the stack must contain the header object
        return OneOrMore(Sequence(
                OneOrMore(FirstOf(RowNonString(rw), RowString(rw)),
                        LineSpace()),
                LineEnd(),
                push(PbnObject.addRow((PbnObject) pop(), rw.get()))));
    }

    protected Rule Section() {
        StringVar tag = new StringVar();
        // replace the tag object with a header object
        return Sequence(tag.set(((PbnObject) pop()).tag()),
                push(PbnObject.pbnHeader(tag.get())), ColumnNames(),
                HeaderEnd(), Rows());
    }

    protected Rule TableRows() {
        // the top item in the stack must contain the header object
        return OneOrMore(TableRow());
    }

    protected Rule TableName() {
        return FirstOf("ScoreTable", "TotalScoreTable", "OptimumResultTable");
    }

    protected int headerSize() {
        return ((PbnObject) peek()).header().size();
    }

    protected Rule PredefinedTable() {
        // replace the tag object with a header object
        return Sequence(LBR, TableName(), push(PbnObject.pbnHeader(match())),
                LQT, ColumnNames(), HeaderEnd(), TableRows());
    }

    protected Rule PbnObject() {
        // push a tag object
        return Sequence(LBR, NameToken(), push(PbnObject.pbnTag(match())), LQT,
                FirstOf(Value(), Section()));
    }

    protected Rule PbnVulnerable() {
        return Sequence("Vulnerable", LQT, Vulnerable(),
                push(PbnObject.pbnValue("Vulnerable", match())));
    }

    protected Rule PbnDealer() {
        return Sequence("Dealer", LQT, Dealer(),
                push(PbnObject.pbnValue("Dealer", match())));
    }

    protected Rule PbnDate() {
        return Sequence("Date", LQT, Date(),
                push(PbnObject.pbnValue("Date", match())));
    }

    protected Rule PbnBoard() {
        return Sequence("Board", LQT, Board(),
                push(PbnObject.pbnValue("Board", match())));
    }

    protected Rule PbnDeal() {
        return Sequence("Deal", LQT, Deal());
    }

    protected Rule PredefinedValue() {
        return Sequence(LBR, FirstOf(PbnDeal(), PbnVulnerable(), PbnDealer(),
                PbnDate(), PbnBoard()), ValueEnd());
    }

    protected Rule Event() {
        Var<Event> ev = new Var<>(new Event());
        return Sequence(
                OneOrMore(FirstOf(PREVALS, PRETABLES, PbnObject()),
                        ev.get().add((PbnObject) pop())),
                push(ev.get()), EmptyLine());
    }

    public Rule Events() {
        Var<Events> evs = new Var<>(new Events());
        return Sequence(Optional(Escapes()),
                OneOrMore(Event(), evs.get().add((Event) pop())),
                push(evs.get()), EOI);
    }

}
