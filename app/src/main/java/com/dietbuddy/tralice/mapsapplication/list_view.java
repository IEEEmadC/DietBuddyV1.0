package com.dietbuddy.tralice.mapsapplication;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class list_view extends AppCompatActivity {
    private DBHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        ListView lisetView= (ListView) findViewById(R.id.listView);
        db=new DBHelper(this);

        ArrayList<String> list=new ArrayList<>();
        Cursor allData = db.getAllData("report");

        if(allData.getCount()==0){
            Toast.makeText(this,"No Data",Toast.LENGTH_LONG);
        }else{
            while(allData.moveToNext()){
                list.add(allData.getString(1));
                ListAdapter listAdapter=new ArrayAdapter<>(this, R.layout.simple_item,list);
                lisetView.setAdapter(listAdapter);
            }
        }

    }
}
