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

public class TEMPHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Mytemp.db";
    public static final String CONTACTS_TABLE_NAME = "temp_data";
    public static final String CONTACTS_COLUMN_TEMP = "temperature";
    public static final String CONTACTS_COLUMN_TIME = "time";
    public final String columnlist[] = new String[] {CONTACTS_COLUMN_TEMP,CONTACTS_COLUMN_TIME};

    private HashMap hp;

    public TEMPHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table temp_data " +
                        "(temperature double, time string)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS temp_data");
        onCreate(db);
    }

    public boolean insertContact(Double temp, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("temperature", temp);
        contentValues.put("time", time);
        db.insert("temp_data", null, contentValues);
        return true;
    }


    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }


    public ArrayList<ArrayList<String>> getAllCotacts() {
        ArrayList<ArrayList<String>> array_list = new ArrayList<ArrayList<String>>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from temp_data", null);
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