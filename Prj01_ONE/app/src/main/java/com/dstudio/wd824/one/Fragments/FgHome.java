package com.dstudio.wd824.one.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dstudio.wd824.one.Data.HttpCallbackListener;
import com.dstudio.wd824.one.Data.HttpUtil;
import com.dstudio.wd824.one.Data.LocalCache;
import com.dstudio.wd824.one.Data.LocalData;
import com.dstudio.wd824.one.MainActivity;
import com.dstudio.wd824.one.R;
import com.jakewharton.disklrucache.DiskLruCache;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by wd824 on 2016/5/5.
 */
public class FgHome extends Fragment implements GestureDetector.OnGestureListener
{
    private TextView dayText;
    private TextView title;
    private TextView author;
    private TextView content;
    private ImageView imageView;

    public GestureDetector detector = new GestureDetector(this);
    private boolean flag;
    private int day = 1;

    private TextView topTitle;
    private ProgressBar bar;
    private ScrollView scrollView;

    private static final int SHOW_CONTENT = 0;
    private static final int SHOW_IMG = 1;
    private static final int WAIT_IMG = 2;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SHOW_CONTENT:
                    String[] data = (String[]) msg.obj;
                    title.setText(data[0]);
                    author.setText(data[1]);
                    content.setText(data[2]);
                    dayText.setText(data[3]);
                    break;
                case SHOW_IMG:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    imageView.setImageBitmap(bitmap);
                    bar.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.view_home, container, false);
        topTitle = (TextView) getActivity().findViewById(R.id.top_title);
        topTitle.setText("ONE");
        scrollView = (ScrollView) getActivity().findViewById(R.id.scroll_view);
        scrollView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    //sendRequestForQue();
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

        bar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
        dayText = (TextView) view.findViewById(R.id.day);
        title = (TextView) view.findViewById(R.id.title);
        author = (TextView) view.findViewById(R.id.author);
        content = (TextView) view.findViewById(R.id.content);
        imageView = (ImageView) view.findViewById(R.id.img);

        day = HttpUtil.selectWhichDay();
        getData();

        return view;
    }

    public void getData()
    {
        File file = getActivity().getFileStreamPath(day + "homedata.txt");
        if (!file.exists())
        {
            Log.d("TAG", "呵呵(￣▽￣)");
            sendRequestForHome(day);
        }
        else
        {
            int updateHour = new Date(file.lastModified()).getHours();

            if(HttpUtil.judgeTime(updateHour))
            {
                sendRequestForHome(day);
            }
            else
            {
                Log.d("TAG", " : (");
                parseJSON(LocalData.load(day + "home", getActivity()));
            }
        }

    }

    public void sendRequestForHome(final int preDay)
    {
        String currentDate = HttpUtil.getCurrentDate("day", preDay);
        String homeAPI = "http://211.152.49.184:7001/OneForWeb/one/getHpinfo?strDate=" + currentDate;
        bar.setVisibility(View.VISIBLE);
        HttpUtil.sendHttpRequest(homeAPI, new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                LocalData.save(response, day + "home", getActivity());
                parseJSON(response);
            }

            @Override
            public void onError(Exception e)
            {
                parseJSON(LocalData.load(day + "home", getActivity()));
            }
        });
    }

    public void parseJSON(String result)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(result);
            final JSONObject hpEntity = new JSONObject(jsonObject.getString("hpEntity"));
            String strTitle = hpEntity.getString("strHpTitle");
            final String imgUrl = hpEntity.getString("strThumbnailUrl");
            String strAuthor = hpEntity.getString("strAuthor");
            String strContent = hpEntity.getString("strContent");
            String strTime = hpEntity.getString("strMarketTime");
            java.util.Date date= java.sql.Date.valueOf(strTime);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
            String time = sdf.format(date);

            String[] data = {strTitle, strAuthor, strContent, time};

            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    Bitmap bitmap = LocalCache.readImgCache(getActivity(), imgUrl);
                    if(bitmap == null)
                    {
                        LocalCache.writeCache(getActivity(), imgUrl, "Bitmap");
                        while (true)
                        {
                            if (LocalCache.writeCache(getActivity(), imgUrl, "Bitmap"))
                            {
                                bitmap = LocalCache.readImgCache(getActivity(), imgUrl);
                                break;
                            }
                        }
                    }
                    Message message1 = new Message();
                    message1.what = SHOW_IMG;
                    message1.obj = bitmap;
                    handler.sendMessage(message1);
                }
            }).start();

            Message message = new Message();
            message.what = SHOW_CONTENT;
            message.obj = data;
            handler.sendMessage(message);
        }
        catch (Exception e)
        {
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
        if((e2.getY() - e1.getY() > 50) && flag)
        {
            sendRequestForHome(whichDay);
        }
        else if(e2.getX() - e1.getX() > 50)
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
        else if (e1.getX() - e2.getX() > 50)
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
