package com.saiful.falldetection;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Dell PC on 31-01-2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contactList.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase sql;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " +
                ContactContract.TABLE_NAME + "("+
                ContactContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +","+
                ContactContract.COLUMN_CONTACT + " TEXT NOT NULL" + ");";

        final String SQL_CREATE_DATA_TABLE = "CREATE TABLE " +
                ContactContract.TABLE_FALL_EVENT + "("+
                ContactContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +","+
                ContactContract.COLUMN_ACCEL + " DOUBLE NOT NULL" +","+
                ContactContract.COLUMN_GYRO + " DOUBLE NOT NULL" +","+
                ContactContract.COLUMN_MAGNETOMETER + " DOUBLE NOT NULL" +","+
                ContactContract.COLUMN_FALL_STATUS + " BOOLEAN NOT NULL" + ");";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DATA_TABLE);
    }

    public void insertIntoDataTable(double accel, double gyro, double magnetometer, boolean fallStatus){

        sql = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ContactContract.COLUMN_ACCEL, accel);
        cv.put(ContactContract.COLUMN_GYRO, gyro);
        cv.put(ContactContract.COLUMN_MAGNETOMETER, magnetometer);
        cv.put(ContactContract.COLUMN_FALL_STATUS, fallStatus);
        long rowID = sql.insert(ContactContract.TABLE_FALL_EVENT, null, cv);
        Log.e("FallEvent", "Newly inserted row ID: " + rowID);
    }

    public double getDataRowsCount(){
        String[] colums = new String[]{ContactContract.COLUMN_ACCEL};
        double count = 0;
        sql = this.getReadableDatabase();
        //Cursor cursor = sql.rawQuery("Select * from "+ ContactContract.TABLE_FALL_EVENT, null);
        Cursor cursor = sql.query(ContactContract.TABLE_FALL_EVENT, null, null, null, null, null, null);
        count = cursor.getCount();

        cursor.moveToFirst();

        while(cursor.isAfterLast() == false){
            Log.e("dbhelper", "Data from db: "+ cursor.getString(cursor.getColumnIndex(ContactContract.COLUMN_FALL_STATUS)));
            cursor.moveToNext();
        }

        cursor.close();
        return count;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }
}
