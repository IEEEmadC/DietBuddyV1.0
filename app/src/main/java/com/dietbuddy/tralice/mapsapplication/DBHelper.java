package com.dietbuddy.tralice.mapsapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.IntegerRes;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Dell on 7/6/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="DietBuddy.db";

    public static final String USER_TABLE_NAME="user";

    public static final String USER_COLUMN_ID="id";
    public  static final String USER_COLUMN_NAME="name";
    public static final String USER_COLUMN_AGE="age";
    public static final String USER_COLUMN_GENDER="gender";
    public static final String USER_COLUMN_WEIGHT="weight";
    public static final String USER_COLUMN_HEIGHT="height";


    public static final String REPORT_TABLE_NAME="report";

    public static final String REPORT_COLUMN_ID="id";
    public static final String REPORT_COLUMN_DATE="date";
    public static final String REPORT_COLUMN_DURATION="duration";
    public static final String REPORT_COLUMN_DISTANCE="distance";
    public static final String REPORT_COLUMN_CALORIES="calories";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.openOrCreateDatabase(("H:\\MapsApplication\\app\\src\\main\\res\\DataBase\\MyDataBase.txt"),null);
        db.execSQL("create table user (id INTEGER PRIMARY KEY AUTOINCREMENT ,name TEXT,age INTEGER,weight DOUBLE,height DOUBLE,gender TEXT)" );
        db.execSQL("create table report (id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT,duration DOUBLE,distance DOUBLE,calories DOUBLE)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS report");
        onCreate(db);
    }

    public Cursor getUserData(String tableName,int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + USER_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return res;
    }
    public Cursor getAllData(String tableName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + tableName, null);
        return res;
    }
    public int deleteRow(String tableName,int id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(tableName," id = ? ",new String[]{Integer.toString(id)});
        return id;
    }

    public boolean insertUser(String name,int age,double weight,double height,String gender){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(USER_COLUMN_NAME,name);
        contentValues.put(USER_COLUMN_AGE,age);
        contentValues.put(USER_COLUMN_WEIGHT,weight);
        contentValues.put(USER_COLUMN_HEIGHT,height);
        contentValues.put(USER_COLUMN_GENDER,gender);
        long result = db.insert(USER_TABLE_NAME, null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    public boolean insertReport(String date, double duration, double distance, double calories){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(REPORT_COLUMN_DATE,date );
        contentValues.put(REPORT_COLUMN_DURATION,duration);
        contentValues.put(REPORT_COLUMN_DISTANCE,distance);
        contentValues.put(REPORT_COLUMN_CALORIES,calories);
        db.insert(REPORT_TABLE_NAME,null,contentValues);
        return true;
    }



}
