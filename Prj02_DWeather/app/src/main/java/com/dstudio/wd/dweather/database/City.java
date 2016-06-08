package com.dstudio.wd.dweather.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dstudio.wd.dweather.R;

/**
 * Created by wd824 on 2016/5/12.
 */
public class City
{
    // private MyDatabaseHelper dbHelper;
    private Context mContext;

    public City(Context mContext)
    {
        // this.dbHelper = dbHelper;
        this.mContext = mContext;
    }

    public Cursor queryCity(String cityName)
    {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(mContext.getString(R.string.db_dir_path) + "/city.db", null);
        Cursor c = db.rawQuery("select _id, city_name from City where city_name like '%" + cityName + "%' limit 10", null);
        return c;
    }

    /**
     * 查询城市ID
     * @return
     */
    public String queryCityId(String cityName)
    {
        // SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(mContext.getString(R.string.db_dir_path) + "/city.db", null);
        Cursor cursor = db.rawQuery("select city_id from City where city_name = ?", new String[]{cityName});
        if (cursor.moveToFirst())
        {
            String cityId =  cursor.getString(cursor.getColumnIndex("city_id"));
            cursor.close();
            db.close();
            return cityId;
        }
        else
        {
            return null;
        }
    }
}
