package com.pbn.pbnjson;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private static transient HashMap<Integer, HashSet<Integer>> rowFilters;

    /***
     * 1. initialize competion type 2.
     */
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
        rowFilters = new HashMap<>();
    }

    public static void setCompetition(String type) {
        competition = type.matches("Individuals|Pairs|Teams") ? type : "";
    }

    /***
     * setScoreTableHeader sets correct ScoreTableHeader given that competion
     * type is valid
     */
    public void setScoreTableHeader() {
        if (competition.matches("Individuals")) {
            // by convention ScoreTable header is for South;
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
     *
     * @return ScoreTableHeader
     */
    public static String[] scoreTableHeader() {
        return scoreTableHeader != null ? JsonScoreTable.scoreTableHeader
                : new String[0];
    }

    /***
     * indiScoreItems gives column names for a direction
     *
     * @param direction
     *            the direction for which header is constructed: "North",
     *            "South", "East", "West"
     */
    public static HashSet<String> indiScoreItems(String direction) {
        HashSet<String> items = new HashSet<>(8);
        if (direction.matches("North|South|East|West")) {
            Collections.addAll(items, "Board", "Contract", "Declarer",
                    "PlayerId_" + direction, "Result", "Lead",
                    "Score_" + direction, "IMP_" + direction, "MP_" + direction,
                    "Percentage_" + direction);
        }
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

    /***
     * data return filtered row which contains the id
     *
     * @param id
     *            PBN id
     */
    public List<String> data(String id) {

        Iterator<List<String>> it = rows.iterator();
        int idPos = -1;
        List<String> row;

        // find the row with id; must exist
        while (it.hasNext() && idPos < 0) {
            row = it.next();
            idPos = findIdPos(row, id);
        }

        return null;
    }

    /***
     * setRowFilters builds correct row filters for the competion type
     */
    private void setRowFilters() {
        if (rowFilters.isEmpty()) {
            if (competition.equals("Individuals")) {
                int x = 0;
                Stream<String> dirs = Stream.of("North", "South", "East",
                        "West");
                Iterator<String> dir = dirs.iterator();
                while (dir.hasNext()) {
                    rowFilters.put(x++, rowFilter(indiScoreItems(dir.next())));
                }
            } else if (competition.equals("Pairs")) {
            } else if (competition.equals("Teams")) {
            }
        }
    }

    /***
     * subRow builds a subrow which matches the id
     *
     * @param id
     *            PBN id of the playing unit (indi, pair, team)
     * @param row
     *            "super" row
     * @return subrow
     */
    private List<String> subRow(List<String> row, String id) {

        HashSet<Integer> filterSet = rowFilters.get(findIdPos(row, id));
        int i = 0;
        Iterator<String> it = row.iterator();
        List<String> subRow = new LinkedList<>();
        while (it.hasNext()) {
            String value = it.next();
            if (filterSet.contains(i++)) {
                subRow.add(value);
            }
        }

        return subRow;
    }

    /**
     *
     * @param finds
     *            position of the id in idIndexes
     * @param id
     *            PBN player, pair or team id
     * @return nonnegative index of idIndexes when found and -1 otherwise
     */
    private int findIdPos(List<String> row, String id) {
        boolean found = false;
        Iterator<Integer> it = idIndexes.iterator();
        int i = 0;
        while (!found && it.hasNext()) {
            it.next();
            i++;
            found = row.get(it.next()).equals(id);
        }
        return found ? i : -1;
    }

}
