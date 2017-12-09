package cs175.babysactivities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nameEdit;
    private EditText birthdayEdit;
    private EditText heightEdit;
    private EditText weightEdit;
    private Button registerButton;

    String name;
    String DOB;
    double height;
    double weight;
    double headSize;
    Boolean editMode;
    DBHelper dbHelper;
    BabyProfile babyProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        SharedPreferences pref = getSharedPreferences("registered", Context.MODE_PRIVATE);
        if(pref.getBoolean("registered", false)){
            Intent intent = new Intent(this, BabyActivities.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("registered", true);
            ed.commit();
        }
        dbHelper = new DBHelper(this);
        babyProfile = new BabyProfile();
        editMode = false;
        nameEdit = (EditText) findViewById(R.id.edit_name);
        birthdayEdit = (EditText) findViewById(R.id.edit_birthday);

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);

        nameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                nameEdit.setError(null);
                if(nameEdit.getText().toString().isEmpty()){
                    nameEdit.setError("Please enter your child's name");
                    registerButton.setClickable(false);
                }else{
                    nameEdit.setError(null);
                    registerButton.setClickable(true);
                }
            }
        });
        setCalendar(birthdayEdit, this);
    }

    public void setCalendar(final EditText editText, final Context context){
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String format = "MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                editText.setText(sdf.format(calendar.getTime()));
            }
        };

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    @Override
    public void onClick(View v) {
        name = nameEdit.getText().toString();
        babyProfile.setName(name);

        DOB = birthdayEdit.getText().toString();
        if(DOB.isEmpty()){
            babyProfile.setDOB("");
        }else babyProfile.setDOB(DOB);

        if(editMode){
            dbHelper.removeBabyProfile(name);
        }

        dbHelper.createProfile(babyProfile);
        Intent intent = new Intent(this, BabyActivities.class);
        intent.putExtra("babyname", name);
        startActivity(intent);
    }

}
