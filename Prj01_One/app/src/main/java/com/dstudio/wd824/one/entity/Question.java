package com.dstudio.wd824.one.entity;

/**
 * Created by wd824 on 2016/6/5.
 */
public class Question
{
    private String title;
    private String quesCont;
    private String answer;
    private String answCont;
    private String editor;
    private String praiseNum;
    private String time;
    private String webLink;

    public Question(String title, String quesCont, String answer, String answCont, String editor, String praiseNum, String time, String webLink)
    {
        this.title = title;
        this.quesCont = quesCont;
        this.answer = answer;
        this.answCont = answCont;
        this.editor = editor;
        this.praiseNum = praiseNum;
        this.time = time;
        this.webLink = webLink;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getQuesCont()
    {
        return quesCont;
    }

    public void setQuesCont(String quesCont)
    {
        this.quesCont = quesCont;
    }

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    public String getAnswCont()
    {
        return answCont;
    }

    public void setAnswCont(String answCont)
    {
        this.answCont = answCont;
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

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
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
