package com.tixon.smarthouseworking.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.tixon.smarthouseworking.model.ScheduleModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tikhon.osipov on 28.04.2016
 */
public class ScheduleDAO extends BaseDaoImpl<ScheduleModel, Integer> {
    public String templateScheduleString = "06:00 19:00;06:00 19:00;06:00 19:00;06:00 19:00;06:00 19:00;06:00 19:00;06:00 19:10;\n";

    protected ScheduleDAO(ConnectionSource connectionSource,
                          Class<ScheduleModel> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<ScheduleModel> getSchedule() throws SQLException {
        return this.queryForAll();
    }

    public String getScheduleStringFormat() throws SQLException {
        ArrayList<ScheduleModel> list = new ArrayList<>(this.queryForAll());
        String schedule = "";
        for(ScheduleModel day: list) {
            schedule += day.getTimeMorning() + " " + day.getTimeEvening() + ";";
        }
        return schedule;
    }

    public void updateScheduleItem(int id, String morningTime, String eveningTime) throws SQLException {
        UpdateBuilder<ScheduleModel, Integer> updateBuilder = updateBuilder();
        updateBuilder.where().eq("scheduleId", id);
        updateBuilder.updateColumnValue(ScheduleModel.TIME_MORNING_NAME, morningTime);
        updateBuilder.updateColumnValue(ScheduleModel.TIME_EVENING_NAME, eveningTime);
        updateBuilder.update();
    }
}
