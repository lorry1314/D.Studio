package com.dstudio.wd.dweather.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wd824 on 2016/5/12.
 */
public class City
{
    private MyDatabaseHelper dbHelper;

    public City(MyDatabaseHelper dbHelper)
    {
        this.dbHelper = dbHelper;
    }

    public Cursor queryCity(String cityName)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("select _id, city_name from City where city_name like '%" + cityName + "%' limit 10", null);
    }

    /**
     * 查询城市ID
     * @return
     */
    public String queryCityId(String cityName)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select city_id from City where city_name = ?", new String[]{cityName});
        if (cursor.moveToFirst())
        {
            return cursor.getString(cursor.getColumnIndex("city_id"));
        }
        else
        {
            return null;
        }
    }
}
