package com.dstudio.wd.dweather.adapter;

import android.os.Handler;
import android.os.Message;
import android.system.ErrnoException;
import android.widget.Toast;

import com.dstudio.wd.dweather.http.HttpCallbackListener;
import com.dstudio.wd.dweather.http.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wd824 on 2016/5/21.
 */
public class CityItem
{
    private String cityName;

    private String cityTmp;

    public CityItem(String cityNam, String cityTmp)
    {
        this.cityName = cityNam;
        this.cityTmp = cityTmp;
    }

    public String getCityName()
    {
        return cityName;
    }

    public String getCityTmp()
    {
        return cityTmp;
    }

}
