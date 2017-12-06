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
    private CheckBox pooBox;
    private CheckBox peeBox;
    private CheckBox bothBox;
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
        pooBox = (CheckBox) findViewById(R.id.poo);
        peeBox = (CheckBox) findViewById(R.id.pee);
        bothBox = (CheckBox) findViewById(R.id.both);
        diaperList = (ListView) findViewById(R.id.diaper_list);

        name = dbHelper.getBabyName();
        nameView.setText(name);
       // LinkedList<String> logs = new LinkedList<>();
        diaperLogs = dbHelper.getAllLog();
        setLogView(diaperLogs);

        pooBox.setOnClickListener(this);
        peeBox.setOnClickListener(this);
        bothBox.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.poo:
                if(pooBox.isChecked()){
                    current = getCurrentTime();
                    dbHelper.insertDiaper("poo", name);
                    String log = "Poo-poo at " + current;
                    dbHelper.insetLog(log, name);
                    diaperLogs.addFirst(log);
                    setLogView(diaperLogs);
                    }
                break;
            case R.id.pee:
                if(peeBox.isChecked()){
                    current = getCurrentTime();
                    dbHelper.insertDiaper("pee", name);
                    String log = "Pee-pee at " + current;
                    dbHelper.insetLog(log, name);
                    diaperLogs.addFirst(log);
                    setLogView(diaperLogs);
                }
                break;
            case R.id.both:
                if(bothBox.isChecked()){
                    current = getCurrentTime();
                    dbHelper.insertDiaper("both", name);
                    String log = "Pee-pee and Poo-pee at " + current;
                    dbHelper.insetLog(log, name);
                    diaperLogs.addFirst(log);
                    setLogView(diaperLogs);
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
    public void setLogView(LinkedList<String> logs){
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
