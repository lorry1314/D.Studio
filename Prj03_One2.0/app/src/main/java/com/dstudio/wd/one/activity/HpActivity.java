package com.dstudio.wd.one.activity;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dstudio.wd.one.R;
import com.dstudio.wd.one.fragment.DetailFragment;
import com.umeng.analytics.MobclickAgent;

public class HpActivity extends AppCompatActivity
{

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        mContext = getApplicationContext();
        String id = getIntent().getExtras().getString("hpid");
        DetailFragment detailFragment = new DetailFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("hpid", id);
        detailFragment.setArguments(bundle);
        transaction.replace(R.id.hp_fragment, detailFragment);
        transaction.commit();
    }

    public void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
