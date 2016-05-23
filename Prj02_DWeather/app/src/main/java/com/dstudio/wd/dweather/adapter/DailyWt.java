package com.dstudio.wd.dweather.adapter;

import android.graphics.Bitmap;

/**
 * Created by wd824 on 2016/5/16.
 */
public class DailyWt
{
    private String txtDate;
    private String txtDailyWt;
    private String txtMaxTmp;
    private String txtMinTmp;
    private Bitmap imgWtIcon;

    public DailyWt(String txtDate, Bitmap imgWtIcon, String txtDailyWt, String txtMaxTmp, String txtMinTmp)
    {
        this.txtDate = txtDate;
        this.txtDailyWt = txtDailyWt;
        this.txtMaxTmp = txtMaxTmp;
        this.txtMinTmp = txtMinTmp;
        this.imgWtIcon = imgWtIcon;
    }

    public void setTxtDate(String txtDate)
    {
        this.txtDate = txtDate;
    }

    public void setTxtDailyWt(String txtDailyWt)
    {
        this.txtDailyWt = txtDailyWt;
    }

    public void setTxtMaxTmp(String txtMaxTmp)
    {
        this.txtMaxTmp = txtMaxTmp;
    }

    public void setTxtMinTmp(String txtMinTmp)
    {
        this.txtMinTmp = txtMinTmp;
    }

    public void setImgWtIcon(Bitmap imgWtIcon)
    {
        this.imgWtIcon = imgWtIcon;
    }

    public String getTxtDate()
    {
        return txtDate;
    }

    public String getTxtDailyWt()
    {
        return txtDailyWt;
    }

    public String getTxtMaxTmp()
    {
        return txtMaxTmp;
    }

    public String getTxtMinTmp()
    {
        return txtMinTmp;
    }

    public Bitmap getImgWtIcon()
    {
        return imgWtIcon;
    }
}
