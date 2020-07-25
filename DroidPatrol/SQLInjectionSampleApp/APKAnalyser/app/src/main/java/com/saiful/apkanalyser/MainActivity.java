package com.saiful.apkanalyser;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText input;

    TextView showInput;


    DatabaseHelper dbhelper;

    SQLiteDatabase db;

    public static final String TB_NAME = "usertable";

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        input = (EditText) findViewById(R.id.editText);

        showInput = (TextView) findViewById(R.id.textView2);

        dbhelper = new DatabaseHelper(this, TB_NAME, null, 1);

        db = dbhelper.getWritableDatabase();

    }


    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_1:

                showResult(input.getText().toString());

                break;

        }

    }


    public void showResult(String info) {

        if (info == null || info.length() <= 0)

            showInput.setText("Please input:");

        else {

            Cursor cursor;

            cursor = db.rawQuery("SELECT * FROM usertable WHERE _id='" + info + "'", null);

            cursor.moveToFirst();

            String result = "";

            while (!cursor.isAfterLast()) {

                result += "id:" + cursor.getInt(0) + "\r\n" + "user:" + cursor.getString(1) + "\r\n" + "pass:" + cursor.getString(2) + "\r\n";

                cursor.moveToNext();

            }

            showInput.setText(result);

            cursor.close();

        }

    }

}