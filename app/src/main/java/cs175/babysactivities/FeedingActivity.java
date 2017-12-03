package cs175.babysactivities;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import android.os.Handler;
import java.util.logging.LogRecord;

public class FeedingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    TextView timeView;
    Switch bottleSwitch;
    Switch leftSwitch;
    Switch rightSwitch;

    Handler handler;
    private long timeBuff;
    private long time;
    private long updateTime;
    private long start;
    private int hours;
    private int minutes;
    private int seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeding);

        handler = new Handler();
        timeView = (TextView) findViewById(R.id.feed_timer_view);
        bottleSwitch = (Switch) findViewById(R.id.switch_bottle);
        leftSwitch = (Switch) findViewById(R.id.switch_left);
        rightSwitch = (Switch) findViewById(R.id.switch_right);

        bottleSwitch.setOnCheckedChangeListener(this);
        leftSwitch.setOnCheckedChangeListener(this);
        rightSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
        }
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time = SystemClock.uptimeMillis() - start;
            updateTime = timeBuff + time;

            seconds = (int) (updateTime / 1000);
            minutes = seconds / 60;
            hours = minutes/60;
            seconds = seconds % 60;
            time = (int) (updateTime % 1000);
            timeView.setText("" +  String.format("%02d", hours) + ":"
                    + String.format("%02d", minutes) + ":"
                    + String.format("%02d", seconds));

            handler.postDelayed(this, 0);
        }
    };


}
