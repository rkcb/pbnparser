package com.pbn.ast;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.pbn.parser.SuitPermutation;

/***
 * PbnObject represents a PBN section, table (special case of section) or value
 */
public class PbnObject extends Pbn {

    private String tag;
    private String value;
    private List<String> header;
    private List<LinkedList<Object>> rows;
    private LinkedList<LinkedList<String>> hands; // NESW; SHDC
    public final int s = 2;

    private static final SuitPermutation p = new SuitPermutation();

    private PbnObject(String tag, String value) {
        this(tag);
        this.value = value;
    }

    private PbnObject(String tag, List<String> header) {
        this(tag);
        this.header = header;
        rows = new LinkedList<>();
    }

    private PbnObject(String tag) {
        value = null;
        header = null;
        rows = null;
        this.tag = tag;
    }

    /***
     * adds a pbn value
     */
    public static PbnObject pbnValue(String tag, String value) {
        return new PbnObject(tag, value);
    }

    /***
     * creates a pbn tag
     */
    public static PbnObject pbnTag(String tag) {
        return new PbnObject(tag);
    }

    public static PbnObject pbnDeal(String dir,
            LinkedList<LinkedList<String>> hands) {
        if (dir != null) { // nonempty deal
            PbnObject o = PbnObject.pbnTag("Deal");
            dir = dir.toUpperCase(Locale.ROOT);
            o.hands = p.permute(dir, hands);
            return o;
        } else { // empty deal
            PbnObject o = PbnObject.pbnTag("Deal");
            o.hands = new LinkedList<>();
            return o;
        }
    }

    /***
     * builds a pbn header
     */
    public static PbnObject pbnHeader(String tag, List<String> header) {
        return new PbnObject(tag, header);
    }

    /***
     * builds a pbn header with an empty header
     */
    public static PbnObject pbnHeader(String tag) {
        return new PbnObject(tag, new LinkedList<>());
    }

    /***
     * builds a pbn header with an empty header
     */
    public static PbnObject pbnHeader(PbnObject tagObj, List<String> header) {
        return pbnHeader(tagObj.tag, header);
    }

    /***
     * addRow adds an empty item row i.e. pbn section data row
     *
     * @param old
     *            contains the old rows
     */
    public static PbnObject addRow(PbnObject old, LinkedList<Object> row) {
        old.rows.add(row);
        return old;
    }

    /**
     * addItem appends the row item to the last row
     *
     * @throws Exception
     */
    public static PbnObject addItem(PbnObject o, String item) {
        o.rows.get(o.rows.size() - 1).add(item);
        return o;
    }

    /**
     * addColumn appends the column name to the header
     */
    public static PbnObject addColumn(PbnObject o, String column) {
        o.header.add(column);
        return o;
    }

    public LinkedList<LinkedList<String>> hands() {
        return hands;
    }

    /***
     * tag pbn tag
     */
    public String tag() {
        return tag;
    }

    /***
     * value
     *
     * @return the value of the tag
     */
    public String value() {
        return value;
    }

    /***
     * header
     *
     * @return pbn table header
     */
    public List<String> header() {
        return Collections.unmodifiableList(header);
    }

    /***
     * rows
     *
     * @return list of data rows of the table
     */
    public List<List<Object>> rows() {
        return Collections.unmodifiableList(rows);
    }

    @Override
    public String toString() {
        return tag == null ? "" : tag;
    }

}
