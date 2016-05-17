package com.dstudio.wd824.one.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dstudio.wd824.one.Data.HttpCallbackListener;
import com.dstudio.wd824.one.Data.HttpUtil;
import com.dstudio.wd824.one.Data.LocalData;
import com.dstudio.wd824.one.MainActivity;
import com.dstudio.wd824.one.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wd824 on 2016/5/5.
 */
public class FgReading extends Fragment implements GestureDetector.OnGestureListener
{
    private TextView dayText;
    private TextView articleTitle;
    private TextView articleAuther;
    private TextView articleContent;
    private TextView articleEditor;
    private TextView praiseNum;

    private ProgressBar bar;
    private Button btnRight;
    private TextView topTitle;
    private ScrollView scrollView;

    public GestureDetector detector = new GestureDetector(this);
    private boolean flag;
    private int day = 1;

    private static final int SHOW_DATA = 0;

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == SHOW_DATA)
            {

                String[] data = (String[]) msg.obj;
                articleTitle.setText(data[0]);
                articleAuther.setText(data[1]);
                articleContent.setText(data[2]);
                articleEditor.setText(data[3]);
                praiseNum.setText(data[4]);
                dayText.setText(data[5]);
                bar.setVisibility(View.GONE);
                scrollView.fullScroll(View.FOCUS_UP);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.view_reading, container, false);
        btnRight = (Button) getActivity().findViewById(R.id.right_button);
        btnRight.setText("");
        topTitle = (TextView) getActivity().findViewById(R.id.top_title);
        topTitle.setText("阅读");
        bar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
        scrollView = (ScrollView) getActivity().findViewById(R.id.scroll_view);
        scrollView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                if((view.getScrollY() == 0) && (motionEvent.getAction() == MotionEvent.ACTION_UP))
                {
                    flag = true;
                }
                else if ((motionEvent.getAction() != MotionEvent.ACTION_UP) || (motionEvent.getAction() != MotionEvent.ACTION_DOWN))
                {
                    flag = false;
                }
                detector.onTouchEvent(motionEvent);
                return false;
            }
        });

        articleTitle = (TextView) view.findViewById(R.id.article_title);
        articleAuther = (TextView) view.findViewById(R.id.article_author);
        articleContent = (TextView) view.findViewById(R.id.article_content);
        articleEditor = (TextView) view.findViewById(R.id.article_editor);
        praiseNum = (TextView) view.findViewById(R.id.praise_num);
        dayText = (TextView) view.findViewById(R.id.day);

        day = HttpUtil.selectWhichDay();
        getData();
        return view;
    }

    public void getData()
    {
        File file = getActivity().getFileStreamPath(day + "readingdata.txt");
        if (!file.exists())
        {
            sendRequestForRd(day);
        }
        else
        {
            int updateHour = new Date(file.lastModified()).getHours();
            if(HttpUtil.judgeTime(updateHour))
            {
                sendRequestForRd(day);
            }
            else
            {
                parseJSON(LocalData.load(day + "reading", getActivity()));
            }
        }
    }

    public void sendRequestForRd(final int whichDay)
    {
        final String readingAPI = "http://211.152.49.184:7001/OneForWeb/one/getOneContentInfo?strDate=" + HttpUtil.getCurrentDate("day", whichDay);
        bar.setVisibility(View.VISIBLE);

        HttpUtil.sendHttpRequest(readingAPI, new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                LocalData.save(response, day + "reading", getActivity());
                parseJSON(response);
            }

            @Override
            public void onError(Exception e)
            {
                ((Activity)getActivity()).runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getActivity(), "似乎没有网...", Toast.LENGTH_LONG).show();
                        bar.setVisibility(View.GONE);
                    }
                });

                if(!(LocalData.load(day + "reading", getActivity())).equals(""))
                {
                    parseJSON(LocalData.load(day + "reading", getActivity()));
                }
            }
        });
    }

    public void parseJSON(String result)
    {
        try
        {
            LocalData.save(result, day + "reading", getActivity());
            JSONObject jsonObject = new JSONObject(result);
            JSONObject contentEntity = new JSONObject(jsonObject.getString("contentEntity"));
            String contTitle = contentEntity.getString("strContTitle");
            String contAuthor = contentEntity.getString("strContAuthor");
            String content = contentEntity.getString("strContent").replace("<br>", "");
            String editor = contentEntity.getString("strContAuthorIntroduce");
            String praiseNum = contentEntity.getString("strPraiseNumber") + " 赞";
            String strTime = contentEntity.getString("strContMarketTime");
            java.util.Date date= java.sql.Date.valueOf(strTime);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
            String time = sdf.format(date);

            String[] data = {contTitle, contAuthor, content, editor, praiseNum, time};
            Message message = new Message();
            message.what = SHOW_DATA;
            message.obj = data;
            handler.sendMessage(message);
        }
        catch (Exception e)
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(getActivity(), "出问题了:(...请刷新下吧", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent)
    {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent)
    {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent)
    {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1)
    {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent)
    {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1)
    {
        int whichDay = HttpUtil.selectWhichDay();
        if((e2.getY() - e1.getY() > 260) && Math.abs(e2.getX() - e1.getX()) < 50 && flag)
        {
            day = HttpUtil.selectWhichDay();
            sendRequestForRd(day);
        }
        else if(e2.getX() - e1.getX() > 50 && Math.abs(e2.getY() - e1.getY()) < 80)
        {

            day++;
            if(day == whichDay + 10)
            {
                Toast.makeText(getActivity(), "没有数据了 (╯‵□′)╯︵┻━┻z", Toast.LENGTH_LONG).show();
                day = whichDay + 9;
            }
            else
            {
                getData();
            }
        }
        else if (e1.getX() - e2.getX() > 50 && Math.abs(e2.getY() - e1.getY()) < 80)
        {
            day--;
            if(day == whichDay - 1)
            {
                Toast.makeText(getActivity(), "已经是最新内容了 :)", Toast.LENGTH_SHORT).show();
                day = whichDay;
            }
            else
            {
                getData();
            }
        }
        Log.d("Quse", day + "");
        return false;
    }
}
