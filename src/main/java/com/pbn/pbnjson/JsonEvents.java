package com.pbn.pbnjson;

import java.util.Iterator;
import java.util.List;

public class JsonEvents {

    private List<JsonEvent> events;
    private JsonTotalScoreTable totalScoreTable;
    private String competition;
    private Boolean mpScoring;
    private Boolean impScoring;
    private Boolean vpScoring;

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

    public JsonEvent event(int i) {
        return events != null && i >= 0 && i < events.size() ? events.get(i)
                : null;
    }

    /***
     * size
     *
     * @return number of JSON events
     */
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
     * private def matchPointScoring(): Boolean = {
     * events.head.header("TotalScoreTable").contains("TotalScoreMP"); } private
     * def impScoring(): Boolean = {
     * events.head.header("TotalScoreTable").contains("TotalScoreIMP"); }
     *
     * private def vpScoring(): Boolean = {
     * events.head.header("ScoreTable").contains("VP_Home") }
     */
}
