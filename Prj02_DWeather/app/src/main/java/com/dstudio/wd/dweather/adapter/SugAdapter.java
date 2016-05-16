package com.dstudio.wd.dweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dstudio.wd.dweather.R;

import java.util.LinkedList;

/**
 * Created by wd824 on 2016/5/14.
 */
public class SugAdapter extends BaseAdapter
{
    private LinkedList<Suggestion> mData;
    private Context mContext;

    public SugAdapter(LinkedList<Suggestion> mData, Context mContext)
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
            view = LayoutInflater.from(mContext).inflate(R.layout.item_suggestion, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.sugIcon = (ImageView) view.findViewById(R.id.sug_icon);
            viewHolder.sugTag = (TextView) view.findViewById(R.id.sug_tag);
            viewHolder.sugTitle = (TextView) view.findViewById(R.id.sug_title);
            viewHolder.sugTxt = (TextView) view.findViewById(R.id.sug_txt);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.sugIcon.setImageResource(mData.get(i).getSugIcon());
        viewHolder.sugTag.setText(mData.get(i).getSugTag());
        viewHolder.sugTitle.setText(mData.get(i).getSugTitle());
        viewHolder.sugTxt.setText(mData.get(i).getSugTxt());
        return view;
    }

    static class ViewHolder
    {
        ImageView sugIcon;
        TextView sugTag;
        TextView sugTitle;
        TextView sugTxt;
    }
}
