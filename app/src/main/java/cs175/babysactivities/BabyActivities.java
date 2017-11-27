package cs175.babysactivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class BabyActivities extends AppCompatActivity implements SensorEventListener{

    private TextView mTextMessage;

    SensorManager sensorManager;
    Sensor sensor;
    int temperature =0;

    //constant to calculate temperature references at keisan.casio.com
    final static float CONSTANT1 = 5.257F;
    final static float CONSTANT2 = 0.0065F;
    final static float CONSTANT3 = 273.15F;

    private TextView tempView;
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

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        tempView = (TextView) findViewById(R.id.temp_view);

        sensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }

    @Override
    public void onResume() {

        super.onResume();
        Sensor tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if(tempSensor != null){
            sensorManager.registerListener(this, tempSensor, sensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_PRESSURE);{
           float pressure;
           float altitude;
           float sealevel;
            pressure = (float) event.values[0];
            sealevel = SensorManager.PRESSURE_STANDARD_ATMOSPHERE;
            altitude = SensorManager.getAltitude(sealevel, pressure);
            temperature = (int) Math.round((1/(1 - Math.pow(Math.E, Math.log(pressure/sealevel)/CONSTANT1)))
                    *(CONSTANT2*altitude) - CONSTANT2*altitude - CONSTANT3);
            tempView.setText(Double.toString(temperature));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
