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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText nameEdit;
    private EditText birthdayEdit;
    private Button registerButton;
    private Button skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("activity_executed", false)){
            Intent intent = new Intent(this, BabyActivities.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }

        nameEdit = (EditText) findViewById(R.id.edit_name);
        birthdayEdit = (EditText) findViewById(R.id.edit_birthday);
        registerButton = (Button) findViewById(R.id.register_button);
        skipButton = (Button) findViewById(R.id.skip_button);

        registerButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, BabyActivities.class);
        startActivity(intent);
    }

}
