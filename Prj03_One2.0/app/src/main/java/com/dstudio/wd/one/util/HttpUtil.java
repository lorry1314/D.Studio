package com.dstudio.wd.one.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by wd824 on 2016/5/3.
 */
public class HttpUtil
{
    public static void sendGet(final String address, final HttpCallbackListener listener)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                HttpURLConnection connection = null;
                try
                {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    // connection.setDoInput(true);
                    // connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        response.append(line);
                    }
                    if (listener != null)
                    {
                        listener.onFinish(response.toString());
                    }
                }
                catch (Exception e)
                {
                    if (listener != null)
                    {
                        listener.onError(e);
                    }
                }
                finally
                {
                    if (connection != null)
                    {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void sendPost(final String address, final String param, final HttpCallbackListener listener)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                HttpURLConnection connection = null;
                PrintWriter out = null;
                try
                {
                    URL url = new URL(address);
                    byte[] data = param.getBytes();
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.getOutputStream().write(data);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        response.append(line);
                    }
                    if (listener != null)
                    {
                        listener.onFinish(response.toString());
                    }
                }
                catch (Exception e)
                {
                    if (listener != null)
                    {
                        listener.onError(e);
                    }
                }
                finally
                {
                    if (connection != null)
                    {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static String getCurrentDate(String option, int day)
    {
        Calendar cal = Calendar.getInstance();
        String currentTime = null;
        if(option.equals("day"))
        {
            cal.add(Calendar.DAY_OF_MONTH, -day);
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
            currentTime = format.format(cal.getTime());
        }
        else if (option.equals("hour"))
        {
            Date date = new Date();
            currentTime = date.getHours() + "";
        }
        return currentTime;
    }

    public static void showShare(Context mContext, String title, String text, String url, String imgUrl)
    {
        ShareSDK.initSDK(mContext);
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        oks.setTitle(title);
        oks.setText(text);
        oks.setTitleUrl(url);
        oks.setUrl(url);
        oks.setSite(url);
        oks.setSiteUrl(url);
        oks.setImageUrl(imgUrl);
        oks.show(mContext);
    }
}
