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

public class ACHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Myac.db";
    public static final String CONTACTS_TABLE_NAME = "ac_data";
    public static final String CONTACTS_COLUMN_ACX = "ac1";
    public static final String CONTACTS_COLUMN_ACY = "ac2";
    public static final String CONTACTS_COLUMN_ACZ = "ac3";
    public static final String CONTACTS_COLUMN_TIME = "time";

    private HashMap hp;

    public ACHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table ac_data " +
                        "(ac1 integer, ac2 integer, ac3 integer, time string)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS ac_data");
        onCreate(db);
    }

    public boolean insertContact (Integer ac1, Integer ac2, Integer ac3, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ac1", ac1);
        contentValues.put("ac2", ac2);
        contentValues.put("ac3", ac3);
        contentValues.put("time", time);
        db.insert("ac_data", null, contentValues);
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
        Cursor res =  db.rawQuery( "select * from ac_data", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ACX)));
            res.moveToNext();
        }
        return array_list;
    }
}

