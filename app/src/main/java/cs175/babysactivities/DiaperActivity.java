package cs175.babysactivities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.LinkedList;

public class DiaperActivity extends AppCompatActivity implements View.OnClickListener{
    private CheckBox poo;
    private CheckBox pee;
    private CheckBox both;
    private ListView diaperList;

    private TextView nameView;
    private String name;

    DBHelper dbHelper;
    LinkedList<String> diaperLogs;
    String current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diaper);

        dbHelper = new DBHelper(this);
        diaperLogs = new LinkedList();

        nameView = (TextView) findViewById(R.id.name_view);
        poo = (CheckBox) findViewById(R.id.poo);
        pee = (CheckBox) findViewById(R.id.pee);
        both = (CheckBox) findViewById(R.id.both);
        diaperList = (ListView) findViewById(R.id.diaper_list);

        name = dbHelper.getBabyName();
        nameView.setText(name);
       // LinkedList<String> logs = new LinkedList<>();
        diaperLogs = dbHelper.getAllLog();
        setLogView(diaperLogs, name);

        poo.setOnClickListener(this);
        pee.setOnClickListener(this);
        both.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.poo:
                if(poo.isChecked()){
                    current = getCurrentTime();
                    dbHelper.insertDiaper("poo", name);
                    String log = "Poo-poo at " + current;
                    dbHelper.insetLog(log, name);
                    diaperLogs.addLast(log);
                    setLogView(diaperLogs, name);
                }
                break;
            case R.id.pee:
                if(pee.isChecked()){
                    current = getCurrentTime();
                    dbHelper.insertDiaper("pee", name);
                    String log = "Pee-pee at " + current;
                    dbHelper.insetLog(log, name);
                    diaperLogs.addLast(log);
                    setLogView(diaperLogs, name);
                }
                break;
            case R.id.both:
                if(both.isChecked()){
                    current = getCurrentTime();
                    dbHelper.insertDiaper("both", name);
                    String log = "Pee-pee and Poo-pee at " + current;
                    dbHelper.insetLog(log, name);
                    diaperLogs.addLast(log);
                    setLogView(diaperLogs, name);
                }
                break;
        }
    }

    public String getCurrentTime(){
        String current = "";
        DateTime dateTime = new DateTime();
        current = dateTime.toString(DateTimeFormat.shortDateTime());
        return current;
    }
    public void setLogView(LinkedList<String> logs, String name){
        LinkedList<String> mlogs = new LinkedList<>();
        for(int i =0; i <logs.size(); i++){
            String type = logs.get(i);
            if(type.startsWith("Poo-poo") || type.startsWith("Pee-pee")){
                mlogs.addLast(logs.get(i));
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mlogs);
        diaperList.setAdapter(arrayAdapter);
        diaperList.setTextFilterEnabled(true);
    }


}
