package com.dstudio.wd.dweather;

import android.view.View;
import android.widget.GridView;

/**
 * Created by wd824 on 2016/5/16.
 */
public class GridViewInScrollView extends GridView
{

    public GridViewInScrollView(android.content.Context context, android.util.AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * Integer.MAX_VALUE >> 2,如果不设置，系统默认设置是显示两条
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
