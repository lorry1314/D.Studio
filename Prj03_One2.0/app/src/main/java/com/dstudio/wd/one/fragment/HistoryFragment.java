package com.dstudio.wd.one.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.dstudio.wd.one.activity.MonthActivity;
import com.dstudio.wd.one.R;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by wd824 on 2016/6/17.
 */
public class HistoryFragment extends Fragment
{

    private Context mContext;
    private List<Map<String, Object>> monthList;
    private ListView listMonth;
    private SimpleAdapter mSimpleAdapter;
    private static final String TAG = "HistoryFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mContext = getActivity().getApplicationContext();
        monthList = new ArrayList<>();
        listMonth = (ListView) view.findViewById(R.id.month_list);
        mSimpleAdapter = new SimpleAdapter(mContext, getMonthList(), R.layout.item_month,
                new String[] {"month"}, new int[] {R.id.item_month});
        listMonth.setAdapter(mSimpleAdapter);
        listMonth.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                TextView txtMonth = (TextView) view.findViewById(R.id.item_month);
                Intent intent = new Intent(mContext, MonthActivity.class);
                intent.putExtra("month", txtMonth.getText());
                startActivity(intent);
            }
        });
        return view;
    }

    /**
     * 显示月份列表
     */
    public List<Map<String, Object>> getMonthList()
    {
        // 日期格式，如 Oct.2012
        SimpleDateFormat sdf = new SimpleDateFormat("MMM.yyyy", Locale.ENGLISH);
        int i = 0;
        while (true)
        {
            Calendar calendar = Calendar.getInstance();
            // 从当前月份往前i个月
            calendar.add(Calendar.MONTH, -i);
            String date = sdf.format(calendar.getTime());
            i++;

            Map<String, Object> map = new HashMap<>();
            map.put("month", date);
            monthList.add(map);

            if ("Oct.2012".equals(date))
            {
                break;
            }
        }
        return monthList;
    }

    public void onResume()
    {
        super.onResume();
        MobclickAgent.onPageStart("HistoryFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    public void onPause()
    {
        super.onPause();
        MobclickAgent.onPageEnd("HistoryFragment");
    }

}
