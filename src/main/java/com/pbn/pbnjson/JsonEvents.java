package com.pbn.pbnjson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import com.pbn.tools.Tools;

/***
 * JsonEvents collects PBN events in one class and assumes that the events are
 * consistent: events contains at most one TotalScoreTable and ScoreTable varies
 * only by row content and the header stays the same
 */
public class JsonEvents {

    private List<JsonEvent> events;
    private JsonTotalScoreTable totalScoreTable;
    private String competition;
    private HashMap<String, JsonEvent> boardsMap;

    public JsonEvents(List<JsonEvent> events) {
        this.events = events;
        JsonEvent from = events.get(0);
        from.initialize();
        competition = from.getCompetition();
        for (JsonEvent e : events) {
            JsonEvent.initialize(e, from);
        }
        if (eventsOk()) {
            mapBoards();
        }
    }

    /***
     * @param json
     *            string (serialization of List<JsonEvent> by Gson)
     */
    public JsonEvents(String json) {
        this(Tools.fromJson(json));
    }

    /***
     * mapBoards board -> event
     */
    private void mapBoards() {
        boardsMap = new HashMap<>();
        for (JsonEvent e : events) {
            boardsMap.put(e.getBoard(), e);
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

    /***
     * event
     *
     * @param board
     * @return event whose board matches
     */
    public JsonEvent event(String board) {
        return boardsMap.getOrDefault(board, null);
    }

    /***
     * eventDescription
     *
     * @return string given in the PBN tag "Event" or empty string
     */
    public String eventDescription() {
        if (events != null && !events.isEmpty()) {
            String e = events.get(0).getEvent();
            return e == null ? "" : e;
        } else {
            return "";
        }
    }

    public Double averageMaxIMP() {
        OptionalDouble d = events.stream().mapToDouble(e -> e.maxIMP())
                .average();

        return d.orElse(-1);
    }

    /***
     * masterPointsEarned computes how much this particular earned master
     * points; see findMasterPoints()
     *
     * @param fedId
     *            federation code
     * @return master points for this player or null if mps not supported
     */
    public Double masterPointsEarned(String fedId) {
        if (hasMasterPoints()) {
            return totalScoreTable.getMasterPoints(fedId);
        } else {
            return null;
        }
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
     * scoreHeader is the header for scoreData(id); note that this uses the
     * first JsonEvent
     *
     * @return header for scoreData using first event; empty list if nonexisting
     */
    public List<String> scoreHeader() {
        if (eventsOk()) {
            return events.get(0).getScoreTable().scoreTableHeader();
        } else {
            return new LinkedList<>();
        }
    }

    /***
     * data find interesting scoreTable rows; for example if id belongs to EW
     * then show opponents
     *
     * @param id
     * @return scoreTable rows which contain the id or an empty list if not
     *         possible
     */
    public List<List<Object>> scoreData(String id) {
        if (eventsOk()) {
            return events.stream().map(e -> e.getScoreTable().subrow(id))
                    .collect(Collectors.toList());
        } else {
            return new LinkedList<>();
        }
    }

    private boolean eventsOk() {
        return events != null && !events.isEmpty();
    }

    /***
     * comparisonHeader
     *
     * @return ComparisonTable header of the first JsonEvent
     */
    public List<String> comparisonHeader() {
        if (eventsOk()) {
            return events.get(0).getScoreTable().comparisonHeader();
        } else {
            return new LinkedList<>();
        }
    }

    /***
     * comparisonData
     *
     * @param board
     *            Pbn value
     *
     * @return ScoreTable data which suits comparisonHeader; returns empty list
     *         in any error case
     */
    public List<List<Object>> comparisonData(String board) {
        if (eventsOk() && boardsMap.containsKey(board)) {
            return boardsMap.get(board).getScoreTable().comparisonData();
        } else {
            return new LinkedList<>();
        }
    }

    public boolean totalScoreTableExists() {
        return totalScoreTable() != null;
    }

    public boolean scoreTableExists() {
        return eventsOk() ? events.get(0).getScoreTable() != null : false;
    }

    public List<String> optimumTableHeader() {
        if (eventsOk()) {
            return events.get(0).getOptimumResultTable().getHeader();
        } else {
            return new LinkedList<>();
        }
    }
}
