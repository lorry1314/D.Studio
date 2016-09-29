package com.dstudio.wd.one.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dstudio.wd.one.activity.HpActivity;
import com.dstudio.wd.one.R;
import com.dstudio.wd.one.adapter.HpAdapter;
import com.dstudio.wd.one.adapter.MyItemClickListener;
import com.dstudio.wd.one.entity.hp.Data;
import com.dstudio.wd.one.entity.hp.Root;
import com.dstudio.wd.one.util.HttpCallbackListener;
import com.dstudio.wd.one.util.HttpUtil;
import com.dstudio.wd.one.util.LocalData;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wd824 on 2016/8/6.
 */
public class ListDetailFragment extends Fragment
{
    private static final String TAG = "ListDetailFragment";
    private Context mContext;

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private HpAdapter hpAdapter;
    private LinkedList<Data> dataList;

    private static final int SHOW_CONTENT = 0;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == SHOW_CONTENT)
            {
                recyclerView.setAdapter(hpAdapter);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_list_detail, container, false);
        mContext = getContext();
        init(view);
        String month = getArguments().getString("month");
        sendRequest(month);
        return view;
    }

    public void init(View view)
    {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_detail);
        layoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(layoutManager);
        dataList = new LinkedList<Data>();
        hpAdapter = new HpAdapter(mContext, dataList, recyclerView);
        hpAdapter.setOnItemClickListener(new MyItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Intent intent = new Intent(mContext, HpActivity.class);
                intent.putExtra("hpid", dataList.get(position).getHpContentId());
                startActivity(intent);
            }
        });
    }

    public void sendRequest(final String month)
    {
        String url = getString(R.string.detail_history_api) + month + getString(R.string.history_api_end);
        HttpUtil.sendGet(url, new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                if (!"".equals(response) && response != null)
                {
                    LocalData.save(response, "hp_history" + month, mContext);
                    parseJson(response);
                }
            }

            @Override
            public void onError(Exception e)
            {
                e.printStackTrace();
                final Exception ex = e;
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(mContext, "数据获取失败", Toast.LENGTH_SHORT).show();
                    }
                });
                parseJson(LocalData.load("hp_history" + month, mContext));
            }
        });
    }

    public void parseJson(String response)
    {
        Root root = new Gson().fromJson(response, Root.class);
        if (root != null && root.getRes() == 0)
        {
            List<Data> datas = root.getData();
            for (int i = 0; i < datas.size(); i++)
            {
                dataList.add(datas.get(i));
            }
            Message message = new Message();
            message.what = SHOW_CONTENT;
            message.obj = null;
            handler.sendMessage(message);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        hpAdapter.flushCache();
        MobclickAgent.onPageEnd("ListDetailFragment");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        hpAdapter.cancalAllTask();
    }

    public void onResume()
    {
        super.onResume();
        MobclickAgent.onPageStart("ListDetailFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }
}
