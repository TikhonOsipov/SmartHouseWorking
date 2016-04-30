package com.tixon.smarthouseworking;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.tixon.smarthouseworking.database.HelperFactory;
import com.tixon.smarthouseworking.model.ArduinoHistory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button bOpen, bClose;
    Button bSetTime, bGetTime;
    RecyclerView recyclerViewHistory;
    HistoryAdapter adapter;
    String macAddress = "98:D3:32:30:45:A6";
    BluetoothSocket socket;
    Handler handler;
    StringBuilder sb = new StringBuilder();

    private ArrayList<ArduinoHistory> history = new ArrayList<>();

    private static final int REQUEST_CODE_BLUETOOTH = 0;
    private static final int REQUEST_CODE_SCHEDULE = 1;

    private boolean connected = false;
    ConnectionTask connectionTask = new ConnectionTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bConnect = (Button) findViewById(R.id.buttonConnect);
        bOpen = (Button) findViewById(R.id.buttonOpen);
        bClose = (Button) findViewById(R.id.buttonClose);

        bSetTime = (Button) findViewById(R.id.buttonSetTime);
        bGetTime = (Button) findViewById(R.id.buttonGetTime);

        recyclerViewHistory = (RecyclerView) findViewById(R.id.historyRecyclerView);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter = new HistoryAdapter(history);
        recyclerViewHistory.setAdapter(adapter);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("\r\n");
                        if (endOfLineIndex > 0) {
                            String sbPrint = sb.substring(0, endOfLineIndex); //extract string
                            sb.delete(0, sb.length());
                            //view.showCurtainState(sbPrint);
                            Log.d("myLogs", "message from Arduino: " + sbPrint);
                            history.add(0, new ArduinoHistory(sbPrint, System.currentTimeMillis()));
                            adapter.notifyDataSetChanged();
                        }
                        break;

                    default: break;
                }
            }
        };

        bOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //turn light on
                try {
                    OutputStream outStream = socket.getOutputStream();
                    int value = 1;
                    outStream.write(value);
                    Log.d("myLogs", "turned on / opening");
                } catch (Exception e) {
                    Log.e("myLogs", "couldn't turn light on: " + e.toString());
                    e.printStackTrace();
                }
            }
        });

        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //turn light off
                try {
                    OutputStream outStream = socket.getOutputStream();
                    int value = 2;
                    outStream.write(value);
                    Log.d("myLogs", "turned off / closing");
                } catch (Exception e) {
                    Log.e("myLogs", "couldn't turn light on: " + e.toString());
                    e.printStackTrace();
                }
            }
        });

        // Time

        bSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set time on Arduino clock
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                String time = getString(R.string.time_format, c);

                try {
                    OutputStream outStream = socket.getOutputStream();
                    int value = 6;
                    outStream.write(value);
                    outStream.write(time.getBytes());
                    Log.d("myLogs", "sent time to Arduino");
                } catch (IOException e) {
                    Log.e("myLogs", "error sending time to Arduino: " + e.toString());
                    e.printStackTrace();
                }
            }
        });

        bGetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get info from Arduino - curtain state and
                try {
                    OutputStream outStream = socket.getOutputStream();
                    int value = 7;
                    outStream.write(value);
                    Log.d("myLogs", "sent request for time to Arduino");
                } catch (IOException e) {
                    Log.e("myLogs", "error sending request for time to Arduino: " + e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menuMainConnect:
                connectionTask.execute();
                break;
            case R.id.menuMainOpenSchedule:
                //startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
                Intent openScheduleIntent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivityForResult(openScheduleIntent, REQUEST_CODE_SCHEDULE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_CODE_SCHEDULE:
                if(resultCode == RESULT_OK) {
                    try {
                        OutputStream outStream = socket.getOutputStream();
                        int value = 5;
                        outStream.write(value);
                        try {
                            String s = HelperFactory.getHelper().getScheduleDAO().getScheduleStringFormat();
                            outStream.write(s.getBytes());
                            Log.d("myLogs", "Schedule string format (debug): " + s);
                        } catch (SQLException sqlException) {
                            Log.e("myLogs", "error to get schedule in string format: " + sqlException.toString());
                            history.add(0, new ArduinoHistory("error: " + sqlException.toString(), System.currentTimeMillis()));
                            adapter.notifyDataSetChanged();
                            sqlException.printStackTrace();
                        }
                    } catch (IOException e) {
                        Log.e("myLogs", "error send time for week: " + e.toString());
                        history.add(0, new ArduinoHistory("error: " + e.toString(), System.currentTimeMillis()));
                        adapter.notifyDataSetChanged();
                        e.printStackTrace();
                    }
                }
                break;
            default: break;
        }
    }

    // Tasks

    private class ConnectionTask extends AsyncTask<Void, Void, Void> {
        private BluetoothAdapter adapter;

        @Override
        protected Void doInBackground(Void... params) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_CODE_BLUETOOTH);
            adapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = adapter.getRemoteDevice(macAddress);
            try {
                if(!connected) {
                    Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                    socket = (BluetoothSocket) m.invoke(device, 1);
                    socket.connect();
                    connected = true;
                }
            } catch (Exception e) {
                connected = false;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ReadTask readTask = new ReadTask();
            if(connected) {
                Log.d("myLogs", "Connected");
                history.add(0, new ArduinoHistory("Connected", System.currentTimeMillis()));
                readTask.execute();
            } else {
                Log.d("myLogs", "Disconnected");
                history.add(0, new ArduinoHistory("Disconnected", System.currentTimeMillis()));
            }
            super.onPostExecute(aVoid);
        }
    }

    private class ReadTask extends AsyncTask<Void, Void, Void> {
        private BluetoothSocket receivingSocket;
        private InputStream inStream;
        private OutputStream outStream;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            receivingSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = receivingSocket.getInputStream();
                tmpOut = receivingSocket.getOutputStream();
            } catch (Exception e) {
                Log.e("myLogs", "ReadTask onPreExecute error: " + e.toString());
                e.printStackTrace();
            }

            inStream = tmpIn;
            outStream = tmpOut;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("myLogs", "ReadTask doInBackground started");
            byte[] buffer = new byte[1024];
            int bytes;

            while(true) {
                try {
                    bytes = inStream.read(buffer);
                    handler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                } catch (Exception e) {
                    Log.e("myLogs", "error in ReceivingDataTask while cycle: " + e.toString());
                    e.printStackTrace();
                    break;
                }
            }
            return null;
        }
    }
}
