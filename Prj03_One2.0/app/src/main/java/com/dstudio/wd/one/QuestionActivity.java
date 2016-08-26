package com.dstudio.wd.one;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dstudio.wd.one.entity.Author;
import com.dstudio.wd.one.entity.essay.Root;
import com.dstudio.wd.one.entity.question.Data;
import com.dstudio.wd.one.util.HttpCallbackListener;
import com.dstudio.wd.one.util.HttpUtil;
import com.dstudio.wd.one.util.LocalData;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class QuestionActivity extends AppCompatActivity
{
    private Context mContext;

    private TextView txtQuestionTitle;
    private TextView txtQuestionContent;
    private TextView txtAnswerTitle;
    private TextView txtAnswerContent;
    private TextView txtAuthorIntroduce;

    private String webUrl;

    private static final String TAG = "QuestionActivity";
    private static final int SHOW_CONTENT = 0;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == SHOW_CONTENT)
            {
                Data data = (Data) msg.obj;
                txtQuestionTitle.setText(data.getQuestionTitle());
                txtQuestionContent.setText(data.getQuestionContent());
                txtAnswerTitle.setText(data.getAnswerTitle());
                txtAnswerContent.setText(Html.fromHtml(data.getAnswerContent()));
                txtAuthorIntroduce.setText(data.getChargeEdt());
                webUrl = data.getWebUrl();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        mContext = QuestionActivity.this;
        initView();

        final String questionId = getIntent().getStringExtra(getString(R.string.question_key));
        File oldFile = getFileStreamPath("question" + questionId + ".txt");
        if (!oldFile.exists())
        {
            sendRequest(questionId);
        }
        else
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    parseResponse(LocalData.load("question" + questionId, mContext));
                }
            }).start();
        }

    }

    public void initView()
    {
        txtQuestionTitle = (TextView) findViewById(R.id.txt_question_title);
        txtQuestionContent = (TextView) findViewById(R.id.txt_question_content);
        txtAnswerTitle = (TextView) findViewById(R.id.txt_answer_title);
        txtAnswerContent = (TextView) findViewById(R.id.txt_answer_content);
        txtAuthorIntroduce = (TextView) findViewById(R.id.txt_author_introduce);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_share)
        {
            HttpUtil.showShare(mContext, txtQuestionTitle.getText().toString(), txtQuestionContent.getText().toString(),
                    webUrl, getString(R.string.icon_url));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendRequest(final String questionId)
    {
        String api = getString(R.string.question_api) + questionId;

        HttpUtil.sendGet(api, new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                parseResponse(response);
                LocalData.save(response, "question" + questionId, mContext);
            }

            @Override
            public void onError(Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    public void parseResponse(String response)
    {
        com.dstudio.wd.one.entity.question.Root root = new Gson().fromJson(response, com.dstudio.wd.one.entity.question.Root.class);
        if (root.getRes() == 0)
        {
            Message msg = new Message();
            msg.what = SHOW_CONTENT;
            msg.obj = root.getData();
            handler.sendMessage(msg);
        }
    }
}
