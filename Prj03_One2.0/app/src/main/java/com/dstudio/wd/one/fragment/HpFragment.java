package com.dstudio.wd.one.fragment;


import android.content.Context;
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

import java.util.ArrayList;

/**
 * Created by wd824 on 2016/8/24.
 */
public class HpFragment extends Fragment
{
    private SectionPageAdapter mSectionPageAdapter;
    private Context mContext;
    private FragmentManager manager;
    private ViewPager mViewPager;
    private ArrayList<String> idList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_hp, container, false);
        mContext = getActivity().getApplication();
        if (getArguments() != null)
        {
            idList = getArguments().getStringArrayList(getString(R.string.id_list_key));
        }

        manager = getFragmentManager();
        mSectionPageAdapter = new SectionPageAdapter(manager);
        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionPageAdapter);
        return view;
    }

    public class SectionPageAdapter extends FragmentPagerAdapter
    {
        public SectionPageAdapter(FragmentManager manager)
        {
            super(manager);
        }

        @Override
        public Fragment getItem(int position)
        {
            String id = idList.get(position);
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
            return idList.size();
        }
    }
}
