package com.dstudio.wd.dweather;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dstudio.wd.dweather.adapter.DailyWt;
import com.dstudio.wd.dweather.adapter.SugAdapter;
import com.dstudio.wd.dweather.adapter.Suggestion;
import com.dstudio.wd.dweather.adapter.WtAdapter;
import com.dstudio.wd.dweather.database.Weather;
import com.dstudio.wd.dweather.http.HttpCallbackListener;
import com.dstudio.wd.dweather.http.HttpUtil;
import com.dstudio.wd.dweather.http.LocalCache;
import com.dstudio.wd.dweather.localdata.LocalData;
import com.dstudio.wd.dweather.tools.ListViewInScrollView;
import com.dstudio.wd.dweather.tools.WtImg;
import com.umeng.analytics.MobclickAgent;

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
    private DrawerLayout drawerLayout;
    private TextView txtUpdateSign;
    private String strParam;

    private ImageView imgWt;
    private TextView txtUpdateTime;
    private TextView txtNowWt;
    private TextView txtNowDgr;
    private TextView txtTodayWt;
    private TextView txtTodayTmp;

    private TextView txtAqiDscb;
    private TextView txtWindDscb;
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

    private final static int SAVE_PARA = 1;
    private final static String KEY = "478855289bf843bea01cbbf983887878";

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SAVE_PARA:
                    LocalData.save((String) msg.obj, strParam, mContext);
                    parseJson((String) msg.obj);
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
        bindView(view);
        String[] fileList = mContext.fileList();
        File file = getActivity().getFileStreamPath(getArguments().getString("key") + ".txt");
        if (!file.exists())
        {
            sendWtRequest(getArguments().getString("key"));
        }
        else
        {
            parseJson(LocalData.load(getArguments().getString("key"), mContext));
            txtUpdateSign.setVisibility(View.VISIBLE);
            sendWtRequest(getArguments().getString("key"));
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
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.main_layout);
        // pgbar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
        txtUpdateSign = (TextView) getActivity().findViewById(R.id.updating);

        /* 当前天气相关控件 */
        imgWt = (ImageView) view.findViewById(R.id.img_weather);
        txtUpdateTime = (TextView) view.findViewById(R.id.update_time);
        txtNowWt = (TextView) view.findViewById(R.id.txt_wether);
        txtNowDgr = (TextView) view.findViewById(R.id.txt_degree);
        txtTodayWt = (TextView) view.findViewById(R.id.txt_today_wt);
        txtTodayTmp = (TextView) view.findViewById(R.id.txt_today_tmp);
        txtNowDgr.setTypeface(fzltxh);
        txtNowWt.setTypeface(fzltxh);
        viewsNow = new View[] {txtNowWt, txtNowDgr, txtUpdateTime, txtTodayWt, txtTodayTmp};

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
            HttpUtil.sendHttpRequest(url, new HttpCallbackListener()
            {
                @Override
                public void onFinish(String result)
                {

                    Message message = new Message();
                    message.what = SAVE_PARA;
                    message.obj = result;
                    handler.sendMessage(message);
                }

                @Override
                public void onError(Exception e)
                {
                    e.printStackTrace();
                    showError();
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

            txtNowWt.setText(txtWeather);
            txtNowDgr.setText(txtTmp);
            txtUpdateTime.setText(MessageFormat.format("数据更新时间 {0}", updataTime));
            txtWindDscb.setText(MessageFormat.format("{0} {1}", windDir, windSc));
            txtHum.setText(MessageFormat.format("湿度 {0}%", hum));
            // 空气质量
            parseAqiData(datas.getJSONObject(0).optJSONObject("aqi"));
            // 7日天气预报
            JSONArray dailyFcst = datas.getJSONObject(0).getJSONArray("daily_forecast");
            parseDailyData(dailyFcst);
            // 生活指数
            parseSugData(datas.getJSONObject(0).getJSONObject("suggestion"));
            // 设置图片以及背景色
            new WtImg(imgWt, drawerLayout, mContext).judgeWt(txtWeather);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            showError();
        }
    }

    public void parseAqiData(JSONObject aqiInfo)
    {
        String[] aqiDatas = new String[8];
        try
        {
            if (aqiInfo != null)
            {
                Log.d("debug", "解析空气质量");
                JSONObject cityAqi = new JSONObject(aqiInfo.getString("city"));
                String aqi = cityAqi.getString("aqi");   // 空气质量指数
                String qlty = cityAqi.getString("qlty"); // 空气质量类别
                String co = cityAqi.getString("co");  // CO 一小时平均值
                String no2 = cityAqi.getString("no2");  // NO2
                String so2 = cityAqi.getString("so2");  // SO2
                String o3 = cityAqi.getString("o3"); // O3
                String pm10 = cityAqi.getString("pm10");  // PM10
                String pm25 = cityAqi.getString("pm25");  // PM2.5
                aqiDatas = new String[] {aqi, qlty, pm25, pm10, so2, no2, co, o3};
            }
            else
            {
                Arrays.fill(aqiDatas, "N/A");
            }
            txtAqiDscb.setText(MessageFormat.format("空气质量 {0} {1}", aqiDatas[0], aqiDatas[1]));
            txtAqi.setText(aqiDatas[0]);
            txtQlty.setText(aqiDatas[1]);
            txtPm25.setText(aqiDatas[2]);
            txtPm10.setText(aqiDatas[3]);
            txtSo2.setText(aqiDatas[4]);
            txtNo2.setText(aqiDatas[5]);
            txtCo.setText(aqiDatas[6]);
            txtO3.setText(aqiDatas[7]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            showError();
        }
    }

    /**
     * 解析7日天气预报
     *
     *
     */
    public void parseDailyData(final JSONArray dailyFcst)
    {
        if (dailyData.size() > 0)
        {
            dailyData.removeAll(dailyData);
            wtAdapter.notifyDataSetChanged();
            listDaily.setAdapter(wtAdapter);
        }

        try
        {
            Log.d("debug", "解析7天预报");
            Weather weather = new Weather(mContext);
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
            wtAdapter.notifyDataSetChanged();
            listDaily.setAdapter(wtAdapter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            showError();
        }
    }

    /**
     * 解析生活提示数据
     *
     */
    public void parseSugData(JSONObject sug)
    {
        try
        {
            String comfBrf = new JSONObject(sug.getString("comf")).getString("brf");   // 舒适度
            String comfTxt = new JSONObject(sug.getString("comf")).getString("txt");

            String cwBrf = new JSONObject(sug.getString("cw")).getString("brf");       // 洗车指数
            String cwTxt = new JSONObject(sug.getString("cw")).getString("txt");

            String drsgBrf = new JSONObject(sug.getString("drsg")).getString("brf");  // 穿衣指数
            String drsgTxt = new JSONObject(sug.getString("drsg")).getString("txt");

            String fluBrf = new JSONObject(sug.getString("flu")).getString("brf");    // 感冒指数
            String fluTxt = new JSONObject(sug.getString("flu")).getString("txt");

            String sportBrf = new JSONObject(sug.getString("sport")).getString("brf");   // 运动指数
            String sportTxt = new JSONObject(sug.getString("sport")).getString("txt");

            String travBrf = new JSONObject(sug.getString("trav")).getString("brf");    // 旅游指数
            String travTxt = new JSONObject(sug.getString("trav")).getString("txt");

            String uvBrf = new JSONObject(sug.getString("uv")).getString("brf");       // 防晒指数
            String uvTxt = new JSONObject(sug.getString("uv")).getString("txt");

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
            sugAdapter.notifyDataSetChanged();
            listSug.setAdapter(sugAdapter);
            txtUpdateSign.setVisibility(View.INVISIBLE);
        }
        catch (Exception e)
        {
            Log.e("ERROR", "sugData解析失败！");
            e.printStackTrace();
            showError();
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

    public void onResume()
    {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause()
    {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }
}
