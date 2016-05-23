package com.dstudio.wd.dweather.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wd824 on 2016/5/20.
 */
public class Weather
{
    private MyDatabaseHelper dbHelper;

    public Weather(MyDatabaseHelper dbHelper)
    {
        this.dbHelper = dbHelper;
    }

    public String queryWt(String wtName)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select wt_icon from Weather where wt_name = ?", new String[] {wtName});
        if (cursor.moveToFirst())
        {
            return cursor.getString(cursor.getColumnIndex("wt_icon"));
        }
        else
        {
            return null;
        }
    }
}
