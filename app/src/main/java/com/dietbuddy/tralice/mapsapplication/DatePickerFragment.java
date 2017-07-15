package com.dietbuddy.tralice.mapsapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dell on 7/8/2017.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public String newDateString="av";
    //public static final String MY_PREFERENCS="abc";
    //public SharedPreferences sharedPreferences;
    public static final String BIRTHDAY_KEY="birthday";
    public Bundle b;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c=Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);



            //sharedPreferen
            // ces=this.getActivity().getSharedPreferences(MY_PREFERENCS,Context.MODE_PRIVATE);
            return new android.app.DatePickerDialog(getActivity(),this,year,month,day);

        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String startDateString = dayOfMonth+"-"+(month+1)+"-"+year;
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date startDate;
            try {
                startDate = df.parse(startDateString);
                newDateString = df.format(startDate);


                ((login_activity)getActivity()).setBirthday(newDateString);


                //Intent i=new Intent(getActivity(),login_activity.class);
                //i.putExtra(BIRTHDAY,newDateString);


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


}


