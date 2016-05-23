package com.dstudio.wd.dweather;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.dstudio.wd.dweather.adapter.CityAdapter;
import com.dstudio.wd.dweather.adapter.CityItem;
import com.dstudio.wd.dweather.database.AutoCompleteAdapter;
import com.dstudio.wd.dweather.database.City;
import com.dstudio.wd.dweather.database.MyDatabaseHelper;
import com.dstudio.wd.dweather.database.OriginalData;
import com.dstudio.wd.dweather.http.HttpCallbackListener;
import com.dstudio.wd.dweather.http.HttpUtil;
import com.dstudio.wd.dweather.location.Location;
import com.dstudio.wd.dweather.location.MyLocationListener;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener
{
    private DrawerLayout mDrawerLayout;
    private LinearLayout leftDrawer;
    private TextView topTitle;
    private Button btnLeft;
    private Button btnRight;
    private AutoCompleteTextView searchTextView;
    private Button btnAddCity;

    private ProgressBar pgbar;
    private ScrollView scrollView;

    private Context mContext;
    private MyDatabaseHelper dbHelper;

    public LocationClient mLocationClient;
    private String lcCity;

    private List<CityItem> cityData = null;
    private CityAdapter cityAdapter = null;
    private ListView listCity;

    private boolean flag = false;   // 初次启动标志
    private boolean needRec = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        dbHelper = new MyDatabaseHelper(this, "CityInfo.db", null, 1);
        bindView();
        createOriData();
        initLocationClient();
    }

    /**
     * 初始化控件
     */
    public void bindView()
    {
        needRec = true;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_layout);
        mDrawerLayout.setScrimColor(getResources().getColor(R.color.white));
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener()
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                if (needRec)
                {
                    recoverCity();
                    needRec = false;
                }
            }
        });
        leftDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        pgbar = (ProgressBar) findViewById(R.id.progress_bar);
        topTitle = (TextView) findViewById(R.id.top_title);
        btnLeft = (Button) findViewById(R.id.left_button);
        btnRight = (Button) findViewById(R.id.right_button);
        searchTextView = (AutoCompleteTextView) findViewById(R.id.search_city);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        btnAddCity = (Button) findViewById(R.id.add_city);
        btnAddCity.setTypeface(iconfont);
        btnAddCity.setOnClickListener(this);

        btnLeft.setTypeface(iconfont);
        btnRight.setTypeface(iconfont);
        btnLeft.setOnClickListener(this);

        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(mContext, android.R.layout.simple_dropdown_item_1line,
                null, new String[] {"city_name"}, new int[] {android.R.id.text1});
        searchTextView.setThreshold(1);
        searchTextView.setTypeface(iconfont);
        searchTextView.setHint(R.string.search);
        searchTextView.setAdapter(autoCompleteAdapter);
        searchTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                showCityList(textView.getText().toString().replace("市", ""));
                searchTextView.setText("");
            }
        });

        cityData = new LinkedList<CityItem>();
        listCity = (ListView) findViewById(R.id.list_city);
        cityAdapter = new CityAdapter((LinkedList<CityItem>) cityData, mContext);
        listCity.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                TextView txtCityName = (TextView) view.findViewById(R.id.item_city_name);
                showWeather(txtCityName.getText().toString());
                topTitle.setText(txtCityName.getText().toString());
                mDrawerLayout.closeDrawer(leftDrawer);
            }
        });

        listCity.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                ((LinkedList<CityItem>) cityData).remove(i);
                cityAdapter.notifyDataSetChanged();
                return false;
            }
        });

    }

    /**
     * 初始化LocationClient类
     */
    public void initLocationClient()
    {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new BDLocationListener()
        {
            @Override
            public void onReceiveLocation(BDLocation bdLocation)
            {
                Log.i("BaiduMap", bdLocation.getCity());
                lcCity = bdLocation.getCity().replace("市", "");
                Log.d("debug", "initLocationClient方法中调用了showWeather");
                showWeather(lcCity);
                showCityList(lcCity);
                topTitle.setText(lcCity);

            }
        });
        new Location(mLocationClient).initLocation();
        mLocationClient.start();
    }

    /**
     * 获取天气
     * @param cityName 城市名称
     */
    public void showWeather(String cityName)
    {
        FgMain fgMain = new FgMain();
        Bundle bundle = new Bundle();
        // 若为首次启动，使用城市名称请求数据，否则使用ID
        if (flag)
        {
            bundle.putString("key", cityName);
        }
        else
        {
            String cityId = new City(dbHelper).queryCityId(cityName);
            Log.d("debug", cityName + " 的ID是：" + cityId);
            bundle.putString("key", cityId);
        }
        fgMain.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frag_layout, fgMain).commit();
        pgbar.setVisibility(View.VISIBLE);
        scrollView.smoothScrollTo(0, 0);
    }

    public void showCityList(final String cityName)
    {
        String KEY = getString(R.string.key);
        String parm;
        String url;
        if (flag)
        {
            parm = cityName;
            url = getString(R.string.wt_api_no_parm) + "city=" + parm + "&key=" + KEY;
        }
        else
        {
            parm = new City(dbHelper).queryCityId(cityName);
            url = getString(R.string.wt_api_no_parm) + "cityid=" + parm + "&key=" + KEY;
        }
        if (!parm.equals(null))
        {
            Log.d("debug", "ok!");
            HttpUtil.sendHttpRequest(url, new HttpCallbackListener()
            {
                @Override
                public void onFinish(String response)
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray datas = jsonObject.getJSONArray("HeWeather data service 3.0");
                        JSONObject nowWeather = datas.getJSONObject(0).getJSONObject("now");
                        final String txtTmp = nowWeather.getString("tmp") + "℃";

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                String cityTmp  = txtTmp;
                                cityData.add(new CityItem(cityName, cityTmp));
                                listCity.setAdapter(cityAdapter);
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e)
                {

                }
            });
        }
        else
        {
        }
    }

    /**
     * 第一次启动， 建立城市数据库/天气图标数据库
     */
    public void createOriData()
    {
        if(judgeVersion())
        {
            flag = true;
            String CITY_API = getString(R.string.city_api);

            dbHelper.getWritableDatabase();
            HttpUtil.sendHttpRequest(CITY_API, new HttpCallbackListener()
            {
                @Override
                public void onFinish(String response)
                {
                    new OriginalData(dbHelper, mContext, lcCity).parseJSONForCity(response);
                }

                @Override
                public void onError(Exception e)
                {
                    Log.d("MainActivity", "获取数据失败 (✖╭╮✖)");
                }
            });

            String wtIconApi = getString(R.string.weather_icon_api);
            HttpUtil.sendHttpRequest(wtIconApi, new HttpCallbackListener()
            {
                @Override
                public void onFinish(String response)
                {
                    Log.d("debug", "ICON, OK");
                    new OriginalData(dbHelper, mContext, lcCity).parseJSONForWt(response);
                }

                @Override
                public void onError(Exception e)
                {
                    Log.e("ERROR", "获取天气列表失败");
                }
            });
        }
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
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.add_city:
                if (searchTextView.getVisibility() == View.GONE)
                {
                    searchTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    searchTextView.setVisibility(View.GONE);
                }
                break;
            case R.id.left_button:
                mDrawerLayout.openDrawer(leftDrawer);
                break;
            default:
                break;
        }
    }

    /**
     * 活动销毁，保存抽屉中已添加的城市名称
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mLocationClient.stop();
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putInt("city_num", cityAdapter.getCount());
        for (int i = 1; i < cityAdapter.getCount(); i++)
        {
            editor.putString("city_" + i, cityData.get(i).getCityName());
            Log.d("debug", "saved：" + cityData.get(i).getCityName());
        }
        editor.commit();
    }

    /**
     * 恢复城市名称
     */
    public void recoverCity()
    {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int cityNum = pref.getInt("city_num", 0);
        for (int i = 1; i < cityNum; i++)
        {
            Log.d("debug", "recover city_" + i + ", " + pref.getString("city_" + i, ""));
            showCityList(pref.getString("city_" + i, ""));
        }
    }
}
