package com.pbn.tools;

import com.pbn.ast.Event;
import com.pbn.pbnjson.JsonEvent;
import com.pbn.pbnjson.JsonOptimumResultTable;
import com.pbn.pbnjson.JsonScoreTable;
import com.pbn.pbnjson.JsonTotalScoreTable;

public class Tools {

    /***
     * Event2JsonEvent copy Event information to JsonEvent
     */
    public static JsonEvent event2JsonEvent(Event e) {

        JsonEvent j = new JsonEvent();

        if (e == null) {
            return j;
        }

        if (e.get("Event") != null) {
            j.setEvent(e.get("Event").value());
        }
        if (e.get("Site") != null) {
            j.setSite(e.get("Site").value());
        }
        if (e.get("Date") != null) {
            j.setDate(e.get("Date").value());
        }
        if (e.get("Board") != null) {
            j.setBoard(e.get("Board").value());
        }
        if (e.get("West") != null) {
            j.setEvent(e.get("West").value());
        }
        if (e.get("North") != null) {
            j.setEvent(e.get("North").value());
        }
        if (e.get("East") != null) {
            j.setEvent(e.get("East").value());
        }
        if (e.get("South") != null) {
            j.setEvent(e.get("South").value());
        }
        if (e.get("Dealer") != null) {
            j.setEvent(e.get("Dealer").value());
        }
        if (e.get("Vulnerable") != null) {
            j.setEvent(e.get("Vulnerable").value());
        }
        if (e.get("Deal") != null) {
            j.setDeal(e.get("Deal").hands());
        }
        if (e.get("Scoring") != null) {
            j.setEvent(e.get("Scoring").value());
        }
        if (e.get("Declarer") != null) {
            j.setEvent(e.get("Declarer").value());
        }
        if (e.get("Contract") != null) {
            j.setEvent(e.get("Contract").value());
        }
        if (e.get("Result") != null) {
            j.setEvent(e.get("Result").value());
        }
        if (e.get("ScoreTable") != null) {
            j.setScoreTable(new JsonScoreTable(e.get("ScoreTable").header(),
                    e.get("ScoreTable").rows()));
        }
        if (e.get("TotalScoreTable") != null) {
            j.setTotalScoreTable(
                    new JsonTotalScoreTable(e.get("TotalScoreTable").header(),
                            e.get("TotalScoreTable").rows()));
        }
        if (e.get("OptimumResultTable") != null) {
            j.setOptimumResultTable(new JsonOptimumResultTable(
                    e.get("OptimumResultTable").header(),
                    e.get("OptimumResultTable").rows()));
        }

        return j;
    }
}
