package com.dstudio.wd.one;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.dstudio.wd.one.fragment.DetailFragment;

public class HpActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
}
