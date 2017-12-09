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
import android.widget.LinearLayout;
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

    long continued = 0;
    boolean started_before = false;
    boolean just_started = false;

    List<String> mLogs;
    ActivityLog activityLog;
    List<ActivityLog> allLogs;
    List<String> dates;
    private String current;
    private String logTime;
    private String logDate;
    private String timeString;
    private String log;
    private LinearLayout layout;
    private LinearLayout layout1;
    List<String> todayLogs;
    List<String> previousLogs;
    private ArrayAdapter<String> today_arrayAdapter;
    private ArrayAdapter<String> previoud_arrayAdapter;

    private boolean addedSupply = false;
    private Supplies supplies;
    private int leftFormula;
    private String dateSupply;
    private TextView formularView;
    final static int POWDER_PER_OZ = 4;
    private int quan;
    private int amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeding);

        data = new ActivityData();
        dataList = new ArrayList<>();
        mLogs = new ArrayList<>();
        allLogs = new ArrayList<>();
        dates = new ArrayList<>();

        supplies = new Supplies();
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

        nameView.setText(name);

       allLogs = dbHelper.getAllLog();
       if(allLogs != null) {
           setLogView(allLogs);
       }

        supplies = getLeftOver();
        formularView = (TextView) findViewById(R.id.formula_view);
        formularView.setText(String.valueOf(supplies.getFormula()));

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
                    amount = Integer.parseInt(quantityEdit.getText().toString());
                    quan = amount *POWDER_PER_OZ;
                    if (addedSupply == true){
                        supplies = getLeftOver();
                        leftFormula = supplies.getFormula();
                        dbHelper.updateSupply(leftFormula - quan, supplies.getDiaper(), supplies.getDate());
                        formularView.setText(String.valueOf(leftFormula-quan));
                    }
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
    public Supplies getLeftOver(){
        //implement supply inventory
        Supplies supplies = dbHelper.getSupplies();
        if(supplies != null){
            addedSupply = true;
        }
        return supplies;
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
    }

    public void setStopButton(Button button){
        button.setTag(0);
        button.setText("stop");
        button.setBackgroundColor(Color.RED);
        just_started = true;
    }

    public void stopBottle(){
        handler.removeCallbacks(runnable);
        //String quan = quantityEdit.getText().toString();
        if (amount == 0) {
            data.setQuanity(0);
        } else data.setQuanity(amount);
        data.setBottleTime(time);
        timeString = activityLog.formatTimeView(time);

        current = activityLog.getCurrentTime();
        logTime = activityLog.splitTime(current);
        logDate = activityLog.splitDate(current);
        log = "Bottle fed " + amount + " oz for " + timeString + " at " + logTime;

        activityLog.setName(name);
        activityLog.setLog(log);
        activityLog.setTime(logTime);
        activityLog.setLogDate(logDate);
        dbHelper.insetLog(activityLog);

        allLogs.add(activityLog);
        setLogView(allLogs);

        time = 0;
        setTime(time, timeView);
    }

    public void stopLeft(){
        handler.removeCallbacks(runnable);
        data.setLeftTime(time);
        timeString = activityLog.formatTimeView(time);

        current = activityLog.getCurrentTime();
        logTime = activityLog.splitTime(current);
        logDate = activityLog.splitDate(current);
        log = "Left fed for " + timeString + " at " + logTime;

        activityLog.setName(name);
        activityLog.setLog(log);
        activityLog.setTime(logTime);
        activityLog.setLogDate(logDate);
        dbHelper.insetLog(activityLog);

        allLogs.add(activityLog);
        setLogView(allLogs);


        time = 0;
        setTime(time, timeView);
    }

    public void stopRight(){
        handler.removeCallbacks(runnable);
        data.setRightTime(time);
        timeString = activityLog.formatTimeView(time);

        current = activityLog.getCurrentTime();
        logTime = activityLog.splitTime(current);
        logDate = activityLog.splitDate(current);
        log = "Right fed for " + timeString + " at " + logTime;

        activityLog.setName(name);
        activityLog.setLog(log);
        activityLog.setTime(logTime);
        activityLog.setLogDate(logDate);
        dbHelper.insetLog(activityLog);

        allLogs.add(activityLog);
        setLogView(allLogs);
        time = 0;
        setTime(time, timeView);
    }


    public void clockRunning(){
        if(started_before==true) {
            start = continued;
        }else{
            start = SystemClock.uptimeMillis();
        }
      handler.postDelayed(runnable, 0);
    }

    public void setLogView(List<ActivityLog> logs){
        String date;
        String type;
        String current = activityLog.getCurrentTime();
        String currentdate = activityLog.splitDate(current);

        //List view of today logs
        layout = (LinearLayout) findViewById(R.id.today_logs);
        TextView today = (TextView) layout.findViewById(R.id.date_view);
        ListView todayLog = (ListView) layout.findViewById(R.id.log_view);


        //List view of previous days logs
        layout1 = (LinearLayout) findViewById(R.id.previous_logs);
        TextView previous = (TextView) layout1.findViewById(R.id.date_view);
        ListView previousLog = (ListView) layout1.findViewById(R.id.log_view);


        ActivityLog log = new ActivityLog();
        todayLogs = new ArrayList<>();
        previousLogs = new ArrayList<>();

        for(int i =0; i<logs.size(); i++){
            log = logs.get(i);
            type = logs.get(i).getLog();
            if(type.startsWith("Right") || type.startsWith("Left") || type.startsWith("Bottle")){
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
        today_arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todayLogs);
        todayLog.setAdapter(today_arrayAdapter);
        todayLog.setTextFilterEnabled(true);

        previoud_arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, previousLogs);
        previousLog.setAdapter(previoud_arrayAdapter);
        previousLog.setTextFilterEnabled(true);
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time = Math.abs(SystemClock.uptimeMillis() - start);
            setTime(time, timeView);
            handler.postDelayed(this, 0);
        }
    };
    public void setTime(long time, TextView timeView){
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
