package com.dstudio.wd.one.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dstudio.wd.one.R;
import com.dstudio.wd.one.entity.hp.Data;
import com.dstudio.wd.one.util.DataCleanManager;
import com.dstudio.wd.one.util.LocalCache;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingActivityFragment extends PreferenceFragment
{
    private Context mContext;
    private Preference cachePre;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        mContext = getActivity();
        cachePre = (Preference) findPreference("setting_cache");
        cachePre.setSummary(DataCleanManager.getTotalCacheSize(mContext));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
    {
        if ("setting_about".equals(preference.getKey()))
        {
            Intent intent = new Intent(mContext, AboutActivity.class);
            startActivity(intent);
        }
        else if ("setting_cache".equals(preference.getKey()))
        {
            DataCleanManager.clearAllCache(mContext);
            Toast.makeText(mContext, "缓存已清除", Toast.LENGTH_SHORT).show();
            cachePre.setSummary(DataCleanManager.getTotalCacheSize(mContext));
        }
        return true;
    }



}
