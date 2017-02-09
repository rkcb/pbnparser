package com.pbn.pbnjson;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class JsonTotalScoreTable extends JsonTable {

    private static transient HashSet<String> numberColumns;
    private static transient HashSet<String> headerItems;

    public JsonTotalScoreTable(List<String> header, List<List<String>> rows) {
        super(header, rows);
        if (numberColumns == null) {
            numberColumns = new HashSet<>(20);

            Collections.addAll(numberColumns, "Rank", "TotalScoreMP",
                    "TotalPercentage", "TotalScore", "TotalIMP", "TotalMP",
                    "NrBoards", "MeanScore", "MeanIMP", "MeanMP");
        }
        if (headerItems == null) {
            headerItems = new HashSet<>(20);
            Collections.addAll(headerItems, "Rank", "PlayerId", "PairId",
                    "TeamId", "TotalScoreMP", "TotalScoreVP", "TotalScoreIMP",
                    "TotalScoreBAM", "TotalPercentage", "Name", "Names",
                    "TeamName", "Roster", "ScorePenalty", "Club", "MP");
        }
    }

    /***
     * filter removes the uninteresting (see headerItems) header items and the
     * corresponding row items
     */
    public void filter() {
        filterTable(headerItems);
    }

}
