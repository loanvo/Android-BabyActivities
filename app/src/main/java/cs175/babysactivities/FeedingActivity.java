package cs175.babysactivities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.telecom.RemoteConnection;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import android.os.Handler;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.LogRecord;

public class FeedingActivity extends AppCompatActivity{

    TextView timeView;
    TextView nameView;
    Button bottleButton;
    Button leftButton;
    Button rightButton;
    EditText quantityEdit;
    ListView feedingLog;

    Handler handler;
    private long time;
    private long start;
    private int hours;
    private int minutes;
    private int seconds;
    DBHelper dbHelper;
    ActivityData data;
    String name;
    ArrayList<ActivityData> dataList;
    private long stopTime;
    LinkedList<String> mLogs;
    long continued = 0;
    boolean started_before = false;
    boolean just_started = false;
    ActivityLog activityLog;
    LinkedList<String> allLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeding);

        data = new ActivityData();
        dataList = new ArrayList<>();
        mLogs = new LinkedList<>();
        allLogs = new LinkedList<>();
        activityLog = new ActivityLog();
        dbHelper = new DBHelper(this);
        name = dbHelper.getBabyName();
        handler = new Handler();
        timeView = (TextView) findViewById(R.id.feed_timer_view);
        nameView = (TextView) findViewById(R.id.name_view);
        bottleButton = (Button) findViewById(R.id.switch_bottle);
        quantityEdit = (EditText) findViewById(R.id.qantity_edit);
        leftButton = (Button) findViewById(R.id.switch_left);
        rightButton = (Button) findViewById(R.id.switch_right);
        feedingLog = (ListView) findViewById(R.id.feeding_logs);

        nameView.setText(name);

        setStartButton(bottleButton);
        setStartButton(leftButton);
        setStartButton(rightButton);

        data = dbHelper.getStatus();
        if(data.getStartType() != null) {
            if (data.getStartType().equals("bottle")) {
                continued = Long.parseLong(data.getStart());
                started_before = true;
                setStopButton(bottleButton);
                clockRunning();
                rightButton.setClickable(false);
                leftButton.setClickable(false);

            } else if (data.getStartType().equals("left")) {
                continued = Long.parseLong(data.getStart());
                started_before = true;
                setStopButton(leftButton);
                clockRunning();
                bottleButton.setClickable(false);
                rightButton.setClickable(false);

            } else if (data.getStartType().equals("right")) {
                continued = Long.parseLong(data.getStart());
                started_before = true;
                setStopButton(rightButton);
                clockRunning();
                leftButton.setClickable(false);
                bottleButton.setClickable(false);

            }else{
                continued =0;
                started_before =false;
                setAllClickable();
                time =0;
            }
        }else{
            time =0;
            continued =0;
            started_before =false;
            setAllClickable();
        }

        bottleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if(just_started == false){
                    leftButton.setClickable(false);
                    rightButton.setClickable(false);
                    setStopButton(bottleButton);
                    clockRunning();
                    data.setStartType("bottle");
                    data.setStart(String.valueOf(start));
                    dbHelper.insertStatus(data, name);
                }else {
                    setAllClickable();
                    setStartButton(bottleButton);
                    stopBottle();
                    dbHelper.removeStatus("bottle");
                    //time = 0;
                }

            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if(just_started == false){
                    bottleButton.setClickable(false);
                   rightButton.setClickable(false);
                    setStopButton(leftButton);
                    clockRunning();
                    data.setStartType("left");
                    data.setStart(String.valueOf(start));
                    dbHelper.insertStatus(data, name);
                }else {
                    setAllClickable();
                    setStartButton(leftButton);
                    stopLeft();
                    dbHelper.removeStatus("left");
                }

            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if(just_started == false ){
                    leftButton.setClickable(false);
                    bottleButton.setClickable(false);
                    setStopButton(rightButton);
                    clockRunning();
                    data.setStartType("right");
                    data.setStart(String.valueOf(start));
                    dbHelper.insertStatus(data, name);
                }else {
                    setAllClickable();
                    setStartButton(rightButton);
                    stopRight();
                    dbHelper.removeStatus("right");
                }

            }
        });

    }
    public void setAllClickable(){
        bottleButton.setClickable(true);
        leftButton.setClickable(true);
        rightButton.setClickable(true);
    }

    public void setStartButton(Button button){
        button.setTag(1);
        button.setText("start");
        button.setBackgroundColor(Color.GREEN);
        just_started = false;
        button.setClickable(true);
    }

    public void setStopButton(Button button){
        button.setTag(0);
        button.setText("stop");
        button.setBackgroundColor(Color.RED);
        just_started = true;
        button.setClickable(true);
    }


    public void stopBottle(){
        String current = getCurrentTime();
        String quan = quantityEdit.getText().toString();
        if (quan.isEmpty()) {
            data.setQuanity(0);
        } else data.setQuanity(Integer.parseInt(quan));
        data.setBottleTime(time);
        String timeString = formatTimeView(time);
        String bottleLog = "Bottle fed " + quan + " oz for " + timeString + " at " + current;
        dbHelper.insetLog(bottleLog, name);
        mLogs.addFirst(bottleLog);
        setLogView();
        handler.removeCallbacks(runnable);
        time = 0;

    }

    public void stopLeft(){

        String current = getCurrentTime();
        data.setLeftTime(time);
        String timeString = formatTimeView(time);
        String log = "Left fed for " + timeString + " at " + current;
        dbHelper.insetLog(log, name);
        mLogs.addFirst(log);
        setLogView();
        handler.removeCallbacks(runnable);
        time = 0;
    }

    public void stopRight(){
        String current = getCurrentTime();
        data.setRightTime(time);
        String timeString = formatTimeView(time);
        String log = "Right fed for " + timeString + " at " + current;
        dbHelper.insetLog(log, name);
        mLogs.addFirst(log);
        setLogView();
        handler.removeCallbacks(runnable);
        time = 0;
    }


    public void clockRunning(){
        if(started_before==true) {
            start = continued;
        }else{
            start = SystemClock.uptimeMillis();
        }
      handler.postDelayed(runnable, 0);
    }
    public String getCurrentTime(){
        String current = "";
        DateTime dateTime = new DateTime();
        //current= String.valueOf(dateTime.getHourOfDay()) + ":"
        //        + String.valueOf(dateTime.getMinuteOfHour());
        current = dateTime.toString(DateTimeFormat.shortDateTime());
        return current;
    }
    public void setLogView(){

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mLogs);
        feedingLog.setAdapter(arrayAdapter);
        feedingLog.setTextFilterEnabled(true);

    }

    public String formatTimeView(long millis){
        String timeView = "";
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        int hours = minutes/60;
        int day = hours/24;
        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;
        if(day < 1){
            if(hours < 1){
                if(minutes < 1){
                    timeView = String.format("%02d", seconds) + " sec";
                }else{
                    timeView = String.format("%02d", minutes) + " min "
                            + String.format("%02d", seconds) + " sec";
                }
            }else{
               timeView = String.format("%02d", hours) + " hr "
                        + String.format("%02d", minutes) + " min "
                        + String.format("%02d", seconds) + " sec";
            }
        }else {
            timeView = "" + String.format("%02d", day) + " day "
                    + String.format("%02d", hours) + " hr "
                    + String.format("%02d", minutes) + " min "
                    + String.format("%02d", seconds) + " sec";
        }
        return timeView;
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time = SystemClock.uptimeMillis() - start;
            setTime(time);

 //           time = (int) (time % 1000);

            handler.postDelayed(this, 0);
        }
    };
    public void setTime(long time){
        seconds = (int) (time / 1000);
        minutes = seconds / 60;
        hours = minutes/60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;
        timeView.setText("" +  String.format("%02d", hours) + ":"
                + String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds));

    }
}
