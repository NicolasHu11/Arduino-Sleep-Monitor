package com.example.btledblinker;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Myheartrate.db";
    public static final String CONTACTS_TABLE_NAME = "heart_rate_data";
    public static final String CONTACTS_COLUMN_HR = "heartrate";
    public static final String CONTACTS_COLUMN_TIME = "time";

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table heart_rate_data " +
                        "(heartrate integer, time string)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS heart_rate_data");
        onCreate(db);
    }

    public boolean insertContact (Integer heartrate, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("heartrate", heartrate);
        contentValues.put("time", time);
        db.insert("heart_rate_data", null, contentValues);
        return true;
    }


    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }


    public ArrayList<String> getAllCotacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from heart_rate_data", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_HR)));
            res.moveToNext();
        }
        return array_list;
    }
}
