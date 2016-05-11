package com.dstudio.wd824.one.Data;

import android.app.Activity;
import android.util.Log;

import com.dstudio.wd824.one.Data.HttpCallbackListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wd824 on 2016/5/3.
 */
public class HttpUtil
{
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener)
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
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
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

    public static boolean judgeTime(int updateHour)
    {
        int currentHour = Integer.parseInt(HttpUtil.getCurrentDate("hour", 0));
        System.out.println(currentHour + "," + updateHour);
        if(currentHour - updateHour >= 5)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static int selectWhichDay()
    {
        int currentHour = Integer.parseInt(getCurrentDate("hour", 0));
        if (currentHour >= 16)
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }


}
