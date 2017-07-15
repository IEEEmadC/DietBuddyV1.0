package com.dietbuddy.tralice.mapsapplication;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.DatePickerDialog;
import com.rey.material.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class login_activity extends Activity {
    private DBHelper db;

    private Button b;
    private EditText birthday_text;
    private EditText userName;
    private EditText weight;
    private EditText height;
    private Spinner spinner;

    public static String firstOpen;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        boolean isFirstRun=getSharedPreferences("PREFERENCES",MODE_PRIVATE).getBoolean("isFirstRun",true);
        if(isFirstRun){
            getSharedPreferences("PREFERENCES",MODE_PRIVATE).edit().putBoolean("isFirstRun",false).commit();
        }else{
            Intent i=new Intent(login_activity.this,MapsActivity.class);
            startActivity(i);
            finish();
        }

        super.onCreate(savedInstanceState);

        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_activity);

        userName=(EditText)findViewById(R.id.userName_text);
        weight=(EditText)findViewById(R.id.weight_text);
        height=(EditText)findViewById(R.id.height_text);
        birthday_text=(EditText)findViewById(R.id.birthday_text);
        spinner = (Spinner) findViewById(R.id.spinner_gender);
        b= (Button) findViewById(R.id.createuser_button);

        db=new DBHelper(this);


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final DatePickerDialog d=new DatePickerDialog(this);
        d.setContentView(R.layout.datepicker);
        final DatePicker datepicker= (DatePicker) findViewById(R.id.datePicker);

        //Create New User

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean bool=db.insertUser(userName.getText().toString(),
                        Calendar.getInstance().get(Calendar.YEAR)-Integer.parseInt(birthday_text.getText().toString().split("-")[2]),
                        Double.parseDouble(weight.getText().toString()),Double.parseDouble(height.getText().toString()),spinner.getSelectedItem().toString());

                Intent i=new Intent(login_activity.this,MapsActivity.class);
                startActivity(i);
                if(bool){

                    Toast.makeText(getApplicationContext(),"Done", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


        birthday_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    DialogFragment dialogFragment=new DatePickerFragment();
                    dialogFragment.show(getFragmentManager(),"datePicker");
                }
            }
        });



    }
    public void setBirthday(String birthday){
        birthday_text.setText(birthday);
    }


}

