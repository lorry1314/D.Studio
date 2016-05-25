package com.dstudio.wd.dweather.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by wd824 on 2016/5/10.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper
{

    private Context mContent;

    public static final String CREATE_CITY = "create table City (" +
            "_id integer primary key autoincrement," +
            "city_id text," +
            "city_name text," +
            "province text)";

    public static final String CREATE_WT = "create table Weather (" +
            "_id integer primary key autoincrement," +
            "wt_code text," +
            "wt_name text," +
            "wt_icon text)";

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        mContent = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_WT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }

}
