package com.pbn.pbnjson;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/***
 * 1. call findMasterPoints
 */

public class JsonTotalScoreTable extends JsonTable {

    private HashSet<String> numberColumns;
    private HashSet<String> headerItems;
    private transient HashMap<Object, Double> masterPoints;
    private transient HashSet<Object> playerFedCodes;
    private transient List<String> dataHeader;
    private transient List<List<Object>> dataRows;

    public JsonTotalScoreTable(List<String> header, List<List<Object>> rows) {
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
        // map number data columns to numbers
        numberMap(numberColumns);
    }

    public void initialize(String competition) {
        this.competition = competition;
    }

    /***
     * resultHeader filtered data
     */
    public List<String> dataHeader() {
        if (dataHeader == null) {
            return header.stream().filter(i -> headerItems.contains(i))
                    .collect(Collectors.toList());
        }
        return dataHeader;
    }

    public List<List<Object>> dataRows() {
        if (dataRows == null) {
            // find indexes of useful columns
            List<Integer> indexes = new LinkedList<>();
            ListIterator<String> it = header.listIterator();
            int i = 0;
            for (String h : header) {
                if (headerItems.contains(h)) {
                    indexes.add(i);
                }
                i++;
            }
            dataRows = new LinkedList<>();
            for (List<Object> row : rows) {
                dataRows.add(subRow(indexes, row));
            }

        }
        return dataRows;
    }

    /***
     * mps extract master points
     *
     * @return master points found; zero if no master points found
     */
    private double masterPoints(List<Object> row, int i) {
        try {
            return Double.parseDouble((String) row.get(i));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /***
     * containsMps find master point rows
     *
     * @return list of rows which contains master points
     */
    private List<List<Object>> containMps() {
        List<List<Object>> mpRows = new LinkedList<>();
        final int i = header.indexOf("MP");

        if (i >= 0) {
            for (List<Object> row : rows) {
                if (masterPoints(row, i) > 0) {
                    mpRows.add(row);
                }
            }
        }

        return mpRows;
    }

    public HashSet<String> getNumberColumns() {
        return numberColumns;
    }

    /***
     * teamIds
     *
     * @return list of fed codes of the team; possible empty
     */
    private List<Object> teamIds(Object rosterDetails) {
        List<Object> ids = new LinkedList<>();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher((String) rosterDetails);
        while (m.find()) {
            ids.add(m.group());
        }
        return ids;
    }

    /*
     * ids finds player id(s) assuming "competion" is defined
     *
     * @param row TotalScoreTable row
     *
     * @return list of PBN ids found
     */
    private List<Object> ids(List<Object> row) {
        List<Object> ids = new LinkedList<>();
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
                ids.addAll(teamIds(row.get(i)));
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
        if (masterPoints == null) {
            findMasterPoints();
        }
        return !masterPoints.isEmpty();
    }

    /***
     * getMasterPoints
     *
     * @fedId federation id
     * @return master points for this id; zero if no masterpoints
     */
    public double getMasterPoints(String fedId) {
        return fedId != null ? masterPoints.getOrDefault(fedId, (double) 0) : 0;
    }

    /***
     * masterPointRegistry
     *
     * @return HashMap which is a mapping between fedId and the master points
     */
    public HashMap<Object, Double> masterPointRegistry() {
        return masterPoints;
    }

    /***
     * findMasterPoints constructs master point "registry" for this
     * TotalScoreTable
     */
    public void findMasterPoints() {
        if (masterPoints == null) {
            masterPoints = new HashMap<>();

            final int mpi = header.indexOf("MP");
            if (mpi >= 0) {
                for (List<Object> row : containMps()) {
                    List<Object> ids = ids(row);
                    double mpts = masterPoints(row, mpi) / ids.size();
                    for (Object id : ids) {
                        masterPoints.put(id, mpts);
                    }
                }
            }
        }
    }

    /***
     * getPlayerFedCodes
     *
     * @return federation codes found in this table
     */
    public HashSet<Object> getPlayerFedCodes() {
        if (playerFedCodes == null) {
            playerFedCodes = new HashSet<>();
            if (competition.equals("Individuals")) {
                playerFedCodes.addAll(column("MemberID"));
            } else if (competition.equals("Pairs")) {
                playerFedCodes.addAll(column("MemberID1"));
                playerFedCodes.addAll(column("MemberID2"));
            } else if (competition.equals("Teams")) {
                int i = header.indexOf("RosterDetails");
                if (i >= 0) {
                    List<Object> rosters = column("RosterDetails");
                    for (Object r : rosters) {
                        playerFedCodes.addAll(teamIds(r));
                    }
                }
            }
        }

        return playerFedCodes;
    }
}
