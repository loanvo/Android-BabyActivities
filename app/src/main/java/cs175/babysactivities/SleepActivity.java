package cs175.babysactivities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SleepActivity extends AppCompatActivity implements View.OnClickListener{
    DBHelper dbHelper;
    ActivityData data;
    //create the feedingActivity for code reuseable
    FeedingActivity feedingActivity;

    private long continued;
    private long time;
    private boolean started_before =false;
    private boolean just_started = false;
    Button sleepButton;
    TextView nameView;
    private String name;

    ListView sleepList;
    private long start;
    Handler handler;
    TextView timeView;

    ActivityLog activityLog;
    private String timeString;
    private String logTime;
    private String logDate;
    private String current;
    private String log;
    List<String> mLogs;
    List<ActivityLog> sleepLogs;
    List<String> todayLogs;
    List<String> previousLogs;
    private ArrayAdapter<String> today_arrayAdapter;
    private ArrayAdapter<String> previoud_arrayAdapter;
    private LinearLayout layout;
    private LinearLayout layout1;

    private Button playMusic;
    boolean played = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        sleepButton = (Button) findViewById(R.id.start_sleep);
        nameView = (TextView) findViewById(R.id.name_view);
        timeView = (TextView) findViewById(R.id.sleep_timer_view) ;
        TextView title = (TextView) findViewById(R.id.title);
        sleepLogs = new ArrayList<>();

        handler = new Handler();
        dbHelper = new DBHelper(this);
        data = new ActivityData();
        feedingActivity = new FeedingActivity();
        activityLog = new ActivityLog();
        mLogs = new ArrayList<>();

        sleepLogs = dbHelper.getAllLog();
        name = dbHelper.getBabyName();
        nameView.setText(name);
        try {
            setLogView(sleepLogs);
        } catch (NullPointerException e){
            title.setText("You have no activity");
        }
        data = dbHelper.getStatus();
        if(data.getStartType() != null) {
            if (data.getStartType().equals("sleep")) {
                continued = Long.parseLong(data.getStart());
                started_before = true;
                //feedingActivity.setStopButton(sleepButton);
                sleepButton.setBackgroundColor(Color.RED);
                sleepButton.setText("stop");
                clockRunning();

            }else{
                continued =0;
                started_before =false;
                time =0;
                sleepButton.setBackgroundColor(Color.GREEN);
                sleepButton.setText("start");
                sleepButton.setClickable(true);
            }
        }else{
            time =0;
            continued =0;
            started_before =false;
            sleepButton.setBackgroundColor(Color.GREEN);
            sleepButton.setText("start");
            sleepButton.setClickable(true);
        }

        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(just_started == false){
                    sleepButton.setBackgroundColor(Color.RED);
                    sleepButton.setText("stop");
                    clockRunning();
                    data.setStartType("sleep");
                    data.setStart(String.valueOf(start));
                    dbHelper.insertStatus(data, name);
                    just_started = true;
                }else {
                    sleepButton.setClickable(true);
                    sleepButton.setBackgroundColor(Color.GREEN);
                    sleepButton.setText("start");
                    stopSleep();
                    dbHelper.removeStatus("sleep");
                    just_started = false;
                    started_before = false;
                }

            }
        });

        playMusic = (Button) findViewById(R.id.music_button);
        playMusic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(played == false){
            playMusic.setBackgroundColor(Color.RED);
            playMusic.setText("stop music");
            startService(new Intent(this, MyService.class));
            played = true;
        }else{
            playMusic.setBackgroundColor(Color.parseColor("#33b5e5"));
            playMusic.setText("play music");
            stopService(new Intent(this, MyService.class));
            played = false;
        }
    }

    public void clockRunning(){
        if(started_before==true) {
            start = continued;
        }else{
            start = SystemClock.uptimeMillis();
        }
        handler.postDelayed(runnable, 0);
    }

    public void stopSleep(){

        data.setSleepTime(time);
        timeString = activityLog.formatTimeView(time);

        current = activityLog.getCurrentTime();
        logTime = activityLog.splitTime(current);
        logDate = activityLog.splitDate(current);
        log = "Slept for " + timeString + " at " + logTime;

        activityLog.setName(name);
        activityLog.setLog(log);
        activityLog.setLogDate(logDate);
        dbHelper.insetLog(activityLog);

        sleepLogs.add(activityLog);
        setLogView(sleepLogs);
        handler.removeCallbacks(runnable);
        time = 0;
    }
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time = SystemClock.uptimeMillis() - start;
            feedingActivity.setTime(time, timeView);
            handler.postDelayed(this, 0);
        }
    };

    public void setLogView(List<ActivityLog> logs){
        String date;
        String type;
        String current = activityLog.getCurrentTime();
        String currentdate = activityLog.splitDate(current);
        ActivityLog log = new ActivityLog();

        //List view of today logs
        layout = (LinearLayout) findViewById(R.id.today_logs);
        TextView today = (TextView) layout.findViewById(R.id.date_view);
        ListView todayLog = (ListView) layout.findViewById(R.id.log_view);

        //List view of previous days logs
        layout1 = (LinearLayout) findViewById(R.id.previous_logs);
        TextView previous = (TextView) layout1.findViewById(R.id.date_view);
        ListView previousLog = (ListView) layout1.findViewById(R.id.log_view);

        todayLogs = new ArrayList<>();
        previousLogs = new ArrayList<>();

        for(int i =0; i<logs.size(); i++){
            log = logs.get(i);
            type = logs.get(i).getLog();
            if(type.startsWith("Slept")){
                date = log.getLogDate();
                if (date.equals(currentdate)) {
                    today.setVisibility(today.VISIBLE);
                    today.setText("Today Activites");
                    todayLogs.add(log.getLog());
                } else {
                    previous.setVisibility(previous.VISIBLE);
                    previous.setText("Previous Days Activities");
                    previousLogs.add(log.getLog() + " on " + log.getLogDate());
                }
            }
        }
        // adapter for today listview
        today_arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todayLogs);
        todayLog.setAdapter(today_arrayAdapter);
        todayLog.setTextFilterEnabled(true);

        // adapter for previous days listview
        previoud_arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, previousLogs);
        previousLog.setAdapter(previoud_arrayAdapter);
        previousLog.setTextFilterEnabled(true);

    }



}
