package com.dstudio.wd.dweather;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Handler;
        import android.os.Bundle;

        import com.dstudio.wd.dweather.http.LocalCache;
        import com.dstudio.wd.dweather.tools.Judgement;
        import com.umeng.analytics.MobclickAgent;

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
        MobclickAgent.openActivityDurationTrack(false);
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
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, isFirst? 2000 : 500);
    }

    /**
     * 第一次启动， 导入包含城市列表和天气列表的静态数据库
     */
    public void loadDatabase() throws IOException
    {
        if (new Judgement(mContext).judgeVersion())
        {
            isFirst = true;
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
            loadWtIcon();
        }
    }

    /**
     * 第一次启动，下载天气小图标到本地
     */
    public void loadWtIcon()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(mContext.getString(R.string.db_dir_path) + "/city.db", null);
                Cursor cursor = db.rawQuery("select * from weather", null);
                while (cursor.moveToNext())
                {
                    String iconUrl = cursor.getString(cursor.getColumnIndex("wt_icon"));
                    LocalCache.writeCache(mContext, iconUrl, "ICON");
                }
                cursor.close();
                db.close();
            }
        }).start();
    }

    public void onResume()
    {
        super.onResume();
        MobclickAgent.onPageStart("SplashActivity"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause()
    {
        super.onPause();
        MobclickAgent.onPageEnd("SplashActivity"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}