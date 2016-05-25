package com.dstudio.wd.dweather.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by wd824 on 2016/5/24.
 */
public class Judgement
{
    private Context mContext;

    public Judgement(Context mContext)
    {
        this.mContext = mContext;
    }

    /**
     * 判断是否为第一次启动
     * @return true-第一次启动
     */
    public boolean judgeVersion()
    {
        float nowVersion = 0;
        try
        {
            nowVersion = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        SharedPreferences sp = mContext.getSharedPreferences("welcomeInfo", Context.MODE_PRIVATE);
        float spVersion = sp.getFloat("spVersion", 0);
        Log.i("Version", "最新版本号" + nowVersion + ", sp版本号" + spVersion);

        if (nowVersion > spVersion)
        {
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("spVersion", nowVersion);
            editor.commit();
            Toast.makeText(mContext, "第一次 呵呵", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 判断当前网络是否可用
     * @return
     */
    public boolean isNetworkAvailable()
    {
        Log.d("debug", "function is OK");
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getActiveNetworkInfo() != null)
        {
            return manager.getActiveNetworkInfo().isAvailable();
        }
        else
        {
            return false;
        }
    }
}
