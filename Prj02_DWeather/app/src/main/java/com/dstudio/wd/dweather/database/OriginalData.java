package com.dstudio.wd.dweather.database;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dstudio.wd.dweather.MainActivity;
import com.dstudio.wd.dweather.http.LocalCache;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by wd824 on 2016/5/21.
 */
public class OriginalData
{
    private MyDatabaseHelper dbHelper;
    private Context mContext;

    public OriginalData(MyDatabaseHelper dbHelper, Context mContext)
    {
        this.dbHelper = dbHelper;
        this.mContext = mContext;
    }

    /**
     * 解析获取的城市数据，写入数据库City表
     * @param response 获取的城市列表数据
     */
    public void parseJSONForCity(String response)
    {
        try
        {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            JSONObject results = new JSONObject(response);
            JSONArray cityInfo = new JSONArray(results.getString("city_info"));
            for(int i = 0; i < cityInfo.length(); i++)
            {
                String cityId = cityInfo.getJSONObject(i).getString("id");
                String cityName = cityInfo.getJSONObject(i).getString("city");
                String province = cityInfo.getJSONObject(i).getString("prov");
                db.execSQL("insert into City (city_id, city_name, province) values(?, ?, ?)",
                        new String[] {cityId, cityName, province});
            }
            Log.d("debug", "City表添加数据完成");
        }
        catch (Exception e)
        {
            Log.d("MainActivity", "数据解析失败 (✖╭╮✖)");
            e.printStackTrace();
        }
    }

    /**
     * 解析天气图标列表， 写入数据库Weather表，并将天气小图标写入缓存
     * @param response 获取的天气图标数据
     */
    public void parseJSONForWt(String response)
    {
        try
        {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            JSONObject results = new JSONObject(response);
            JSONArray condInfo = new JSONArray(results.getString("cond_info"));
            for (int i = 0; i < condInfo.length(); i++)
            {
                String wtCode = condInfo.getJSONObject(i).getString("code");
                String wtName = condInfo.getJSONObject(i).getString("txt");
                String wtIcon = condInfo.getJSONObject(i).getString("icon");
                db.execSQL("insert into Weather (wt_code, wt_name, wt_icon) values(?, ?, ?)",
                        new String[] {wtCode, wtName, wtIcon});
                LocalCache.writeCache(mContext, wtIcon, "ICON");      // 将天气图标写入缓存
            }
            Log.d("debug", "Weather表添加数据完成");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
