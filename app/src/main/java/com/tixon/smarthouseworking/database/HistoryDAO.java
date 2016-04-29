package com.tixon.smarthouseworking.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.tixon.smarthouseworking.model.ArduinoHistory;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by tikhon.osipov on 28.04.2016
 */
public class HistoryDAO extends BaseDaoImpl<ArduinoHistory, Integer> {
    protected HistoryDAO(ConnectionSource connectionSource,
                         Class<ArduinoHistory> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<ArduinoHistory> getAllHistory() throws SQLException {
        return this.queryForAll();
    }
}
