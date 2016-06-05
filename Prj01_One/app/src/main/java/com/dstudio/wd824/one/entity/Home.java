package com.dstudio.wd824.one.entity;

/**
 * Created by wd824 on 2016/6/5.
 */
public class Home
{
    private String strTitle;
    private String strAuthor;
    private String strContent;
    private String time;
    private String imgUrl;
    private String webLink;

    public Home()
    {
        super();
    }

    public Home(String strTitle, String strAuthor, String strContent, String time, String webLink, String imgUrl)
    {
        this.strTitle = strTitle;
        this.strAuthor = strAuthor;
        this.strContent = strContent;
        this.time = time;
        this.webLink = webLink;
        this.imgUrl = imgUrl;
    }

    public String getStrTitle()
    {
        return strTitle;
    }

    public void setStrTitle(String strTitle)
    {
        this.strTitle = strTitle;
    }

    public String getStrAuthor()
    {
        return strAuthor;
    }

    public void setStrAuthor(String strAuthor)
    {
        this.strAuthor = strAuthor;
    }

    public String getStrContent()
    {
        return strContent;
    }

    public void setStrContent(String strContent)
    {
        this.strContent = strContent;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getImgUrl()
    {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl)
    {
        this.imgUrl = imgUrl;
    }

    public String getWebLink()
    {
        return webLink;
    }

    public void setWebLink(String webLink)
    {
        this.webLink = webLink;
    }
}
