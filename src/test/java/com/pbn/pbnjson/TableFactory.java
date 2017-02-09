package com.pbn.pbnjson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

/* TableFactory gives the header and contents for TotalScoreTable, ScoreTable and ComparisonTable
 */
public class TableFactory {

    private List<JsonEvent> events;
    private JsonTotalScoreTable totalScoreTable;

    public TableFactory() {
    }

    public static TableFactory getFactory(List<JsonEvent> events) {
        TableFactory factory = new TableFactory();
        factory.events = events != null ? events : new ArrayList<>();
        if (!events.isEmpty() && events.get(0).getTotalScoreTable() != null) {
            factory.totalScoreTable = events.get(0).getTotalScoreTable();
        }
        return factory;
    }

    /**
     * totalScoreTableHeader assumes that every JsonTotalScoreTable has called
     * filter() before calling this method
     */
    public String[] totalScoreTableHeader() {
        JsonTotalScoreTable table = events.get(0).getTotalScoreTable();
        return table != null ? table.header.toArray(new String[0])
                : new String[0];
    }

    /**
     * totalScoreTableHeader assumes that every JsonTotalScoreTable has called
     * filter() before calling this method
     */
    public String[][] totalScoreData() {
        if (totalScoreTable != null) {
            // map rows to arrays and the outer list to an array
            return totalScoreTable.rows.stream()
                    .map(r -> r.toArray(new String[0]))
                    .collect(Collectors.toList()).toArray(new String[0][0]);
        }
        return null;
    }

    @Test
    public void testing() {
    }
}
