package com.dstudio.wd.dweather.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dstudio.wd.dweather.R;

import java.util.LinkedList;

/**
 * Created by wd824 on 2016/5/21.
 */
public class CityAdapter extends BaseAdapter
{
    private LinkedList<CityItem> mData;
    private Context mContext;

    public CityAdapter(LinkedList<CityItem> mData, Context mContext)
    {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        ViewHolder viewHolder = null;
        if (view == null)
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_city, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.txtCity = (TextView) view.findViewById(R.id.item_city_name);
            viewHolder.txtTmp = (TextView) view.findViewById(R.id.item_city_tmp);
            Typeface fzltxh = Typeface.createFromAsset(mContext.getAssets(), "fzltxh.TTF");
            Typeface fzlt = Typeface.createFromAsset(mContext.getAssets(), "fzlt.ttf");
            viewHolder.txtTmp.setTypeface(fzltxh);
            viewHolder.txtCity.setTypeface(fzlt);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.txtCity.setText(mData.get(i).getCityName());
        viewHolder.txtTmp.setText(mData.get(i).getCityTmp());
        return view;
    }

    static class ViewHolder
    {
        TextView txtCity;
        TextView txtTmp;
    }
}
