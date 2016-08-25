package com.dstudio.wd824.one;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.dstudio.wd.one.fragment.DetailFragment;
import com.dstudio.wd.one.fragment.HistoryFragment;
import com.dstudio.wd.one.fragment.ReadingFragment;
import com.dstudio.wd.one.util.HttpCallbackListener;
import com.dstudio.wd.one.util.HttpUtil;
import com.dstudio.wd.one.util.LocalData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String TAG = "MainActivity";
    private Context mContext;
    private DetailFragment fgDetail;
    private ReadingFragment fgReading;
    private HistoryFragment fgHistory;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    private ArrayList<String> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = getApplicationContext();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        manager = getFragmentManager();
        transaction = manager.beginTransaction();

        switch (item.getItemId())
        {
            case R.id.nav_detail:  // 首页
                Log.d(TAG, "item nav_detail");
                transaction.replace(R.id.fram_layout, fgDetail);
                break;
            case R.id.nav_read:    // 阅读
                transaction.replace(R.id.fram_layout, fgReading);
                break;
            case R.id.nav_history:  // 过往
                transaction.replace(R.id.fram_layout, fgHistory);
                break;
        }
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initView()
    {
        fgDetail = new DetailFragment();
        fgReading = new ReadingFragment();
        fgHistory = new HistoryFragment();

        manager = getFragmentManager();
        transaction = manager.beginTransaction();

        idList = getIntent().getStringArrayListExtra(getString(R.string.id_list_key));
        Bundle bundleDetail = new Bundle();
        bundleDetail.putStringArrayList(getString(R.string.id_list_key), idList);
        fgDetail.setArguments(bundleDetail);

        transaction.replace(R.id.fram_layout, fgDetail);
        transaction.commit();
        getReadingIndex();
    }

    public void getReadingIndex()
    {
        HttpUtil.sendGet(getString(R.string.reading_index) + "0", new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                LocalData.save(response, "reading_index", mContext);
                setBundleForRd(response);
            }

            @Override
            public void onError(Exception e)
            {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
                String response = LocalData.load("reading_index", mContext);
                setBundleForRd(response);
            }
        });
    }

    public void setBundleForRd(String response)
    {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.reading_index_key), response);
        fgReading.setArguments(bundle);
    }
}
