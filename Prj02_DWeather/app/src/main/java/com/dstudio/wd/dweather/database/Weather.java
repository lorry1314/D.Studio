package com.dstudio.wd.dweather.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dstudio.wd.dweather.R;

/**
 * Created by wd824 on 2016/5/20.
 */
public class Weather
{
    private Context mContext;

    public Weather(Context mContext)
    {
        this.mContext = mContext;
    }

    public String queryWt(String wtName)
    {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(mContext.getString(R.string.db_dir_path) + "/city.db", null);
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
