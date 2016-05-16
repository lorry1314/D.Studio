package com.dstudio.wd.dweather.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dstudio.wd.dweather.GridViewInScrollView;
import com.dstudio.wd.dweather.ListViewInScrollView;
import com.dstudio.wd.dweather.MainActivity;
import com.dstudio.wd.dweather.R;
import com.dstudio.wd.dweather.adapter.DailyWt;
import com.dstudio.wd.dweather.adapter.SugAdapter;
import com.dstudio.wd.dweather.adapter.Suggestion;
import com.dstudio.wd.dweather.adapter.WtAdapter;
import com.dstudio.wd.dweather.database.MyDatabaseHelper;
import com.dstudio.wd.dweather.http.HttpCallbackListener;
import com.dstudio.wd.dweather.http.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by wd824 on 2016/5/12.
 */
public class WeatherData extends MainActivity
{
    private Context context;
    private String cityName;
    private MyDatabaseHelper dbHelper;

    private TextView txtUpdateTime;
    private TextView txtNowWt;
    private TextView txtNowDgr;
    private TextView txtTodayWt;
    private TextView txtTodayTmp;
    private ProgressBar pgBar;
    private ScrollView scrollView;

    private String[] nowWeather;

    private List<Suggestion> sugData = null;
    private SugAdapter sugAdapter = null;
    private ListViewInScrollView listSug;

    private List<DailyWt> dailyData = null;
    private WtAdapter wtAdapter = null;
    private ListViewInScrollView listDaily;

    private final static int SHOW_NOW = 1;
    private final static int SHOW_SUG = 2;

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SHOW_NOW:
                    nowWeather = (String[]) msg.obj;
                    txtNowWt.setText(nowWeather[0]);
                    txtNowDgr.setText(nowWeather[1]);
                    txtUpdateTime.setText("数据更新时间 " + nowWeather[2]);
                    txtTodayWt.setText(nowWeather[3]);
                    txtTodayTmp.setText(nowWeather[4]);
                    pgBar.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    private final static String KEY = "478855289bf843bea01cbbf983887878";

    public WeatherData(MyDatabaseHelper dbHelper, String cityName, Context context)
    {
        this.dbHelper = dbHelper;
        this.cityName = cityName;
        this.context = context;
    }

    public void initView(View[] views)
    {
        txtNowWt = (TextView) views[0];
        txtNowDgr = (TextView) views[1];
        txtUpdateTime = (TextView) views[2];
        txtTodayWt = (TextView) views[3];
        txtTodayTmp = (TextView) views[4];
        pgBar = (ProgressBar) views[5];
    }

    /**
     * 查询城市ID
     * @return
     */
    public String queryCityId()
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


    public void setSug(List<Suggestion> sugData, SugAdapter sugAdapter, ListViewInScrollView listSug, ScrollView scrollView)
    {
        this.sugData = sugData;
        this.sugAdapter = sugAdapter;
        this.listSug = listSug;
        this.scrollView = scrollView;
    }

    public void setDaily(List<DailyWt> dailyData, WtAdapter wtAdapter, ListViewInScrollView listDaily)
    {
        this.dailyData = dailyData;
        this.wtAdapter = wtAdapter;
        this.listDaily = listDaily;
    }

    public void sendWtRequest()
    {
        if (!queryCityId().equals(null))
        {
            String url = "https://api.heweather.com/x3/weather?cityid=" + queryCityId() + "&key=" + KEY;
            HttpUtil.sendHttpRequest(url, new HttpCallbackListener()
            {
                @Override
                public void onFinish(String response)
                {
                    parseJson(response);
                }

                @Override
                public void onError(Exception e)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(), "获取数据失败 (✖╭╮✖)", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        else
        {
            Toast.makeText(context, "出错了 ( ▼-▼ )", Toast.LENGTH_LONG).show();
        }
    }

    public void parseJson(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray datas = jsonObject.getJSONArray("HeWeather data service 3.0");

            JSONObject basicInfo = datas.getJSONObject(0).getJSONObject("basic");
            JSONObject updateInfo = new JSONObject(basicInfo.getString("update"));
            String updataTime = updateInfo.getString("loc").substring(11);  // 数据更新时间（当前时区）

            JSONObject nowWeather = datas.getJSONObject(0).getJSONObject("now");
            JSONObject cond = new JSONObject(nowWeather.getString("cond"));
            String txtWeather = cond.getString("txt");             // 当前天气情况
            String txtTmp = nowWeather.getString("tmp") + "℃";     // 当前气温

            JSONObject wind = new JSONObject(nowWeather.getString("wind"));
            String txtWind = wind.getString("dir") + " " + wind.getString("sc") + "级";  // 风向，风力

            JSONObject aqiInfo = datas.getJSONObject(0).getJSONObject("aqi");
            JSONObject cityAqi = new JSONObject(aqiInfo.getString("city"));
            String txtAqi = cityAqi.getString("aqi");   // 空气质量指数
            String txtQlty = cityAqi.getString("qlty"); // 空气质量类别
            String txtCO = cityAqi.getString("co");  // CO 一小时平均值
            String txtNO2 = cityAqi.getString("no2");  // NO2
            String txtSO2 = cityAqi.getString("so2");  // SO2
            String txtO3 = cityAqi.getString("o3"); // O3
            String txtPm10 = cityAqi.getString("pm10");  // PM10
            String txtPm25 = cityAqi.getString("pm25");  // PM2.5

            Log.d("debug", "当前天气情况：" + txtWeather + ", 气温：" + txtTmp + "," + txtWind + ", 空气指数："
                    + txtAqi + " " + txtQlty + ", 数据更新时间：" + updataTime);

            // 7日天气预报

            JSONArray dailyFcst = datas.getJSONObject(0).getJSONArray("daily_forecast");
            String[] todayFcst = parseDailyData(dailyFcst);

            // 生活指数
            parseSugData(datas.getJSONObject(0).getJSONObject("suggestion"));

            String[] nowDatas = {txtWeather, txtTmp, updataTime, todayFcst[0], todayFcst[1]};  // 实时天气及今日天气信息

            Message message = new Message();
            message.what = SHOW_NOW;
            message.obj = nowDatas;
            handler.sendMessage(message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String[] parseDailyData(final JSONArray dailyFcst)
    {
        String todayWt = null;
        String todayTmp = null;
        String[] todayFcst = null;
        try
        {
            for (int i = 0; i < dailyFcst.length(); i++)
            {
                String txtDate = dailyFcst.getJSONObject(i).getString("date");   // 日期
                JSONObject dateCond = dailyFcst.getJSONObject(i).getJSONObject("cond");
                String txtDay = dateCond.getString("txt_d");  // 白天天气
                String txtNight = dateCond.getString("txt_n"); // 晚间天气
                JSONObject dateTmp = dailyFcst.getJSONObject(i).getJSONObject("tmp");
                String minTmp = dateTmp.getString("min");  // 最低气温
                String maxTmp = dateTmp.getString("max");  // 最高气温
                String txt = txtDay.equals(txtNight) ? txtDay : txtDay + "转" + txtNight;
                if (i == 0)
                {
                    todayWt = txt;
                    todayTmp = minTmp + "~" + maxTmp + "℃";
                }
                todayFcst = new String[] {todayWt, todayTmp};
                dailyData.add(new DailyWt(txtDate, txt, maxTmp, minTmp));
            }

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    listDaily.setAdapter(wtAdapter);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return todayFcst;
    }

    /**
     * 解析生活提示数据
     * @param sug
     */
    public void parseSugData(JSONObject sug)
    {
        try
        {
            final String comfBrf = new JSONObject(sug.getString("comf")).getString("brf");   // 舒适度
            final String comfTxt = new JSONObject(sug.getString("comf")).getString("txt");

            final String cwBrf = new JSONObject(sug.getString("cw")).getString("brf");       // 洗车指数
            final String cwTxt = new JSONObject(sug.getString("cw")).getString("txt");

            final String drsgBrf = new JSONObject(sug.getString("drsg")).getString("brf");  // 穿衣指数
            final String drsgTxt = new JSONObject(sug.getString("drsg")).getString("txt");

            final String fluBrf = new JSONObject(sug.getString("flu")).getString("brf");    // 感冒指数
            final String fluTxt = new JSONObject(sug.getString("flu")).getString("txt");

            final String sportBrf = new JSONObject(sug.getString("sport")).getString("brf");   // 运动指数
            final String sportTxt = new JSONObject(sug.getString("sport")).getString("txt");

            final String travBrf = new JSONObject(sug.getString("trav")).getString("brf");    // 旅游指数
            final String travTxt = new JSONObject(sug.getString("trav")).getString("txt");

            final String uvBrf = new JSONObject(sug.getString("uv")).getString("brf");       // 防晒指数
            final String uvTxt = new JSONObject(sug.getString("uv")).getString("txt");

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (sugData.size() > 0)
                    {
                        sugData.removeAll(sugData);
                        sugAdapter.notifyDataSetChanged();
                    }

                    sugData.add(new Suggestion(R.drawable.cfm, " 舒适度：", comfBrf, comfTxt));
                    sugData.add(new Suggestion(R.drawable.xiche, " 洗车指数：", cwBrf, cwTxt));
                    sugData.add(new Suggestion(R.drawable.chuanyi, " 穿衣指数：", drsgBrf, drsgTxt));
                    sugData.add(new Suggestion(R.drawable.ganmao, " 感冒指数：", fluBrf, fluTxt));
                    sugData.add(new Suggestion(R.drawable.yundong, " 运动指数：", sportBrf, sportTxt));
                    sugData.add(new Suggestion(R.drawable.lvyou, " 旅游指数：", travBrf, travTxt));
                    sugData.add(new Suggestion(R.drawable.fangshai, " 防晒指数：", uvBrf, uvTxt));
                    sugAdapter.notifyDataSetChanged();
                    listSug.setAdapter(sugAdapter);
                    scrollView.smoothScrollTo(0, 0);
                }
            });
        }
        catch (Exception e)
        {
            Log.e("ERROR", "sugData解析失败！");
            e.printStackTrace();
        }
    }

}

