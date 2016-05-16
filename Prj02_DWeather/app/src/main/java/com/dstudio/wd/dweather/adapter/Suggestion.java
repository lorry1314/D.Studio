package com.dstudio.wd.dweather.adapter;

/**
 * 生活提示
 * Created by wd824 on 2016/5/14.
 */
public class Suggestion
{
    private int sugIcon;
    private String sugTag;
    private String sugTitle;
    private String sugTxt;

    public Suggestion(int sugIcon, String sugTag, String sugTitle, String sugTxt)
    {
        this.sugIcon = sugIcon;
        this.sugTag = sugTag;
        this.sugTitle = sugTitle;
        this.sugTxt = sugTxt;
    }

    public void setSugIcon(int sugIcon)
    {
        this.sugIcon = sugIcon;
    }

    public void setSugTag(String sugTag)
    {
        this.sugTag = sugTag;
    }

    public void setSugTitle(String sugTitle)
    {
        this.sugTitle = sugTitle;
    }

    public void setSugTxt(String sugTxt)
    {
        this.sugTxt = sugTxt;
    }

    public int getSugIcon()
    {
        return sugIcon;
    }

    public String getSugTag()
    {
        return sugTag;
    }

    public String getSugTitle()
    {
        return sugTitle;
    }

    public String getSugTxt()
    {
        return sugTxt;
    }
}
