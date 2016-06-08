package com.dstudio.wd.dweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dstudio.wd.dweather.http.HttpCallbackListener;
import com.dstudio.wd.dweather.http.HttpUtil;
import com.dstudio.wd.dweather.http.LocalCache;
import com.dstudio.wd.dweather.tools.Judgement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends Activity
{
    private Context mContext;
    private boolean isFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = SplashActivity.this;
        try
        {
            loadDatabase();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("isFirst", isFirst);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 1000);
    }

    /**
     * 第一次启动， 导入城市数据库/天气图标数据库
     */
    public void loadDatabase() throws IOException
    {
        if (new Judgement(mContext).judgeVersion())
        {
            String dbDirPath = getString(R.string.db_dir_path);
            File dbDir = new File(dbDirPath);
            if (!dbDir.exists() || !dbDir.isDirectory())
            {
                dbDir.mkdir();
            }
            InputStream is = mContext.getAssets().open("city.db");
            FileOutputStream os = new FileOutputStream(dbDirPath + "/city.db");
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0)
            {
                os.write(buffer, 0, count);
            }
            is.close();
            os.close();
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    loadWtIcon();
                }
            }).start();
        }
    }

    public void loadWtIcon()
    {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(mContext.getString(R.string.db_dir_path) + "/city.db", null);
        Cursor cursor = db.rawQuery("select * from weather", null);
        if (cursor.moveToFirst())
        {
            while (cursor.moveToNext())
            {
                String iconUrl = cursor.getString(cursor.getColumnIndex("wt_icon"));
                Log.i("Icon", iconUrl);
                LocalCache.writeCache(mContext, iconUrl, "ICON");

            }
            cursor.close();
            db.close();
        }

    }
}