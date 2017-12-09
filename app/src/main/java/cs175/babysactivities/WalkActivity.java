package cs175.babysactivities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WalkActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener{
    DBHelper dbHelper;
    Handler handler;


    private TextView nameView;
    private TextView timeView;
    private TextView stepView;
    private Button startWalk;

    private List<ActivityLog> walkLogs;
    ActivityLog activityLog;
    ActivityData data;
    FeedingActivity feedingActivity;

    private LinearLayout layout;
    private LinearLayout layout1;
    List<String> todayLogs;
    List<String> previousLogs;
    private ArrayAdapter<String> today_arrayAdapter;
    private ArrayAdapter<String> previoud_arrayAdapter;

    private String name;
    private long continued;
    private long time;
    private boolean started_before;
    private boolean just_started;
    private long start;

    private String timeString;
    private String logTime;
    private String logDate;
    private String current;
    private String log;

    //Sensor's variable
    SensorManager sensorManager;
    Sensor mStepCounter;
    private int mSteps;
    private int mCounterSteps = 0;
    private int currentSteps = 0;
    private int totalStep = 0;

   Button compass;
   boolean compassed;

    static TextView tempView;
    static TextView cityName;
    private LinearLayout mlayout;
    private TextView warning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);

        tempView = (TextView) findViewById(R.id.temp_view);
        cityName = (TextView) findViewById(R.id.city_name);
        mlayout = (LinearLayout) findViewById(R.id.walk_warning);
        warning = (TextView) mlayout.findViewById(R.id.warning);
        getLocation();

        dbHelper = new DBHelper(this);
        handler = new Handler();

        //Initialize sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        nameView = (TextView) findViewById(R.id.name_view);
        timeView = (TextView) findViewById(R.id.walk_timer_view);
        stepView = (TextView) findViewById(R.id.step_view);
        startWalk = (Button) findViewById(R.id.start_walk);
        if(just_started == true || compassed == true){
            stepView.setText(String.valueOf(mSteps));

        }
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
                    stepView.setText("0");
                    startWalk.setBackgroundColor(Color.RED);
                    startWalk.setText("stop");
                    clockRunning();
                    data.setStartType("walk");
                    data.setStart(String.valueOf(start));
                    dbHelper.insertStatus(data, name);
                    just_started = true;
                    //started_before = true;
                    int temp =0;
                    try{
                        temp = Integer.parseInt(tempView.getText().toString());
                    }catch (Exception e){
                        warning.setVisibility(v.INVISIBLE);
                    }
                    if(temp != 0 && temp <= 60){
                        warning.setVisibility(v.VISIBLE);
                        warning.setText("It's too cold outside, wear warm before you go!");
                    }else{
                        warning.setVisibility(v.INVISIBLE);
                    }
                }else {
                    startWalk.setClickable(true);
                    startWalk.setBackgroundColor(Color.GREEN);
                    startWalk.setText("start");
                    stopWalk();
                    dbHelper.removeStatus("walk");
                    just_started = false;
                    started_before = false;
                }
            }
        });
        compass = (Button) findViewById(R.id.compass_button);
        compass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        compassed = true;
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }

    public void getLocation(){
        //implement weather
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //String provider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(final Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                DownloadTask task = new DownloadTask();
                task.execute("http://samples.openweathermap.org/data/2.5/weather?lat=" + String.valueOf(lat)+
                        "&lon=" + String.valueOf(lng) + "&appid=ec1ccfd2f6689df719001f54c09e5dde");
            }


            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        /*
        Location location = locationManager.getLastKnownLocation(provider);



*/
    }



    public void stopWalk(){
        handler.removeCallbacks(runnable);
        data.setWalkTime(time);
        timeString = activityLog.formatTimeView(time);
        String numberSteps = stepView.getText().toString();
        current = activityLog.getCurrentTime();
        logTime = activityLog.splitTime(current);
        logDate = activityLog.splitDate(current);
        log = "Walked " + numberSteps + " steps in " + timeString + " at " + logTime;

        activityLog.setName(name);
        activityLog.setLog(log);
        activityLog.setLogDate(logDate);
        dbHelper.insetLog(activityLog);

        walkLogs.add(activityLog);
        setLogView(walkLogs);

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
        layout1 = (LinearLayout) findViewById(R.id.previous_logs);
        TextView previous = (TextView) layout1.findViewById(R.id.date_view);
        ListView previousLog = (ListView) layout1.findViewById(R.id.log_view);

        todayLogs = new ArrayList<>();
        previousLogs = new ArrayList<>();

        for (int i = 0; i < logs.size(); i++) {
            log = logs.get(i);
            type = logs.get(i).getLog();
            if (type.startsWith("Walked")) {
                date = log.getLogDate();
                if (date.equals(currentdate)) {
                    today.setVisibility(today.VISIBLE);
                    today.setText("Today Activites");
                    todayLogs.add(log.getLog());
                } else {
                    previous.setVisibility(today.VISIBLE);
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

    @Override
    public void onResume() {

        super.onResume();
        // only register the listener if start button has not been click
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null){
            sensorManager.registerListener(this, countSensor, sensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if(started_before==false){
            if(countSensor != null) {
                sensorManager.unregisterListener(this,countSensor);
            }else
                Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }else{
            sensorManager.registerListener(this,countSensor, sensorManager.SENSOR_DELAY_FASTEST);

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if(just_started==true) {
                if (mCounterSteps < 1) {
                    // initial value
                    mCounterSteps = (int) event.values[0];
                }
                // Calculate steps taken based on first counter value received.
                currentSteps = (int) event.values[0];
                dbHelper.insertWalk(name, currentSteps);
                    mSteps = currentSteps - mCounterSteps;
                    stepView.setText(String.valueOf(mSteps));
            }else if(started_before==true){
                totalStep =(int) event.values[0] - dbHelper.getSteps();
                stepView.setText(String.valueOf(totalStep));
            }else{
                stepView.setText("0");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
