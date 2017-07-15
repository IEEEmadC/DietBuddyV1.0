package com.dietbuddy.tralice.mapsapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.provider.MediaStore;
import android.renderscript.Double2;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.SpeedView;

import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class bmi_activity extends AppCompatActivity {

    private SpeedView speedView;

    private EditText weight;
    private EditText height;
    private EditText age;
    private Button calBMI;
    private TextView bmi_text;
    private TextView ideal_weight_text;
    private TextView result;
    private RadioButton male;

    private RadioButton female;
    private android.support.v7.app.ActionBar mActionBar;

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_activity);
        speedView= (SpeedView) findViewById(R.id.bmi_meter);
        speedView.setMaxSpeed(50);



        weight= (EditText) findViewById(R.id.weight_text);
        height= (EditText) findViewById(R.id.height_text);
        calBMI= (Button) findViewById(R.id.start_BMI);
        age=(EditText)findViewById(R.id.age_text);
        male=(RadioButton)findViewById(R.id.gender_male);
        female=(RadioButton)findViewById(R.id.gender_female);
        bmi_text= (TextView) findViewById(R.id.bmi_text);
        ideal_weight_text=(TextView)findViewById(R.id.ideal_weight_text);
        result=(TextView)findViewById(R.id.result);

        mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View actionBar = mInflater.inflate(R.layout.customappbar, null);


        TextView mTitleTextView = (TextView) actionBar.findViewById(R.id.title_text);
        mTitleTextView.setText("BMI CALCULATOR");
        mActionBar.setCustomView(actionBar);

        mActionBar.setDisplayShowCustomEnabled(true);
        ((Toolbar) actionBar.getParent()).setContentInsetsAbsolute(0, 0);

        BoomMenuButton rightBmb = (BoomMenuButton) actionBar.findViewById(R.id.action_bar_right_bmb);

        rightBmb.setButtonEnum(ButtonEnum.Ham);
        rightBmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
        rightBmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);
        int images[] = {R.drawable.ic_burn_calories, R.drawable.ic_bmi_calculator, R.drawable.ic_recepe_finder, R.drawable.ic_records};
        String normalText[] = {"Burn Calories", "BMI Calculator", "Find Recepe", "Reports"};
        for (int i = 0; i < rightBmb.getPiecePlaceEnum().pieceNumber(); i++)
            rightBmb.addBuilder(new HamButton.Builder().normalImageRes(images[i]).normalText(normalText[i]).
                    shadowEffect(true).listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                    if (index == 1) {
                        Intent i = new Intent(bmi_activity.this, bmi_activity.class);
                        startActivity(i);

                    } else if (index == 3) {
                        Intent i = new Intent(bmi_activity.this, history.class);
                        startActivity(i);
                    } else if (index == 2) {
                        Intent i = new Intent(bmi_activity.this, activity_recycler_view.class);
                        startActivity(i);
                    } else if (index == 0) {
                        Intent i = new Intent(bmi_activity.this, MapsActivity.class);
                        startActivity(i);
                    }

                }
            }));

        dbHelper=new DBHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query="SELECT * FROM user WHERE id = ?";
        Cursor cursor=db.rawQuery(query,new String[]{"1"});
        cursor.moveToFirst();
        String age1=cursor.getString(cursor.getColumnIndex(DBHelper.USER_COLUMN_AGE));
        String gender1 = cursor.getString(cursor.getColumnIndex(DBHelper.USER_COLUMN_GENDER));
        String weight1 = cursor.getString(cursor.getColumnIndex(DBHelper.USER_COLUMN_WEIGHT));
        String height1 = cursor.getString(cursor.getColumnIndex(DBHelper.USER_COLUMN_HEIGHT));
        age.setText(age1);
        weight.setText(weight1);
        height.setText(height1);
        if(gender1.equalsIgnoreCase("male")){
            male.setChecked(true);
        }else if(gender1.equalsIgnoreCase("female")){
            female.setChecked(true);
        }
        speedView.requestFocus();



        double calculateBMI = calculateBMI(Double.parseDouble(height.getText().toString()), Double.parseDouble(bmi_activity.this.weight.getText().toString()));

        speedView.speedTo((float)calculateBMI);
        bmi_text.setText("Your BMI is "+(int)Math.round(calculateBMI));
        checkResult(calculateBMI);
        double idealWeight = DetermineIdealWeight(male.isChecked(), Integer.parseInt(age.getText().toString()), Double.parseDouble(height.getText().toString()));
        ideal_weight_text.setText("You are supposed to be "+Math.round(idealWeight)+ " kg in weight");


        calBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double calculateBMI = calculateBMI(Double.parseDouble(height.getText().toString()), Double.parseDouble(bmi_activity.this.weight.getText().toString()));
                speedView.speedTo((float)calculateBMI);
                bmi_text.setText("Your BMI is "+(int)Math.round(calculateBMI));
                checkResult(calculateBMI);
                double idealWeight = DetermineIdealWeight(male.isChecked(), Integer.parseInt(age.getText().toString()), Double.parseDouble(height.getText().toString()));
                ideal_weight_text.setText("You are supposed to be "+(int)Math.round(idealWeight)+ " kg in weight");


            }
        });
    }

    private double calculateBMI(double height_meters,double weight_kilo){
        double BMI = (weight_kilo / (height_meters * height_meters));
        return BMI;
    }

    private double DetermineIdealWeight(boolean isMale,int age,double height) {
        double ideal=0;
        if (isMale){
            ideal = 50 + 2.3 *((height*39.3701) - 60);
        }else{
            ideal= 45.5 + 2.3 *(( height*39.3701) - 60) ;
        }

        return ideal;
    }

    private void checkResult(double bmi){
        String result1="";

        if(bmi< 18.5){
            result1="UnderWeight";
            result.setTextColor(Color.RED);

        }else if(18.5<=bmi && bmi<=24.9){
            result1="Normal Weight";
            result.setTextColor(Color.GREEN);

        }else if(25.0<=bmi && bmi<=29.9){
            result1="Over Weight";
            result.setTextColor(Color.YELLOW);

        }else if(30.0<=bmi &&  bmi<=34.9){
            result1="Class I Obesity";
            result.setTextColor(Color.argb(50,255,0,0));

        }else if(35.0<=bmi && bmi<=39.9){
            result1="Class II Obesity";
            result.setTextColor(Color.argb(25,255,0,0));
        }else if(bmi>=40.0){
            result1="Class III Obesity";
            result.setTextColor(Color.argb(0,255,0,0));
        }

        result.setText(result1);
    }

}
