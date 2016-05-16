package com.dstudio.wd.dweather.adapter;

/**
 * Created by wd824 on 2016/5/16.
 */
public class DailyWt
{
    private String txtDate;
    private String txtDailyWt;
    private String txtMaxTmp;
    private String txtMinTmp;

    public DailyWt(String txtDate, String txtDailyWt, String txtMaxTmp, String txtMinTmp)
    {
        this.txtDate = txtDate;
        this.txtDailyWt = txtDailyWt;
        this.txtMaxTmp = txtMaxTmp;
        this.txtMinTmp = txtMinTmp;
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
}
