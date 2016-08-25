package com.dstudio.wd.one.entity.hp;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wd824 on 2016/8/7.
 */
public class Data
{
    @SerializedName("hpcontent_id")
    private String hpContentId;

    @SerializedName("hp_title")
    private String hpTitle;

    @SerializedName("author_id")
    private String authorId;

    @SerializedName("hp_img_url")
    private String hpImgUrl;

    @SerializedName("hp_img_original_url")
    private String hpImgOriginalUrl;

    @SerializedName("hp_author")
    private String hpAuthor;

    @SerializedName("ipad_url")
    private String ipadUrl;

    @SerializedName("hp_content")
    private String hpContent;

    @SerializedName("hp_makettime")
    private String hpMakettime;

    @SerializedName("last_update_date")
    private String lastUpdateDate;

    @SerializedName("web_url")
    private String webUrl;

    @SerializedName("web_img_url")
    private String webImgUrl;

    @SerializedName("praisenum")
    private int praiseNum;

    @SerializedName("sharenum")
    private int shareNum;

    @SerializedName("commentnum")
    private int commentNum;

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

    public String getAuthorId()
    {
        return authorId;
    }

    public void setAuthorId(String authorId)
    {
        this.authorId = authorId;
    }

    public String getHpImgUrl()
    {
        return hpImgUrl;
    }

    public void setHpImgUrl(String hpImgUrl)
    {
        this.hpImgUrl = hpImgUrl;
    }

    public String getHpImgOriginalUrl()
    {
        return hpImgOriginalUrl;
    }

    public void setHpImgOriginalUrl(String hpImgOriginalUrl)
    {
        this.hpImgOriginalUrl = hpImgOriginalUrl;
    }

    public String getHpAuthor()
    {
        return hpAuthor;
    }

    public void setHpAuthor(String hpAuthor)
    {
        this.hpAuthor = hpAuthor;
    }

    public String getIpadUrl()
    {
        return ipadUrl;
    }

    public void setIpadUrl(String ipadUrl)
    {
        this.ipadUrl = ipadUrl;
    }

    public String getHpContent()
    {
        return hpContent;
    }

    public void setHpContent(String hpContent)
    {
        this.hpContent = hpContent;
    }

    public String getHpMakettime()
    {
        return hpMakettime;
    }

    public void setHpMakettime(String hpMakettime)
    {
        this.hpMakettime = hpMakettime;
    }

    public String getLastUpdateDate()
    {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate)
    {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getWebUrl()
    {
        return webUrl;
    }

    public void setWebUrl(String webUrl)
    {
        this.webUrl = webUrl;
    }

    public String getWebImgUrl()
    {
        return webImgUrl;
    }

    public void setWebImgUrl(String webImgUrl)
    {
        this.webImgUrl = webImgUrl;
    }

    public int getPraiseNum()
    {
        return praiseNum;
    }

    public void setPraiseNum(int praiseNum)
    {
        this.praiseNum = praiseNum;
    }

    public int getShareNum()
    {
        return shareNum;
    }

    public void setShareNum(int shareNum)
    {
        this.shareNum = shareNum;
    }

    public int getCommentNum()
    {
        return commentNum;
    }

    public void setCommentNum(int commentNum)
    {
        this.commentNum = commentNum;
    }
}
