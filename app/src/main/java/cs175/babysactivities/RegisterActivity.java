package cs175.babysactivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Date;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

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
        heightEdit = (EditText) findViewById(R.id.edit_height);
        weightEdit = (EditText) findViewById(R.id.edit_weight);
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
    }

    @Override
    public void onClick(View v) {
        name = nameEdit.getText().toString();
        babyProfile.setName(name);

        DOB = birthdayEdit.getText().toString();
        babyProfile.setDOB(DOB);

        String h = heightEdit.getText().toString();
        if(h.isEmpty()){
            babyProfile.setHeight(0.0);
        }else height = Double.parseDouble(h);

        String w = weightEdit.getText().toString();
        if(w.isEmpty()){
            babyProfile.setWeight(0.0);
        }else weight = Double.parseDouble(w);

        babyProfile.setHeadsize(0.0);
        if(editMode){
            dbHelper.removeBabyProfile(name);
        }

        dbHelper.createProfile(babyProfile);
        Intent intent = new Intent(this, BabyActivities.class);
        intent.putExtra("babyname", name);
        startActivity(intent);
    }

}
