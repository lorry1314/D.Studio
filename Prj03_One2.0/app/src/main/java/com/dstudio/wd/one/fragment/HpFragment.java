package com.dstudio.wd.one.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dstudio.wd.one.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Created by wd824 on 2016/8/24.
 */
public class HpFragment extends Fragment
{
    private View view;
    private SectionPageAdapter mSectionPageAdapter;
    private FragmentManager manager;
    private ViewPager mViewPager;
    private ArrayList<String> idList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (view != null)
        {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
            {
                parent.removeView(view);
            }
            return view;
        }

        view = inflater.inflate(R.layout.fragment_hp, container, false);
        if (getArguments() != null)
        {
            idList = getArguments().getStringArrayList(getString(R.string.id_list_key));
        }

        manager = getFragmentManager();
        mSectionPageAdapter = new SectionPageAdapter(manager, idList);
        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionPageAdapter);
        return view;
    }

    public class SectionPageAdapter extends FragmentPagerAdapter
    {
        private ArrayList<String> list;

        public SectionPageAdapter(FragmentManager manager, ArrayList<String> list)
        {
            super(manager);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position)
        {
            String id = list.get(position);
            Log.e("id", position + ":" + id);
            DetailFragment detailFragment = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("hpid", id);
            detailFragment.setArguments(bundle);
            return detailFragment;
        }

        @Override
        public int getCount()
        {
            return list.size();
        }
    }

    public void onResume()
    {
        super.onResume();
        MobclickAgent.onPageStart("HpFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    public void onPause()
    {
        super.onPause();
        MobclickAgent.onPageEnd("HpFragment");
    }
}
