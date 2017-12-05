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
    LinkedList<String> mLogs;
    IMyAidlInterface remoteService;
    RemoteConnection remoteConnection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeding);

        //Initialize the service
        remoteConnection = new RemoteConnection();
        Intent intent = new Intent();
        intent.setClassName("cs175.babysactivities", cs175.babysactivities.MyService.class.getName());
        if (!bindService(intent, remoteConnection, BIND_AUTO_CREATE)) {
            Toast.makeText(this, "Fail to bind the remote service ", Toast.LENGTH_LONG).show();
        }
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
    }
     class RemoteConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IMyAidlInterface.Stub.asInterface((IBinder) service);
            Toast.makeText(FeedingActivity.this, "Remote Service connected.", Toast.LENGTH_LONG).show();
        }

       @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteService = null;
            Toast.makeText(FeedingActivity.this, "Remote Service Disconnected", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(remoteConnection);
        remoteConnection = null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_bottle:
                if (bottleSwitch.isChecked()) {
                    //get the real time when start feeding
                    current = getCurrentTime();
                    try {
                        clockRunning();
                        throw new RemoteException();
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }

                    //start = SystemClock.uptimeMillis();
                    //handler.postDelayed(runnable, 0);
                    leftSwitch.setClickable(false);
                    rightSwitch.setClickable(false);
                } else {

                    leftSwitch.setClickable(true);
                    bottleSwitch.setClickable(true);
                    rightSwitch.setClickable(true);
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
                    current = getCurrentTime();
                    clockRunning();
                    bottleSwitch.setClickable(false);
                    rightSwitch.setClickable(false);
                } else {

                    leftSwitch.setClickable(true);
                    bottleSwitch.setClickable(true);
                    rightSwitch.setClickable(true);

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
                    clockRunning();
                    //start = SystemClock.uptimeMillis();
                    //handler.postDelayed(runnable, 0);
                    bottleSwitch.setClickable(false);
                    leftSwitch.setClickable(false);
                } else {

                    leftSwitch.setClickable(true);
                    bottleSwitch.setClickable(true);
                    rightSwitch.setClickable(true);

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
        /*
        if(bottleSwitch.isChecked() || leftSwitch.isChecked() || rightSwitch.isChecked()){
            if(bottleSwitch.isChecked()){
                leftSwitch.setClickable(false);
                rightSwitch.setClickable(false);
                handler.removeCallbacks(runnable);
            }else if(leftSwitch.isChecked()){
                bottleSwitch.setClickable(false);
                rightSwitch.setClickable(false);
                handler.removeCallbacks(runnable);
            }else if(rightSwitch.isChecked()){
                leftSwitch.setClickable(false);
                bottleSwitch.setClickable(false);
                handler.removeCallbacks(runnable);
            }
            start = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
        }else{
            leftSwitch.setClickable(true);
            bottleSwitch.setClickable(true);
            rightSwitch.setClickable(true);
            timeBuff += time;
            handler.removeCallbacks(runnable);
        }*/
    }
    public void clockRunning(){
        start = SystemClock.uptimeMillis();
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

            seconds = (int) (time / 1000);
            minutes = seconds / 60;
            hours = minutes/60;
            seconds = seconds % 60;
            minutes = minutes % 60;
            hours = hours % 24;
 //           time = (int) (time % 1000);
            timeView.setText("" +  String.format("%02d", hours) + ":"
                    + String.format("%02d", minutes) + ":"
                    + String.format("%02d", seconds));

            handler.postDelayed(this, 0);
        }
    };

    public void runningService(){
        try{
            clockRunning();
            setLogView();
            throw new RemoteException("no Remote Service found");
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

}
