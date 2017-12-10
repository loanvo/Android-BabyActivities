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
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SleepActivity extends AppCompatActivity{
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

    boolean played = false;
    LoginButton loginButton;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
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

        // set baby name view
        name = dbHelper.getBabyName();
        nameView.setText(name);

        //get all sleep activities
        sleepLogs = dbHelper.getAllLog();

        // catch exception when the log is empty
        // get started activity and show them to user
        try {
            setLogView(sleepLogs);
        } catch (NullPointerException e){
            title.setText("You have no activity");
        }
        // check to see which activity is going on
        data = dbHelper.getStatus();

        //check if the start button has been pressed before when the user go to the other activity
        if(data.getStartType() != null) {
            if (data.getStartType().equals("sleep")) {
                //get the time when the actitity is just starting
                continued = Long.parseLong(data.getStart());
                started_before = true;  //true when the activity has been press start button
                sleepButton.setBackgroundColor(Color.RED);
                sleepButton.setText("stop");
                // set the running clock
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
        // implementation for the sleep button
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(just_started == false){
                    sleepButton.setBackgroundColor(Color.RED);
                    sleepButton.setText("stop");
                    clockRunning();
                    data.setStartType("sleep");
                    data.setStart(String.valueOf(start));
                    // when it is pressed start the status is insert to the database
                    // with the start time and the type of activity, sleep in this case
                    dbHelper.insertStatus(data, name);
                    just_started = true;
                }else {
                    sleepButton.setClickable(true);
                    sleepButton.setBackgroundColor(Color.GREEN);
                    sleepButton.setText("start");
                    stopSleep();
                    dbHelper.removeStatus("sleep"); // remove the status sleep when the button stop is pressed
                    just_started = false;
                    started_before = false;
                }

            }
        });

        //implementation for facebook login
        loginButton = (LoginButton)findViewById(R.id.faceboo_login);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(SleepActivity.this, "Login Succeed \n" + loginResult.getAccessToken().getUserId()
                + "\n" + loginResult.getAccessToken().getToken(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(SleepActivity.this, "Login Failed" , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //Implementation for music play on the background
    // those 2 method will interact with MyService.java class to make
    // the music keep playing even when the user close the apps
    public void playMusic(View view){
        startService(new Intent(this, MyService.class));
    }
    public void stopMusic(View view){
        stopService(new Intent(this, MyService.class));
    }

    // implemetation for the running clock to indicate how long the activity has been started
    // by using Handler
    public void clockRunning(){
        if(started_before==true) {
            // if the activity has been started and user went to the other actitvity and came back
            // this will keep the clock runs continuously instead of start from 00:00:00
            start = continued;
        }else{
            start = SystemClock.uptimeMillis();
        }
        handler.postDelayed(runnable, 0);
    }
    // Runnable with Handler to keep clock running
    // we will get the time of activity has been running here
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time = SystemClock.uptimeMillis() - start;
            feedingActivity.setTime(time, timeView);
            handler.postDelayed(this, 0);
        }
    };
    // implementation for stop button
    public void stopSleep(){
        //the gotten time from runnable will store into the object ActivityData data
        data.setSleepTime(time);
        // format the time with helper function formatTime() to a string to display to user
        timeString = activityLog.formatTimeView(time);

        current = activityLog.getCurrentTime();     //get current date and time with helper function getcurrent time in ActivityLog.java
        logTime = activityLog.splitTime(current);   // get the  time only
        logDate = activityLog.splitDate(current);   // get date only
        log = "Slept for " + timeString + " at " + logTime; // format activity log for sleep activity

        activityLog.setName(name);
        activityLog.setLog(log);
        activityLog.setLogDate(logDate);
        dbHelper.insetLog(activityLog);     // insert the log detail into database

        sleepLogs = dbHelper.getAllLog();   //query the logs from database
        setLogView(sleepLogs);              // show the log to user
        handler.removeCallbacks(runnable);  // stop the clock
        time = 0;
    }

    // helper function to format how the log display to user in 2 lists
    // one for current day and one for previous days
    // the list will be invisible if there is no activity
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
