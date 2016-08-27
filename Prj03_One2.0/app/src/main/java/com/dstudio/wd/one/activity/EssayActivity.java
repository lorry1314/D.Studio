package com.dstudio.wd.one.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dstudio.wd.one.R;
import com.dstudio.wd.one.entity.Author;
import com.dstudio.wd.one.entity.essay.Data;
import com.dstudio.wd.one.entity.essay.Root;
import com.dstudio.wd.one.util.HttpCallbackListener;
import com.dstudio.wd.one.util.HttpUtil;
import com.dstudio.wd.one.util.LocalCache;
import com.dstudio.wd.one.util.LocalData;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

public class EssayActivity extends AppCompatActivity
{
    private TextView txtTitle;
    private TextView txtAuthor;
    private TextView txtContent;
    private ImageView imgAuthor;
    private TextView txtAuthorIntroduce;

    private Context mContext;

    private String webUrl;
    private String imgUrl;
    private String guideWord;

    private static final String TAG = "EssayActivity";
    private static final int SHOW_CONTENT = 0;
    private static final int SHOW_IMG = 1;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == SHOW_CONTENT)
            {
                Data data = (Data) msg.obj;
                List<Author> authorList = data.getAuthor();

                String authorImg = authorList.get(0).getWebUrl();
                setAuthorImg(authorImg);
                txtAuthor.setText(authorList.get(0).getUserNmae());
                txtTitle.setText(data.getHpTitle());
                txtContent.setText(Html.fromHtml(data.getHpContent()));
                txtAuthorIntroduce.setText(data.getHpAuthorIntroduce());
                webUrl = data.getWebUrl();
                imgUrl = data.getWbImgUrl();
                guideWord = data.getGuideWord();
            }
            else if (msg.what == SHOW_IMG)
            {
                Bitmap bmAuthor = (Bitmap) msg.obj;
                imgAuthor.setImageBitmap(bmAuthor);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essay);
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
        mContext = EssayActivity.this;
        initView();

        String contentId = getIntent().getStringExtra(getString(R.string.essay_key));
        File file = mContext.getFileStreamPath("essay" + contentId + ".txt");
        if (!file.exists())
        {
            sendRequest(contentId);
        }
        else
        {
            final String data = LocalData.load("essay" + contentId, mContext);
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    parseResponse(data);
                }
            }).start();
        }
    }

    public void initView()
    {
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtAuthor = (TextView) findViewById(R.id.txt_author);
        txtContent = (TextView) findViewById(R.id.txt_content);
        imgAuthor = (ImageView) findViewById(R.id.img_author);
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
            HttpUtil.showShare(mContext, txtTitle.getText().toString(), txtContent.getText().toString(),
                    webUrl, getString(R.string.icon_url));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendRequest(final String contentId)
    {
        String api = getString(R.string.essay_api) + contentId;

        HttpUtil.sendGet(api, new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                parseResponse(response);
                LocalData.save(response, "essay" + contentId, mContext);
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
        Root root = new Gson().fromJson(response, Root.class);
        if (root.getRes() == 0)
        {
            Message message = new Message();
            message.what = SHOW_CONTENT;
            message.obj = root.getData();
            handler.sendMessage(message);
        }
    }

    public void setAuthorImg(final String imgUrl)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Bitmap bitmap = LocalCache.readImgCache(mContext, imgUrl);
                if(bitmap == null)
                {
                    LocalCache.writeCache(mContext, imgUrl, "Bitmap");
                    while (true)
                    {
                        if (LocalCache.writeCache(mContext, imgUrl, "Bitmap"))
                        {
                            bitmap = LocalCache.readImgCache(mContext, imgUrl);
                            break;
                        }
                    }
                }
                Message msg = new Message();
                msg.what = SHOW_IMG;
                msg.obj = bitmap;
                handler.sendMessage(msg);
            }
        }).start();
    }
}
