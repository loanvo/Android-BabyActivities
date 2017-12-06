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
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.LinkedList;

public class SleepActivity extends AppCompatActivity {
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
    LinkedList<String> sleepLogs;
    ListView sleepList;
    private long start;
    Handler handler;
    TextView timeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        sleepButton = (Button) findViewById(R.id.start_sleep);
        nameView = (TextView) findViewById(R.id.name_view);
        sleepList = (ListView) findViewById(R.id.sleepLogs);
        timeView = (TextView) findViewById(R.id.sleep_timer_view) ;
        sleepLogs = new LinkedList<>();

        handler = new Handler();
        dbHelper = new DBHelper(this);
        data = new ActivityData();
        feedingActivity = new FeedingActivity();
        sleepLogs = dbHelper.getAllLog();
        name = dbHelper.getBabyName();
        nameView.setText(name);
        setLogView(sleepLogs);
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
//                final int status = (Integer) v.getTag();
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
                }

            }
        });
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
        String current = "";
        DateTime dateTime = new DateTime();
        current = dateTime.toString(DateTimeFormat.shortDateTime());

        data.setSleepTime(time);
        String timeString = feedingActivity.formatTimeView(time);
        String log = "Slept for " + timeString + " at " + current;
        dbHelper.insetLog(log, name);
        //LinkedList<String> mlogs = new LinkedList<>();
        sleepLogs.addFirst(log);
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
    public void setLogView(LinkedList<String> logs){
        for(int i =0; i <logs.size(); i++){
            if(logs.get(i).startsWith("Slept")){
                sleepLogs.addLast(logs.get(i));
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sleepLogs);
        sleepList.setAdapter(arrayAdapter);
        sleepList.setTextFilterEnabled(true);
    }

}
