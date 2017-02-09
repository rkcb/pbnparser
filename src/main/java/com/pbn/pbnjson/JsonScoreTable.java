package com.pbn.pbnjson;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonScoreTable extends JsonTable {

    private static transient HashSet<String> numberColumns;
    private static transient HashSet<String> indiTableItems;
    private static transient HashSet<String> pairTableItems;
    private static transient HashSet<String> teamTableItems;
    private static transient HashSet<String> comparisonItems;
    private static transient String competition = "";
    private static transient HashSet<String> indiScoreItems;
    private static transient String[] scoreTableHeader;
    private static transient HashSet<String> idColumns; // in header
    private static transient List<Integer> idIndexes; // in header
    private static transient List<List<Integer>> rowFilters;

    public JsonScoreTable(List<String> header, List<List<String>> rows) {
        super(header, rows);
        if (numberColumns == null) {
            numberColumns = new HashSet<>(20);
            Collections.addAll(numberColumns, "Rank", "Result", "Score_NS",
                    "Score_EW", "IMP_NS", "IMP_EW", "MP_NS", "MP_EW",
                    "Percentage_NS", "Percentage_EW", "Percentage_North",
                    "Percentage_East", "Percentage_South", "Percentage_West",
                    "Multiplicity");
        }

        idIndexes = new LinkedList<>();
        rowFilters = new LinkedList<>();
    }

    public static void setCompetition(String type) {
        competition = type.matches("Individuals|Pairs|Teams") ? type : "";
    }

    /***
     * setScoreTableHeader sets correct ScoreTableHeader given that competion
     * type is valid
     */
    public void setScoreTableHeader() {
        // Individuals
        if (competition.matches("Individuals")) {
            // by convention ScoreTable header is for South
            indiScoreItems = indiScoreItems("South");
            // keep only items interesting for South
            scoreTableHeader = header.stream()
                    .filter(i -> indiScoreItems.contains(i))
                    .collect(Collectors.toList()).toArray(new String[0]);
        } else if (competition.matches("Pairs")) {

        } else if (competition.matches("Teams")) {

        }
    }

    /***
     * scoreTableHeader gives the PBN ScoreTableHeader set by competion type
     */
    public static String[] scoreTableHeader() {
        return scoreTableHeader != null ? JsonScoreTable.scoreTableHeader
                : new String[0];
    }

    /***
     * indiScoreItems gives column names for a direction
     *
     * @param direction
     *            the direction for which header is constructed
     */
    public static HashSet<String> indiScoreItems(String direction) {
        HashSet<String> items = new HashSet<>(8);
        Collections.addAll(items, "Board", "Contract", "Declarer",
                "PlayerId_" + direction, "Result", "Lead", "Score_" + direction,
                "IMP_" + direction, "MP_" + direction,
                "Percentage_" + direction);
        return items;
    }

    /***
     * idIndexes finds the indexes of the header where the ids locate and stores
     * it
     */
    public void setIdIndexes() {
        List<String> ids = new LinkedList<>();

        if (competition.equals("Individuals")) {
            Collections.addAll(ids, "PlayerId_North", "PlayerId_South",
                    "PlayerId_East", "PlayerId_West");
        } else if (competition.equals("Pairs")) {
            Collections.addAll(ids, "PairId_NS", "PairId_EW");
        } else if (competition.equals("Teams")) {
            Collections.addAll(ids, "TeamId_Home", "TeamId_Away");
        }

        idIndexes = ids.stream().map(i -> header.indexOf(i))
                .collect(Collectors.toList());
    }

    private void setRowFilters() {
        if (competition.equals("Individuals")) {

        } else if (competition.equals("Pairs")) {
        } else if (competition.equals("Teams")) {
        }
    }

    /***
     * foundId finds if possible index where id occurred in header
     *
     * @param PBN
     *            player, pair or team id
     * @return nonnegative integer when found and -1 otherwise
     */
    private int foundId(List<String> row, String id) {
        int found = -1;
        if (!idIndexes.isEmpty()) {
            Iterator<Integer> i = idIndexes.iterator();
            while (found < 0) {
                int x = i.next();
                found = row.get(i.next()).equals(id) ? x : -1;
            }
        }
        return found;
    }

    // /***
    // * findRow
    // *
    // * @return the row with the id or an empty row
    // */
    // public List<String> findRow(String id) {
    // List<String> row = new LinkedList<>();
    // Iterator<List<String>> i = rows.iterator();
    // boolean found = false;
    // while (!found && i.hasNext()) {
    // row = i.next();
    // found = foundId(row, id) >= 0;
    // }
    // return found ? row : new LinkedList<>();
    // }

}
