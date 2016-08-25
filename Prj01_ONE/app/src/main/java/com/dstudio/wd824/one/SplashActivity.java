package com.dstudio.wd824.one;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.dstudio.wd.one.util.HttpCallbackListener;
import com.dstudio.wd.one.util.HttpUtil;
import com.dstudio.wd.one.util.LocalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplashActivity extends Activity
{

    private ArrayList<String> idList = new ArrayList<>();

    private Context mContext;

    private static final String TAG = "SplashActivity";

    private static final int RECEIVED_ID_LIST = 0;

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == RECEIVED_ID_LIST)
            {
                if (msg.obj != null)
                {
                    showMain();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = getApplicationContext();
        sendRequest();
    }

    public void sendRequest()
    {
        HttpUtil.sendGet(getString(R.string.id_list), new HttpCallbackListener()
        {
            @Override
            public void onFinish(String response)
            {
                LocalData.save(response, "idlist", mContext);
                parseIdList(response);
            }

            @Override
            public void onError(Exception e)
            {
                Log.e(TAG, "发送请求失败！" + e.getMessage());
                e.printStackTrace();
                final String response = LocalData.load("idlist", mContext);
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        parseIdList(response);
                    }
                }).start();
            }
        });
    }

    public void parseIdList(String response)
    {
        try
        {
            JSONObject results = new JSONObject(response);
            String res= results.getString("res");
            if (res.equals("0"))
            {
                JSONArray data = results.getJSONArray("data");
                for (int i = 0; i < data.length(); i++)
                {
                    idList.add(data.getString(i));
                }
            }

            Message message = new Message();
            message.what = RECEIVED_ID_LIST;
            message.obj = idList;
            handler.sendMessage(message);
        }
        catch (JSONException e)
        {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public void showMain()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putStringArrayListExtra("id_list", idList);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 1000);
    }
}
