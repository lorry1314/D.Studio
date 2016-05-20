package com.dstudio.wd824.one.fragments;

import android.app.Activity;
import android.app.Fragment;
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

import com.dstudio.wd824.one.data.HttpCallbackListener;
import com.dstudio.wd824.one.data.HttpUtil;
import com.dstudio.wd824.one.data.LocalData;
import com.dstudio.wd824.one.R;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by wd824 on 2016/5/5.
 */
public class FgQuestion extends Fragment implements GestureDetector.OnGestureListener
{
    private TextView topTitle;
    private Button btnRight;
    private ProgressBar bar;
    private ScrollView scrollView;

    public GestureDetector detector = new GestureDetector(this);

    private int day = 1;
    private Boolean flag;
    private String webLink;

    private TextView question;
    private TextView questionCont;
    private TextView answer;
    private TextView answerCont;
    private TextView editor;
    private TextView praiseNum;
    private TextView dayText;

    private static final int SHOW_DATA = 0;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == SHOW_DATA)
            {
                String[] data = (String[]) msg.obj;
                question.setText(data[0]);
                questionCont.setText("\n" + data[1]);
                answer.setText(data[2]);
                answerCont.setText("\n" + data[3]);
                editor.setText(data[4]);
                praiseNum.setText(data[5]);
                dayText.setText(data[6]);
                webLink = data[7];
                bar.setVisibility(View.GONE);
                scrollView.fullScroll(View.FOCUS_UP);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.view_question, container, false);
        topTitle = (TextView) getActivity().findViewById(R.id.top_title);
        topTitle.setText("问答");
        btnRight = (Button) getActivity().findViewById(R.id.right_button);
        btnRight.setText(R.string.share);
        btnRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ShareSDK.initSDK(getActivity());
                OnekeyShare oks = new OnekeyShare();
                oks.disableSSOWhenAuthorize();
                oks.setTitle(question.getText().toString());
                oks.setTitleUrl(webLink);
                oks.setText(questionCont.getText().toString());
                oks.setUrl(webLink);
                oks.setSiteUrl(webLink);
                oks.setImageUrl("http://img.wdjimg.com/mms/icon/v1/a/91/946b23773692af0e351772392298c91a_256_256.png");
                oks.show(getActivity());
            }
        });

        bar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
        scrollView = (ScrollView) getActivity().findViewById(R.id.scroll_view);
        scrollView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                if((view.getScrollY() == 0) && (motionEvent.getAction() == MotionEvent.ACTION_UP))
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

        question = (TextView) view.findViewById(R.id.question);
        questionCont = (TextView) view.findViewById(R.id.question_cont);
        answer = (TextView) view.findViewById(R.id.answer);
        answerCont = (TextView) view.findViewById(R.id.answer_content);
        editor = (TextView) view.findViewById(R.id.editor);
        praiseNum = (TextView) view.findViewById(R.id.praise_num);
        dayText = (TextView) view.findViewById(R.id.day);
        getData();
        return view;
    }

    public void getData()
    {
        File file = getActivity().getFileStreamPath(day + "questiondata.txt");
        if (!file.exists())
        {
            sendRequestForQue(day);
        }
        else
        {
            int updateHour = new Date(file.lastModified()).getHours();
            if(HttpUtil.judgeTime(updateHour))
            {
                sendRequestForQue(day);
            }
            else
            {
                parseJSON(LocalData.load(day + "question", getActivity()));
            }
        }
    }

    public void sendRequestForQue(final int preDay)
    {
        String quesAPI = "http://211.152.49.184:7001/OneForWeb/one/getOneQuestionInfo?strDate=" + HttpUtil.getCurrentDate("day", preDay);
        bar.setVisibility(View.VISIBLE);

        HttpUtil.sendHttpRequest(quesAPI, new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                LocalData.save(response, day + "question", getActivity());
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

                if(!(LocalData.load(day + "question", getActivity())).equals(""))
                {
                    parseJSON(LocalData.load(day + "question", getActivity()));
                }
            }
        });
    }

    public void parseJSON(String result)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject questionEntity = new JSONObject(jsonObject.getString("questionAdEntity"));
            String question = questionEntity.getString("strQuestionTitle");
            String quesCont = questionEntity.getString("strQuestionContent");
            String answer = questionEntity.getString("strAnswerTitle");
            String answCont = questionEntity.getString("strAnswerContent").replace("<br>", "\n").replace("<strong>", "").replace("</strong>", "");
            String editor = questionEntity.getString("sEditor");
            String praiseNum = questionEntity.getString("strPraiseNumber") + " 赞";
            String strTime = questionEntity.getString("strQuestionMarketTime");
            java.util.Date date= java.sql.Date.valueOf(strTime);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
            String time = sdf.format(date);
            String webLink = questionEntity.getString("sWebLk");

            String[] data = {question, quesCont, answer, answCont, editor, praiseNum, time, webLink};
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
        if((e2.getY() - e1.getY() > 260) && Math.abs(e2.getX() - e1.getX()) < 50 && flag)
        {
            day = 1;
            sendRequestForQue(day);
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
        Log.d("debug", day + "");
        return false;
    }
}
