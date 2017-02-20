package com.pbn.pbnjson;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 1. call findMasterPoints
 */

public class JsonTotalScoreTable extends JsonTable {

    private static transient HashSet<String> numberColumns;
    private static transient HashSet<String> headerItems;
    private static transient HashMap<String, Double> masterPoints;
    private static transient HashSet<String> playerFedCodes;

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
        masterPoints = new HashMap<>();
    }

    /***
     * filter removes the uninteresting (see headerItems) header items and the
     * corresponding row items
     */
    public void filter() {
        filterTable(headerItems);
    }

    /***
     * mps extract master points
     *
     * @return master points found; zero if no master points found
     */
    private double masterPoints(List<String> row, int i) {
        try {
            return Double.parseDouble(row.get(i));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /***
     * containsMps find master point rows
     *
     * @return list of rows which contains master points
     */
    private List<List<String>> containMps() {
        List<List<String>> mpRows = new LinkedList<>();
        final int i = header.indexOf("MP");

        if (i >= 0) {
            for (List<String> row : rows) {
                if (masterPoints(row, i) > 0) {
                    mpRows.add(row);
                }
            }
        }

        return mpRows;
    }

    /*
     * ids finds player id(s)
     *
     * @param row TotalScoreTable row
     *
     * @return list of PBN ids found
     */
    private List<String> ids(List<String> row) {
        List<String> ids = new LinkedList<>();
        if (competition.equals("Individuals")) {
            int i = header.indexOf("PlayerID");
            if (i >= 0) {
                ids.add(row.get(i));
            }
        } else if (competition.equals("Pairs")) {
            int i = header.indexOf("PlayerID1");
            int j = header.indexOf("PlayerID2");
            if (i >= 0 && j >= 0) {
                ids.add(row.get(i));
                ids.add(row.get(j));
            }
        } else if (competition.equals("Teams")) {
            int i = header.indexOf("RosterDetails");
            if (i >= 0) {
                // fed code must be a number this to work!
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(row.get(i));
                while (m.find()) {
                    ids.add(m.group());
                }
            }
        }

        return ids;
    }

    /***
     * hasMasterPoints
     *
     * @return true iff this table contains master points
     */
    public boolean hasMasterPoints() {
        return masterPoints.isEmpty();
    }

    public double getMasterPoints(String fedId) {
        return fedId != null ? masterPoints.getOrDefault(fedId, (double) 0) : 0;
    }

    /***
     * findMasterPoints constructs master point "registry" for this
     * TotalScoreTable
     */
    public void findMasterPoints() {
        final int mpi = header.indexOf("MP");
        for (List<String> row : containMps()) {
            List<String> ids = ids(row);
            double mpts = masterPoints(row, mpi) / ids.size();
            for (String id : ids) {
                masterPoints.put(id, mpts);
            }
        }
    }

    /***
     * getPlayerFedCodes
     *
     * @return federation codes found in this table
     */
    public HashSet<String> getPlayerFedCodes() {
        if (playerFedCodes == null) {
            playerFedCodes = new HashSet<>();
        }

        return null;
    }
}
