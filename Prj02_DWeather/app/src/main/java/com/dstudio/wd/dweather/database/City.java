package com.dstudio.wd.dweather.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wd824 on 2016/5/12.
 */
public class City
{
    private MyDatabaseHelper myDatabaseHelper;

    public City(MyDatabaseHelper myDatabaseHelper)
    {
        this.myDatabaseHelper = myDatabaseHelper;
    }

    public Cursor queryCity(String cityName)
    {
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        return db.rawQuery("select _id, city_name from City where city_name like '%" + cityName + "%' limit 10", null);
    }
}
