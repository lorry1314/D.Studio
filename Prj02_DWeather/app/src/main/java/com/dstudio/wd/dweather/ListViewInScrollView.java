package com.dstudio.wd.dweather;

import android.content.Context;
import android.widget.ListView;

/**
 * Created by wd824 on 2016/5/15.
 */
public class ListViewInScrollView extends ListView
{

    public ListViewInScrollView(android.content.Context context, android.util.AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * Integer.MAX_VALUE >> 2,如果不设置，系统默认设置是显示两条
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}