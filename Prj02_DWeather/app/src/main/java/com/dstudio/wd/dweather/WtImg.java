package com.dstudio.wd.dweather;

import android.widget.ImageView;

import com.dstudio.wd.dweather.adapter.WtAdapter;

import java.util.Date;

/**
 * 根据当前时间以及实时天气，切换图片
 * Created by wd824 on 2016/5/23.
 */
public class WtImg
{
    private ImageView imgWt;       // 相应控件
    private boolean isRain = false;

    public WtImg(ImageView imgWt)
    {
        this.imgWt = imgWt;
    }

    public void judgeWt(String nowWt)
    {
        if (nowWt.contains("多云"))
        {
            imgWt.setImageResource(R.drawable.cloudy);
        }
        else if (nowWt.contains("阴"))
        {
            imgWt.setImageResource(R.drawable.overcast);
        }
        else if (nowWt.contains("阵雨") | nowWt.contains("细雨") | nowWt.contains("小雨"))
        {
            isRain = true;
            selectHour(R.drawable.rain);
        }
        else if (nowWt.contains("大雨") | nowWt.contains("暴雨") | nowWt.contains("雷阵雨"))
        {
            isRain = true;
            selectHour(R.drawable.heavy_rain);
        }
        else
        {
            selectHour(0);
        }
    }

    public void selectHour(int resId)
    {
        int hours = new Date().getHours();
        if (hours < 5)
        {
            showImg(isRain ? R.drawable.night_rain : R.drawable.night_1);
        }
        else if (hours < 7)
        {
            showImg(isRain ? R.drawable.night_rain : R.drawable.morning);
        }
        else if (hours < 11)
        {
            showImg(isRain ? resId : R.drawable.fine_1);
        }
        else if (hours < 15)
        {
            showImg(isRain ? resId : R.drawable.fine);
        }
        else if (hours < 19)
        {
            showImg(isRain ? resId : R.drawable.evening);
        }
        else if (hours < 22)
        {
            showImg(isRain ? R.drawable.night_rain : R.drawable.night);
        }
        else
        {
            showImg(isRain ? R.drawable.night_rain : R.drawable.night_1);
        }
    }

    public void showImg(int resId)
    {
        imgWt.setImageResource(resId);
    }
}
