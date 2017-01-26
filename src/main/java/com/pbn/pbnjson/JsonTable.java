package com.pbn.pbnjson;

import java.util.List;

public class JsonTable {

    protected List<String> header;
    protected List<List<String>> rows;

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
}
