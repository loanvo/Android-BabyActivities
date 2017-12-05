package cs175.babysactivities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import android.os.Handler;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.LogRecord;

public class FeedingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    TextView timeView;
    TextView nameView;
    Switch bottleSwitch;
    Switch leftSwitch;
    Switch rightSwitch;
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
    private String current;
    private long stopTime;
    LinkedList<String> mLogs;
    long continued = 0;
    boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeding);

        data = new ActivityData();
        dataList = new ArrayList<>();
        mLogs = new LinkedList<>();
        dbHelper = new DBHelper(this);
        name = dbHelper.getBabyName();
        handler = new Handler();
        timeView = (TextView) findViewById(R.id.feed_timer_view);
        nameView = (TextView) findViewById(R.id.name_view);
        bottleSwitch = (Switch) findViewById(R.id.switch_bottle);
        quantityEdit = (EditText) findViewById(R.id.qantity_edit);
        leftSwitch = (Switch) findViewById(R.id.switch_left);
        rightSwitch = (Switch) findViewById(R.id.switch_right);
        feedingLog = (ListView) findViewById(R.id.feeding_logs);

        nameView.setText(name);
        bottleSwitch.setOnCheckedChangeListener(this);
        leftSwitch.setOnCheckedChangeListener(this);
        rightSwitch.setOnCheckedChangeListener(this);

        data = dbHelper.getStatus();
        if(data.getStartType() != null) {
            if (data.getStartType().equals("bottle")) {
                continued = Long.parseLong(data.getStart());
                started = true;
                //setTime(cont);
                bottleSwitch.setChecked(true);
                // leftSwitch.setClickable(false);
                // rightSwitch.setClickable(false);
            } else if (data.getStartType().equals("left")) {
                continued = Long.parseLong(data.getStart());
                started = true;
                leftSwitch.setChecked(true);
            } else if (data.getStartType().equals("right")) {
                continued = Long.parseLong(data.getStart());
                rightSwitch.setChecked(true);
                started = true;
            } else {
                continued = 0;
                started = false;
            }
        }else{
            continued =0;
            started =false;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_bottle:
                if (bottleSwitch.isChecked()) {

                    //get the real time when start feeding
                    current = getCurrentTime();
                    clockRunning();

                    leftSwitch.setClickable(false);
                    rightSwitch.setClickable(false);
                    if(data.getStartType() != null && data.getStartType().equals("bottle")){
                        dbHelper.removeStatus(data.getStartType());
                    }
                    data.setStartType("bottle");
                    data.setStart(String.valueOf(start));
                    dbHelper.insertStatus(data,name);
                } else {
                    leftSwitch.setClickable(true);
                    bottleSwitch.setClickable(true);
                    rightSwitch.setClickable(true);

                    stopTime = SystemClock.uptimeMillis();
                    data.setStop(String.valueOf(stopTime));
                    data.setStopType("bottle");
                    dbHelper.updateStatus("bottle",data, name);

                    String quan = quantityEdit.getText().toString();
                    if (quan.isEmpty()) {
                        data.setQuanity(0);
                    } else data.setQuanity(Integer.parseInt(quan));
                    data.setBottleTime(time);
                    String timeString = formatTimeView(time);
                    String bottleLog = "Bottle fed " + quan + " oz for " + timeString + " at " + current;
                    mLogs.addFirst(bottleLog);
                    dbHelper.insertBottleFeedTime(data, name);
                   // dataList = dbHelper.getBottleFeed();
                    setLogView();
                    handler.removeCallbacks(runnable);
                    time = 0;
                }
                break;
            case R.id.switch_left:
                if (leftSwitch.isChecked()) {
                    //get the real time when start feeding
                    if(data.getStartType() != null &&data.getStartType().equals("left")){
                        dbHelper.removeStatus(data.getStartType());
                    }
                    data.setStartType("left");
                    data.setStart(String.valueOf(start));
                    dbHelper.insertStatus(data,name);

                    current = getCurrentTime();
                    clockRunning();
                    bottleSwitch.setClickable(false);
                    rightSwitch.setClickable(false);
                } else {

                    leftSwitch.setClickable(true);
                    bottleSwitch.setClickable(true);
                    rightSwitch.setClickable(true);

                    stopTime = SystemClock.uptimeMillis();
                    data.setStop(String.valueOf(stopTime));
                    data.setStopType("left");
                    dbHelper.updateStatus("left",data, name);

                    data.setLeftTime(time);
                    data.setRightTime(0);
                    dbHelper.insertBreastFeedTime(data, name);
                    //dataList = dbHelper.getBreastFeed();
                    String timeString = formatTimeView(time);
                    String leftLog = "Left fed for " + timeString + " at " + current;
                    mLogs.addFirst(leftLog);
                    setLogView();
                    handler.removeCallbacks(runnable);
                    time = 0;
                }
                break;
            case R.id.switch_right:
                if (rightSwitch.isChecked()) {
                    //get the real time when start feeding
                    current = getCurrentTime();
                    if(data.getStartType() != null &&data.getStartType().equals("right")){
                        dbHelper.removeStatus(data.getStartType());
                    }
                    data.setStartType("right");
                    data.setStart(String.valueOf(start));
                    dbHelper.insertStatus(data,name);
                    clockRunning();
                    bottleSwitch.setClickable(false);
                    leftSwitch.setClickable(false);
                } else {

                    leftSwitch.setClickable(true);
                    bottleSwitch.setClickable(true);
                    rightSwitch.setClickable(true);

                    stopTime = SystemClock.uptimeMillis();
                    data.setStop(String.valueOf(stopTime));
                    data.setStopType("right");
                    dbHelper.updateStatus("right", data, name);

                    data.setRightTime(time);
                    data.setLeftTime(0);
                    dbHelper.insertBreastFeedTime(data, name);
                    //dataList = dbHelper.getBreastFeed();
                    String timeString = formatTimeView(time);
                    String leftLog = "Right fed for " + timeString + " at " + current;
                    mLogs.addFirst(leftLog);
                    setLogView();
                    handler.removeCallbacks(runnable);
                    time = 0;
                }
                break;
        }

    }



    public void clockRunning(){
        if(started==true) {
            start = continued;
        }else{
            start = SystemClock.uptimeMillis();
        }
      handler.postDelayed(runnable, 0);
    }
    public String getCurrentTime(){
        String current = "";
        DateTime dateTime = new DateTime();
        current= String.valueOf(dateTime.getHourOfDay()) + ":"
                + String.valueOf(dateTime.getMinuteOfHour());
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
