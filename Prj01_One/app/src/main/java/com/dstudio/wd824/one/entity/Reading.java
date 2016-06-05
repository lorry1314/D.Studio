package com.dstudio.wd824.one.entity;

/**
 * Created by wd824 on 2016/6/5.
 */
public class Reading
{
    private String contTitle;
    private String contAuthor;
    private String content;
    private String editor;
    private String praiseNum;
    private String time;
    private String webLink;
    private String imgUrl;

    public Reading()
    {
        super();
    }

    public Reading(String contTitle, String contAuthor, String content, String editor,
                   String praiseNum, String time, String webLink, String imgUrl)
    {
        this.contTitle = contTitle;
        this.contAuthor = contAuthor;
        this.content = content;
        this.editor = editor;
        this.praiseNum = praiseNum;
        this.time = time;
        this.webLink = webLink;
        this.imgUrl = imgUrl;
    }

    public String getContTitle()
    {
        return contTitle;
    }

    public void setContTitle(String contTitle)
    {
        this.contTitle = contTitle;
    }

    public String getContAuthor()
    {
        return contAuthor;
    }

    public void setContAuthor(String contAuthor)
    {
        this.contAuthor = contAuthor;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getEditor()
    {
        return editor;
    }

    public void setEditor(String editor)
    {
        this.editor = editor;
    }

    public String getPraiseNum()
    {
        return praiseNum;
    }

    public void setPraiseNum(String praiseNum)
    {
        this.praiseNum = praiseNum;
    }

    public String getWebLink()
    {
        return webLink;
    }

    public void setWebLink(String webLink)
    {
        this.webLink = webLink;
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
}
