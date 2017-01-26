package com.pbn.pbnjson;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class JsonTotalScoreTable extends JsonTable {

    private HashSet<String> numberColumns;

    public JsonTotalScoreTable(List<String> header, List<List<String>> rows) {
        super(header, rows);
        if (numberColumns == null) {
            numberColumns = new HashSet<>();

            Collections.addAll(numberColumns, "Rank", "TotalScoreMP",
                    "TotalPercentage", "TotalScore", "TotalIMP", "TotalMP",
                    "NrBoards", "MeanScore", "MeanIMP", "MeanMP");
        }
    }

}
