package cs175.babysactivities;

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
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WalkActivity extends AppCompatActivity {
    DBHelper dbHelper;
    Handler handler;

    private Button playMusic;
    private TextView nameView;
    private TextView timeView;
    private TextView stepView;
    private Button startWalk;

    private List<ActivityLog> walkLogs;
    ActivityLog activityLog;
    ActivityData data;
    FeedingActivity feedingActivity;

    private LinearLayout layout;
    List<String> todayLogs;
    List<String> previousLogs;
    private ArrayAdapter<String> today_arrayAdapter;
    private ArrayAdapter<String> previoud_arrayAdapter;

    private String name;
    private long continued;
    private long time;
    private boolean started_before =false;
    private boolean just_started = false;
    private long start;

    private String timeString;
    private String logTime;
    private String logDate;
    private String current;
    private String log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);

        dbHelper = new DBHelper(this);
        handler = new Handler();

        playMusic = (Button) findViewById(R.id.music_button);
        nameView = (TextView) findViewById(R.id.name_view);
        timeView = (TextView) findViewById(R.id.walk_timer_view);
        stepView = (TextView) findViewById(R.id.step_view);
        startWalk = (Button) findViewById(R.id.start_walk);

        walkLogs = new ArrayList<>();
        activityLog = new ActivityLog();
        data = new ActivityData();
        feedingActivity = new FeedingActivity();

        name = dbHelper.getBabyName();
        nameView.setText(name);

        walkLogs = dbHelper.getAllLog();
        setLogView(walkLogs);

        data = dbHelper.getStatus();
        if(data.getStartType() != null) {
            if (data.getStartType().equals("walk")) {
                continued = Long.parseLong(data.getStart());
                started_before = true;
                //feedingActivity.setStopButton(sleepButton);
                startWalk.setBackgroundColor(Color.RED);
                startWalk.setText("stop");
                clockRunning();

            }else{
                continued =0;
                started_before =false;
                time =0;
                startWalk.setBackgroundColor(Color.GREEN);
                startWalk.setText("start");
                startWalk.setClickable(true);
            }
        }else{
            time =0;
            continued =0;
            started_before =false;
            startWalk.setBackgroundColor(Color.GREEN);
            startWalk.setText("start");
            startWalk.setClickable(true);
        }

        startWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(just_started == false){
                    startWalk.setBackgroundColor(Color.RED);
                    startWalk.setText("stop");
                    clockRunning();
                    data.setStartType("walk");
                    data.setStart(String.valueOf(start));
                    dbHelper.insertStatus(data, name);
                    just_started = true;
                }else {
                    startWalk.setClickable(true);
                    startWalk.setBackgroundColor(Color.GREEN);
                    startWalk.setText("start");
                    stopWalk();
                    dbHelper.removeStatus("walk");
                    just_started = false;
                }

            }
        });

    }

    public void stopWalk(){

        data.setWalkTime(time);
        timeString = activityLog.formatTimeView(time);

        current = activityLog.getCurrentTime();
        logTime = activityLog.splitTime(current);
        logDate = activityLog.splitDate(current);
        log = "Walked for " + timeString + " at " + logTime;

        activityLog.setName(name);
        activityLog.setLog(log);
        activityLog.setLogDate(logDate);
        dbHelper.insetLog(activityLog);

        walkLogs.add(activityLog);
        setLogView(walkLogs);
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

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time = SystemClock.uptimeMillis() - start;
            feedingActivity.setTime(time, timeView);
            handler.postDelayed(this, 0);
        }
    };

    public void setLogView(List<ActivityLog> logs) {
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
        layout = (LinearLayout) findViewById(R.id.previous_logs);
        TextView previous = (TextView) layout.findViewById(R.id.date_view);
        ListView previousLog = (ListView) layout.findViewById(R.id.log_view);

        todayLogs = new ArrayList<>();
        previousLogs = new ArrayList<>();

        for (int i = 0; i < logs.size(); i++) {
            log = logs.get(i);
            type = logs.get(i).getLog();
            if (type.startsWith("Walked")) {
                date = log.getLogDate();
                if (date.equals(currentdate)) {
                    today.setText("Today Activites");
                    todayLogs.add(log.getLog());
                } else {
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
