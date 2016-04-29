package com.tixon.smarthouseworking.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by tikhon.osipov on 28.04.2016
 */

@DatabaseTable(tableName = "history")
public class ArduinoHistory {

    public ArduinoHistory(String historyItem, long time) {
        this.historyItem = historyItem;
        this.time = time;
    }

    public ArduinoHistory() {}

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String historyItem;

    @DatabaseField
    private long time;

    // Getters and setters

    public int getId() {
        return id;
    }

    public String getHistoryItem() {
        return historyItem;
    }

    public void setHistoryItem(String historyItem) {
        this.historyItem = historyItem;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
