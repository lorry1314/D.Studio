package com.dstudio.wd.one;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

    private RequestQueue mQueue;

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
        mQueue = Volley.newRequestQueue(mContext);
        sendRequest();
    }

    public void sendRequest()
    {
        JsonObjectRequest request = new JsonObjectRequest(getString(R.string.id_list), null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject jsonObject)
                    {
                        LocalData.save(jsonObject.toString(), "idlist", mContext);
                        parseIdList(jsonObject.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e(TAG, "获取数据失败！" + volleyError.toString());
                        parseIdList(LocalData.load("idlist", mContext));
                    }
                });
        mQueue.add(request);
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
                // mQueue.stop();
                SplashActivity.this.finish();
            }
        }, 1000);
    }
}
