package com.dstudio.wd.dweather.http;

/**
 * Created by wd824 on 2016/5/3.
 */
public interface HttpCallbackListener
{
    void onFinish(String response);

    void onError(Exception e);
}
