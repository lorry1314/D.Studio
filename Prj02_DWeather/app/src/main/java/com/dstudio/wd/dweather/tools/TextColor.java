package com.dstudio.wd.dweather.tools;

import android.util.Log;
import android.widget.TextView;

import com.dstudio.wd.dweather.R;

/**
 * Created by wd824 on 2016/5/31.
 */
public class TextColor
{
    private TextView txtQlty;

    public TextColor (TextView txtQlty)
    {
        this.txtQlty = txtQlty;
    }

    public void changeFontColor()
    {
        String txt = txtQlty.getText().toString();
        Log.d("debug", txt);
        int colorId = 0;
        switch (txt)
        {
            case "优":
                colorId = R.color.level_1;
                break;
            case "良":
                colorId = R.color.level_2;
                break;
            case "轻度污染":
                colorId = R.color.level_3;
                Log.d("debug", "3");
                break;
            case "中度污染":
                colorId = R.color.level_4;
                break;
            case "重度污染":

                colorId = R.color.level_5;
                break;
        }
        // txtQlty.setTextColor(colorId);
        txtQlty.setBackgroundResource(colorId);
    }
}
