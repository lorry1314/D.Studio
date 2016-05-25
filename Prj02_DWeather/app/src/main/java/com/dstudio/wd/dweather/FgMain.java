package com.dstudio.wd.dweather;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dstudio.wd.dweather.adapter.DailyWt;
import com.dstudio.wd.dweather.adapter.SugAdapter;
import com.dstudio.wd.dweather.adapter.Suggestion;
import com.dstudio.wd.dweather.adapter.WtAdapter;
import com.dstudio.wd.dweather.database.MyDatabaseHelper;
import com.dstudio.wd.dweather.database.Weather;
import com.dstudio.wd.dweather.http.HttpCallbackListener;
import com.dstudio.wd.dweather.http.HttpUtil;
import com.dstudio.wd.dweather.http.LocalCache;
import com.dstudio.wd.dweather.localdata.LocalData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wd824 on 2016/5/21.
 */
public class FgMain extends Fragment
{
    private Context mContext;
    private ProgressBar pgbar;
    private MyDatabaseHelper dbHelper;
    private String strParam;

    private ImageView imgWt;
    private TextView txtUpdateTime;
    private TextView txtNowWt;
    private TextView txtNowDgr;
    private TextView txtTodayWt;
    private TextView txtTodayTmp;

    private TextView txtAqiDscb;
    private TextView txtWindDscb;;
    private TextView txtHum;

    private TextView txtAqi;
    private TextView txtQlty;
    private TextView txtPm25;
    private TextView txtPm10;
    private TextView txtSo2;
    private TextView txtNo2;
    private TextView txtCo;
    private TextView txtO3;

    View[] viewsNow = null;
    View[] viewsAqi = null;

    private List<Suggestion> sugData = null;
    private SugAdapter sugAdapter = null;
    private ListViewInScrollView listSug;

    private List<DailyWt> dailyData = null;
    private WtAdapter wtAdapter = null;
    private ListViewInScrollView listDaily;

    private final static int SHOW_NOW = 1;
    private final static int SHOW_AQI = 2;
    private final static int SAVE_PARA = 3;
    private final static String KEY = "478855289bf843bea01cbbf983887878";

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SHOW_NOW:
                    String[] nowWeather = (String[]) msg.obj;
                    txtNowWt.setText(nowWeather[0]);
                    txtNowDgr.setText(nowWeather[1]);
                    txtUpdateTime.setText(MessageFormat.format("数据更新时间 {0}", nowWeather[2]));
                    txtWindDscb.setText(MessageFormat.format("{0} {1}", nowWeather[3], nowWeather[4]));
                    txtHum.setText(MessageFormat.format("湿度 {0}%", nowWeather[5]));
                    new WtImg(imgWt).judgeWt(nowWeather[0]);
                    break;
                case SHOW_AQI:
                    String[] aqiDatas = (String[]) msg.obj;
                    txtAqiDscb.setText(MessageFormat.format("空气质量 {0} {1}", aqiDatas[0], aqiDatas[1]));
                    txtAqi.setText(aqiDatas[0]);
                    txtQlty.setText(aqiDatas[1]);
                    txtPm25.setText(aqiDatas[2]);
                    txtPm10.setText(aqiDatas[3]);
                    txtSo2.setText(aqiDatas[4]);
                    txtNo2.setText(aqiDatas[5]);
                    txtCo.setText(aqiDatas[6]);
                    txtO3.setText(aqiDatas[7]);
                    break;
                case SAVE_PARA:
                    LocalData.save((String) msg.obj, strParam, mContext);
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.e("fg", "onCreatView");
        View view = inflater.inflate(R.layout.fg_main, container, false);

        mContext = getActivity();
        dbHelper = new MyDatabaseHelper(mContext, "CityInfo.db", null, 1);
        bindView(view);
        String[] fileList = mContext.fileList();
        File file = getActivity().getFileStreamPath(getArguments().getString("key") + ".txt");
        Log.d("debug", file.exists() + "");


        if (!file.exists())
        {
            sendWtRequest(getArguments().getString("key"));
        }
        else
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.d("debug", getArguments().getString("key"));
                    parseJson(LocalData.load(getArguments().getString("key"), mContext));
                    Log.d("debug", "本地数据");
                    sendWtRequest(getArguments().getString("key"));
                }
            }).start();

        }
        return view;
    }

    /**
     * 控件初始化
     * @param view
     */
    public void bindView(View view)
    {
        Typeface fzltxh = Typeface.createFromAsset(getActivity().getAssets(), "fzltxh.TTF");
        pgbar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);

        /* 当前天气相关控件 */
        imgWt = (ImageView) view.findViewById(R.id.img_weather);
        txtUpdateTime = (TextView) view.findViewById(R.id.update_time);
        txtNowWt = (TextView) view.findViewById(R.id.txt_wether);
        txtNowDgr = (TextView) view.findViewById(R.id.txt_degree);
        txtTodayWt = (TextView) view.findViewById(R.id.txt_today_wt);
        txtTodayTmp = (TextView) view.findViewById(R.id.txt_today_tmp);
        txtNowDgr.setTypeface(fzltxh);
        txtNowWt.setTypeface(fzltxh);
        viewsNow = new View[] {txtNowWt, txtNowDgr, txtUpdateTime, txtTodayWt, txtTodayTmp, pgbar};

        txtAqiDscb = (TextView) view.findViewById(R.id.aqi_describe);
        txtWindDscb = (TextView) view.findViewById(R.id.wind_describe);
        txtHum = (TextView) view.findViewById(R.id.hum);

        /* 空气指数相关控件 */
        txtAqi = (TextView) view.findViewById(R.id.txt_aqi);
        txtQlty = (TextView) view.findViewById(R.id.txt_qlty);
        txtPm25 = (TextView) view.findViewById(R.id.txt_pm25);
        txtPm10 = (TextView) view.findViewById(R.id.txt_pm10);
        txtSo2 = (TextView) view.findViewById(R.id.txt_so2);
        txtNo2 = (TextView) view.findViewById(R.id.txt_no2);
        txtCo = (TextView) view.findViewById(R.id.txt_co);
        txtO3 = (TextView) view.findViewById(R.id.txt_o3);
        viewsAqi = new View[] {txtAqi, txtQlty, txtPm25, txtPm10, txtSo2, txtNo2, txtCo, txtO3};

        /* 生活指数 */
        listSug = (ListViewInScrollView) view.findViewById(R.id.list_sug);
        sugData = new LinkedList<>();
        sugAdapter = new SugAdapter((LinkedList<Suggestion>) sugData, mContext);
        /* 7日天气 */
        listDaily = (ListViewInScrollView) view.findViewById(R.id.daily_list);
        dailyData = new LinkedList<>();
        wtAdapter = new WtAdapter((LinkedList<DailyWt>) dailyData, mContext);
    }

    /**
     * 发送数据请求
     *
     */
    public void sendWtRequest(String para)
    {
        /*
          若参数包含"CN", 代表参数为城市id，通过id发送请求；否则通过城市名称发送请求
         */
        if (para != null)
        {
            strParam = para;
            String url = "https://api.heweather.com/x3/weather?cityid=" + para + "&key=" + KEY;
            if (!para.startsWith("CN"))
            {
                url = "https://api.heweather.com/x3/weather?city=" + para + "&key=" + KEY;
            }

            HttpUtil.sendHttpRequest(url, new HttpCallbackListener()
            {
                @Override
                public void onFinish(String result)
                {

                    Message message = new Message();
                    message.what = SAVE_PARA;
                    message.obj = result;
                    handler.sendMessage(message);
                    Log.d("debug", "网络数据");
                    parseJson(result);
                    pgbar.setVisibility(View.GONE);
                    LocalData.save(result, strParam, mContext);
                }

                @Override
                public void onError(Exception e)
                {

                }
            });
        }
        else
        {
            Toast.makeText(mContext, "出错了 ( ▼-▼ )", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 解析服务器返回天气数据
     * @param response 返回数据
     */
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
            String windDir = wind.getString("dir");            // 风向
            String windSc = wind.getString("sc") + "级";       // 风力
            String hum = nowWeather.getString("hum");          // 湿度
            String vis = nowWeather.getString("vis") + "km";   // 能见度

            String[] nowDatas = {txtWeather, txtTmp, updataTime, windDir, windSc, hum, vis};  // 实时天气及今日天气信息
            Message msgNow = new Message();
            msgNow.what = SHOW_NOW;
            msgNow.obj = nowDatas;
            handler.sendMessage(msgNow);

            if (datas.getJSONObject(0).optString("aqi").equals(""))
            {
                String[] aqiDatas = new String[8];
                Arrays.fill(aqiDatas, "N/A");
                Message message = new Message();
                message.what = SHOW_AQI;
                message.obj = aqiDatas;
                handler.sendMessage(message);
            }
            else
            {
                parseAqiData(datas.getJSONObject(0).getJSONObject("aqi"));  // 空气指数
            }

            // 7日天气预报
            JSONArray dailyFcst = datas.getJSONObject(0).getJSONArray("daily_forecast");
            parseDailyData(dailyFcst);

            // 生活指数
            parseSugData(datas.getJSONObject(0).getJSONObject("suggestion"));

        }
        catch (Exception e)
        {
            showError();
            e.printStackTrace();
        }
    }

    public void parseAqiData(JSONObject aqiInfo)
    {
        try
        {
            JSONObject cityAqi = new JSONObject(aqiInfo.getString("city"));
            String txtAqi = cityAqi.getString("aqi");   // 空气质量指数
            Log.d("debug", txtAqi);
            String txtQlty = cityAqi.getString("qlty"); // 空气质量类别
            String txtCO = cityAqi.getString("co");  // CO 一小时平均值
            String txtNO2 = cityAqi.getString("no2");  // NO2
            String txtSO2 = cityAqi.getString("so2");  // SO2
            String txtO3 = cityAqi.getString("o3"); // O3
            String txtPm10 = cityAqi.getString("pm10");  // PM10
            String txtPm25 = cityAqi.getString("pm25");  // PM2.5
            String[] aqiDatas = {txtAqi, txtQlty, txtPm25, txtPm10, txtSO2, txtNO2, txtCO, txtO3};

            Message message = new Message();
            message.what = SHOW_AQI;
            message.obj = aqiDatas;
            handler.sendMessage(message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 解析7日天气预报
     *
     *
     */
    public void parseDailyData(final JSONArray dailyFcst)
    {

        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                if (dailyData.size() > 0)
                {
                    dailyData.removeAll(dailyData);
                    wtAdapter.notifyDataSetChanged();
                    listDaily.setAdapter(wtAdapter);
                }


                try
                {
                    Weather weather = new Weather(dbHelper);
                    for (int i = 0; i < dailyFcst.length(); i++)
                    {
                        String txtDate = dailyFcst.getJSONObject(i).getString("date").substring(5);   // 日期
                        JSONObject dateCond = dailyFcst.getJSONObject(i).getJSONObject("cond");
                        String txtDay = dateCond.getString("txt_d");  // 白天天气
                        String txtNight = dateCond.getString("txt_n"); // 晚间天气
                        JSONObject dateTmp = dailyFcst.getJSONObject(i).getJSONObject("tmp");
                        String minTmp = dateTmp.getString("min");  // 最低气温
                        String maxTmp = dateTmp.getString("max");  // 最高气温
                        String txt = txtDay.equals(txtNight) ? txtDay : txtDay + "转" + txtNight;
                        if (i == 0)
                        {
                            txtTodayWt.setText(txt);
                            txtTodayTmp.setText(MessageFormat.format("{0}~{1}℃", minTmp, maxTmp));
                        }
                        Log.d("debug", txtDay + ", " + weather.queryWt(txtDay) );
                        Bitmap wtIcon = LocalCache.readImgCache(mContext, weather.queryWt(txtDay), "ICON");
                        Log.d("debug", (wtIcon == null) + "");
                        dailyData.add(new DailyWt(txtDate, wtIcon, txt, maxTmp, minTmp));
                    }
                    listDaily.setAdapter(wtAdapter);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 解析生活提示数据
     *
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

            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (sugData.size() > 0)
                    {
                        sugData.removeAll(sugData);
                        sugAdapter.notifyDataSetChanged();
                        listSug.setAdapter(sugAdapter);
                    }
                    sugData.add(new Suggestion(R.drawable.cfm, " 舒适度：", comfBrf, comfTxt));
                    sugData.add(new Suggestion(R.drawable.xiche, " 洗车指数：", cwBrf, cwTxt));
                    sugData.add(new Suggestion(R.drawable.chuanyi, " 穿衣指数：", drsgBrf, drsgTxt));
                    sugData.add(new Suggestion(R.drawable.ganmao, " 感冒指数：", fluBrf, fluTxt));
                    sugData.add(new Suggestion(R.drawable.yundong, " 运动指数：", sportBrf, sportTxt));
                    sugData.add(new Suggestion(R.drawable.lvyou, " 旅游指数：", travBrf, travTxt));
                    sugData.add(new Suggestion(R.drawable.fangshai, " 防晒指数：", uvBrf, uvTxt));
                    // sugAdapter.notifyDataSetChanged();
                    listSug.setAdapter(sugAdapter);
                }
            });
        }
        catch (Exception e)
        {
            Log.e("ERROR", "sugData解析失败！");
            e.printStackTrace();
        }
    }

    public void showError()
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(mContext, "出错了.. :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.e("fg", "onCreat");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.e("fg", "onResume");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.e("fg", "onPause");
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.e("fg", "onDestoryView");
    }
}
