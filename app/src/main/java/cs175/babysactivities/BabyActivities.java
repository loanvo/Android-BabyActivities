package cs175.babysactivities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.DateInterval;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class BabyActivities extends AppCompatActivity implements View.OnClickListener {
    private TextView mTextMessage;

    private Button feedButton;
    private Button diaperButton;
    private Button sleepButton;
    private Button walkButton;

    private TextView nameView;
    private TextView ageView;
    private ListView logList;

    DBHelper dbHelper;
    BabyProfile babyProfile;
    String today;
    FeedingActivity feedingActivity;
    List<ActivityLog> allLogs;
    ActivityLog activityLog;
    List<String> mLogs;
    private LinearLayout layout;
    private LinearLayout layout1;
    List<String> todayLogs;
    List<String> previousLogs;
    private ArrayAdapter<String> today_arrayAdapter;
    private ArrayAdapter<String> previoud_arrayAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_activities);

        dbHelper = new DBHelper(this);
        babyProfile = new BabyProfile();
        activityLog = new ActivityLog();

        mLogs = new ArrayList<>();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        babyProfile = dbHelper.getBabyInfo();
        nameView = (TextView) findViewById(R.id.name_view);
        nameView.setText(babyProfile.getName());

        String birthday = babyProfile.getDOB();
        ageView = (TextView) findViewById(R.id.age_view);
        ageView.setText(getAge(birthday));

        //display all logs
        allLogs = new ArrayList<>();
        allLogs = dbHelper.getAllLog();
        if (allLogs != null) {
            setLogView(allLogs);
        }
        nameView.setOnClickListener(this);
        ageView.setOnClickListener(this);
    }

    public void setLogView(List<ActivityLog> logs){
        String date;
        String current = activityLog.getCurrentTime();
        String currentdate = activityLog.splitDate(current);
        ActivityLog log = new ActivityLog();
        todayLogs = new ArrayList<>();
        previousLogs = new ArrayList<>();

        //List view of today logs
        layout = (LinearLayout) findViewById(R.id.today_logs);
        TextView today = (TextView) layout.findViewById(R.id.date_view);
        ListView todayLog = (ListView) layout.findViewById(R.id.log_view);

        //List view of previous days logs
        layout = (LinearLayout) findViewById(R.id.previous_logs);
        TextView previous = (TextView) layout.findViewById(R.id.date_view);
        ListView previousLog = (ListView) layout.findViewById(R.id.log_view);


        for(int i =0; i<logs.size(); i++) {
            log = logs.get(i);
            date = log.getLogDate();
            if (date.equals(currentdate)) {
                todayLogs.add(log.getLog());
                today.setText("Today Activites");

            } else {
                previousLogs.add(log.getLog() + " on " + log.getLogDate());
                previous.setText("Previous Days Activities");
            }
        }
        today_arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todayLogs);
        todayLog.setAdapter(today_arrayAdapter);
        todayLog.setTextFilterEnabled(true);

        previoud_arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, previousLogs);
        previousLog.setAdapter(previoud_arrayAdapter);
        previousLog.setTextFilterEnabled(true);
    }

    public String getAge(String birthday){
        String age ="";
        if(birthday != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
                LocalDate dt = formatter.parseLocalDate(birthday);
                int days = Days.daysBetween(dt, LocalDate.now()).getDays();
                int month = dt.getMonthOfYear();
                int months;
                if (month == 1) {
                    months = days / 28;
                } else if (month % 2 == 0) {
                    months = days / 30;
                } else {
                    months = days / 31;
                }
                int years = months / 12;
                months = months % 12;
                days = days % 365;
                if (years < 1) {
                    if (months < 1) {
                        age = "is " + days + " days old";
                    } else {
                        age = "is " + months + " months " + days + " days old";
                    }
                } else {
                    age = "is " + years + " years " + months + " months " + days + " days old";
                }
            } catch (IllegalArgumentException e) {
                age = "wrong format birthday";
            }
        }else{
            age = "not provide birthday";
        }
        return age;
    }

    @Override
    public void onClick(View v) {
        final Dialog dialog = new Dialog(BabyActivities.this);
        dialog.setContentView(R.layout.edit_profile);
        dialog.setTitle("Edit Baby Information");
        final EditText nameEdit = (EditText) dialog.findViewById(R.id.edit_name);
        final EditText birthdayEdit = (EditText) dialog.findViewById(R.id.edit_birthday);
        final EditText heightEdit = (EditText) dialog.findViewById(R.id.edit_height);
        final EditText weightEdit = (EditText) dialog.findViewById(R.id.edit_weight);
        final EditText headEdit = (EditText) dialog.findViewById(R.id.edit_head);
        Button saveButton = (Button) dialog.findViewById(R.id.register_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        nameEdit.setText(babyProfile.getName());
        birthdayEdit.setText(babyProfile.getDOB());
        weightEdit.setText(String.valueOf(babyProfile.getWeight()));
        heightEdit.setText(String.valueOf(babyProfile.getHeight()));
        headEdit.setText(String.valueOf(babyProfile.getHeight()));

        RegisterActivity activity = new RegisterActivity();
        activity.setCalendar(birthdayEdit, this);
        dialog.show();
        saveButton.setOnClickListener(new View.OnClickListener() {
            String name = "";
            String birth = "";
            double w = 0.0;
            double h = 0.0;
            double head = 0.0;
            @Override
            public void onClick(View v) {
                name = nameEdit.getText().toString();
                birth = birthdayEdit.getText().toString();
                w = Double.parseDouble(weightEdit.getText().toString());
                h = Double.parseDouble(heightEdit.getText().toString());
                head = Double.parseDouble(headEdit.getText().toString());
                saveProfile(name, birth, w, h, head);
                dialog.dismiss();
            }
        });
    }

    public void saveProfile(String n, String d, double w, double h, double head){
       babyProfile = dbHelper.getBabyInfo();
       /*if(n.equals(babyProfile.getName())){
           dbHelper.removeBabyProfile(n);
       }*/
       dbHelper.removeBabyProfile(babyProfile.getName());
       babyProfile.setName(n);
       babyProfile.setDOB(d);
       babyProfile.setWeight(w);
       babyProfile.setHeight(h);
       babyProfile.setHeadsize(head);

       dbHelper.createProfile(babyProfile);
       nameView.setText(n);
       ageView.setText(getAge(d));
    }


    public void startFeed(View v){
        Intent intent = new Intent(this, FeedingActivity.class);
        startActivity(intent);
    }

    public void getDiaper(View v){
        Intent intent = new Intent(this, DiaperActivity.class);
        startActivity(intent);
    }

    public void startSleep(View v){
        Intent intent = new Intent(this, SleepActivity.class);
        startActivity(intent);
    }

    public void startWalk(View v){
        Intent intent = new Intent(this, WalkActivity.class);
        startActivity(intent);
    }

    public void addSupply(View view){
        final Dialog dialog = new Dialog(BabyActivities.this);
        dialog.setContentView(R.layout.new_supply);
        dialog.setTitle("Add Supply");
        final EditText formulaEdit = (EditText) dialog.findViewById(R.id.formula_edit);
        final EditText diaperEdit = (EditText) dialog.findViewById(R.id.diaper_edit);

        Button saveButn = (Button) dialog.findViewById(R.id.save_btn);
        Button cancelButn = (Button) dialog.findViewById(R.id.cancel_btn);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
        saveButn.setOnClickListener(new View.OnClickListener() {
            int formula  = 0;
            int diaper = 0;

            @Override
            public void onClick(View v) {
                formula = Integer.parseInt(formulaEdit.getText().toString());
                diaper = Integer.parseInt(diaperEdit.getText().toString());

                dbHelper.insertSupply(formula, diaper);
                dialog.dismiss();
            }
        });
    }
}
