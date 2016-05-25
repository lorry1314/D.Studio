package com.dstudio.wd.dweather;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.dstudio.wd.dweather.localdata.LocalData;
import com.dstudio.wd.dweather.location.Location;
import com.dstudio.wd.dweather.location.MyLocationListener;
import com.dstudio.wd.dweather.tools.Judgement;

import org.json.JSONArray;
import org.json.JSONObject;
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

    private boolean firstFlag = false;   // 初次启动标志
    private boolean needRec = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        dbHelper = new MyDatabaseHelper(this, "CityInfo.db", null, 1);
        bindView();           // 初始化控件
        setViewListener();
        if (new Judgement(mContext).isNetworkAvailable())
        {
            pgbar.setVisibility(View.VISIBLE);
            createOriData();      // 若为首次启动，建立本地数据库
            initLocationClient(); // 定位初始化
        }
        else
        {
            /*
             当前无网络时，由SharedPreferences保存的城市名称获取本地数据
             */
            Toast.makeText(mContext, "当前无网络 :(", Toast.LENGTH_LONG).show();
            String savedCityName = getSharedPreferences("city", MODE_PRIVATE).getString("city", "");
            if (!savedCityName.equals(""))
            {
                showWeather(savedCityName);
                topTitle.setText(savedCityName);
            }
        }
        needRec = true;
    }

    /**
     * 初始化控件
     */
    public void bindView()
    {
        needRec = true;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_layout);
        mDrawerLayout.setScrimColor(getResources().getColor(R.color.white));

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

        cityData = new LinkedList<>();
        listCity = (ListView) findViewById(R.id.list_city);
        cityAdapter = new CityAdapter((LinkedList<CityItem>) cityData, mContext);
    }

    /**
     * 设置控件监听事件
     */
    public void setViewListener()
    {
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

        listCity.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (new Judgement(mContext).isNetworkAvailable())
                {
                    TextView txtCityName = (TextView) view.findViewById(R.id.item_city_name);
                    showWeather(txtCityName.getText().toString());
                    topTitle.setText(txtCityName.getText().toString());
                    // topTitle.setCompoundDrawables(null, null, null, null);
                    mDrawerLayout.closeDrawer(leftDrawer);
                }
                else
                {
                    Toast.makeText(mContext, "当前无网络 :(", Toast.LENGTH_LONG).show();
                }
            }
        });

        listCity.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                cityData.remove(i);
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
                Log.d("debug", "Baidu Start！");
                Log.i("BaiduMap", bdLocation.getCity());
                lcCity = bdLocation.getCity().replace("市", "");
                showWeather(lcCity);
                showCityList(lcCity);
                topTitle.setText(lcCity);
                SharedPreferences.Editor editor = getSharedPreferences("city", MODE_PRIVATE).edit();
                editor.putString("city", lcCity);
                editor.commit();
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
        Bundle bundle = new Bundle();
        // 若为首次启动，使用城市名称请求数据，否则使用ID
        if (firstFlag)
        {
            bundle.putString("key", cityName);
        }
        else
        {
            String cityId = new City(dbHelper).queryCityId(cityName);
            Log.d("debug", cityName + " 的ID是：" + cityId);
            bundle.putString("key", cityId);
        }
        bundle.putBoolean("isFirst", firstFlag);
        FgMain fgMain = new FgMain();
        fgMain.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frag_layout, fgMain);
        transaction.commit();
        pgbar.setVisibility(View.VISIBLE);
    }

    public void showCityList(final String cityName)
    {
        String KEY = getString(R.string.key);
        String parm;
        String url;
        if (firstFlag)
        {
            parm = cityName;
            url = getString(R.string.wt_api_no_parm) + "city=" + parm + "&key=" + KEY;
        }
        else
        {
            parm = new City(dbHelper).queryCityId(cityName);
            url = getString(R.string.wt_api_no_parm) + "cityid=" + parm + "&key=" + KEY;
        }
        if (!parm.equals(""))
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
                                cityData.add(new CityItem(cityName, txtTmp));
                                cityAdapter.notifyDataSetChanged();
                                listCity.setAdapter(cityAdapter);
                                if (needRec)
                                {
                                    recoverCity();  // 恢复已添加城市列表
                                    needRec = false;
                                    refreshData(1);
                                }

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
    }

    /**
     * 第一次启动， 建立城市数据库/天气图标数据库
     */
    public void createOriData()
    {
        if(new Judgement(mContext).judgeVersion())
        {
            firstFlag = true;
            String CITY_API = getString(R.string.city_api);
            dbHelper.getWritableDatabase();
            HttpUtil.sendHttpRequest(CITY_API, new HttpCallbackListener()
            {
                @Override
                public void onFinish(String response)
                {
                    new OriginalData(dbHelper, mContext).parseJSONForCity(response);
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
                    new OriginalData(dbHelper, mContext).parseJSONForWt(response);
                }

                @Override
                public void onError(Exception e)
                {
                    Log.e("ERROR", "获取天气列表失败");
                }
            });
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
     * 活动销毁，保存已添加的城市名称
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
            editor.putString("city_tmp_" + i, cityData.get(i).getCityTmp());
        }
        editor.commit();
    }

    /**
     * 恢复城市列表
     */
    public void recoverCity()
    {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int cityNum = pref.getInt("city_num", 0);
        for (int i = 1; i < cityNum; i++)
        {
            Log.d("debug", "recover city_" + i + ", " + pref.getString("city_" + i, ""));
            // showCityList(pref.getString("city_" + i, ""));
            cityData.add(new CityItem(pref.getString("city_" + i, ""), pref.getString("city_tmp_" + i, "")));
            cityAdapter.notifyDataSetChanged();
            listCity.setAdapter(cityAdapter);
        }
    }

    /**
     * 刷新城市列表数据
     * @param start ListView起始位置
     */
    public void refreshData(int start)
    {
        int dataNum = cityAdapter.getCount();
        Log.d("debug", "number is" + dataNum);
        for (int i = start; i < dataNum; i++)
        {
            Log.d("debug", cityData.get(i).getCityName());
            getCityTmp(cityData.get(i).getCityName(), i);
        }
    }

    /**
     * 获取实时温度
     * @param cityName
     * @param position
     */
    public void getCityTmp(final String cityName, final int position)
    {
        String KEY = getString(R.string.key);
        String url = getString(R.string.wt_api_no_parm) + "city=" + cityName + "&key=" + KEY;
        if (!cityName.equals(""))
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
                                cityData.get(position).setCityTmp(txtTmp);
                                cityAdapter.notifyDataSetChanged();
                                listCity.setAdapter(cityAdapter);
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(mContext, "出错了..", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(mContext, "出错了..", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }

    /*
    public void setLocationSymbol()
    {
        Drawable drawable = getResources().getDrawable(R.drawable.place);
        assert drawable != null;
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        topTitle.setCompoundDrawables(drawable, null, null, null);
    }
    */
}
