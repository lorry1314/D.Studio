package com.dstudio.wd.one.entity.essay;

import com.dstudio.wd.one.entity.Author;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wd824 on 2016/6/23.
 */
public class Data
{
    @SerializedName("content_id")
    private String contentId;

    @SerializedName("hp_title")
    private String hpTitle;

    @SerializedName("sub_title")
    private String subTitle;

    @SerializedName("hp_author")
    private String hpAuthor;

    @SerializedName("auth")
    private String authIt;

    @SerializedName("hp_author_introduce")
    private String hpAuthorIntroduce;

    @SerializedName("hp_content")
    private String hpContent;

    @SerializedName("hp_makettime")
    private String hpMakettime;

    @SerializedName("web_name")
    private String wbName;

    @SerializedName("web_img_url")
    private String wbImgUrl;

    @SerializedName("last_update_date")
    private String lastUpdateDate;

    @SerializedName("web_url")
    private String webUrl;

    @SerializedName("guide_word")
    private String guideWord;

    private String audio;

    private List<Author> author ;

    private int praisenum;

    private int sharenum;

    private int commentnum;

    public String getContentId()
    {
        return contentId;
    }

    public void setContentId(String contentId)
    {
        this.contentId = contentId;
    }

    public String getHpTitle()
    {
        return hpTitle;
    }

    public void setHpTitle(String hpTitle)
    {
        this.hpTitle = hpTitle;
    }

    public String getSubTitle()
    {
        return subTitle;
    }

    public void setSubTitle(String subTitle)
    {
        this.subTitle = subTitle;
    }

    public String getHpAuthor()
    {
        return hpAuthor;
    }

    public void setHpAuthor(String hpAuthor)
    {
        this.hpAuthor = hpAuthor;
    }

    public String getAuthIt()
    {
        return authIt;
    }

    public void setAuthIt(String authIt)
    {
        this.authIt = authIt;
    }

    public String getHpAuthorIntroduce()
    {
        return hpAuthorIntroduce;
    }

    public void setHpAuthorIntroduce(String hpAuthorIntroduce)
    {
        this.hpAuthorIntroduce = hpAuthorIntroduce;
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

    public String getWbName()
    {
        return wbName;
    }

    public void setWbName(String wbName)
    {
        this.wbName = wbName;
    }

    public String getWbImgUrl()
    {
        return wbImgUrl;
    }

    public void setWbImgUrl(String wbImgUrl)
    {
        this.wbImgUrl = wbImgUrl;
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

    public String getGuideWord()
    {
        return guideWord;
    }

    public void setGuideWord(String guideWord)
    {
        this.guideWord = guideWord;
    }

    public String getAudio()
    {
        return audio;
    }

    public void setAudio(String audio)
    {
        this.audio = audio;
    }

    public List<Author> getAuthor()
    {
        return author;
    }

    public void setAuthor(List<Author> author)
    {
        this.author = author;
    }

    public int getPraisenum()
    {
        return praisenum;
    }

    public void setPraisenum(int praisenum)
    {
        this.praisenum = praisenum;
    }

    public int getSharenum()
    {
        return sharenum;
    }

    public void setSharenum(int sharenum)
    {
        this.sharenum = sharenum;
    }

    public int getCommentnum()
    {
        return commentnum;
    }

    public void setCommentnum(int commentnum)
    {
        this.commentnum = commentnum;
    }
}
