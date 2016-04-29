package com.tixon.smarthouseworking.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.tixon.smarthouseworking.model.ArduinoHistory;
import com.tixon.smarthouseworking.model.ScheduleModel;

import java.sql.SQLException;

/**
 * Created by tikhon.osipov on 28.04.2016
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "smart_house.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "myLogsDatabase";

    private HistoryDAO historyDAO = null;
    private ScheduleDAO scheduleDAO = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ArduinoHistory.class);
            TableUtils.createTable(connectionSource, ScheduleModel.class);
        } catch (SQLException e) {
            Log.e(TAG, "error creating DB: " + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public HistoryDAO getHistoryDAO() throws SQLException {
        if(historyDAO == null) {
            historyDAO = new HistoryDAO(getConnectionSource(), ArduinoHistory.class);
        }
        return historyDAO;
    }

    public ScheduleDAO getScheduleDAO() throws SQLException {
        if(scheduleDAO == null) {
            scheduleDAO = new ScheduleDAO(getConnectionSource(), ScheduleModel.class);
        }
        return scheduleDAO;
    }

    @Override
    public void close() {
        super.close();
        historyDAO = null;
        scheduleDAO = null;
    }
}
