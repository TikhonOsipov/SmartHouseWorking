package com.tixon.smarthouseworking;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.tixon.smarthouseworking.database.HelperFactory;
import com.tixon.smarthouseworking.database.ScheduleDAO;
import com.tixon.smarthouseworking.model.ScheduleModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by tikhon on 29.04.16
 */
public class ScheduleActivity extends AppCompatActivity {

    ScheduleDAO scheduleDAO;
    TextView tvMorningMonday, tvMorningTuesday, tvMorningWednesday, tvMorningThursday, tvMorningFriday, tvMorningSaturday, tvMorningSunday;
    TextView tvEveningMonday, tvEveningTuesday, tvEveningWednesday, tvEveningThursday, tvEveningFriday, tvEveningSaturday, tvEveningSunday;

    ArrayList<ScheduleModel> schedule = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        tvMorningMonday = (TextView) findViewById(R.id.tvTimeMorningMonday);
        tvMorningTuesday = (TextView) findViewById(R.id.tvTimeMorningTuesday);
        tvMorningWednesday = (TextView) findViewById(R.id.tvTimeMorningWednesday);
        tvMorningThursday = (TextView) findViewById(R.id.tvTimeMorningThursday);
        tvMorningFriday = (TextView) findViewById(R.id.tvTimeMorningFriday);
        tvMorningSaturday = (TextView) findViewById(R.id.tvTimeMorningSaturday);
        tvMorningSunday = (TextView) findViewById(R.id.tvTimeMorningSunday);

        tvEveningMonday = (TextView) findViewById(R.id.tvTimeEveningMonday);
        tvEveningTuesday = (TextView) findViewById(R.id.tvTimeEveningTuesday);
        tvEveningWednesday = (TextView) findViewById(R.id.tvTimeEveningWednesday);
        tvEveningThursday = (TextView) findViewById(R.id.tvTimeEveningThursday);
        tvEveningFriday = (TextView) findViewById(R.id.tvTimeEveningFriday);
        tvEveningSaturday = (TextView) findViewById(R.id.tvTimeEveningSaturday);
        tvEveningSunday = (TextView) findViewById(R.id.tvTimeEveningSunday);

        try {
            scheduleDAO = HelperFactory.getHelper().getScheduleDAO();
            schedule.clear();
            schedule.addAll(scheduleDAO.getSchedule());
            Log.d("myLogs", "schedule size: " + schedule.size());
            if(schedule.isEmpty()) {
                scheduleDAO.create(new ScheduleModel("06:00", "19:00"));
                scheduleDAO.create(new ScheduleModel("06:00", "19:00"));
                scheduleDAO.create(new ScheduleModel("06:00", "19:00"));
                scheduleDAO.create(new ScheduleModel("06:00", "19:00"));
                scheduleDAO.create(new ScheduleModel("06:00", "19:00"));
                scheduleDAO.create(new ScheduleModel("06:00", "19:00"));
                scheduleDAO.create(new ScheduleModel("06:00", "19:00"));
            }
            schedule.clear();
            schedule.addAll(scheduleDAO.getSchedule());
            showSchedule(schedule);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.schedule_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menuScheduleSet:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutMonday:
                showTimePickerDialog(1);
                break;
            case R.id.layoutTuesday:
                showTimePickerDialog(2);
                break;
            case R.id.layoutWednesday:
                showTimePickerDialog(3);
                break;
            case R.id.layoutThursday:
                showTimePickerDialog(4);
                break;
            case R.id.layoutFriday:
                showTimePickerDialog(5);
                break;
            case R.id.layoutSaturday:
                showTimePickerDialog(6);
                break;
            case R.id.layoutSunday:
                showTimePickerDialog(7);
                break;
            default: break;
        }
    }

    private void showTimePickerDialog(final int id) {
        String timeMorning = schedule.get(id-1).getTimeMorning();
        final String timeEvening = schedule.get(id-1).getTimeEvening();
        TimePickerDialog dialogMorning = new TimePickerDialog(ScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                final int hourMorning = hourOfDay;
                final int minuteMorning = minute;
                TimePickerDialog dialogEvening = new TimePickerDialog(ScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            scheduleDAO.updateScheduleItem(id, convertToTime(hourMorning, minuteMorning), convertToTime(hourOfDay, minute));
                            Log.d("myLogs", "db updated: " + convertToTime(hourMorning, minuteMorning) + ":" + convertToTime(hourOfDay, minute));
                            schedule.clear();
                            schedule.addAll(scheduleDAO.getSchedule());
                            showSchedule(schedule);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }, getHour(timeEvening), getMinute(timeEvening), true);
                dialogEvening.setMessage("Set evening time");
                dialogEvening.show();
            }
        }, getHour(timeMorning), getMinute(timeMorning), true);
        dialogMorning.setMessage("Set morning time");
        dialogMorning.show();
    }

    private String convertToTime(int h, int m) {
        return String.format(Locale.getDefault(), "%1$02d", h) + ":"
                + String.format(Locale.getDefault(), "%1$02d", m);
    }

    private int getHour(String time) {
        return Integer.parseInt(time.split(":")[0]);
    }

    private int getMinute(String time) {
        return Integer.parseInt(time.split(":")[1]);
    }

    private void showSchedule(ArrayList<ScheduleModel> list) {
        tvMorningMonday.setText(list.get(0).getTimeMorning());
        tvMorningTuesday.setText(list.get(1).getTimeMorning());
        tvMorningWednesday.setText(list.get(2).getTimeMorning());
        tvMorningThursday.setText(list.get(3).getTimeMorning());
        tvMorningFriday.setText(list.get(4).getTimeMorning());
        tvMorningSaturday.setText(list.get(5).getTimeMorning());
        tvMorningSunday.setText(list.get(6).getTimeMorning());

        tvEveningMonday.setText(list.get(0).getTimeEvening());
        tvEveningTuesday.setText(list.get(1).getTimeEvening());
        tvEveningWednesday.setText(list.get(2).getTimeEvening());
        tvEveningThursday.setText(list.get(3).getTimeEvening());
        tvEveningFriday.setText(list.get(4).getTimeEvening());
        tvEveningSaturday.setText(list.get(5).getTimeEvening());
        tvEveningSunday.setText(list.get(6).getTimeEvening());
    }
}
