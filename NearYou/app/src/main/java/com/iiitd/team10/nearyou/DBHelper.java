package com.iiitd.team10.nearyou;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/**
 * Created by Naveen Patidar on 04-Nov-15.
 */
public class DBHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "nearyou";
    private static final String TABLE_NAME = "nearyoutable";
    private static final String PLACE_COLUMN="place";
    private static final String PLACE_STATUS_COLUMN="status";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + TABLE_NAME +
                        "(" + PLACE_COLUMN + " text primary key," + PLACE_STATUS_COLUMN + " int)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertPlace  (String place, int status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLACE_COLUMN, place);
        contentValues.put(PLACE_STATUS_COLUMN, status);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateStatus (String place, int status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLACE_COLUMN,place);
        contentValues.put(PLACE_STATUS_COLUMN,status);
        db.update(TABLE_NAME, contentValues, "place = ? ", new String[]{place});
        return true;
    }

    public boolean check()
    {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME, null );
        if (res.getCount()>0)
            return true;



        return false;
    }

    public boolean delete()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        return true;
    }

    public ArrayList<String> getAllPlaces()
    {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(PLACE_COLUMN)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Integer> getAllPlacesStatus()
    {
        ArrayList<Integer> array_list = new ArrayList<Integer>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getInt(res.getColumnIndex(PLACE_STATUS_COLUMN)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String>getAllSelectdPlaces()
    {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" where "+PLACE_STATUS_COLUMN+"=1", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(PLACE_COLUMN)));
            res.moveToNext();
        }
        return array_list;    }

}
