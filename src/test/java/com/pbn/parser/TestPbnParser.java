package com.pbn.parser;

import java.util.LinkedList;

import org.parboiled.Rule;
import org.parboiled.support.Var;

import com.pbn.ast.Pbn;
import com.pbn.ast.PbnObject;

/***
 * TestPbnParser exists only to test PbnParser rules individually
 */

public class TestPbnParser extends PbnParser {

    protected PbnObject addItem(Pbn obj, String item) {
        return PbnObject.addItem((PbnObject) obj, item);
    }

    protected Rule TestComments() {
        return Sequence(Comment(), EOI);
    }

    protected Rule TestValue() {
        return Sequence(PbnObject(), EOI);
    }

    protected Rule TestSection() {
        return Sequence(PbnObject(), EOI);
    }

    protected Rule TestColumnNames() {
        return Sequence(push(PbnObject.pbnHeader("Tag")), ColumnNames(), EOI);
    }

    protected Rule TestEvent() {
        return Sequence(Event(), EOI);
    }

    protected Rule TestEvents() {
        return Sequence(Events(), EOI);
    }

    protected Rule TestString(LinkedList<String> l) {
        Var<LinkedList<String>> rw = new Var<>(new LinkedList<>());
        return Sequence('"', String(rw), l.add(rw.get().get(0)), '"', EOI);
    }

    protected Rule TestDate() {
        return Sequence(Date(), EOI);
    }

    protected Rule TestPredefinedValues() {
        return Sequence(PREVALS, EOI);
    }

    protected Rule TestDeal() {
        return Sequence(push(PbnObject.pbnTag("Tag")), Deal(), EOI);
    }

    protected Rule TestTableRows() {
        return Sequence(push(PbnObject.pbnHeader("ScoreTable")), TableRows(),
                EOI);
    }

    public Rule TestPredefinedTable() {
        return Sequence(PredefinedTable(), EOI);
    }

}