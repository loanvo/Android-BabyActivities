package cs175.babysactivities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.DateInterval;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class BabyActivities extends AppCompatActivity implements SensorEventListener, View.OnClickListener{

    private TextView mTextMessage;

    SensorManager sensorManager;
    Sensor sensor;
    float temperature =0f;

    //constant to calculate temperature references at keisan.casio.com
    final static float CONSTANT1 = 5.257F;
    final static float CONSTANT2 = 0.0065F;
    final static float CONSTANT3 = 273.15F;

    private TextView tempView;
    private Button feedButton;
    private Button diaperButton;
    private Button sleepButton;
    private Button walkButton;

    private TextView nameView;
    private TextView ageView;

    DBHelper dbHelper;
    BabyProfile babyProfile;
    String today;

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
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        tempView = (TextView) findViewById(R.id.temp_view);

        babyProfile = dbHelper.getBabyInfo();
        nameView = (TextView) findViewById(R.id.name_view);
        nameView.setText(babyProfile.getName());

        String birthday = babyProfile.getDOB();
        ageView = (TextView) findViewById(R.id.age_view);
        ageView.setText(getAge(birthday));
        sensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        nameView.setOnClickListener(this);
        ageView.setOnClickListener(this);
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

    @Override
    public void onResume() {

        super.onResume();
        Sensor tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if(tempSensor != null){
            sensorManager.registerListener(this, tempSensor, sensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Your phone doesn't support /n" + "temperature detect feature.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_PRESSURE);{
           float pressure;
           float altitude;
           float sealevel;
           double temp;
            pressure = (float) event.values[0];
            sealevel = SensorManager.PRESSURE_STANDARD_ATMOSPHERE;
            altitude = SensorManager.getAltitude(sealevel, pressure);
            temp =  Double.valueOf((1/(1 - Math.pow(Math.E, Math.log(pressure/sealevel)/CONSTANT1)))
                   *(CONSTANT2*altitude) - CONSTANT2*altitude - CONSTANT3).floatValue();
            temperature = Math.round(temp * 10f) / 10f; //round up 1 decimal
            tempView.setText(Float.toString(temperature));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
}
