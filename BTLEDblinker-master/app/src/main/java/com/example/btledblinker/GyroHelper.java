package com.example.btledblinker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class GyroHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Mygyro.db";
    public static final String CONTACTS_TABLE_NAME = "gyro_data";
    public static final String CONTACTS_COLUMN_GYROX = "gyro1";
    public static final String CONTACTS_COLUMN_GYROY = "gyro2";
    public static final String CONTACTS_COLUMN_GYROZ = "gyro3";
    public static final String CONTACTS_COLUMN_TIME = "time";
    public final String columnlist[] = new String[] {CONTACTS_COLUMN_GYROX,CONTACTS_COLUMN_GYROY,CONTACTS_COLUMN_GYROZ,CONTACTS_COLUMN_TIME};

    private HashMap hp;

    public GyroHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table gyro_data " +
                        "(gyro1 integer, gyro2 integer, gyro3 integer, time string)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS gyro_data");
        onCreate(db);
    }

    public boolean insertContact (Integer gyro1, Integer gyro2, Integer gyro3, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("gyro1", gyro1);
        contentValues.put("gyro2", gyro2);
        contentValues.put("gyro3", gyro3);
        contentValues.put("time", time);
        db.insert("gyro_data", null, contentValues);
        return true;
    }


    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }


//    public ArrayList<String> getAllCotacts() {
//        ArrayList<String> array_list = new ArrayList<String>();
//
//        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from gyro_data", null );
//        res.moveToFirst();
//
//        while(res.isAfterLast() == false){
//            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_GYROX)));
//            res.moveToNext();
//        }
//        return array_list;
//    }

    public ArrayList<ArrayList<String>> getAllCotacts() {
        ArrayList<ArrayList<String>> array_list = new ArrayList<ArrayList<String>>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from gyro_data", null);

        res.moveToFirst();

        for (String i:columnlist) {
            ArrayList<String> a = new ArrayList<String>();
            Log.d("column name", i);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                a.add(res.getString(res.getColumnIndex(i)));
                res.moveToNext();
            }
            array_list.add(a);
        }
        return array_list;
    }
}

