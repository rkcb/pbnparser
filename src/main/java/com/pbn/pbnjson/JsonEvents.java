package com.pbn.pbnjson;

import java.util.Iterator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class JsonEvents {

    private List<JsonEvent> events;
    private JsonTotalScoreTable totalScoreTable;
    private String competition;

    public JsonEvents(List<JsonEvent> events) {
        this.events = events;
        competition = "";
        JsonEvent from = events.get(0);
        from.initialize();
        for (JsonEvent e : events) {
            JsonEvent.initialize(e, from);
        }
    }

    /***
     * totalScoreTable
     *
     * @return totalScoreTable found in the events
     */
    public JsonTotalScoreTable totalScoreTable() {
        if (totalScoreTable == null && events != null) {
            Iterator<JsonEvent> i = events.iterator();
            // find first event with totalScoreTable if possible
            while (i.hasNext() && totalScoreTable == null) {
                totalScoreTable = i.next().getTotalScoreTable();
            }
        }
        return totalScoreTable;
    }

    /***
     * hasMasterPoints
     *
     * @return true if events contain master points and false otherwise
     */
    public boolean hasMasterPoints() {
        if (events != null) {
            if (totalScoreTable() != null) {
                return totalScoreTable.hasMasterPoints();
            }
        }
        return false;
    }

    /***
     * competion shows competition type
     *
     * @return "Individuals", "Pairs", "Teams" or ""
     */
    public String competion() {
        if (competition.isEmpty()) {
            if (events != null) {
                String s = events.get(0).getCompetition();
                s = s == null ? "" : s;
                s = s.matches("Individuals|Pairs|Teams") ? s : "";
                competition = s;
            }
        }
        return competition;
    }

    public Double averageMaxIMP() {
        OptionalDouble d = events.stream().mapToDouble(e -> e.maxIMP())
                .average();

        return d.orElse(-1);
    }

    /***
     * get
     *
     * @param i
     *            index of the events
     * @return event with this index or null otherwise
     */
    public JsonEvent get(int i) {
        return events != null && i >= 0 && i < events.size() ? events.get(i)
                : null;
    }

    public int size() {
        return events != null ? events.size() : 0;
    }

    public boolean mpScoring() {
        return totalScoreTable == null ? false
                : totalScoreTable.header.contains("TotalScoreMP");
    }

    public boolean impScoring() {
        return totalScoreTable == null ? false
                : totalScoreTable.header.contains("TotalScoreIMP");
    }

    public boolean vpScoring() {
        return totalScoreTable == null ? false
                : totalScoreTable.header.contains("TotalScoreVP");
    }

    /***
     * data find interesting scoreTable rows
     *
     * @param id
     * @return scoreTable rows which contain the id
     */
    public List<List<String>> data(String id) {
        return events.stream().map(e -> e.getScoreTable().subrow(id))
                .collect(Collectors.toList());
    }

}
