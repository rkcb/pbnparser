package com.pbn.pbnjson;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonTable {

    protected List<String> header;
    protected List<List<String>> rows;
    protected String competition;

    public JsonTable(List<String> header, List<List<String>> rows) {
        this.header = header;
        this.rows = rows;
    }

    public List<String> getHeader() {
        return header;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public int size() {
        return header.size();
    }

    /***
     * rowFilter builds an index list which contains positions of the items;
     *
     * @param subHeaderItems
     *            items of the header
     */
    protected HashSet<Integer> rowFilter(HashSet<String> subHeaderItems) {
        HashSet<Integer> indexes = new HashSet<>(subHeaderItems.size());

        Iterator<String> headeri = header.iterator();
        int i = 0;

        while (headeri.hasNext() && indexes.size() < subHeaderItems.size()) {
            String item = headeri.next();
            if (subHeaderItems.contains(item)) {
                indexes.add(i);
            }
            i++;
        }

        return indexes;
    }

    public void setCompetition(String type) {
        competition = type.matches("Individuals|Pairs|Teams") ? type : "";
    }

    /***
     * filterTable removes header and the corresponding row items which are not
     * contained in headerItems;
     */
    protected void filterTable(HashSet<String> headerItems) {
        if (header == null || rows == null || headerItems == null) {
            return;
        }

        // remove uninteresting row items
        Iterator<String> it = header.iterator();
        int i = 0; // length of prefix accepted
        while (it.hasNext()) {
            String item = it.next();
            if (!headerItems.contains(item)) {
                for (List<String> row : rows) {
                    row.remove(i);
                }
            } else {
                i++;
            }
        }

        // remove uninteresting header items
        header = header.stream().filter(c -> headerItems.contains(c))
                .collect(Collectors.toList());
    }

    /***
     * column
     *
     * @param col
     *            column header name
     * @return items of this column; empty list if nonexisting
     */
    public List<String> column(String col) {
        int i = header.indexOf(col);
        return i >= 0
                ? rows.stream().map(r -> r.get(i)).collect(Collectors.toList())
                : new LinkedList<>();

    }

}
