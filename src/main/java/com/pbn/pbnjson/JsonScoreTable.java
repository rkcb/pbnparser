package com.pbn.pbnjson;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class JsonScoreTable extends JsonTable {

    private static HashSet<String> numberColumns;

    public JsonScoreTable(List<String> header, List<List<String>> rows) {
        super(header, rows);
        if (numberColumns == null) {
            numberColumns = new HashSet<>();

            Collections.addAll(numberColumns, "Rank", "Result", "Score_NS",
                    "Score_EW", "IMP_NS", "IMP_EW", "MP_NS", "MP_EW",
                    "Percentage_NS", "Percentage_EW", "Percentage_North",
                    "Percentage_East", "Percentage_South", "Percentage_West",
                    "Multiplicity");
        }
    }

}
