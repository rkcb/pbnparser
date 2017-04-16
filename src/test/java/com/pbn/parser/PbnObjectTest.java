package com.pbn.parser;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

import org.junit.Test;

import com.pbn.ast.PbnObject;

public class PbnObjectTest {

    @Test
    public void PbnObjectValueTest() {
        PbnObject o = PbnObject.pbnValue("Tag", "Value");
        assertTrue(o.tag().equals("Tag"));
        assertTrue(o.value().equals("Value"));
    }

    @Test
    public void PbnObjectHeaderTest() {
        PbnObject o = PbnObject.pbnHeader("Tag");
        assertTrue(o.tag().equals("Tag"));
        assertTrue(o.header() != null);
        assertTrue(o.rows() != null);
    }

    @Test
    public void PbnObjectRowTest() {
        PbnObject o = PbnObject.pbnHeader("Tag");
        LinkedList<Object> row = new LinkedList<>();
        Collections.addAll(row, "a", "b");
        PbnObject.addRow(o, row);
        assertTrue(o.tag().equals("Tag"));
        assertTrue(Objects.deepEquals(row, o.rows().get(0)));
    }

    @Test
    public void PbnObjectAddRowTest() {
        PbnObject o = PbnObject.pbnHeader("Tag");
        LinkedList<Object> row = new LinkedList<>();
        row.add("test");
        o = PbnObject.addRow(o, row);
        assertTrue(Objects.deepEquals(row, o.rows().get(0)));
    }

    @Test
    public void PbnAddItemTest() {
        PbnObject o = PbnObject.pbnHeader("Tag");
        PbnObject.addRow(o, new LinkedList<>());
        PbnObject.addItem(o, "a");
        PbnObject.addItem(o, "b");
        assertTrue(o.rows().size() == 1);

        LinkedList<String> row = new LinkedList<>();
        Collections.addAll(row, "a", "b");
        assertTrue(Objects.deepEquals(row, o.rows().get(0)));
    }

}
