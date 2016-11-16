package com.iiitd.team10.nearyou;

/**
 * Created by Naveen Patidar on 05-Nov-15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper1 extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "nearyou1";
    private static final String TABLE_NAME = "preferencestatus";
    private static final String STATUS_COLUMN="statusid";
    private static final String STATUS_COLUMN_VALUE="statusvalue";

    public DBHelper1(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + TABLE_NAME +
                        "(" + STATUS_COLUMN + " text primary key," + STATUS_COLUMN_VALUE + " int)"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertValue(String statuscol, int statusvalue)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS_COLUMN, statuscol);
        contentValues.put(STATUS_COLUMN_VALUE, statusvalue);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateStatusValue (String statuscol, int statusvalue)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS_COLUMN, statuscol);
        contentValues.put(STATUS_COLUMN_VALUE,statusvalue);
        db.update(TABLE_NAME, contentValues, "statusid = ? ", new String[] {statuscol } );
        return true;
    }

    public int getStatus()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME, null );
        res.moveToFirst();
        int a=0;
        while(res.isAfterLast() == false){
            a=res.getInt(res.getColumnIndex(STATUS_COLUMN_VALUE));
            res.moveToNext();
        }
        return a;
    }

}