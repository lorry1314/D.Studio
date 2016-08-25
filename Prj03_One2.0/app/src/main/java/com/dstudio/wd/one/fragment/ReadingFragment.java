package com.dstudio.wd.one.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dstudio.wd.one.EssayActivity;
import com.dstudio.wd.one.adapter.MyAdapter;
import com.dstudio.wd.one.QuestionActivity;
import com.dstudio.wd.one.R;
import com.dstudio.wd.one.entity.Author;
import com.dstudio.wd.one.entity.Data;
import com.dstudio.wd.one.entity.Items;
import com.dstudio.wd.one.entity.Reading;
import com.dstudio.wd.one.entity.Root;
import com.dstudio.wd.one.util.HttpCallbackListener;
import com.dstudio.wd.one.util.HttpUtil;
import com.dstudio.wd.one.adapter.MyItemClickListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wd824 on 2016/6/17.
 */
public class ReadingFragment extends Fragment
{
    private Context mContext;

    private LinkedList<Reading> readingList = null;
    private MyAdapter myAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private boolean flag = true;
    private static final String TAG = "ReadingFragment";
    private static final int SHOW_LIST = 0;
    private static final int SHOW_ESSAY = 1;
    private static final int SHOW_QA = 2;

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == SHOW_LIST)
            {
                switch ((int) msg.obj)
                {
                    case SHOW_ESSAY:
                        myAdapter.setType(SHOW_ESSAY);
                        break;
                    case SHOW_QA:
                        myAdapter.setType(SHOW_QA);
                        break;
                    default:
                        break;
                }
                if (flag)
                {
                    recyclerView.setAdapter(myAdapter);
                }
                else
                {
                    myAdapter.notifyDataSetChanged();
                }
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_reading, container, false);
        mContext = getActivity().getApplicationContext();
        String response = "";
        String date = "";
        int type = 0;
        if (getArguments() != null)
        {
            response = getArguments().getString(getString(R.string.reading_index_key), null);
            date = getArguments().getString("month");
            type = getArguments().getInt("type");
        }

        initView(view);
        if (response != null &&!"".equals(response))
        {
            parseJson(response);
        }
        if (date != null && !"".equals(date))
        {
            if (type == SHOW_ESSAY)
            {
                sendRequestForEssay(date);
            }
            else
            {
                sendRequestForQA(date);
            }
            recyclerView.setOnScrollListener(null);
        }
        return view;
    }

    public void initView(View view)
    {
        readingList = new LinkedList<Reading>();
        recyclerView = (RecyclerView) view.findViewById(R.id.reading_index);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter = new MyAdapter(readingList);
        recyclerView.setOnScrollListener(new OnScrollListener()
        {
            int i = 1;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                int itemSize = myAdapter.getItemCount() - 1;
                if (layoutManager.findLastCompletelyVisibleItemPosition() == itemSize)
                {
                    if (i > 6)
                    {
                        Toast.makeText(mContext, "是不是滑得太累了😪\r\n去“过往”栏目可查看任意日期的内容", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        sendRequest(i++);
                        Log.d(TAG, i + "");
                        flag = false;
                    }
                }
            }
        });

        myAdapter.setOnItemClickListener(new MyItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Reading reading = myAdapter.getItem(position);
                Intent intent;
                if (view.getId() == R.id.item_essay)
                {
                    Log.d(TAG, reading.getContentId() + ", " + reading.getHpTitle());
                    intent = new Intent(mContext, EssayActivity.class);
                    intent.putExtra(getString(R.string.essay_key), reading.getContentId());
                    startActivity(intent);
                }
                else
                {
                    Log.d(TAG, reading.getQuestionId() + ", " + reading.getQuestionTitle());
                    intent = new Intent(mContext, QuestionActivity.class);
                    intent.putExtra(getString(R.string.question_key), reading.getQuestionId());
                    startActivity(intent);
                }

            }
        });
    }

    public void parseJson(String response)
    {
        final String result = response;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Root root = new Gson().fromJson(result, Root.class);
                if (root.getRes() == 0)
                {
                    List<Data> dataList = root.getData();
                    for (int i = 0; i < dataList.size(); i++)
                    {
                        String date = dataList.get(i).getDate();
                        Log.d(TAG, date);
                        List<Items> itemsList = dataList.get(i).getItems();
                        parseItems(itemsList);
                    }
                    Message msg = new Message();
                    msg.what = SHOW_LIST;
                    msg.obj = SHOW_LIST;
                    handler.sendMessage(msg);
                }
            }
        }).start();

    }

    public void parseItems(List<Items> itemsList)
    {
        Reading reading = new Reading();
        for (int i = 0; i < itemsList.size(); i++)
        {
            reading.setTime(itemsList.get(i).getTime().substring(0, 10));
            int type = itemsList.get(i).getType();

            try
            {
                JSONObject content = new JSONObject(itemsList.get(i).getContent().toString());

                if (type == 3)
                {
                    reading.setQuestionId(content.getString("question_id"));
                    reading.setQuestionMakettime(content.getString("question_makettime"));
                    reading.setQuestionTitle(content.getString("question_title"));
                    reading.setAnswerTitle(content.getString("answer_title"));
                    reading.setAnswerContent(content.getString("answer_content"));
                }
                else if (type == 1)
                {
                    reading.setContentId(content.getString("content_id"));
                    reading.setHpMakettime(content.getString("hp_makettime"));
                    reading.setHpTitle(content.getString("hp_title"));
                    reading.setGuideWord(content.getString("guide_word"));
                    Author[] author = new Gson().fromJson(content.getString("author"), Author[].class);
                    List<Author> authorList = Arrays.asList(author);
                    reading.setAuthor(authorList);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        readingList.add(reading);
    }

    public void sendRequest(int i)
    {
        HttpUtil.sendGet(getString(R.string.reading_index) + i, new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                parseJson(response);
            }

            @Override
            public void onError(Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    public void sendRequestForEssay(String date)
    {
        String url = getString(R.string.essay_history_api) + date + getString(R.string.history_api_end);
        HttpUtil.sendGet(url, new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                parseJsonForEssay(response);
            }

            @Override
            public void onError(Exception e)
            {

            }
        });
    }

    public void parseJsonForEssay(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getInt("res") == 0)
            {
                JSONArray datas = jsonObject.getJSONArray("data");
                for (int i = 0; i < datas.length(); i++)
                {
                    Reading reading = new Reading();
                    JSONObject content = new JSONObject(datas.get(i).toString());
                    reading.setContentId(content.getString("content_id"));
                    reading.setHpMakettime(content.getString("hp_makettime"));
                    reading.setHpTitle(content.getString("hp_title"));
                    reading.setGuideWord(content.getString("guide_word"));
                    Author[] author = new Gson().fromJson(content.getString("author"), Author[].class);
                    List<Author> authorList = Arrays.asList(author);
                    reading.setAuthor(authorList);
                    readingList.add(reading);
                }
                Message message = new Message();
                message.what = SHOW_LIST;
                message.obj = SHOW_ESSAY;
                handler.sendMessage(message);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void sendRequestForQA(String date)
    {
        String url = getString(R.string.question_history_api) + date + getString(R.string.history_api_end);
        HttpUtil.sendGet(url, new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                parseJsonForQA(response);
            }

            @Override
            public void onError(Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    public void parseJsonForQA(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getInt("res") == 0)
            {
                JSONArray datas = jsonObject.getJSONArray("data");
                for (int i = 0; i < datas.length(); i++)
                {
                    Reading reading = new Reading();
                    JSONObject content = new JSONObject(datas.get(i).toString());
                    reading.setQuestionId(content.getString("question_id"));
                    reading.setQuestionMakettime(content.getString("question_makettime"));
                    reading.setQuestionTitle(content.getString("question_title"));
                    reading.setAnswerTitle(content.getString("answer_title"));
                    reading.setAnswerContent(content.getString("answer_content"));
                    readingList.add(reading);
                }
                Message message = new Message();
                message.what = SHOW_LIST;
                message.obj = SHOW_QA;
                handler.sendMessage(message);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
