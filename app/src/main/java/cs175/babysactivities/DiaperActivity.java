package cs175.babysactivities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import static java.lang.Math.abs;
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
    private LinearLayout layout1;

    Supplies supplies;
    boolean addedSupply = false;
    private int leftDiaper;
    private int leftFormula;
    private String dateSupply;
    private TextView diaperView;

    private GestureDetectorCompat mDetector;
    private ListView todayLog;
    private ListView previousLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diaper);

        dbHelper = new DBHelper(this);
        diaperLogs = new ArrayList<>();
        activityLog = new ActivityLog();
        supplies = new Supplies();

        nameView = (TextView) findViewById(R.id.name_view);
        pooBox = (CheckBox) findViewById(R.id.poo);
        peeBox = (CheckBox) findViewById(R.id.pee);
        bothBox = (CheckBox) findViewById(R.id.both);

        name = dbHelper.getBabyName();
        nameView.setText(name);
        //get started activity from database and show to user
        diaperLogs = dbHelper.getAllLog();
        if(diaperLogs != null) {
            setLogView(diaperLogs);
        }
        pooBox.setOnClickListener(this);
        peeBox.setOnClickListener(this);
        bothBox.setOnClickListener(this);
        supplies = getLeftOver();
        diaperView = (TextView) findViewById(R.id.diaper_view);
        //show the quantity of diaper that retrieve from the database
        diaperView.setText(String.valueOf(supplies.getDiaper()));

    }

    // get the number of supplies has been left after uses
    public Supplies getLeftOver(){
        //implement supply inventory
        Supplies supplies = dbHelper.getSupplies();
        if(supplies != null){
            addedSupply = true;     //set true for addedSupply to know if user has been enter supply before or not
        }                           // this will prevent the leftover to be negative
        return supplies;
    }

    // implementation onclick for checkbox
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.poo:
                if(pooBox.isChecked()){
                    // insert type of using diaper
                    dbHelper.insertDiaper("poo", name);
                    // using the boolean addedSupply to detect when need to do the subtraction
                    if (addedSupply == true){
                        supplies = getLeftOver();
                        leftDiaper = supplies.getDiaper();
                        if (leftDiaper > 0) {
                            dbHelper.updateSupply(supplies.getFormula(), leftDiaper - 1, supplies.getDate());
                            diaperView.setText(String.valueOf(leftDiaper - 1));
                        }else{
                            // supply has not been added, no need to subtract, just set the leftover to 0
                            diaperView.setText("0");
                        }
                    }
                    current = activityLog.getCurrentTime();
                    logTime = activityLog.splitTime(current);
                    logDate = activityLog.splitDate(current);
                    log = "Poo-poo at " + logTime;

                    activityLog.setName(name);
                    activityLog.setLog(log);
                    activityLog.setLogDate(logDate);
                    dbHelper.insetLog(activityLog); //insert activity to database

                    //get log from database to show to user
                    diaperLogs=dbHelper.getAllLog();
                    setLogView(diaperLogs);
                    pooBox.setChecked(false);
                    }
                break;
            case R.id.pee:
                if(peeBox.isChecked()){
                    dbHelper.insertDiaper("pee", name);
                    if (addedSupply == true){
                        supplies = getLeftOver();
                        leftDiaper = supplies.getDiaper();
                        if (leftDiaper > 0) {
                            dbHelper.updateSupply(supplies.getFormula(), leftDiaper - 1, supplies.getDate());
                            diaperView.setText(String.valueOf(leftDiaper - 1));
                        }else{
                            diaperView.setText("0");
                        }
                    }else{
                        diaperView.setText("N/A");
                    }
                    current = activityLog.getCurrentTime();
                    logTime = activityLog.splitTime(current);
                    logDate = activityLog.splitDate(current);
                    log = "Pee-pee at " + logTime;

                    activityLog.setName(name);
                    activityLog.setLog(log);
                    activityLog.setLogDate(logDate);
                    dbHelper.insetLog(activityLog);

                    diaperLogs = dbHelper.getAllLog();
                    setLogView(diaperLogs);
                    peeBox.setChecked(false);
                }
                break;
            case R.id.both:
                if(bothBox.isChecked()){
                    dbHelper.insertDiaper("both", name);
                    if (addedSupply == true){
                        supplies = getLeftOver();
                        if (leftDiaper > 0) {
                            dbHelper.updateSupply(supplies.getFormula(), leftDiaper - 1, supplies.getDate());
                            diaperView.setText(String.valueOf(leftDiaper - 1));
                        }else{
                            diaperView.setText("0");
                        }
                    }
                    current = activityLog.getCurrentTime();
                    logTime = activityLog.splitTime(current);
                    logDate = activityLog.splitDate(current);
                    log = "Pee-pee and Poo-poo at " + logTime;

                    activityLog.setName(name);
                    activityLog.setLog(log);
                    activityLog.setLogDate(logDate);
                    dbHelper.insetLog(activityLog);

                    diaperLogs = dbHelper.getAllLog();
                    setLogView(diaperLogs);
                    bothBox.setChecked(false);
                }
                break;
        }
    }
    // helper funcion to set up the view, need to check if the type is poo,pee or both
    // to display in the diaper activity instead of display all (including sleep, feed..)
    public void setLogView(List<ActivityLog> logs){
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        String date;
        String type;
        String current = activityLog.getCurrentTime();
        String currentdate = activityLog.splitDate(current);
        ActivityLog log = new ActivityLog();
        //List view of today logs
        layout = (LinearLayout) findViewById(R.id.today_logs);
        TextView today = (TextView) layout.findViewById(R.id.date_view);
        todayLog = (ListView) layout.findViewById(R.id.log_view);


        //List view of previous days logs
        layout1 = (LinearLayout) findViewById(R.id.previous_logs);
        TextView previous = (TextView) layout1.findViewById(R.id.date_view);
        previousLog = (ListView) layout1.findViewById(R.id.log_view);


        todayLogs = new ArrayList<>();
        previousLogs = new ArrayList<>();
        for(int i =0; i<logs.size(); i++){
            log = logs.get(i);
            type = logs.get(i).getLog();
            if(type.startsWith("Poo-poo") || type.startsWith("Pee-pee")){
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

        todayLog.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return true;
            }
        });
        //ListAdapter is written below to customize the swipe left and right on each item in list
        ListAdapter<String> arrayAdapter = new ListAdapter<String>(this, android.R.layout.simple_list_item_1, todayLogs);
        todayLog.setAdapter(arrayAdapter);

        todayLog.setTextFilterEnabled(true);

        ListAdapter<String> preAdapter = new ListAdapter<String>(this, android.R.layout.simple_list_item_1, previousLogs);
        previousLog.setAdapter(preAdapter);
        previousLog.setTextFilterEnabled(true);


    }
    // Customize gesture to detect swipe left and right
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_NONE = 0;
        private static final int SWIPE_LEFT = 1;
        private static final int SWIPE_RIGHT = 2;
        ArrayList<View> views = new ArrayList<>();
        @Override
        public boolean onFling(MotionEvent ev1, MotionEvent ev2,
                               float velocityX, float velocityY) {
            views.add(findViewById(R.id.name_view));
            int action = SWIPE_NONE;
            if (abs(ev2.getY() - ev1.getY()) < 50) {
                if ((ev1.getX() - ev2.getX()) > 300) {
                    action = SWIPE_LEFT;
                } else if ((ev2.getX() - ev1.getX()) > 300) {
                    action = SWIPE_RIGHT;
                }
            }
            int pos = todayLog.pointToPosition((int)ev1.getX(),(int)ev1.getY());
            View child = todayLog.getChildAt(pos);
            if (child != null) {
                if(action == SWIPE_LEFT) {
                    child.findViewById(R.id.trash).setVisibility(View.VISIBLE);
                }
                if(action == SWIPE_RIGHT) {
                    child.findViewById(R.id.trash).setVisibility(View.INVISIBLE);
                }
            }

            return super.onFling(ev1, ev2, velocityX, velocityY);
        }
    }

    // implementation for trash button that appear when swipe left
    // it will be remove from the log also from the database if button is clicked
    public void deleteLog(View view){
        RelativeLayout ParentRow = (RelativeLayout) view.getParent();
        TextView text = (TextView)ParentRow.getChildAt(0);
        String removeLog = text.getText().toString();
        dbHelper.removeLog(removeLog);
        List<ActivityLog> updateLog = dbHelper.getAllLog();
        setLogView(updateLog);
        Supplies mSupplies = new Supplies();
        mSupplies = dbHelper.getSupplies();
        int updateDiaper = mSupplies.getDiaper() + 1;
        diaperView.setText(String.valueOf(updateDiaper));
        dbHelper.updateSupply(mSupplies.getFormula(), updateDiaper, mSupplies.getDate());
        ParentRow.refreshDrawableState();

    }
    //Custom list view for diaper log
    class ListAdapter<T> extends ArrayAdapter<T> {
        public ListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public ListAdapter(Context context, int resource, List<T> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.log_row, null);
            }

            T p = getItem(position);

            if (p != null) {
                TextView textView = (TextView) v.findViewById(R.id.item);

                if (textView != null) {
                    textView.setText(p.toString());
                }
            }
            return v;
        }
    }
}


