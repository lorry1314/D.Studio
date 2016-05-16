package com.dstudio.wd.dweather;


import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.dstudio.wd.dweather.adapter.DailyWt;
import com.dstudio.wd.dweather.adapter.SugAdapter;
import com.dstudio.wd.dweather.adapter.Suggestion;
import com.dstudio.wd.dweather.adapter.WtAdapter;
import com.dstudio.wd.dweather.data.WeatherData;
import com.dstudio.wd.dweather.database.AutoCompleteAdapter;
import com.dstudio.wd.dweather.database.MyDatabaseHelper;
import com.dstudio.wd.dweather.http.HttpCallbackListener;
import com.dstudio.wd.dweather.http.HttpUtil;
import com.dstudio.wd.dweather.location.MyLocationListener;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity
{
    private TextView topTitle;
    private Button btnLeft;
    private Button btnRight;
    private AutoCompleteTextView searchTextView;
    private ProgressBar pgbar;
    private ScrollView scrollView;
    private TextView txtUpdateTime;
    private TextView txtNowWt;
    private TextView txtNowDgr;
    private TextView txtTodayWt;
    private TextView txtTodayTmp;

    private List<Suggestion> sugData = null;
    private SugAdapter sugAdapter = null;
    private ListViewInScrollView listSug;

    private List<DailyWt> dailyData = null;
    private WtAdapter wtAdapter = null;
    private ListViewInScrollView listDaily;

    private Context mContext;
    private MyDatabaseHelper dbHelper;

    public LocationClient mLocationClient;
    public BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        bindView();

        dbHelper = new MyDatabaseHelper(this, "CityInfo.db", null, 1);

        /**
         * 第一次启动， 建立城市数据库
         */
        if(judgeVersion())
        {
            String CITY_API = "https://api.heweather.com/x3/citylist?search=allchina&key=478855289bf843bea01cbbf983887878";
            dbHelper.getWritableDatabase();
            HttpUtil.sendHttpRequest(CITY_API, new HttpCallbackListener()
            {
                @Override
                public void onFinish(String response)
                {
                    parseJSONForCity(response);
                }

                @Override
                public void onError(Exception e)
                {
                    Log.d("MainActivity", "获取数据失败 (✖╭╮✖)");
                }
            });
        }

        initLocationClient();
    }

    /**
     * 初始化控件
     */
    public void bindView()
    {
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        pgbar = (ProgressBar) findViewById(R.id.progress_bar);
        topTitle = (TextView) findViewById(R.id.top_title);
        btnLeft = (Button) findViewById(R.id.left_button);
        btnRight = (Button) findViewById(R.id.right_button);
        searchTextView = (AutoCompleteTextView) findViewById(R.id.search_city);
        btnRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (searchTextView.getVisibility() == View.GONE)
                {
                    searchTextView.setVisibility(View.VISIBLE);
                    scrollView.scrollTo(0, 0);
                }
                else
                {
                    searchTextView.setVisibility(View.GONE);
                }
                scrollView.smoothScrollTo(0, 0);
            }
        });

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        btnLeft.setTypeface(iconfont);
        btnRight.setTypeface(iconfont);
        topTitle.setTypeface(iconfont);

        txtUpdateTime = (TextView) findViewById(R.id.update_time);
        txtNowWt = (TextView) findViewById(R.id.txt_wether);
        txtNowDgr = (TextView) findViewById(R.id.txt_degree);
        txtTodayWt = (TextView) findViewById(R.id.txt_today_wt);
        txtTodayTmp = (TextView) findViewById(R.id.txt_today_tmp);

        final View[] viewsNowWt = {txtNowWt, txtNowDgr, txtUpdateTime, txtTodayWt, txtTodayTmp, pgbar};

        listSug = (ListViewInScrollView) findViewById(R.id.list_sug);
        sugData = new LinkedList<Suggestion>();
        sugAdapter = new SugAdapter((LinkedList<Suggestion>) sugData, mContext);

        listDaily = (ListViewInScrollView) findViewById(R.id.daily_list);
        dailyData = new LinkedList<DailyWt>();
        wtAdapter = new WtAdapter((LinkedList<DailyWt>) dailyData, mContext);

        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(mContext, android.R.layout.simple_dropdown_item_1line,
                null, new String[] {"city_name"}, new int[] {android.R.id.text1});
        searchTextView.setThreshold(1);
        searchTextView.setAdapter(autoCompleteAdapter);
        searchTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                TextView textView  = (TextView) view.findViewById(android.R.id.text1);
                getWeather((String) textView.getText(), viewsNowWt);
                topTitle.setText(textView.getText());
                searchTextView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 初始化LocationClient类
     */
    public void initLocationClient()
    {
        final View[] viewsNowWt = {txtNowWt, txtNowDgr, txtUpdateTime, txtTodayWt, txtTodayTmp, pgbar};

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new BDLocationListener()
        {
            @Override
            public void onReceiveLocation(BDLocation bdLocation)
            {
                Log.i("BaiduMap", bdLocation.getCity());
                getWeather(bdLocation.getCity().replace("市", ""), viewsNowWt);
                topTitle.setText(bdLocation.getCity().replace("市", ""));
            }
        });


        // mLocationClient.registerLocationListener(new MyLocationListener());
        initLocation();
        mLocationClient.start();
    }

    /**
     * 设置定位参数
     */
    private void initLocation()
    {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系

        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    /**
     * 获取天气
     * @param cityName
     * @param views
     */
    public void getWeather(String cityName, View[] views)
    {
        WeatherData weatherData = new WeatherData(dbHelper, cityName, mContext);
        weatherData.initView(views);
        weatherData.sendWtRequest();
        weatherData.setSug(sugData, sugAdapter, listSug, scrollView);
        weatherData.setDaily(dailyData, wtAdapter, listDaily);
        pgbar.setVisibility(View.VISIBLE);
    }

    /**
     * 解析获取的城市数据，写入数据库
     * @param response 服务器返回JSON数据
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
        }
        catch (Exception e)
        {
            Log.d("MainActivity", "数据解析失败 (✖╭╮✖)");
            e.printStackTrace();
        }

    }

    /**
     * 判断是否为第一次启动
     * @return
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

        SharedPreferences sp = getSharedPreferences("welcomeInfo", MODE_PRIVATE);
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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mLocationClient.stop();
    }
}
