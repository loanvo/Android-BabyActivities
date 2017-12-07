package cs175.babysactivities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DiaperActivity extends AppCompatActivity implements View.OnClickListener{
    private CheckBox pooBox;
    private CheckBox peeBox;
    private CheckBox bothBox;
    private ListView diaperList;

    private TextView nameView;
    private String name;

    DBHelper dbHelper;
    List<ActivityLog> diaperLogs;
    private String current;
    ActivityLog activityLog;
    private String log;
    private String logTime;
    private String logDate;
    List<String> todayLogs;
    List<String> previousLogs;
    private ArrayAdapter<String> today_arrayAdapter;
    private ArrayAdapter<String> previoud_arrayAdapter;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diaper);

        dbHelper = new DBHelper(this);
        diaperLogs = new ArrayList<>();
        activityLog = new ActivityLog();
        nameView = (TextView) findViewById(R.id.name_view);
        pooBox = (CheckBox) findViewById(R.id.poo);
        peeBox = (CheckBox) findViewById(R.id.pee);
        bothBox = (CheckBox) findViewById(R.id.both);

        name = dbHelper.getBabyName();
        nameView.setText(name);
        diaperLogs = dbHelper.getAllLog();
        if(diaperLogs != null) {
            setLogView(diaperLogs);
        }
        pooBox.setOnClickListener(this);
        peeBox.setOnClickListener(this);
        bothBox.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.poo:
                if(pooBox.isChecked()){
                    dbHelper.insertDiaper("poo", name);

                    current = activityLog.getCurrentTime();
                    logTime = activityLog.splitTime(current);
                    logDate = activityLog.splitDate(current);
                    log = "Poo-poo at " + logTime;

                    activityLog.setName(name);
                    activityLog.setLog(log);
                    activityLog.setLogDate(logDate);
                    dbHelper.insetLog(activityLog);

                    diaperLogs.add(activityLog);
                    setLogView(diaperLogs);
                    pooBox.setChecked(false);
                    }
                break;
            case R.id.pee:
                if(peeBox.isChecked()){
                    dbHelper.insertDiaper("pee", name);

                    current = activityLog.getCurrentTime();
                    logTime = activityLog.splitTime(current);
                    logDate = activityLog.splitDate(current);
                    log = "Pee-pee at " + logTime;

                    activityLog.setName(name);
                    activityLog.setLog(log);
                    activityLog.setLogDate(logDate);
                    dbHelper.insetLog(activityLog);

                    diaperLogs.add(activityLog);
                    setLogView(diaperLogs);
                    peeBox.setChecked(false);
                }
                break;
            case R.id.both:
                if(bothBox.isChecked()){
                    dbHelper.insertDiaper("both", name);

                    current = activityLog.getCurrentTime();
                    logTime = activityLog.splitTime(current);
                    logDate = activityLog.splitDate(current);
                    log = "Pee-pee and Poo-poo at " + logTime;

                    activityLog.setName(name);
                    activityLog.setLog(log);
                    activityLog.setLogDate(logDate);
                    dbHelper.insetLog(activityLog);

                    diaperLogs.add(activityLog);
                    setLogView(diaperLogs);
                    bothBox.setChecked(false);
                }
                break;
        }
    }

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
        today.setText("Today Activites");

        //List view of previous days logs
        layout = (LinearLayout) findViewById(R.id.previous_logs);
        TextView previous = (TextView) layout.findViewById(R.id.date_view);
        ListView previousLog = (ListView) layout.findViewById(R.id.log_view);
        previous.setText("Previous Days Activities");

        todayLogs = new ArrayList<>();
        previousLogs = new ArrayList<>();
        for(int i =0; i<logs.size(); i++){
            log = logs.get(i);
            type = logs.get(i).getLog();
            if(type.startsWith("Poo-poo") || type.startsWith("Pee-pee")){
                date = log.getLogDate();
                if (date.equals(currentdate)) {

                    todayLogs.add(log.getLog());
                } else {

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
}
