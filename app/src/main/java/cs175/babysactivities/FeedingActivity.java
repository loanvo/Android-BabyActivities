package cs175.babysactivities;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import android.os.Handler;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.LogRecord;

public class FeedingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    TextView timeView;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeding);

        data = new ActivityData();
        dataList = new ArrayList<>();
        dbHelper = new DBHelper(this);
        name = dbHelper.getBabyName();
        handler = new Handler();
        timeView = (TextView) findViewById(R.id.feed_timer_view);
        bottleSwitch = (Switch) findViewById(R.id.switch_bottle);
        quantityEdit = (EditText) findViewById(R.id.qantity_edit);
        leftSwitch = (Switch) findViewById(R.id.switch_left);
        rightSwitch = (Switch) findViewById(R.id.switch_right);
        feedingLog = (ListView) findViewById(R.id.feeding_logs);

        bottleSwitch.setOnCheckedChangeListener(this);
        leftSwitch.setOnCheckedChangeListener(this);
        rightSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_bottle:
                if (bottleSwitch.isChecked()) {
                    //get the real time when start feeding
                    current = getCurrentTime();
                    start = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
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
                    dbHelper.insertBottleFeedTime(data, name);
                    dataList = dbHelper.getBottleFeed();
                    setLogView(dataList, start);
                    handler.removeCallbacks(runnable);
                    time = 0;
                }
                break;
            case R.id.switch_left:
                if (leftSwitch.isChecked()) {
                    //get the real time when start feeding
                    current = getCurrentTime();
                    start = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    bottleSwitch.setClickable(false);
                    rightSwitch.setClickable(false);
                } else {

                    leftSwitch.setClickable(true);
                    bottleSwitch.setClickable(true);
                    rightSwitch.setClickable(true);

                    data.setLeftTime(time);
                    data.setRightTime(0);
                    dbHelper.insertBreastFeedTime(data, name);
                    dataList = dbHelper.getBreastFeed();
                    setLogView(dataList,start);
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

    public String getCurrentTime(){
        String current = "";
        DateTime dateTime = new DateTime();
        current= String.valueOf(dateTime.getHourOfDay()) + ":"
                + String.valueOf(dateTime.getMinuteOfHour());
        return current;
    }
    public void setLogView(ArrayList<ActivityData> dataList, long start){
        List<String> bottleLogs = new ArrayList<>();
        for(int i =0; i<dataList.size(); i++){
            long time = dataList.get(i).getBottleTime();
            int quantity = dataList.get(i).getQuanity();

            //convert time of feeding in millis to hh:mm:ss
            String timeView = formatTimeView(time);

            bottleLogs.add(" fed " + quantity + " oz formula milk for " + timeView + " at " + current );
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bottleLogs);
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


}
