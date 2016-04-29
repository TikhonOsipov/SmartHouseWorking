package com.tixon.smarthouseworking.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by tikhon.osipov on 28.04.2016
 */

@DatabaseTable(tableName = "schedule")
public class ScheduleModel {

    public static final String TIME_MORNING_NAME = "time_morning";
    public static final String TIME_EVENING_NAME = "time_evening";

    public ScheduleModel() {}

    public ScheduleModel(String timeMorning, String timeEvening) {
        this.timeMorning = timeMorning;
        this.timeEvening = timeEvening;
    }

    @DatabaseField(generatedId = true, columnName = "scheduleId")
    private int id;

    @DatabaseField(columnName = TIME_MORNING_NAME)
    private String timeMorning;

    @DatabaseField(columnName = TIME_EVENING_NAME)
    private String timeEvening;

    // Getters and setters

    public int getId() {
        return id;
    }

    public String getTimeMorning() {
        return timeMorning;
    }

    public void setTimeMorning(String timeMorning) {
        this.timeMorning = timeMorning;
    }

    public String getTimeEvening() {
        return timeEvening;
    }

    public void setTimeEvening(String timeEvening) {
        this.timeEvening = timeEvening;
    }
}
