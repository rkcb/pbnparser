package com.pbn.pbnjson;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonScoreTable extends JsonTable {

    private transient HashSet<String> numberColumns;
    private transient List<String> scoreTableHeader; // fixed for a
                                                     // direction
    private transient List<String> comparisonHeader;
    private transient List<Integer> idIndexes; // in header
    private transient int minId = -1;
    private transient int maxId = -1;
    private transient HashMap<Integer, HashSet<Integer>> rowFilters;

    private transient HashSet<String> comparisonItems;
    private transient List<Integer> comparisonItemIndexes;

    /***
     * 1. set competion type 2. call setIdIndexes 3. setMinMaxId 4. call
     * setIdIndexes 5. call setScoreTableHeader 6. call subrow
     */

    public void initialize(String competition) {
        this.competition = competition;
        setIdIndexes();
        setRowFilters();
        setMinMaxId();
        setIdIndexes();
        setScoreTableHeader();
        setComparisonHeader();
        findComparisonDataIndexes();
    }

    private void findComparisonDataIndexes() {
        // comparison table is only used in indi and pair games
        if (comparisonItems != null && !comparisonItems.isEmpty()) {
            comparisonItemIndexes = new LinkedList<>();
            int i = 0;
            for (String col : header) {
                if (comparisonItems.contains(col)) {
                    comparisonItemIndexes.add(i);
                }
                i++;
            }
        }
    }

    /***
     * initialize copies "from" values to "to" values
     *
     * @param to
     * @param from
     *
     */
    public static void initialize(JsonScoreTable to, JsonScoreTable from) {
        to.competition = from.competition;
        to.numberColumns = from.numberColumns;
        to.scoreTableHeader = from.scoreTableHeader;
        to.comparisonHeader = from.comparisonHeader;
        to.idIndexes = from.idIndexes;
        to.minId = from.minId;
        to.maxId = from.maxId;
        to.rowFilters = from.rowFilters;
        to.comparisonItems = from.comparisonItems;
        to.comparisonItemIndexes = from.comparisonItemIndexes;
    }

    /***
     * filters
     *
     * @return return indexes of interesting row items; see setRowFilters
     */
    protected HashMap<Integer, HashSet<Integer>> filters() {
        return rowFilters;
    }

    /***
     * indiScoreItems gives column names for a direction
     *
     * @param direction
     *            the direction for which header is constructed: "North",
     *            "South", "East", "West"
     */

    protected static String indiScoreDirection(String direction) {
        return direction.matches("North|South") ? "NS" : "EW";
    }

    protected static String otherDirection(String direction) {
        return direction.equals("NS") ? "EW" : "NS";
    }

    protected static String otherTeam(String team) {
        return team.equals("Home") ? "Away" : "Home";
    }

    /***
     * indiScoreItems picks up the items in the header
     *
     * @param the
     *            interesting direction
     * @return hashset of the items
     */
    protected static HashSet<String> indiScoreItems(String direction) {
        HashSet<String> items = new HashSet<>();
        if (direction.matches("North|South|East|West")) {
            Collections.addAll(items, "PlayerId_" + direction, "Contract",
                    "Declarer", "Result", "Lead",
                    "Score_" + indiScoreDirection(direction),
                    "IMP_" + direction, "MP_" + direction,
                    "Percentage_" + direction);
        } else if (direction.matches("NS|EW")) {
            Collections.addAll(items, "");
        }
        return items;
    }

    /***
     * pairScoreItems picks up the items in the header
     *
     * @param the
     *            interesting direction
     * @return hashset of the items
     */
    public static HashSet<String> pairScoreItems(String direction) {
        HashSet<String> items = new HashSet<>();
        if (direction.matches("NS|EW")) {
            Collections.addAll(items, "PairId_" + otherDirection(direction),
                    "Contract", "Declarer", "Result", "Lead",
                    "Score_" + direction, "IMP_" + direction, "MP_" + direction,
                    "Percentage_" + direction);
        }
        return items;
    }

    /***
     * teamScoreItems picks up the items in the header
     *
     * @param the
     *            interesting direction
     * @return hashset of the items
     */
    protected static HashSet<String> teamScoreItems(String team) {
        HashSet<String> items = new HashSet<>();
        if (team.matches("Home|Away")) {
            Collections.addAll(items, "Round", "TeamId_" + otherTeam(team),
                    "VP_" + team, "BAM_" + team);
        }
        return items;
    }

    /***
     * scoreItemIndexes computes scoreItemIndexes
     *
     * @param items
     *            header column names
     * @return set of indexes
     */

    protected HashSet<Integer> scoreItemIndexes(HashSet<String> items) {
        Iterator<String> hi = header.iterator();
        HashSet<Integer> indexSet = new HashSet<>(items.size());
        int i = 0;
        while (hi.hasNext()) {
            if (items.contains(hi.next())) {
                indexSet.add(i);
            }
            i++;
        }

        return indexSet;
    }

    /***
     * scoreTableHeader gives the PBN ScoreTableHeader set by competion type;
     * use this with the data(String id) function
     *
     * @return ScoreTableHeader
     */
    public List<String> scoreTableHeader() {
        return scoreTableHeader != null ? scoreTableHeader : new LinkedList<>();
    }

    /***
     * setRowFilters maps id indexes to a set of indexes in the header which are
     * interesting for this id index set
     */
    protected void setRowFilters() {

        if (rowFilters == null) {
            rowFilters = new HashMap<>();
            if (competition.equals("Individuals")) {
                List<String> dirs = new LinkedList<>();
                Collections.addAll(dirs, "North", "South", "East", "West");
                Iterator<String> diri = dirs.iterator();
                Iterator<Integer> idi = idIndexes.iterator();
                while (idi.hasNext() && diri.hasNext()) {
                    // map dir id index to corresponding index set for
                    // directions
                    rowFilters.put(idi.next(),
                            scoreItemIndexes(indiScoreItems(diri.next())));
                }
            } else if (competition.equals("Pairs")) {
                rowFilters.put(idIndexes.get(0),
                        scoreItemIndexes(pairScoreItems("NS")));
                rowFilters.put(idIndexes.get(1),
                        scoreItemIndexes(pairScoreItems("EW")));
            } else if (competition.equals("Teams")) {
                rowFilters.put(idIndexes.get(0),
                        scoreItemIndexes(teamScoreItems("Home")));
                rowFilters.put(idIndexes.get(1),
                        scoreItemIndexes(teamScoreItems("Away")));
            }
        }
    }

    /***
     * setAdjacent tests are id positions adjacent
     */
    public void setMinMaxId() {
        if (idIndexes.size() < 2) {
            return;
        }

        Iterator<Integer> it = idIndexes.iterator();
        int x = it.next();
        boolean adjacent = true;
        while (it.hasNext() && adjacent) {
            int y = it.next();
            adjacent = x + 1 == y;
            x = y;
        }
        if (adjacent) {
            minId = idIndexes.get(0);
            maxId = idIndexes.get(idIndexes.size() - 1);
        }
    }

    public JsonScoreTable(List<String> header, List<List<Object>> rows) {
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

    /***
     * containsId finds the id index
     *
     * @param row
     *            any row this table
     * @param id
     *            PBN id
     * @return index of id or -1 if not found
     */
    protected int containsId(List<Object> row, String id) {
        if (minId >= 0) {
            int index = row.subList(minId, maxId + 1).indexOf(id);
            return index >= 0 ? index + minId : -1;
        } else {
            boolean found = false;
            Iterator<Integer> i = idIndexes.iterator();
            int index = -1;
            while (!found && i.hasNext()) {
                index = i.next();
                found = row.get(index).equals(id);
            }
            return index >= 0 ? index : -1;
        }
    }

    /***
     * row searches for a row with the id
     *
     * @param id
     *            that is contained in this row
     * @return the row which contains the id or an empty list otherwise; this
     *         row contains only items which are interesting for this playing
     *         unit; see setRowFilters and rowFilters
     */
    public List<Object> subrow(String id) {

        Iterator<List<Object>> ri = rows.iterator();
        List<Object> row = new LinkedList<>();
        int index = -1;
        while (index < 0 && ri.hasNext()) {
            row = ri.next();
            index = containsId(row, id);
        }

        // traverse row and check whether index belongs to desired indexes
        List<Object> subrow = new LinkedList<>();
        if (index >= 0) {
            Iterator<Object> rowi = row.iterator();
            int i = 0;
            HashSet<Integer> items = rowFilters.get(index);

            while (i < row.size()) {
                Object value = rowi.next();
                if (items.contains(i++)) {
                    subrow.add(value);
                }
            }
        }

        return subrow;
    }

    /***
     * idIndexes finds the indexes of the header where the ids locate and stores
     * it
     */
    protected void setIdIndexes() {
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
     * setScoreTableHeader sets correct ScoreTableHeader given that competion
     * type is valid
     */
    protected void setScoreTableHeader() {
        if (competition.matches("Individuals")) {
            // by convention ScoreTable header is for South;
            HashSet<String> items = indiScoreItems("South");
            // keep only items interesting for South
            scoreTableHeader = header.stream().filter(i -> items.contains(i))
                    .collect(Collectors.toList());
        } else if (competition.matches("Pairs")) {
            HashSet<String> items = pairScoreItems("NS");
            // as above, for NS
            scoreTableHeader = header.stream().filter(i -> items.contains(i))
                    .collect(Collectors.toList());
        } else if (competition.matches("Teams")) {
            HashSet<String> items = new HashSet<>(4);
            Collections.addAll(items, "Round", "TeamId_Away", "VP_Home",
                    "BAM_Home");
            scoreTableHeader = header.stream().filter(i -> items.contains(i))
                    .collect(Collectors.toList());
        }
    }

    /***
     * setComparisonHeader constructs the comparison header
     */
    public void setComparisonHeader() {
        if (comparisonItems == null && competition != null
                && competition.matches("Individuals|Pairs")) {
            comparisonItems = new HashSet<>();

            Collections.addAll(comparisonItems, "PairId_NS", "PairId_EW",
                    "PlayerId_North", "PlayerId_South", "PlayerId_East",
                    "PlayerId_West", "Contract", "Declarer", "Result", "Lead",
                    "IMP_NS", "IMP_EW", "MP_NS", "MP_EW", "Percentage_NS",
                    "Percentage_EW", "Percentage_North", "Percentage_East",
                    "MP_North", "MP_East", "IMP_North", "IMP_East", "BAM_North",
                    "BAM_East");

            comparisonHeader = header.stream()
                    .filter(i -> comparisonItems.contains(i))
                    .collect(Collectors.toList());
        }
    }

    public List<Integer> comparisonIndexes() {
        return comparisonItemIndexes;
    }

    /***
     * comparisonHeader
     *
     * @return the header of the comparison table
     */
    public List<String> comparisonHeader() {
        return comparisonHeader != null ? comparisonHeader : new LinkedList<>();
    }

    /***
     * comparisonData
     *
     * @return row items which are under comparisonHeader
     */
    public List<List<Object>> comparisonData() {
        List<List<Object>> subrows = new LinkedList<>();
        if (comparisonItems != null && competition != null
                && competition.matches("Individuals|Pairs")) {
            for (List<Object> r : rows) {
                subrows.add(subRow(comparisonItemIndexes, r));
            }
        }

        return subrows;
    }

}
