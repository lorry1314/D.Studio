package com.dstudio.wd824.one.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dstudio.wd824.one.data.HttpCallbackListener;
import com.dstudio.wd824.one.data.HttpUtil;
import com.dstudio.wd824.one.data.LocalCache;
import com.dstudio.wd824.one.data.LocalData;
import com.dstudio.wd824.one.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

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
    private Button btnSaveImg;
    private String webLink;
    private String imgUrl;
    private String imgPath;

    public GestureDetector detector = new GestureDetector(this);
    private boolean flag;
    private int day = 1;

    private TextView topTitle;
    private Button btnRight;
    private ProgressBar bar;
    private ScrollView scrollView;

    private static final int SHOW_CONTENT = 0;
    private static final int SHOW_IMG = 1;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SHOW_CONTENT:             // 显示文本部分
                    String[] data = (String[]) msg.obj;
                    title.setText(data[0]);
                    author.setText(data[1]);
                    content.setText(data[2]);
                    dayText.setText(data[3]);
                    imgUrl = data[4];
                    webLink = data[5];
                    break;
                case SHOW_IMG:                // 显示图片
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
        btnRight = (Button) getActivity().findViewById(R.id.right_button);
        btnRight.setText(R.string.share);
        btnRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)                 // 社交平台分享参数设置
            {
                ShareSDK.initSDK(getActivity());
                OnekeyShare oks = new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();
                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                oks.setTitle(title.getText().toString());
                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                oks.setTitleUrl(webLink);
                // text是分享文本，所有平台都需要这个字段
                oks.setText(content.getText().toString());
                oks.setImageUrl(imgUrl);
                // url仅在微信（包括好友和朋友圈）中使用
                oks.setUrl(webLink);
                // site是分享此内容的网站名称，仅在QQ空间使用
                oks.setSite(webLink);
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                oks.setSiteUrl(webLink);
                // 启动分享GUI
                oks.show(getActivity());
            }
        });


        bar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
        dayText = (TextView) view.findViewById(R.id.day);
        title = (TextView) view.findViewById(R.id.title);
        author = (TextView) view.findViewById(R.id.author);
        content = (TextView) view.findViewById(R.id.content);
        imageView = (ImageView) view.findViewById(R.id.img);
        btnSaveImg = (Button) view.findViewById(R.id.save_img);
        Typeface iconfont = Typeface.createFromAsset(getActivity().getAssets(), "iconfont.ttf");
        btnSaveImg.setTypeface(iconfont);
        btnSaveImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                imageView.setDrawingCacheEnabled(true);
                Bitmap bitmap = imageView.getDrawingCache();
                if (bitmap != null)
                {
                    new SaveImageTask().execute(bitmap);
                }
            }
        });


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
        getData();
        return view;
    }

    /**
     * 选择获取数据的方式
     */
    public void getData()
    {
        File file = getActivity().getFileStreamPath(day + "homedata.txt");
        if (!file.exists())
        {
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
                parseJSON(LocalData.load(day + "home", getActivity()));
            }
        }
    }

    /**
     * 发送数据请求
     * @param preDay 当前日期往前的天数
     */
    public void sendRequestForHome(int preDay)
    {
        String currentDate = HttpUtil.getCurrentDate("day", preDay);
        Log.d("debug", "currentDate" + currentDate);
        String homeAPI = "http://211.152.49.184:7001/OneForWeb/one/getHpinfo?strDate=" + currentDate;
        bar.setVisibility(View.VISIBLE);
        HttpUtil.sendHttpRequest(homeAPI, new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                Log.d("debug", "finish!");
                LocalData.save(response, day + "home", getActivity());
                parseJSON(response);
            }

            @Override
            public void onError(Exception e)
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getActivity(), "似乎没有网...", Toast.LENGTH_LONG).show();
                        bar.setVisibility(View.GONE);
                    }
                });

                // 若发送网络请求失败， 尝试读取本地数据
                if(!(LocalData.load(day + "home", getActivity())).equals(""))
                {
                    parseJSON(LocalData.load(day + "home", getActivity()));
                }
            }
        });
    }

    /**
     * 解析JSON数据
     * @param result
     */
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
            String webLink = hpEntity.getString("sWebLk");
            java.util.Date date= java.sql.Date.valueOf(strTime);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
            String time = sdf.format(date);

            String[] data = {strTitle, strAuthor, strContent, time, imgUrl, webLink};

            // 图片加载线程
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

    /**
     * 手势判断
     * @param e1
     * @param e2
     * @param v
     * @param v1
     * @return
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1)
    {
        if((e2.getY() - e1.getY() > 260) && Math.abs(e2.getX() - e1.getX()) < 50 && flag)
        {
            day = 1;
            sendRequestForHome(day);
        }
        else if(e2.getX() - e1.getX() > 50 && Math.abs(e2.getY() - e1.getY()) < 80)
        {

            day++;
            if(day == 10)
            {
                Toast.makeText(getActivity(), "没有数据了 (╯‵□′)╯︵┻━┻z", Toast.LENGTH_LONG).show();
                day = 9;
            }
            else
            {
                getData();
            }
        }
        else if (e1.getX() - e2.getX() > 50 && Math.abs(e2.getY() - e1.getY()) < 80)
        {
            day--;
            if(day == 0)
            {
                Toast.makeText(getActivity(), "已经是最新内容了 :)", Toast.LENGTH_SHORT).show();
                day = 1;
            }
            else
            {
                getData();
            }
        }
        return false;
    }

    /**
     * 图片下载子线程
     */
    class SaveImageTask extends AsyncTask<Bitmap, Void, String>
    {
        @Override
        protected String doInBackground(Bitmap... bitmaps)
        {
            String result = null;
            try
            {
                String sdcardDir = Environment.getExternalStorageDirectory().toString();
                File file = new File(sdcardDir + "/Download/ONE");
                if (!file.exists())
                {
                    file.mkdir();
                }
                File imageFile = new File(file.getAbsolutePath(), new Date().getTime() + ".jpg");
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                Bitmap image = bitmaps[0];
                image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                result = "图片保存在了" + file.getAbsolutePath() + "目录下";
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s)
        {
            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            imageView.setDrawingCacheEnabled(false);
        }
    }

}
