package com.saiful.apkanalyser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TB_NAME="usertable";

    public static final String ID="_id";

    public static final String USERNAME="username";

    public static final String PASSWORD="password";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,

                          int version) {

        super(context, name, factory, version);

        // TODO Auto-generated constructor stub

    }

    @Override

    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + " (" + ID + " INTEGER PRIMARY KEY," + USERNAME + " VARCHAR," + PASSWORD + " VARCHAR )");

        db.execSQL("INSERT INTO " + TB_NAME + "(" + ID + "," + USERNAME + "," + PASSWORD + ") VALUES" + "('1','admin','admin888')");

        db.execSQL("INSERT INTO " + TB_NAME + "(" + ID + "," + USERNAME + "," + PASSWORD + ") VALUES" + "('2','root','root123')");

        db.execSQL("INSERT INTO " + TB_NAME + "(" + ID + "," + USERNAME + "," + PASSWORD + ") VALUES" + "('3','wanqing','wanqing')");

    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // TODO Auto-generated method stub

    }

    @Override

    public void onOpen(SQLiteDatabase db) {

        // TODO Auto-generated method stub

        super.onOpen(db);

    }

}