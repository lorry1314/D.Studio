package com.dstudio.wd.one.entity;

/**
 * Created by wd824 on 2016/6/15.
 */
public class Detail
{
    private String hpContentId;

    private String hpTitle;

    private String hpImgUrl;

    private String hpAuthor;

    private String hpContent;

    private String hpMaketTime;

    private String lastUpdate;

    private String praiseNum;

    private String webUrl;

    public Detail()
    {

    }

    public Detail(String hpContentId, String hpTitle, String hpImgUrl, String hpAuthor, String hpContent,
                  String hpMaketTime, String lastUpdate, String praiseNum, String webUrl)
    {
        this.hpContentId = hpContentId;
        this.hpTitle = hpTitle;
        this.hpImgUrl = hpImgUrl;
        this.hpAuthor = hpAuthor;
        this.hpContent = hpContent;
        this.hpMaketTime = hpMaketTime;
        this.lastUpdate = lastUpdate;
        this.praiseNum = praiseNum;
        this.webUrl = webUrl;
    }

    public String getHpContentId()
    {
        return hpContentId;
    }

    public void setHpContentId(String hpContentId)
    {
        this.hpContentId = hpContentId;
    }

    public String getHpTitle()
    {
        return hpTitle;
    }

    public void setHpTitle(String hpTitle)
    {
        this.hpTitle = hpTitle;
    }

    public String getHpImgUrl()
    {
        return hpImgUrl;
    }

    public void setHpImgUrl(String hpImgUrl)
    {
        this.hpImgUrl = hpImgUrl;
    }

    public String getHpAuthor()
    {
        return hpAuthor;
    }

    public void setHpAuthor(String hpAuthor)
    {
        this.hpAuthor = hpAuthor;
    }

    public String getHpContent()
    {
        return hpContent;
    }

    public void setHpContent(String hpContent)
    {
        this.hpContent = hpContent;
    }

    public String getHpMaketTime()
    {
        return hpMaketTime;
    }

    public void setHpMaketTime(String hpMaketTime)
    {
        this.hpMaketTime = hpMaketTime;
    }

    public String getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    public String getPraiseNum()
    {
        return praiseNum;
    }

    public void setPraiseNum(String praiseNum)
    {
        this.praiseNum = praiseNum;
    }

    public String getWebUrl()
    {
        return webUrl;
    }

    public void setWebUrl(String webUrl)
    {
        this.webUrl = webUrl;
    }
}
