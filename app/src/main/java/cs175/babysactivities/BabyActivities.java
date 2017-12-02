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
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

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
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        LocalDate dt = formatter.parseLocalDate(birthday);
        int years = Years.yearsBetween(dt, LocalDate.now()).getYears();
        int months = Months.monthsBetween(dt, LocalDate.now()).getMonths();
        int days = Days.daysBetween(dt, LocalDate.now()).getDays();
        if(years <1 ){
            if(months < 1) {
                age = String.valueOf(days) + " days";
            }else {
                age = String.valueOf(months) + " months " + String.valueOf(days) + " days";
            }
        }else {
            age = String.valueOf(years) + " years "
                    + String.valueOf(months) + " months " + String.valueOf(days) + " days";
        }
        return age;
    }

    @Override
    public void onClick(View v) {
        Dialog dialog = new Dialog(BabyActivities.this);
        dialog.setContentView(R.layout.edit_profile);
        dialog.setTitle("Edit Baby Information");
        TextView nameEdit = (TextView) dialog.findViewById(R.id.edit_name);
        TextView birthdayEdit = (TextView) dialog.findViewById(R.id.edit_birthday);
        TextView heightEdit = (TextView) dialog.findViewById(R.id.edit_height);
        TextView weightEdit = (TextView) dialog.findViewById(R.id.edit_weight);
        TextView headEdit = (TextView) dialog.findViewById(R.id.edit_head);
        Button saveButton = (Button) dialog.findViewById(R.id.register_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
        nameEdit.setText(babyProfile.getName());
        birthdayEdit.setText(babyProfile.getDOB());
        weightEdit.setText(String.valueOf(babyProfile.getWeight()));
        heightEdit.setText(String.valueOf(babyProfile.getHeight()));
        headEdit.setText(String.valueOf(babyProfile.getHeight()));
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
