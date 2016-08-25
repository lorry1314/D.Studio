，
package com.dstudio.wd.dweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dstudio.wd.dweather.R;

import java.util.LinkedList;
import java.util.zip.Inflater;

/**
 * Created by wd824 on 2016/5/16.
 */
public class WtAdapter extends BaseAdapter
{
    private LinkedList<DailyWt> dailyData;
    private Context mContext;

    public WtAdapter(LinkedList<DailyWt> dailyData, Context mContext)
    {
        this.dailyData = dailyData;
        this.mContext = mContext;
    }

    @Override
    public int getCount()
    {
        return dailyData.size();
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        ViewHolder viewHolder = null;
        if (view == null)
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_daily, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.txtDate = (TextView) view.findViewById(R.id.txt_date);
            viewHolder.txtDailyWt = (TextView) view.findViewById(R.id.txt_daily_wt);
            viewHolder.txtMaxTmp = (TextView) view.findViewById(R.id.txt_max);
            viewHolder.txtMinTmp = (TextView) view.findViewById(R.id.txt_min);
            viewHolder.imgWtIcon = (ImageView) view.findViewById(R.id.wt_icon);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.txtDate.setText(dailyData.get(i).getTxtDate());
        viewHolder.txtDailyWt.setText(dailyData.get(i).getTxtDailyWt());
        viewHolder.txtMaxTmp.setText(dailyData.get(i).getTxtMaxTmp());
        viewHolder.txtMinTmp.setText(dailyData.get(i).getTxtMinTmp());
        viewHolder.imgWtIcon.setImageBitmap(dailyData.get(i).getImgWtIcon());
        return view;
    }

    static class ViewHolder
    {
        TextView txtDate;
        TextView txtDailyWt;
        TextView txtMaxTmp;
        TextView txtMinTmp;
        ImageView imgWtIcon;
    }
}
