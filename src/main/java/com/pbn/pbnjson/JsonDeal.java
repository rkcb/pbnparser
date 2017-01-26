package com.pbn.pbnjson;

public class JsonDeal {

    private JsonHand north;
    private JsonHand east;
    private JsonHand south;
    private JsonHand west;

    public JsonDeal(JsonHand north, JsonHand east, JsonHand south,
            JsonHand west) {
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
    }

    public JsonHand getNorth() {
        return north;
    }

    public void setNorth(JsonHand north) {
        this.north = north;
    }

    public JsonHand getEast() {
        return east;
    }

    public void setEast(JsonHand east) {
        this.east = east;
    }

    public JsonHand getSouth() {
        return south;
    }

    public void setSouth(JsonHand south) {
        this.south = south;
    }

    public JsonHand getWest() {
        return west;
    }

    public void setWest(JsonHand west) {
        this.west = west;
    }

}
