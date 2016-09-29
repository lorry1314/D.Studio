package com.dstudio.wd.dweather;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.dstudio.wd.dweather.http.HttpCallbackListener;
import com.dstudio.wd.dweather.http.HttpUtil;
import com.dstudio.wd.dweather.localdata.LocalData;
import com.dstudio.wd.dweather.location.Location;
import com.dstudio.wd.dweather.location.MyLocationListener;
import com.dstudio.wd.dweather.tools.Judgement;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private DrawerLayout mDrawerLayout;
    private LinearLayout leftDrawer;
    private TextView topTitle;
    private Button btnLeft;
    private Button btnRight;
    private Button btnCancel;
    private AutoCompleteTextView searchTextView;

    private ProgressBar pgbar;
    private ScrollView scrollView;

    private Context mContext;

    private FgMain fgMain;
    private FragmentTransaction transaction;

    public LocationClient mLocationClient;
    private String lcCity;

    private List<CityItem> cityData = null;
    private CityAdapter cityAdapter = null;
    private ListView listCity;

    private boolean needRec = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        bindView();           // 初始化控件
        setViewListener();    // 注册监听器
        String savedCityName = getSharedPreferences("city", MODE_PRIVATE).getString("city", "");
        if (!savedCityName.equals(""))     // 读取SP保存的城市名称
        {
            showWeather(savedCityName);
            topTitle.setText(savedCityName);
        }
        if (new Judgement(mContext).isNetworkAvailable())
        {
            initLocationClient(); // 若网络畅通，执行定位初始化
        }
        else
        {
            Toast.makeText(mContext, "当前无网络 :(", Toast.LENGTH_LONG).show();
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
        topTitle = (TextView) findViewById(R.id.top_title);
        btnLeft = (Button) findViewById(R.id.left_button);
        btnRight = (Button) findViewById(R.id.right_button);
        btnCancel = (Button) findViewById(R.id.cancel_button);
        searchTextView = (AutoCompleteTextView) findViewById(R.id.search_city);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        btnLeft.setTypeface(iconfont);
        btnRight.setTypeface(iconfont);

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
        btnLeft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mDrawerLayout.openDrawer(leftDrawer);
            }
        });

        searchTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                showCityList(textView.getText().toString().replace("市", ""));
                searchTextView.setText("");
                // 收回虚拟键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
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
                    mDrawerLayout.closeDrawer(leftDrawer);
                }
                else
                {
                    Toast.makeText(mContext, "当前无网络 :(", Toast.LENGTH_LONG).show();
                }
            }
        });

        /*
         * 长按城市条目，删除操作
         */
        listCity.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                final ImageButton btnDelete = (ImageButton) view.findViewById(R.id.delete_button);
                final TextView txtCityName = (TextView) view.findViewById(R.id.item_city_name);
                final LinearLayout cityItem = (LinearLayout) view.findViewById(R.id.item_city);
                final int itemPosition = i;
                cityItem.setPadding(0, 0, 0, 0);
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        cityData.remove(itemPosition);
                        cityAdapter.notifyDataSetChanged();
                        cityItem.setPadding(40, 0, 0, 0);
                        btnDelete.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.INVISIBLE);
                        Toast.makeText(mContext, txtCityName.getText() + " 已删除", Toast.LENGTH_SHORT).show();
                    }
                });
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        cityItem.setPadding(40, 0, 0, 0);
                        btnDelete.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.INVISIBLE);
                    }
                });
                return false;
            }
        });

        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener()
        {
            @Override
            public void onDrawerClosed(View drawerView)
            {
                // 侧边栏关闭, 收回虚拟键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
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
                lcCity = bdLocation.getCity().replace("市", "");   // 当前位置所处城市/地区
                showCityList(lcCity);
                showWeather(lcCity);
                topTitle.setText(lcCity);
                SharedPreferences.Editor editor = getSharedPreferences("city", MODE_PRIVATE).edit();   // 保存当前城市名称到SP
                editor.putString("city", lcCity);
                editor.commit();
                mLocationClient.stop();             // 停止定位服务
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
        String cityId = new City(mContext).queryCityId(cityName);
        Log.d("debug", cityName + " 的ID是：" + cityId);
        bundle.putString("key", cityId);
        fgMain = new FgMain();
        transaction = getFragmentManager().beginTransaction();
        fgMain.setArguments(bundle);
        transaction.replace(R.id.frag_layout, fgMain);
        transaction.commit();
    }

    /**
     * 将城市，实时温度添加至侧边栏ListView
     * @param cityName
     */
    public void showCityList(final String cityName)
    {
        String KEY = getString(R.string.key);   // 和风天气KEY
        String parm = new City(mContext).queryCityId(cityName);
        String url = getString(R.string.wt_api_no_parm) + "cityid=" + parm + "&key=" + KEY;
        if (!parm.equals(null))
        {
            Log.d("MainActivity", parm);
            Log.d("MainActivity", "url is: " + url);
            HttpUtil.sendHttpRequest(url, new HttpCallbackListener()
            {
                @Override
                public void onFinish(String response)
                {
                    try
                    {
                        Log.d("MainActivity", "response is: " + response);
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
                        showError();
                    }
                }

                @Override
                public void onError(Exception e)
                {
                    e.printStackTrace();
                    showError();
                }
            });
        }
    }

    /**
     * 活动销毁，保存已添加的城市名称至SP
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
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
     * 从SP恢复城市列表
     */
    public void recoverCity()
    {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int cityNum = pref.getInt("city_num", 0);
        for (int i = 1; i < cityNum; i++)
        {
            Log.d("debug", "recover city_" + i + ", " + pref.getString("city_" + i, ""));
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
                        showError();
                    }
                }

                @Override
                public void onError(Exception e)
                {
                    e.printStackTrace();
                    showError();
                }
            });
        }
    }

    /**
     * Toast提示出错
     */
    public void showError()
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

    public void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }

    public void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
